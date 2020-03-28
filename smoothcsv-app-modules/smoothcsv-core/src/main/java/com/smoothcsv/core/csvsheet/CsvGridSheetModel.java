/*
 * Copyright 2016 kohii
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.smoothcsv.core.csvsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.csvsheet.edits.ChangeValueEdit;
import com.smoothcsv.core.csvsheet.edits.ChangeValuesEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteCellEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteColumnsEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteRowsEdit;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoableEdit;
import com.smoothcsv.core.csvsheet.edits.InserColumnsEdit;
import com.smoothcsv.core.csvsheet.edits.InserRowsEdit;
import com.smoothcsv.core.csvsheet.edits.InsertCellEdit;
import com.smoothcsv.core.csvsheet.edits.PartialSortEdit;
import com.smoothcsv.core.csvsheet.edits.SortEdit;
import com.smoothcsv.core.csvsheet.edits.SpecifiedRowsSortEdit;
import com.smoothcsv.core.csvsheet.edits.ToggleHeaderRowEdit;
import com.smoothcsv.core.sort.CsvSorter;
import com.smoothcsv.core.sort.CsvSorter.SortResult;
import com.smoothcsv.core.sort.SortCriteria;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.swing.gridsheet.event.GridSheetDataEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetStructureEvent;
import com.smoothcsv.swing.gridsheet.model.CellRect;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetRow;
import lombok.Setter;

/**
 * @author kohii
 */
public class CsvGridSheetModel extends GridSheetModel {

  @Setter
  private Consumer<GridSheetUndoableEdit> undableEditListener;

  private boolean useFirstRowAsHeader = false;

  private boolean collectingEditDisabled = false;

  /**
   * @param dataList
   * @param rowCount
   * @param columnCount
   */
  public CsvGridSheetModel(List<List<String>> dataList, int rowCount, int columnCount) {
    super(dataList, rowCount, columnCount);
  }

  /**
   * @param dataList
   */
  public CsvGridSheetModel(List<List<String>> dataList) {
    super(dataList);
  }

  /**
   * @param dataList
   * @param rowCount
   * @param columnCount
   */
  public CsvGridSheetModel(List<List<String>> dataList,
                           int rowCount,
                           int columnCount,
                           List<String> columnNames) {
    super(dataList, rowCount, columnCount);
    this.useFirstRowAsHeader = true;
    for (int i = 0; i < columnCount; i++) {
      GridSheetColumn col = getColumn(i);
      col.setName(columnNames.get(i));
    }
  }

  /**
   * @param index
   * @param columnIds
   * @param data
   */
  public void insertColumn(int index, long[] columnIds, String[][] data) {
    GridSheetColumn[] columns = new GridSheetColumn[columnIds.length];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = createDefaultColumn(columnIds[i]);
    }
    insertColumn(index, columns);

    for (int i = 0; i < data.length; i++) {
      String[] columnData = data[i];
      for (int j = 0; j < columnData.length; j++) {
        String val = columnData[j];
        List<String> rowData = getRowDataAt(j);
        if (val != null) {
          rowData.set(index + i, val);
          // } else {
          // rowData.subList(index + i, rowData.size()).clear();
        }
      }
    }
  }

  /**
   * @param index
   * @param data
   */
  public void insertRow(int index, String[][] data) {
    insertRow(index, data.length);
    for (int i = 0; i < data.length; i++) {
      String[] rowData = data[i];
      setRowDataAt(index + i, rowData);
    }
  }

  public void setSizeAt(int rowIndex, int size) {
    List<String> rowData = getRowDataAt(rowIndex);
    if (rowData.size() <= size) {
      String val = getDefaultValue();
      for (int i = size - rowData.size() - 1; i >= 0; i--) {
        rowData.add(val);
      }
    } else {
      rowData.subList(size, rowData.size()).clear();
    }
  }

  public void deleteCell(int rowIndex, int from, int to) {
    List<String> rowData = getRowDataAt(rowIndex);
    List<String> truncateRange = rowData.subList(from, to + 1);
    String[] truncatedData = truncateRange.toArray(new String[0]);
    truncateRange.clear();
    collectEdit(new DeleteCellEdit(rowIndex, from, truncatedData));
    fireDataUpdated(rowIndex, from, rowIndex, getColumnCount(), false);
  }

  public void insertCell(int rowIndex, int columnIndex, String[] data) {
    List<String> rowData = getRowDataAt(rowIndex);
    int rowColumnSize = rowData.size();
    if (rowColumnSize + data.length > getColumnCount()) {
      GridSheetColumn[] columns = new GridSheetColumn[rowColumnSize + data.length - getColumnCount()];
      for (int i = 0; i < columns.length; i++) {
        columns[i] = createDefaultColumn();
      }
      this.columns.addAll(Arrays.asList(columns));
      fireColumnsInserted(rowColumnSize, columns, false);
    }
    rowData.addAll(columnIndex, Arrays.asList(data));
    collectEdit(new InsertCellEdit(rowIndex, columnIndex, data));
    fireDataUpdated(rowIndex, columnIndex, rowIndex, getColumnCount(), false);
  }

  @Override
  public void setValueAt(String aValue, int row, int column) {
    String oldValue = getValueAt(row, column);
    if (!equals(aValue, oldValue)) {
      collectEdit(new ChangeValueEdit(oldValue, aValue, row, column));
    }
    super.setValueAt(aValue, row, column);
  }

  @Override
  public void setValuesAt(List<List<String>> valuesList, int row, int column) {
    if (valuesList.isEmpty()) {
      return;
    }
    int maxColumnCont = valuesList.stream().mapToInt(v -> v.size()).max().getAsInt();
    List<List<String>> oldValuesList = getValuesAt(row, column, valuesList.size(), maxColumnCont);
    collectEdit(new ChangeValuesEdit(oldValuesList, valuesList, row, column));
    super.setValuesAt(valuesList, row, column);
  }

  @Override
  protected void fireColumnsInserted(int index, GridSheetColumn[] columnsInserted, boolean createData) {
    collectEdit(new InserColumnsEdit(index, columnsInserted.length));
    super.fireColumnsInserted(index, columnsInserted, createData);
  }

  @Override
  protected void fireColumnsDeleted(int index, GridSheetColumn[] columnsRemoved) {
    long[] columnIds = new long[columnsRemoved.length];
    String[][] data = new String[columnsRemoved.length][];
    for (int i = 0; i < columnsRemoved.length; i++) {
      columnIds[i] = columnsRemoved[i].getId();
      data[i] = getColumnDataAt(index + i);
    }
    collectEdit(new DeleteColumnsEdit(index, columnIds, data));
    super.fireColumnsDeleted(index, columnsRemoved);
  }

  @Override
  protected void fireRowsInserted(int index, GridSheetRow[] rowsInserted) {
    collectEdit(new InserRowsEdit(index, rowsInserted.length));
    super.fireRowsInserted(index, rowsInserted);
  }

  @Override
  protected void fireRowsDeleted(int index, GridSheetRow[] rowsRemoved) {
    String[][] data = new String[rowsRemoved.length][];
    for (int i = 0; i < rowsRemoved.length; i++) {
      data[i] = getRowDataAt(index + i).toArray(new String[0]);
    }
    collectEdit(new DeleteRowsEdit(index, data));
    super.fireRowsDeleted(index, rowsRemoved);
  }

  @Override
  protected void deleteColumnData(int index, int numColumns) {
    int rowCount = getRowCount();
    for (int r = 0; r < rowCount; r++) {
      List<String> rowData = getRowDataAt(r);
      if (index < rowData.size()) {
        rowData.subList(index, Math.min(index + numColumns, rowData.size())).clear();
      }
    }
    fireDataUpdated(0, index, GridSheetDataEvent.TO_THE_END, GridSheetDataEvent.TO_THE_END, true);
  }

  @Override
  protected void insertColumnData(int index, int numColumns) {
    int rowCount = getRowCount();
    for (int r = 0; r < rowCount; r++) {
      List<String> rowData = getRowDataAt(r);
      if (index <= rowData.size()) {
        String[] elements = new String[numColumns];
        Arrays.fill(elements, getDefaultValue());
        List<String> newData = Arrays.asList(elements);
        rowData.addAll(index, newData);
      }
    }
    fireDataUpdated(0, index, GridSheetDataEvent.TO_THE_END, GridSheetDataEvent.TO_THE_END, true);
  }

  private String[] getColumnDataAt(int columnIndex) {
    int rowCount = getRowCount();
    String[] columnData = new String[rowCount];
    for (int j = 0; j < rowCount; j++) {
      columnData[j] = getValueAt(j, columnIndex);
    }
    return columnData;
  }

  // for sort
  public void sort(List<SortCriteria> criterias) {
    SortResult sortResult = CsvSorter.sort(criterias, dataList, true);
    collectEdit(new SortEdit(criterias, sortResult.getOrder()));
    this.dataList = sortResult.getSortedData();
    fireStructureChanged(GridSheetStructureEvent.SORT_ROWS);
  }

  public void sort(List<SortCriteria> criterias, int[] targetRows) {
    List<List<String>> targetDataList = new ArrayList<>();
    for (int i = 0; i < targetRows.length; i++) {
      int r = targetRows[i];
      targetDataList.add(dataList.get(r));
    }
    SortResult sortResult = CsvSorter.sort(criterias, targetDataList, true);
    undableEditListener
        .accept(new SpecifiedRowsSortEdit(criterias, sortResult.getOrder(), targetRows));
    for (int i = 0; i < targetRows.length; i++) {
      int r = targetRows[i];
      dataList.set(r, sortResult.getSortedData().get(i));
    }
    fireStructureChanged(GridSheetStructureEvent.SORT_ROWS);
  }

  public void sort(List<SortCriteria> criterias, CellRect targetCells) {
    setAdjusting(true);
    List<List<String>> targetDataList = new ArrayList<>(targetCells.getNumRows());
    for (int r = targetCells.getRow(); r <= targetCells.getLastRow(); r++) {
      List<String> rowData = new ArrayList(targetCells.getNumColumns());
      for (int c = targetCells.getColumn(); c <= targetCells.getLastColumn(); c++) {
        String v = getValueAt(r, c);
        if (v == null) {
          throw new AppException("WSCA0005");
        }
        rowData.add(v);
      }
      targetDataList.add(rowData);
    }
    SortResult sortResult = CsvSorter.sort(criterias, targetDataList, false);
    collectEdit(new PartialSortEdit(criterias, sortResult.getOrder(), targetCells));
    for (int i = 0; i < targetCells.getNumRows(); i++) {
      List<String> rowData = getRowDataAt(i + targetCells.getRow());
      List<String> sortedRowData = sortResult.getSortedData().get(i);
      for (int c = targetCells.getColumn(); c <= targetCells.getLastColumn(); c++) {
        rowData.set(c, sortedRowData.get(c - targetCells.getColumn()));
      }
    }
    fireDataUpdated(targetCells.getRow(), targetCells.getColumn());
    fireDataUpdated(targetCells.getLastRow(), targetCells.getLastColumn());
    setAdjusting(false);
  }

  public List<List<String>> getDataList(int rowFrom, int rowTo) {
    List<List<String>> ret = new ArrayList<List<String>>();
    for (int r = rowFrom; r < rowTo; r++) {
      ret.add(Collections.unmodifiableList(dataList.get(r)));
    }
    return Collections.unmodifiableList(ret);
  }

  @Override
  public String getColumnName(int column) {
    if (useFirstRowAsHeader) {
      return getColumn(column).getName();
    }
    return super.getColumnName(column);
  }

  public boolean usesFirstRowAsHeader() {
    return useFirstRowAsHeader;
  }

  public void setUseFirstRowAsHeader(boolean b) {
    if (useFirstRowAsHeader == b) {
      return;
    }

    collectingEditDisabled = true;
    try {
      if (b) {
        List<String> firstRow = getRowDataAt(0);
        int columnCount = getColumnCount();
        for (int c = 0; c < columnCount; c++) {
          GridSheetColumn col = getColumn(c);
          if (c < firstRow.size()) {
            col.setName(firstRow.get(c));
          }
        }
        deleteRow(0);
      } else {
        int columnCount = getColumnCount();
        List<String> firstRow = new ArrayList<>(columnCount);
        for (int c = 0; c < columnCount; c++) {
          GridSheetColumn col = getColumn(c);
          String val = col.getName();
          col.setName(null);
          if (val == null) {
            break;
          }
          firstRow.add(val);
        }
        String[] firstRowData = firstRow.toArray(new String[0]);
        insertRow(0, new String[][]{firstRowData});
      }
    } finally {
      collectingEditDisabled = false;
    }

    collectEdit(new ToggleHeaderRowEdit());
    useFirstRowAsHeader = b;
  }

  private void collectEdit(GridSheetUndoableEdit undableEdit) {
    if (collectingEditDisabled) {
      return;
    }
    undableEditListener.accept(undableEdit);
  }

  private static final boolean equals(String o0, String o1) {
    if (StringUtils.isEmpty(o0)) {
      return StringUtils.isEmpty(o1);
    } else {
      if (StringUtils.isEmpty(o1)) {
        return false;
      } else {
        return o0.toString().equals(o1.toString());
      }
    }
  }

  @Override
  protected String createDefaultHeaderValue() {
    if (useFirstRowAsHeader) {
      return "";
    } else {
      return null;
    }
  }
}
