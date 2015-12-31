/*
 * Copyright 2015 kohii
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

import lombok.Setter;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.csvsheet.edits.ChangeValueEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteCellEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteColumnsEdit;
import com.smoothcsv.core.csvsheet.edits.DeleteRowsEdit;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndableEdit;
import com.smoothcsv.core.csvsheet.edits.InserColumnsEdit;
import com.smoothcsv.core.csvsheet.edits.InserRowsEdit;
import com.smoothcsv.core.csvsheet.edits.InsertCellEdit;
import com.smoothcsv.core.csvsheet.edits.PartialSortEdit;
import com.smoothcsv.core.csvsheet.edits.SortEdit;
import com.smoothcsv.core.csvsheet.edits.SpecifiedRowsSortEdit;
import com.smoothcsv.core.sort.CsvSorter;
import com.smoothcsv.core.sort.SortCriteria;
import com.smoothcsv.core.sort.CsvSorter.SortResult;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.swing.gridsheet.event.GridSheetDataEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetStructureEvent;
import com.smoothcsv.swing.gridsheet.model.CellRect;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetRow;

/**
 * @author kohii
 *
 */
public class CsvGridSheetModel extends GridSheetModel {

  @Setter
  private Consumer<GridSheetUndableEdit> undableEditListener;

  /**
   * @param dataList
   * @param rowCount
   * @param columnCount
   */
  public CsvGridSheetModel(List<List> dataList, int rowCount, int columnCount) {
    super(dataList, rowCount, columnCount);
  }

  /**
   * @param dataList
   */
  public CsvGridSheetModel(List<List> dataList) {
    super(dataList);
  }

  /**
   * @param index
   * @param data
   */
  public void insertColumn(int index, Object[][] data) {
    insertColumn(index, data.length);
    for (int i = 0; i < data.length; i++) {
      Object[] columnData = data[i];
      for (int j = 0; j < columnData.length; j++) {
        Object val = columnData[j];
        List rowData = getRowDataAt(j);
        if (val != null) {
          rowData.set(index + i, val);
//        } else {
//          rowData.subList(index + i, rowData.size()).clear();
        }
      }
    }
  }

  /**
   * @param index
   * @param data
   */
  public void insertRow(int index, Object[][] data) {
    insertRow(index, data.length);
    for (int i = 0; i < data.length; i++) {
      Object[] rowData = data[i];
      setRowDataAt(index + i, rowData);
    }
  }

  public void setSizeAt(int rowIndex, int size) {
    List rowData = getRowDataAt(rowIndex);
    if (rowData.size() <= size) {
      Object val = getDefaultValue();
      for (int i = size - rowData.size() - 1; i >= 0; i--) {
        rowData.add(val);
      }
    } else {
      rowData.subList(size, rowData.size()).clear();
    }
  }

  public void deleteCell(int rowIndex, int from, int to) {
    List rowData = getRowDataAt(rowIndex);
    List truncateRange = rowData.subList(from, to + 1);
    Object[] truncatedData = truncateRange.toArray();
    truncateRange.clear();
    undableEditListener.accept(new DeleteCellEdit(rowIndex, from, truncatedData));
    fireDataUpdated(rowIndex, from, rowIndex, getColumnCount(), false);
  }

  public void insertCell(int rowIndex, int columnIndex, Object[] data) {
    List rowData = getRowDataAt(rowIndex);
    rowData.addAll(columnIndex, Arrays.asList(data));
    undableEditListener.accept(new InsertCellEdit(rowIndex, columnIndex, data));
    fireDataUpdated(rowIndex, columnIndex, rowIndex, getColumnCount(), false);
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    Object oldValue = getValueAt(row, column);
    if (!equals(aValue, oldValue)) {
      undableEditListener.accept(new ChangeValueEdit(oldValue, aValue, row, column));
    }
    super.setValueAt(aValue, row, column);
  }

  @Override
  protected void fireColumnsInserted(int index, GridSheetColumn[] columnsInserted) {
    undableEditListener.accept(new InserColumnsEdit(index, columnsInserted.length));
    super.fireColumnsInserted(index, columnsInserted);
  }

  @Override
  protected void fireColumnsDeleted(int index, GridSheetColumn[] columnsRemoved) {
    Object[][] data = new Object[columnsRemoved.length][];
    for (int i = 0; i < columnsRemoved.length; i++) {
      data[i] = getColumnDataAt(index + i);
    }
    undableEditListener.accept(new DeleteColumnsEdit(index, data));
    super.fireColumnsDeleted(index, columnsRemoved);
  }

  @Override
  protected void fireRowsInserted(int index, GridSheetRow[] rowsInserted) {
    undableEditListener.accept(new InserRowsEdit(index, rowsInserted.length));
    super.fireRowsInserted(index, rowsInserted);
  }

  @Override
  protected void fireRowsDeleted(int index, GridSheetRow[] rowsRemoved) {
    Object[][] data = new Object[rowsRemoved.length][];
    for (int i = 0; i < rowsRemoved.length; i++) {
      data[i] = getRowDataAt(index + i).toArray();
    }
    undableEditListener.accept(new DeleteRowsEdit(index, data));
    super.fireRowsDeleted(index, rowsRemoved);
  }

  @Override
  protected void deleteColumnData(int index, int numColumns) {
    int rowCount = getRowCount();
    for (int r = 0; r < rowCount; r++) {
      List rowData = getRowDataAt(r);
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
      List rowData = getRowDataAt(r);
      if (index <= rowData.size()) {
        Object[] elements = new Object[numColumns];
        Arrays.fill(elements, getDefaultValue());
        List<Object> newData = Arrays.asList(elements);
        rowData.addAll(index, newData);
      }
    }
    fireDataUpdated(0, index, GridSheetDataEvent.TO_THE_END, GridSheetDataEvent.TO_THE_END, true);
  }

  private Object[] getColumnDataAt(int columnIndex) {
    int rowCount = getRowCount();
    Object[] columnData = new Object[rowCount];
    for (int j = 0; j < rowCount; j++) {
      columnData[j] = getValueAt(j, columnIndex);
    }
    return columnData;
  }

  // for sort
  public void sort(List<SortCriteria> criterias) {
    SortResult sortResult = CsvSorter.sort(criterias, dataList, true);
    undableEditListener.accept(new SortEdit(criterias, sortResult.getOrder()));
    this.dataList = sortResult.getSortedData();
    fireStructureChanged(GridSheetStructureEvent.SORT_ROWS);
  }

  public void sort(List<SortCriteria> criterias, int[] targetRows) {
    List<List> targetDataList = new ArrayList<>();
    for (int i = 0; i < targetRows.length; i++) {
      int r = targetRows[i];
      targetDataList.add(dataList.get(r));
    }
    SortResult sortResult = CsvSorter.sort(criterias, targetDataList, true);
    undableEditListener.accept(new SpecifiedRowsSortEdit(criterias, sortResult.getOrder(),
        targetRows));
    for (int i = 0; i < targetRows.length; i++) {
      int r = targetRows[i];
      dataList.set(r, sortResult.getSortedData().get(i));
    }
    fireStructureChanged(GridSheetStructureEvent.SORT_ROWS);
  }

  public void sort(List<SortCriteria> criterias, CellRect targetCells) {
    setAdjusting(true);
    List<List> targetDataList = new ArrayList<>(targetCells.getNumRows());
    for (int r = targetCells.getRow(); r <= targetCells.getLastRow(); r++) {
      List rowData = new ArrayList(targetCells.getNumColumns());
      for (int c = targetCells.getColumn(); c <= targetCells.getLastColumn(); c++) {
        Object v = getValueAt(r, c);
        if (v == null) {
          throw new AppException("WSCA0005");
        }
        rowData.add(v);
      }
      targetDataList.add(rowData);
    }
    SortResult sortResult = CsvSorter.sort(criterias, targetDataList, false);
    undableEditListener.accept(new PartialSortEdit(criterias, sortResult.getOrder(), targetCells));
    for (int i = 0; i < targetCells.getNumRows(); i++) {
      List rowData = getRowDataAt(i + targetCells.getRow());
      List sortedRowData = sortResult.getSortedData().get(i);
      for (int c = targetCells.getColumn(); c <= targetCells.getLastColumn(); c++) {
        rowData.set(c, sortedRowData.get(c - targetCells.getColumn()));
      }
    }
    fireDataUpdated(targetCells.getRow(), targetCells.getColumn());
    fireDataUpdated(targetCells.getLastRow(), targetCells.getLastColumn());
    setAdjusting(false);
  }

  public List<List> getDataList(int rowFrom, int rowTo) {
    List<List> ret = new ArrayList<List>();
    for (int r = rowFrom; r < rowTo; r++) {
      ret.add(Collections.unmodifiableList(dataList.get(r)));
    }
    return Collections.unmodifiableList(ret);
  }

  private static final boolean equals(Object o0, Object o1) {
    if (ObjectUtils.isEmpty(o0)) {
      return ObjectUtils.isEmpty(o1);
    } else {
      if (ObjectUtils.isEmpty(o1)) {
        return false;
      } else {
        return o0.toString().equals(o1.toString());
      }
    }
  }
}
