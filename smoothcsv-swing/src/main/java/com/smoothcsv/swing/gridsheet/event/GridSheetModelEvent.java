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
package com.smoothcsv.swing.gridsheet.event;

import com.smoothcsv.swing.gridsheet.model.GridSheetModel;


@SuppressWarnings("serial")
public class GridSheetModelEvent extends java.util.EventObject {

  /**
   * Identifies the addtion of new rows or columns.
   */
  public static final int INSERT = 1;
  /**
   * Identifies a change to existing data.
   */
  public static final int UPDATE = 0;
  /**
   * Identifies the removal of rows or columns.
   */
  public static final int DELETE = -1;

  /**
   * Identifies the header row.
   */
  public static final int HEADER_ROW = -1;

  /**
   * Specifies all columns in a row or rows.
   */
  public static final int ALL_COLUMNS = -1;

  //
  // Instance Variables
  //

  protected int type;
  protected int firstRow;
  protected int lastRow;
  protected int column;

  //
  // Constructors
  //

  /**
   * All row data in the table has changed, listeners should discard any state that was based on the
   * rows and requery the <code>GridSheetModel</code> to get the new row count and all the
   * appropriate values. The <code>JTable</code> will repaint the entire visible region on receiving
   * this event, querying the model for the cell values that are visible. The structure of the table
   * ie, the column names, types and order have not changed.
   */
  public GridSheetModelEvent(GridSheetModel source) {
    // Use Integer.MAX_VALUE instead of getRowCount() in case rows were
    // deleted.
    this(source, 0, Integer.MAX_VALUE, ALL_COLUMNS, UPDATE);
  }

  /**
   * This row of data has been updated. To denote the arrival of a completely new table with a
   * different structure use <code>HEADER_ROW</code> as the value for the <code>row</code>. When the
   * <code>JTable</code> receives this event and its <code>autoCreateColumnsFromModel</code> flag is
   * set it discards any TableColumns that it had and reallocates default ones in the order they
   * appear in the model. This is the same as calling <code>setModel(GridSheetModel)</code> on the
   * <code>JTable</code>.
   */
  public GridSheetModelEvent(GridSheetModel source, int row) {
    this(source, row, row, ALL_COLUMNS, UPDATE);
  }

  /**
   * The data in rows [<I>firstRow</I>, <I>lastRow</I>] have been updated.
   */
  public GridSheetModelEvent(GridSheetModel source, int firstRow, int lastRow) {
    this(source, firstRow, lastRow, ALL_COLUMNS, UPDATE);
  }

  /**
   * The cells in column <I>column</I> in the range [<I>firstRow</I>, <I>lastRow</I>] have been
   * updated.
   */
  public GridSheetModelEvent(GridSheetModel source, int firstRow, int lastRow, int column) {
    this(source, firstRow, lastRow, column, UPDATE);
  }

  /**
   * The cells from (firstRow, column) to (lastRow, column) have been changed. The <I>column</I>
   * refers to the column index of the cell in the model's co-ordinate system. When <I>column</I> is
   * ALL_COLUMNS, all cells in the specified range of rows are considered changed.
   * <p>
   * The <I>type</I> should be one of: INSERT, UPDATE and DELETE.
   */
  public GridSheetModelEvent(GridSheetModel source, int firstRow, int lastRow, int column,
                             int type) {
    super(source);
    this.firstRow = firstRow;
    this.lastRow = lastRow;
    this.column = column;
    this.type = type;
  }

  //
  // Querying Methods
  //

  /**
   * Returns the first row that changed. HEADER_ROW means the meta data, ie. names, types and order
   * of the columns.
   */
  public int getFirstRow() {
    return firstRow;
  }

  ;

  /**
   * Returns the last row that changed.
   */
  public int getLastRow() {
    return lastRow;
  }

  ;

  /**
   * Returns the column for the event. If the return value is ALL_COLUMNS; it means every column in
   * the specified rows changed.
   */
  public int getColumn() {
    return column;
  }

  ;

  /**
   * Returns the type of event - one of: INSERT, UPDATE and DELETE.
   */
  public int getType() {
    return type;
  }
}
