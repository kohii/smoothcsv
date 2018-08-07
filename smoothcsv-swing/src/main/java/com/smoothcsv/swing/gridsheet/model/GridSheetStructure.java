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
package com.smoothcsv.swing.gridsheet.model;
///*
// * Copyright 2015 kohii
// * 
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
// * in compliance with the License. You may obtain a copy of the License at
// * 
// * http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software distributed under the License
// * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// * or implied. See the License for the specific language governing permissions and limitations under
// * the License.
// */
//package com.smoothcsv.swing.components.gridsheet.model;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Consumer;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import com.smoothcsv.commons.collections.ExArrayList;
//import com.smoothcsv.swing.components.gridsheet.event.GridSheetStructureEvent;
//
///**
// *
// * @author kohii
// */
//public class GridSheetStructure implements IGridSheetStructure {
//
//  private List<Consumer<GridSheetStructureEvent>> listenerList = new ArrayList<>();
//
//  private ExArrayList<GridSheetColumn> columns;
//  private ExArrayList<GridSheetRow> rows;
//
//  @Getter
//  private boolean adjusting;
//
//  @Getter
//  @Setter
//  private int minColumnWidth = 7;
//  @Getter
//  @Setter
//  private int maxColumnWidth = 1000;
//  @Getter
//  @Setter
//  private int minRowHeight = 1;
//  @Getter
//  @Setter
//  private int maxRowHeight = 300;
//  @Getter
//  @Setter
//  private int defaultColumnWidth = 40;
//  @Getter
//  @Setter
//  private int defaultRowHeight = 17;
//
//  private int totalColumnWidth = -1;
//  private int totalRowHeight = -1;
//
//  /**
//   * 
//   */
//  public GridSheetStructure() {
//    rows = new ExArrayList<>();
//    columns = new ExArrayList<>();
//  }
//
//
//  //
//  // Get structure data
//  //
//
//  public GridSheetColumn getColumn(int index) {
//    return columns.get(index);
//  }
//
//  public GridSheetRow getRow(int index) {
//    return rows.get(index);
//  }
//
//  public int getColumnCount() {
//    return columns.size();
//  }
//
//  public int getRowCount() {
//    return rows.size();
//  }
//
//
//  //
//  // Change structure
//  //
//
//  public void addColumn(GridSheetColumn column) {
//    insertColumn(getColumnCount(), column);
//  }
//
//  public void addColumn(List<GridSheetColumn> column) {
//    insertColumn(getColumnCount(), column);
//  }
//
//  public void insertColumn(int index, GridSheetColumn column) {
//    columns.add(index, column);
//    fireColumnsInserted(index, Arrays.asList(column));
//  }
//
//  public void insertColumn(int index, List<GridSheetColumn> column) {
//    columns.addAll(index, column);
//    fireColumnsInserted(index, column);
//  }
//
//  public void insertColumn(int index, int numColumns) {
//    GridSheetColumn[] column = new GridSheetColumn[numColumns];
//    for (int i = 0; i < column.length; i++) {
//      column[i] = new GridSheetColumn(getDefaultColumnWidth(), this);
//    }
//    insertColumn(index, Arrays.asList(column));
//  }
//
//  public GridSheetColumn removeColumn(int index) {
//    GridSheetColumn column = columns.remove(index);
//    fireColumnsRemoved(index, columns);
//    return column;
//  }
//
//  public List<GridSheetColumn> removeColumn(int index, int numColumns) {
//    GridSheetColumn[] arrayColumnsRemoved = new GridSheetColumn[numColumns];
//    for (int i = 0; i < arrayColumnsRemoved.length; i++) {
//      arrayColumnsRemoved[i] = columns.get(index + i);
//    }
//    List<GridSheetColumn> columnsRemoved = Arrays.asList(arrayColumnsRemoved);
//    columns.removeRange(index, index + numColumns - 1);
//    fireColumnsRemoved(index, columnsRemoved);
//    return columnsRemoved;
//  }
//
//  public void addRow(GridSheetRow row) {
//    insertRow(getRowCount(), row);
//  }
//
//  public void addRow(List<GridSheetRow> row) {
//    insertRow(getRowCount(), row);
//  }
//
//  public void insertRow(int index, GridSheetRow row) {
//    rows.add(index, row);
//    fireRowsInserted(index, Arrays.asList(row));
//  }
//
//  public void insertRow(int index, List<GridSheetRow> row) {
//    rows.addAll(index, row);
//    fireRowsInserted(index, row);
//  }
//
//  public void insertRow(int index, int numRows) {
//    GridSheetRow[] row = new GridSheetRow[numRows];
//    for (int i = 0; i < row.length; i++) {
//      row[i] = new GridSheetRow(getDefaultRowHeight(), this);
//    }
//    insertRow(index, Arrays.asList(row));
//  }
//
//  public GridSheetRow removeRow(int index) {
//    GridSheetRow row = rows.remove(index);
//    fireRowsRemoved(index, rows);
//    return row;
//  }
//
//  public List<GridSheetRow> removeRow(int index, int numRows) {
//    GridSheetRow[] arrayRowsRemoved = new GridSheetRow[numRows];
//    for (int i = 0; i < arrayRowsRemoved.length; i++) {
//      arrayRowsRemoved[i] = rows.get(index + i);
//    }
//    List<GridSheetRow> rowsRemoved = Arrays.asList(arrayRowsRemoved);
//    rows.removeRange(index, index + numRows - 1);
//    fireRowsRemoved(index, rowsRemoved);
//    return rowsRemoved;
//  }
//
//  // Events -------------------------
//
//  public void addListener(Consumer<GridSheetStructureEvent> l) {
//    listenerList.add(l);
//  }
//
//  public void removeListener(Consumer<GridSheetStructureEvent> l) {
//    listenerList.remove(l);
//  }
//
//
//  protected void fireColumnsInserted(int index, List<GridSheetColumn> columnsInserted) {
//    invalidateWidthCache();
//    if (!adjusting) {
//      if (!listenerList.isEmpty()) {
//        GridSheetStructureEvent e =
//            new GridSheetStructureEvent(this, GridSheetStructureEvent.INSERT_COLUMN, adjusting,
//                index, columnsInserted.size(), getRowCount(), getColumnCount());
//        for (Consumer<GridSheetStructureEvent> l : listenerList) {
//          l.accept(e);
//        }
//      }
//    }
//  }
//
//  protected void fireColumnsRemoved(int index, List<GridSheetColumn> columnsRemoved) {
//    invalidateWidthCache();
//    if (!adjusting) {
//      if (!listenerList.isEmpty()) {
//        GridSheetStructureEvent e =
//            new GridSheetStructureEvent(this, GridSheetStructureEvent.REMOVE_COLUMN, adjusting,
//                index, columnsRemoved.size(), getRowCount(), getColumnCount());
//        for (Consumer<GridSheetStructureEvent> l : listenerList) {
//          l.accept(e);
//        }
//      }
//    }
//  }
//
//  protected void fireRowsInserted(int index, List<GridSheetRow> rowsInserted) {
//    invalidateHeightCache();
//    if (!adjusting) {
//      if (!listenerList.isEmpty()) {
//        GridSheetStructureEvent e =
//            new GridSheetStructureEvent(this, GridSheetStructureEvent.INSERT_ROW, adjusting, index,
//                rowsInserted.size(), getRowCount(), getColumnCount());
//        for (Consumer<GridSheetStructureEvent> l : listenerList) {
//          l.accept(e);
//        }
//      }
//    }
//  }
//
//  protected void fireRowsRemoved(int index, List<GridSheetRow> rowsRemoved) {
//    invalidateHeightCache();
//    if (!listenerList.isEmpty()) {
//      GridSheetStructureEvent e =
//          new GridSheetStructureEvent(this, GridSheetStructureEvent.REMOVE_ROW, adjusting, index,
//              rowsRemoved.size(), getRowCount(), getColumnCount());
//      for (Consumer<GridSheetStructureEvent> l : listenerList) {
//        l.accept(e);
//      }
//    }
//  }
//
//  public void fireVisibleColumnsUpdated() {
//    fireWidthUpdated();
//    invokeListeners(GridSheetStructureEvent.UPDATE_VISIBLE_COLUMNS);
//  }
//
//  public void fireVisibleRowsUpdated() {
//    fireHeightUpdated();
//    invokeListeners(GridSheetStructureEvent.UPDATE_VISIBLE_ROWS);
//  }
//
//  public void fireWidthUpdated() {
//    invalidateWidthCache();
//    invokeListeners(GridSheetStructureEvent.UPDATE_WIDTH);
//
//  }
//
//  public void fireHeightUpdated() {
//    invalidateHeightCache();
//    invokeListeners(GridSheetStructureEvent.UPDATE_HEIGHT);
//  }
//
//  private void invokeListeners(int type) {
//    if (!listenerList.isEmpty()) {
//      GridSheetStructureEvent e = new GridSheetStructureEvent(this, type, adjusting);
//      for (Consumer<GridSheetStructureEvent> l : listenerList) {
//        l.accept(e);
//      }
//    }
//  }
//
//  // Width and Heidht ---------------------------
//
//  public int getTotalColumnWidth() {
//    if (totalColumnWidth == -1) {
//      totalColumnWidth = 0;
//      for (int i = 0; i < columns.size(); i++) {
//        totalColumnWidth += columns.get(i).getWidth();
//      }
//    }
//    return totalColumnWidth;
//  }
//
//  protected void invalidateWidthCache() {
//    totalColumnWidth = -1;
//  }
//
//
//  public int getTotalRowHeight() {
//    if (totalRowHeight == -1) {
//      totalRowHeight = 0;
//      for (int i = 0; i < rows.size(); i++) {
//        totalRowHeight += rows.get(i).getHeight();
//      }
//    }
//    return totalRowHeight;
//  }
//
//  protected void invalidateHeightCache() {
//    totalRowHeight = -1;
//  }
//
//  // Name ------------------------
//  public String getColumnName(int column) {
//    return String.valueOf(column + 1);
//  }
//
//  public String getRowName(int row) {
//    return String.valueOf(row + 1);
//  }
//}
