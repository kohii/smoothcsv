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
package com.smoothcsv.swing.gridsheet;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.LayerUI;

import com.smoothcsv.swing.gridsheet.event.GridSheetDataEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetStructureEvent;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetRow;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.model.IGridSheetModel;
import com.smoothcsv.swing.gridsheet.model.IGridSheetStructure;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetColorProvider;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetColorProvider;
import lombok.Getter;

public class GridSheetPane extends JPanel implements IGridSheetStructure {

  private static final long serialVersionUID = -1931802727701510494L;

  /* Model --------------------------------------- */

  /**
   * The <code>GridGridModel</code> that provides the data displayed by this
   * <code>GridSheetPane</code>.
   */
  @Getter
  protected IGridSheetModel model;

  private int[] columnModelIndices;
  private int[] rowModelIndices;

  /**
   * The <code>GridSheetSelectionModel</code> of the table.
   */
  @Getter
  protected GridSheetSelectionModel selectionModel;

  /**
   * Color provider..
   */
  @Getter
  protected GridSheetColorProvider colorProvider;

  /* Children --------------------------------------- */

  /**
   * The JScrollPane working with the grid sheet.
   */
  @Getter
  private final GridSheetScrollPane scrollPane;

  /**
   * The main table working with the grid sheet.
   */
  @Getter
  private final GridSheetTable table;

  /**
   * The <code>GridSheetColumnHeader</code> working with the grid sheet.
   */
  @Getter
  protected GridSheetColumnHeader columnHeader;

  /**
   * The <code>GridSheetRowHeader</code> working with the grid sheet.
   */
  @Getter
  protected GridSheetRowHeader rowHeader;

  /**
   * The <code>GridSheetCornerHeader</code> working with the grid sheet.
   */
  @Getter
  protected GridSheetCornerHeader cornerHeader;

  public GridSheetPane(GridSheetModel gm) {
    super(new BorderLayout(0, 0));
    setBorder(null);
    setFocusable(false);

    this.colorProvider = createColorProvider();

    this.table = createTable();
    this.columnHeader = createColumnHeader();
    this.rowHeader = createRowHeader();
    this.cornerHeader = createCornerHeader();

    setModel(gm);

    GridSheetSelectionModel sm = createSelectionModel();
    setSelectionModel(sm);

    LayerUI<GridSheetTable> layerUI = new GridSheetLayerUI(this);
    JLayer<GridSheetTable> jlayer = new JLayer<GridSheetTable>(table, layerUI);

    scrollPane = createScrollPane();
    scrollPane.setViewportView(jlayer);
    scrollPane.setColumnHeaderView(columnHeader);
    scrollPane.setRowHeaderView(rowHeader);
    scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerHeader);

    add(scrollPane);
  }

  public void setColorProvider(GridSheetColorProvider colorProvider) {
    if (colorProvider == null) {
      throw new IllegalArgumentException("New colorProvider is null");
    }
    this.colorProvider = colorProvider;
    repaint();
  }

  protected GridSheetColorProvider createColorProvider() {
    return new DefaultGridSheetColorProvider();
  }

  //
  // Initialize GridSheetPane
  //

  protected GridSheetTable createTable() {
    return new GridSheetTable(this);
  }

  protected GridSheetColumnHeader createColumnHeader() {
    return new GridSheetColumnHeader(this);
  }

  protected GridSheetRowHeader createRowHeader() {
    return new GridSheetRowHeader(this);
  }

  protected GridSheetCornerHeader createCornerHeader() {
    return new GridSheetCornerHeader(this);
  }

  protected GridSheetScrollPane createScrollPane() {
    return new GridSheetScrollPane(this);
  }

  /**
   * Returns the default selection model object, which is a <code>DefaultGridSelectionModel</code>.
   * A subclass can override this method to return a different selection model object.
   *
   * @return the default selection model object
   * @see javax.swing.DefaultGridSelectionModel
   */
  protected GridSheetSelectionModel createSelectionModel() {
    return new DefaultGridSheetSelectionModel(this);
  }

  //
  // Selection methods
  //

  /**
   * Selects all rows, columns, and cells in the table.
   */
  public void selectAll(boolean resetAnchor) {
    // If I'm currently editing, then I should stop editing
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }

    selectionModel.setValueIsAdjusting(true);
    selectionModel.setRowHeaderSelected(true);
    selectionModel.setColumnHeaderSelected(true);
    if (resetAnchor) {
      selectionModel.setSelectionInterval(0, 0, getRowCount() - 1, getColumnCount() - 1);
    } else {
      selectionModel.setSelectionIntervalNoChangeAnchor(0, 0, getRowCount() - 1,
          getColumnCount() - 1);
    }
    selectionModel.setValueIsAdjusting(false);
  }

  /**
   * Selects all columns, and cells in the table.
   */
  public void selectEntireColumn() {
    // If I'm currently editing, then I should stop editing
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }
    selectionModel.setValueIsAdjusting(true);
    selectionModel.selectEntireColumn();
    selectionModel.setColumnHeaderSelected(true);
    selectionModel.setRowHeaderSelected(false);
    selectionModel.setValueIsAdjusting(false);
  }

  /**
   * Selects all rows, and cells in the table.
   */
  public void selectEntireRow() {
    // If I'm currently editing, then I should stop editing
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }
    selectionModel.setValueIsAdjusting(true);
    selectionModel.selectEntireRow();
    selectionModel.setRowHeaderSelected(true);
    selectionModel.setColumnHeaderSelected(false);
    selectionModel.setValueIsAdjusting(false);
  }

  // /**
  // * Returns the indices of all selected rows.
  // *
  // * @return an array of integers containing the indices of all selected
  // rows,
  // * or an empty array if no row is selected
  // * @see #getSelectedRow
  // */
  // public int[] getSelectedRows() {
  // selectionModel.getSelectedRows();
  // }
  //
  // /**
  // * Returns the indices of all selected columns.
  // *
  // * @return an array of integers containing the indices of all selected
  // * columns, or an empty array if no column is selected
  // * @see #getSelectedColumn
  // */
  // public int[] getSelectedColumns() {
  // return columnModel.getSelectedColumns();
  // }
  // /**
  // * Returns the number of selected rows.
  // *
  // * @return the number of selected rows, 0 if no rows are selected
  // */
  // public int getSelectedRowCount() {
  // return selectionModel.getSelectedRowCount();
  // }
  //
  // /**
  // * Returns the number of selected columns.
  // *
  // * @return the number of selected columns, 0 if no columns are selected
  // */
  // public int getSelectedColumnCount() {
  // return selectionModel.getSelectedColumnCount();
  // }

  /**
   * Returns true if the specified index is in the valid range of rows, and the row at that index is
   * selected.
   *
   * @return true if <code>row</code> is a valid index and the row at that index is selected (where
   * 0 is the first row)
   */
  public boolean isRowSelected(int row) {
    return selectionModel.isRowSelected(row);
  }

  /**
   * Returns true if the specified index is in the valid range of columns, and the column at that
   * index is selected.
   *
   * @param column the column in the column model
   * @return true if <code>column</code> is a valid index and the column at that index is selected
   * (where 0 is the first column)
   */
  public boolean isColumnSelected(int column) {
    return selectionModel.isColumnSelected(column);
  }

  /**
   * Returns true if the specified indices are in the valid range of rows and columns and the cell
   * at the specified position is selected.
   *
   * @param row    the row being queried
   * @param column the column being queried
   * @return true if <code>row</code> and <code>column</code> are valid indices and the cell at
   * index <code>(row, column)</code> is selected, where the first row and first column are
   * at index 0
   */
  public boolean isCellSelected(int row, int column) {
    return selectionModel.isCellSelected(row, column);
  }

  //
  // Managing models
  //

  /**
   * Sets the data model for this table to <code>newModel</code> and registers with it for listener
   * notifications from the new data model.
   *
   * @param dataModel the new data source for this table
   * @throws IllegalArgumentException if <code>newModel</code> is <code>null</code>
   * @beaninfo bound: true description: The model that is the source of the data for this view.
   * @see #getStructure
   */
  public void setModel(IGridSheetModel dataModel) {
    if (dataModel == null) {
      throw new IllegalArgumentException("Cannot set a null GridModel");
    }
    if (this.model != dataModel) {
      IGridSheetModel old = this.model;
      if (old != null) {
        old.removeValueChangeListener(this::cellValueChanged);
        old.removeStructureChangeListener(this::structureChanged);
      }
      this.model = dataModel;
      dataModel.addValueChangeListener(this::cellValueChanged);
      dataModel.addStructureChangeListener(this::structureChanged);

      // tableChanged(new GridSheetModelEvent(dataModel, GridSheetModelEvent.HEADER_ROW));

      firePropertyChange("model", old, dataModel);
    }
  }

  public void setSelectionModel(GridSheetSelectionModel newModel) {
    if (newModel == null) {
      throw new IllegalArgumentException("Cannot set a null SelectionModel");
    }

    GridSheetSelectionModel oldModel = selectionModel;

    if (newModel != oldModel) {
      if (oldModel != null) {
        oldModel.removeGridSelectionListener(table);
        oldModel.removeColumnHeaderSelectionListener(columnHeader);
        oldModel.removeRowHeaderSelectionListener(rowHeader);
        oldModel.removeCornerHeaderSelectionListener(cornerHeader);
      }

      selectionModel = newModel;
      newModel.addGridSelectionListener(table);
      newModel.addColumnHeaderSelectionListener(columnHeader);
      newModel.addRowHeaderSelectionListener(rowHeader);
      newModel.addCornerHeaderSelectionListener(cornerHeader);

      firePropertyChange("selectionModel", oldModel, newModel);
      repaint();
    }
  }

  /**
   * Returns the cell value at <code>row</code> and <code>column</code>.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display order, and not in the
   * <code>GridSheetModel</code>'s column order. This is an important distinction because as the
   * user rearranges the columns in the table, the column at a given index in the view will change.
   * Meanwhile the user's actions never affect the model's column ordering.
   *
   * @param row    the row whose value is to be queried
   * @param column the column whose value is to be queried
   * @return the Object at the specified cell
   */
  public Object getValueAt(int row, int column) {
    return model.getValueAt(convertRowIndexToModel(row), convertColumnIndexToModel(column));
  }

  /**
   * Sets the value for the cell in the table model at <code>row</code> and <code>column</code>.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display order, and not in the
   * <code>GridSheetModel</code>'s column order. This is an important distinction because as the
   * user rearranges the columns in the table, the column at a given index in the view will change.
   * Meanwhile the user's actions never affect the model's column ordering.
   * <p>
   * <code>aValue</code> is the new value.
   *
   * @param aValue the new value
   * @param row    the row of the cell to be changed
   * @param column the column of the cell to be changed
   * @see #getValueAt
   */
  public void setValueAt(Object aValue, int row, int column) {
    model.setValueAt(aValue, convertRowIndexToModel(row), convertColumnIndexToModel(column));
  }

  //
  // Cover methods for various models and helper methods
  //

  /**
   * Returns the index of the column that <code>point</code> lies in, or -1 if the result is not in
   * the range [0, <code>getColumnCount()</code>-1].
   *
   * @param point the location of interest
   * @return the index of the column that <code>point</code> lies in, or -1 if the result is not in
   * the range [0, <code>getColumnCount()</code> -1]
   * @see #rowAtPoint
   */
  public int columnAtPoint(Point point) {
    return columnAtPoint(point.x);
  }

  /**
   * Returns the index of the column that <code>point</code> lies in, or -1 if the result is not in
   * the range [0, <code>getColumnCount()</code>-1].
   *
   * @param point the location of interest
   * @return the index of the column that <code>point</code> lies in, or -1 if the result is not in
   * the range [0, <code>getColumnCount()</code> -1]
   * @see #rowAtPoint
   */
  public int columnAtPoint(int point) {
    if (point < 0) {
      return -1;
    }
    int cc = getColumnCount();
    for (int column = 0; column < cc; column++) {
      point = point - getColumn(column).getWidth();
      if (point < 0) {
        return column;
      }
    }
    return -1;
  }

  public int columnAtPoint(Point point, int min, int max) {
    int x = point.x;
    if (x < min) {
      return min;
    }
    int cc = getColumnCount();
    for (int column = 0; column < cc; column++) {
      x = x - getColumn(column).getWidth();
      if (x < min) {
        return column;
      }
    }
    return max;
  }

  /**
   * Returns the index of the row that <code>point</code> lies in, or -1 if the result is not in the
   * range [0, <code>getRowCount()</code>-1].
   *
   * @param point the location of interest
   * @return the index of the row that <code>point</code> lies in, or -1 if the result is not in the
   * range [0, <code>getRowCount()</code>-1]
   * @see #columnAtPoint
   */
  public int rowAtPoint(Point point) {
    return rowAtPoint(point.y);
  }

  /**
   * Returns the index of the row that <code>point</code> lies in, or -1 if the result is not in the
   * range [0, <code>getRowCount()</code>-1].
   *
   * @param point the location of interest
   * @return the index of the row that <code>point</code> lies in, or -1 if the result is not in the
   * range [0, <code>getRowCount()</code>-1]
   * @see #columnAtPoint
   */
  public int rowAtPoint(int point) {
    if (point < 0) {
      return -1;
    }
    int rc = getRowCount();
    for (int row = 0; row < rc; row++) {
      point = point - getRow(row).getHeight();
      if (point < 0) {
        return row;
      }
    }
    return -1;
  }

  public int rowAtPoint(Point point, int min, int max) {
    int y = point.y;
    if (y < min) {
      return min;
    }
    int rc = getRowCount();
    for (int row = 0; row < rc; row++) {
      y = y - getRow(row).getHeight();
      if (y < min) {
        return row;
      }
    }
    return max;
  }

  public int viewIndexForColumn(GridSheetColumn aColumn) {
    for (int column = 0; column < getColumnCount(); column++) {
      if (getColumn(column) == aColumn) {
        return column;
      }
    }
    return -1;
  }

  //
  // GridSheet status support
  //

  /**
   * Returns true if a cell is being edited.
   *
   * @return true if the table is editing a cell
   */
  public boolean isEditing() {
    return table.isEditing();
  }

  //
  // Implementing GridSheetModelListener interface
  //

  /**
   * Invoked when this table's <code>GridSheetModel</code> generates a
   * <code>GridSheetModelEvent</code>. The <code>GridSheetModelEvent</code> should be constructed in
   * the coordinate system of the model; the appropriate mapping to the view coordinate system is
   * performed by this <code>JTable</code> when it receives the event.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by
   * <code>JTable</code>.
   * <p>
   * Note that as of 1.3, this method clears the selection, if any.
   */
  public void cellValueChanged(GridSheetDataEvent e) {
    if (e.isStructureChanged()) {
      // if structure was changed, #structureChanged() will repaint ui.
      return;
    }

    if (e == null || e.getFirstRow() == GridSheetDataEvent.HEADER_INDEX) {
      // The whole thing changed
      // selectionModel.clearSelection();
      table.resizeAndRepaint();
      columnHeader.resizeAndRepaint();
      return;
    }

    Rectangle dirtyRegion;
    if (e.getFirstRow() == GridSheetDataEvent.ALL_CELLS) {
      // 1 or more rows changed
      // dirtyRegion = new Rectangle(0, start * getRowHeight(), model.getTotalColumnWidth(), 0);

      table.repaint();
      return;
    } else {
      // A cell or column of cells has changed.
      // Unlike the rest of the methods in the JTable, the
      // GridSheetModelEvent
      // uses the coordinate system of the model instead of the view.
      // This is the only place in the JTable where this "reverse mapping"
      // is used.
      dirtyRegion = table.getCellRect(e.getFirstRow(), e.getFirstColumn(), true);
    }

    if (e.getLastRow() != GridSheetDataEvent.TO_THE_END) {
      dirtyRegion = dirtyRegion.union(table.getCellRect(e.getLastRow(), e.getLastColumn(), true));
      table.repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
    } else {
      Rectangle visibleRect = table.getVisibleRect();
      dirtyRegion.width = visibleRect.x + visibleRect.width - dirtyRegion.x;
      dirtyRegion.height = visibleRect.y + visibleRect.height - dirtyRegion.y;
      table.repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
      // selectionModel.clearSelection();
      // table.resizeAndRepaint();
    }
  }

  public void structureChanged(GridSheetStructureEvent e) {
    table.resizeAndRepaint();

    switch (e.getType()) {
      case GridSheetStructureEvent.INSERT_COLUMN:
      case GridSheetStructureEvent.REMOVE_COLUMN:
      case GridSheetStructureEvent.UPDATE_WIDTH:
      case GridSheetStructureEvent.UPDATE_VISIBLE_COLUMNS:
        columnHeader.resizeAndRepaint();
        break;
      case GridSheetStructureEvent.INSERT_ROW:
      case GridSheetStructureEvent.REMOVE_ROW:
      case GridSheetStructureEvent.UPDATE_HEIGHT:
      case GridSheetStructureEvent.UPDATE_VISIBLE_ROWS:
      case GridSheetStructureEvent.SORT_ROWS:
        rowHeader.resizeAndRepaint();
        break;
      case GridSheetStructureEvent.CHANGE_DATALIST:
        rowHeader.resizeAndRepaint();
        columnHeader.resizeAndRepaint();
        // revalidate();
        // repaint();
        break;
    }

    if (e.getType() == GridSheetStructureEvent.REMOVE_ROW
        || e.getType() == GridSheetStructureEvent.REMOVE_COLUMN
        || e.getType() == GridSheetStructureEvent.SORT_ROWS) {
      repaint();
    }
  }

  // /*
  // * Invoked when rows have been inserted into the table. <p> Application code will not use these
  // * methods explicitly, they are used internally by JTable.
  // *
  // * @param e the GridSheetModelEvent encapsulating the insertion
  // */
  // private void tableRowsInserted(GridSheetModelEvent e) {
  // int start = e.getFirstRow();
  // int end = e.getLastRow();
  // if (start < 0) {
  // start = 0;
  // }
  // if (end < 0) {
  // end = getRowCount() - 1;
  // }
  //
  // // Adjust the selection to account for the new rows.
  // int length = end - start + 1;
  // // selectionModel.insertIndexInterval(start, length, true);
  //
  // int rh = getRowHeight();
  // Rectangle drawRect =
  // new Rectangle(0, start * rh, model.getTotalColumnWidth(), (getRowCount() - start) * rh);
  //
  // revalidate();
  // // PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
  // // repaint still required in the unusual case where there is no
  // // ScrollPane
  // repaint(drawRect);
  // }
  //
  // /*
  // * Invoked when rows have been removed from the table. <p> Application code will not use these
  // * methods explicitly, they are used internally by JTable.
  // *
  // * @param e the GridSheetModelEvent encapsulating the deletion
  // */
  // private void tableRowsDeleted(GridSheetModelEvent e) {
  // int start = e.getFirstRow();
  // int end = e.getLastRow();
  // if (start < 0) {
  // start = 0;
  // }
  // if (end < 0) {
  // end = getRowCount() - 1;
  // }
  //
  // int deletedCount = end - start + 1;
  // int previousRowCount = getRowCount() + deletedCount;
  // // Adjust the selection to account for the new rows
  // selectionModel.removeRowIndexInterval(start, end, getRowCount());
  //
  // int rh = getRowHeight();
  // Rectangle drawRect =
  // new Rectangle(0, start * rh, model.getTotalColumnWidth(), (previousRowCount - start) * rh);
  //
  // revalidate();
  // // PENDING(milne) revalidate calls repaint() if parent is a ScrollPane
  // // repaint still required in the unusual case where there is no
  // // ScrollPane
  // repaint(drawRect);
  // }
  //
  // //
  // // Implementing GridColumnModelListener interface
  // //
  // /**
  // * Invoked when a column is added to the table column model.
  // * <p>
  // * Application code will not use these methods explicitly, they are used internally by JTable.
  // *
  // * @see GridColumnModelListener
  // */
  // @Override
  // public void columnAdded(GridSheetColumnModelEvent e) {
  // // If I'm currently editing, then I should stop editing
  // if (isEditing()) {
  // table.removeEditor();
  // }
  // table.resizeAndRepaint();
  // }
  //
  // /**
  // * Invoked when a column is removed from the table column model.
  // * <p>
  // * Application code will not use these methods explicitly, they are used internally by JTable.
  // *
  // * @see GridColumnModelListener
  // */
  // @Override
  // public void columnRemoved(GridSheetColumnModelEvent e) {
  // // If I'm currently editing, then I should stop editing
  // if (isEditing()) {
  // table.removeEditor();
  // }
  // table.resizeAndRepaint();
  // }
  //
  // /**
  // * Invoked when the selection model of the <code>GridColumnModel</code> is
  // * changed.
  // * <p>
  // * Application code will not use these methods explicitly, they are used
  // * internally by JTable.
  // *
  // * @param e
  // * the event received
  // * @see GridColumnModelListener
  // */
  // public void columnSelectionChanged(ListSelectionEvent e) {
  // boolean isAdjusting = e.getValueIsAdjusting();
  // if (columnSelectionAdjusting && !isAdjusting) {
  // // The assumption is that when the model is no longer adjusting
  // // we will have already gotten all the changes, and therefore
  // // don't need to do an additional paint.
  // columnSelectionAdjusting = false;
  // return;
  // }
  // columnSelectionAdjusting = isAdjusting;
  // // The getCellRect() call will fail unless there is at least one row.
  // if (getRowCount() <= 0 || getColumnCount() <= 0) {
  // return;
  // }
  // int firstIndex = limit(e.getFirstIndex(), 0, getColumnCount() - 1);
  // int lastIndex = limit(e.getLastIndex(), 0, getColumnCount() - 1);
  // int minRow = 0;
  // int maxRow = getRowCount() - 1;
  //
  // minRow = selectionModel.getMinRowSelectionIndex();
  // maxRow = selectionModel.getMaxRowSelectionIndex();
  // int leadRow = getAdjustedIndex(selectionModel.getLeadSelectionIndex(),
  // true);
  //
  // if (minRow == -1 || maxRow == -1) {
  // if (leadRow == -1) {
  // // nothing to repaint, return
  // return;
  // }
  //
  // // only thing to repaint is the lead
  // minRow = maxRow = leadRow;
  // } else {
  // // We need to consider more than just the range between
  // // the min and max selected index. The lead row, which could
  // // be outside this range, should be considered also.
  // if (leadRow != -1) {
  // minRow = Math.min(minRow, leadRow);
  // maxRow = Math.max(maxRow, leadRow);
  // }
  // }
  //
  // Rectangle firstColumnRect = getCellRect(minRow, firstIndex, false);
  // Rectangle lastColumnRect = getCellRect(maxRow, lastIndex, false);
  // Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
  // repaint(dirtyRegion);
  // }

  //
  // Informally implement the GridSheetModel interface.
  //

  /**
   * Maps the index of the column in the view at <code>viewColumnIndex</code> to the index of the
   * column in the table model. Returns the index of the corresponding column in the model. If
   * <code>viewColumnIndex</code> is less than zero, returns <code>viewColumnIndex</code>.
   *
   * @param viewColumnIndex the index of the column in the view
   * @return the index of the corresponding column in the model
   * @see #convertColumnIndexToView
   */
  public int convertColumnIndexToModel(int viewColumnIndex) {
    return columnModelIndices == null ? viewColumnIndex : columnModelIndices[viewColumnIndex];
  }

  // /**
  // * Maps the index of the column in the table model at <code>modelColumnIndex</code> to the index
  // * of the column in the view. Returns the index of the corresponding column in the view; returns
  // * -1 if this column is not being displayed. If <code>modelColumnIndex</code> is less than zero,
  // * returns <code>modelColumnIndex</code>.
  // *
  // * @param modelColumnIndex the index of the column in the model
  // * @return the index of the corresponding column in the view
  // *
  // * @see #convertColumnIndexToModel
  // */
  // public int convertColumnIndexToView(int modelColumnIndex) {
  // return modelColumnIndex;
  // }

  // /**
  // * Maps the index of the row in terms of the <code>GridSheetModel</code> to the view. If the
  // * contents of the model are not sorted the model and view indices are the same.
  // *
  // * @param modelRowIndex the index of the row in terms of the model
  // * @return the index of the corresponding row in the view, or -1 if the row isn't visible
  // * @throws IndexOutOfBoundsException if sorting is enabled and passed an index outside the
  // number
  // * of rows of the <code>GridSheetModel</code>
  // * @see javax.swing.table.TableRowSorter
  // * @since 1.6
  // */
  // public int convertRowIndexToView(int modelRowIndex) {
  // return modelRowIndex;
  // }

  /**
   * Maps the index of the row in terms of the view to the underlying <code>GridSheetModel</code>.
   * If the contents of the model are not sorted the model and view indices are the same.
   *
   * @param viewRowIndex the index of the row in the view
   * @return the index of the corresponding row in the model
   * @throws IndexOutOfBoundsException if sorting is enabled and passed an index outside the range
   *                                   of the <code>JTable</code> as determined by the method <code>getRowCount</code>
   * @see javax.swing.table.TableRowSorter
   * @see #getRowCount
   * @since 1.6
   */
  public int convertRowIndexToModel(int viewRowIndex) {
    return rowModelIndices == null ? viewRowIndex : rowModelIndices[viewRowIndex];
  }

  //
  // Structures
  //

  public IGridSheetStructure getStructure() {
    return model;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getColumn(int)
   */
  @Override
  public GridSheetColumn getColumn(int index) {
    return getStructure().getColumn(convertColumnIndexToModel(index));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getRow(int)
   */
  @Override
  public GridSheetRow getRow(int index) {
    return getStructure().getRow(convertRowIndexToModel(index));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return columnModelIndices == null ? getStructure().getColumnCount() : columnModelIndices.length;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getRowCount()
   */
  @Override
  public int getRowCount() {
    return rowModelIndices == null ? getStructure().getRowCount() : rowModelIndices.length;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#addColumn(com.smoothcsv.
   * swing.components.gridsheet.model.GridSheetColumn)
   */
  @Override
  public void addColumn(GridSheetColumn column) {
    getStructure().addColumn(column);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#addColumn(java.util.List)
   */
  @Override
  public void addColumn(GridSheetColumn[] column) {
    getStructure().addColumn(column);
  }


  @Override
  public void addColumn(int numColumns) {
    getStructure().addColumn(numColumns);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertColumn(int,
   * com.smoothcsv.swing.components.gridsheet.model.GridSheetColumn)
   */
  @Override
  public void insertColumn(int index, GridSheetColumn column) {
    getStructure().insertColumn(convertColumnIndexToModel(index), column);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertColumn(int,
   * java.util.List)
   */
  @Override
  public void insertColumn(int index, GridSheetColumn[] column) {
    getStructure().insertColumn(convertColumnIndexToModel(index), column);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertColumn(int, int)
   */
  @Override
  public void insertColumn(int index, int numColumns) {
    getStructure().insertColumn(convertColumnIndexToModel(index), numColumns);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#removeColumn(int)
   */
  @Override
  public GridSheetColumn deleteColumn(int index) {
    return getStructure().deleteColumn(convertColumnIndexToModel(index));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#addRow(com.smoothcsv.
   * swing.components.gridsheet.model.GridSheetRow)
   */
  @Override
  public void addRow(GridSheetRow row) {
    getStructure().addRow(row);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#addRow(java.util.List)
   */
  @Override
  public void addRow(GridSheetRow[] row) {
    getStructure().addRow(row);
  }

  @Override
  public void addRow(int numRows) {
    getStructure().addRow(numRows);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertRow(int,
   * com.smoothcsv.swing.components.gridsheet.model.GridSheetRow)
   */
  @Override
  public void insertRow(int index, GridSheetRow row) {
    getStructure().insertRow(convertRowIndexToModel(index), row);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertRow(int,
   * java.util.List)
   */
  @Override
  public void insertRow(int index, GridSheetRow[] row) {
    getStructure().insertRow(convertRowIndexToModel(index), row);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#insertRow(int, int)
   */
  @Override
  public void insertRow(int index, int numRows) {
    getStructure().insertRow(convertRowIndexToModel(index), numRows);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#removeRow(int)
   */
  @Override
  public GridSheetRow deleteRow(int index) {
    return getStructure().deleteRow(convertRowIndexToModel(index));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getColumnName(int)
   */
  @Override
  public String getColumnName(int column) {
    return model.getColumnName(convertColumnIndexToModel(column));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getRowName(int)
   */
  @Override
  public String getRowName(int row) {
    return model.getRowName(convertRowIndexToModel(row));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getTotalColumnWidth()
   */
  @Override
  public int getTotalColumnWidth() {
    return model == null ? 0 : model.getTotalColumnWidth();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.components.gridsheet.model.IGridSheetStructure#getTotalRowHeight()
   */
  @Override
  public int getTotalRowHeight() {
    return model == null ? 0 : model.getTotalRowHeight();
  }

  public boolean areAllColumnsVisivle() {
    return this.columnModelIndices == null;
  }

  public boolean areAllRowsVisivle() {
    return this.rowModelIndices == null;
  }


  protected void updateColumnModelIndices() {
    IGridSheetStructure structure = getStructure();
    int columnCount = structure.getColumnCount();
    int[] columnModelIndices = new int[columnCount];
    int visibleColumnCount = 0;
    for (int i = 0, cIndex = 0; i < columnCount; i++) {
      columnModelIndices[cIndex] = i;
      if (structure.getColumn(i).isVisible()) {
        cIndex++;
        visibleColumnCount++;
      }
    }
    if (visibleColumnCount == columnCount) {
      this.columnModelIndices = null;
    } else {
      this.columnModelIndices = Arrays.copyOf(columnModelIndices, visibleColumnCount);
    }
  }

  protected void updateRowModelIndices() {
    IGridSheetStructure structure = getStructure();
    int rowCount = structure.getRowCount();
    int[] rowModelIndices = new int[rowCount];
    int visibleRowCount = 0;
    for (int i = 0, rIndex = 0; i < rowCount; i++) {
      rowModelIndices[rIndex] = i;
      if (structure.getRow(i).isVisible()) {
        rIndex++;
        visibleRowCount++;
      }
    }
    if (visibleRowCount == rowCount) {
      this.rowModelIndices = null;
    } else {
      this.rowModelIndices = Arrays.copyOf(rowModelIndices, visibleRowCount);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getDefaultRowHeight()
   */
  @Override
  public int getDefaultRowHeight() {
    return model.getDefaultRowHeight();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#setDefaultRowHeight(int)
   */
  @Override
  public void setDefaultRowHeight(int defaultRowHeight) {
    model.setDefaultRowHeight(defaultRowHeight);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getDefaultColumnWidth()
   */
  @Override
  public int getDefaultColumnWidth() {
    return model.getDefaultColumnWidth();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#setDefaultColumnWidth(int)
   */
  @Override
  public void setDefaultColumnWidth(int defaultColumnWidth) {
    model.setDefaultColumnWidth(defaultColumnWidth);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getMinRowHeight()
   */
  @Override
  public int getMinRowHeight() {
    return model.getMinRowHeight();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getMaxRowHeight()
   */
  @Override
  public int getMaxRowHeight() {
    return model.getMaxRowHeight();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getMaxColumnWidth()
   */
  @Override
  public int getMaxColumnWidth() {
    return model.getMaxColumnWidth();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.swing.gridsheet.model.IGridSheetStructure#getMinColumnWidth()
   */
  @Override
  public int getMinColumnWidth() {
    return model.getMinColumnWidth();
  }
}
