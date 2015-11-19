/*
 * Copyright 2014 kohii.
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
package com.smoothcsv.core.macro.api;

import com.smoothcsv.core.macro.api.impl.CsvProperties;


/**
 *
 * @author kohei
 */
public interface CsvSheet {

  /**
   * Activates this sheet.
   *
   * @return the newly active sheet
   */
  CsvSheet activate();

  /**
   * Gets the position of the sheet in the application. Starts at 1.
   *
   * @return the position of the sheet
   */
  int getIndex();

  // File Details ///////////////////////////////////////////////////////////

  /**
   * Returns the pathname of the file to be saved.
   * 
   * @return the pathname of the file
   */
  String getPathname();

  /**
   * Sets the pathname of the file to be saved.
   * 
   * @param pathname the pathname of the file
   */
  void setPathname(String pathname);

  /**
   * Returns the properties that will be used for saving data to a file.
   * 
   * @return the properties
   */
  CsvProperties getProperties();

  /**
   * Sets the properties that will be used for saving data to a file.
   * 
   * @param properties the properties to set
   * @return the sheet, useful for method chaining
   */
  CsvSheet setProperties(CsvProperties properties);

  /**
   * Returns true if this editor has been modified.
   * 
   * @return whether this editor has been modified or not
   */
  boolean isModified();

  // File Operations ////////////////////////////////////////////////////////
  /**
   * Saves changes to the specified file. The first time you save a CSV, use
   * {@link #setPathname(String)} before calling this method to specify a name for the file, or use
   * {@link #saveAs(String)} instead. Otherwise the user is asked to supply a pathname.
   */
  void save();

  /**
   * Saves changes to the different file.
   * 
   * @param pathname the pathname of the file to be saved.
   */
  void saveAs(String pathname);

  // Close //////////////////////////////////////////////////////////////////
  /**
   * Closes this sheet.
   */
  void close();

  /**
   * Closes this sheet.
   * 
   * @param saveChanges If there are no changes to the sheet, this argument is ignored. If there are
   *        changes to the sheet, this argument specifies whether changes should be saved. If set to
   *        true, changes are saved to the file. If there is not yet a file pathname associated with
   *        the sheet, then pathname is used. If pathname is omitted, the user is asked to supply a
   *        pathname.
   * @param pathname
   */
  void close(boolean saveChanges, String pathname);

  // History ////////////////////////////////////////////////////////////////

  /**
   * Undo the last change.
   * 
   * @return true if the undoing has succeeded, false if the undoing couldn't complete
   */
  boolean undo();

  /**
   * Redo the last change.
   * 
   * @return true if the redoing has succeeded, false if the redoing couldn't complete
   */
  boolean redo();

  // width and height ////////////////////////////////////////////////////////
  /**
   * Sets the width of the given column to fit its contents.
   *
   * @param columnPosition the position of the given column to resize
   * @return the sheet, useful for method chaining
   */
  CsvSheet autoResizeColumn(int columnPosition);

  /**
   * Gets the height in pixels of the given row.
   *
   * @param rowPosition the position of the row to examine
   * @return row height in pixels
   */
  int getRowHeight(int rowPosition);


  // /**
  // * Sets the row height of the given row in pixels.
  // *
  // * @param rowPosition the row position to change
  // * @param height height in pixels to set it to
  // * @return the sheet, useful for method chaining
  // */
  // CsvSheet setRowHeight(int rowPosition, int height);

  /**
   * Gets the width in pixels of the given column.
   *
   * @param columnPosition the position of the column to examine
   * @return column width in pixels
   */
  int getColumnWidth(int columnPosition);

  /**
   * Sets the width of the given column in pixels.
   * 
   * @param columnPosition the position of the given column to set
   * @param width the width in pixels to set it to
   * @return the sheet, useful for method chaining
   */
  CsvSheet setColumnWidth(int columnPosition, int width);

  // add and remove //////////////////////////////////////////////////////////
  // /**
  // * Appends a row to the sheet.
  // *
  // * @param rowContents an array of values to insert after the last row in the sheet
  // * @return the sheet, useful for method chaining
  // */
  // ICsvSheet appendRow(Object[] rowContents);
  //
  // /**
  // * Appends a column to the sheet.
  // *
  // * @param columnContents an array of values to insert after the last column in the sheet
  // * @return the sheet, useful for method chaining
  // */
  // ICsvSheet appendColumn(Object[] columnContents);

  // /**
  // * Inserts columns as many as selected columns after the selected columns
  // *
  // * @return the sheet, useful for method chaining
  // */
  // ICsvSheet insertColumnsAfter();
  //
  // /**
  // * Inserts columns as many as selected columns before the selected columns
  // *
  // * @return the sheet, useful for method chaining
  // */
  // ICsvSheet insertColumnsBefore();

  /**
   * Inserts a row after the given row position.
   *
   * @param the row after which the new row should be added
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertRowAfter(int afterPosition);

  /**
   * Inserts a number of rows after the given row position.
   *
   * @param afterPosition the row after which the new row should be added
   * @param howMany the number of rows to insert
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertRowsAfter(int afterPosition, int howMany);

  /**
   * Inserts a row before the given row position.
   *
   * @param the row before which the new row should be added
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertRowBefore(int beforePosition);

  /**
   * Inserts a number of rows before the given row position.
   *
   * @param beforePosition the row before which the new row should be added
   * @param howMany the number of rows to insert
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertRowsBefore(int beforePosition, int howMany);

  /**
   * Deletes the row at the given row position.
   *
   * @param rowPosition the position of the row, starting at 1 for the first row
   * @return the sheet, useful for method chaining
   */
  CsvSheet deleteRow(int rowPosition);

  /**
   * Deletes a number of rows starting at the given row position.
   *
   * @param rowPosition the position of the first row to delete
   * @param howMany the number of rows to delete
   * @return the sheet, useful for method chaining
   */
  CsvSheet deleteRows(int rowPosition, int howMany);


  /**
   * Inserts a column after the given column position.
   *
   * @param the column after which the new column should be added
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertColumnAfter(int afterPosition);

  /**
   * Inserts a number of columns after the given column position.
   *
   * @param afterPosition the column after which the new column should be added
   * @param howMany the number of columns to insert
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertColumnsAfter(int afterPosition, int howMany);

  /**
   * Inserts a column before the given column position.
   *
   * @param the column before which the new column should be added
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertColumnBefore(int beforePosition);

  /**
   * Inserts a number of columns before the given column position.
   *
   * @param beforePosition the column before which the new column should be added
   * @param howMany the number of columns to insert
   * @return the sheet, useful for method chaining
   */
  CsvSheet insertColumnsBefore(int beforePosition, int howMany);

  /**
   * Deletes the column at the given row position.
   *
   * @param columnPosition the position of the column, starting at 1 for the first column
   * @return the sheet, useful for method chaining
   */
  CsvSheet deleteColumn(int columnPosition);

  /**
   * Deletes a number of columns starting at the given column position.
   *
   * @param columnPosition the position of the first column to delete
   * @param howMany the number of columns to delete
   * @return the sheet, useful for method chaining
   */
  CsvSheet deleteColumns(int columnPosition, int howMany);

  // structure ////////////////////////////////////////////////////////////////
  int getNumRows();

  int getNumColumns();

  // frozen //////////////////////////////////////////////////////////////////
  // /**
  // * Returns the number of frozen rows.
  // *
  // * @return the number of frozen rows
  // */
  // int getFrozenows();
  //
  // /**
  // * Returns the number of frozen columns.
  // *
  // * @return the number of frozen columns
  // */
  // int getFrozenColumns();

  // hide and show ///////////////////////////////////////////////////////////
  // /**
  // * Hides the rows in the given range.
  // *
  // * @param rows the row range to hide
  // */
  // void hideRows(IRange rows);
  //
  // /**
  // * Hides one or more consecutive rows starting at the given index.
  // *
  // * @param rowsIndex the starting index of the rows to hide
  // * @param numRow the number of rows to hide
  // */
  // void hideRows(int rowsIndex, int numRow);
  //
  // /**
  // * Hides the columns in the given range.
  // *
  // * @param columns the column range to hide
  // */
  // void hideColumns(IRange columns);
  //
  // /**
  // * Hides one or more consecutive columns starting at the given index.
  // *
  // * @param columnIndex the starting index of the columns to hide
  // * @param numColumns the number of columns to hide
  // */
  // void hideColumns(int columnIndex, int numColumns);
  //
  // /**
  // * Unhides the column at the given index.
  // *
  // * @param columnIndex the index of the column to unhide
  // */
  // void showColumns(int columnIndex);
  //
  // /**
  // * Unhides one or more consecutive columns starting at the given index.
  // *
  // * @param columnIndex the index of the column to unhide
  // * @param numColumns the number of columns to unhide
  // */
  // void showColumns(int columnIndex, int numColumns);
  //
  // /**
  // * Unhides the row at the given index.
  // *
  // * @param rowIndex the index of the row to unhide
  // */
  // void showRows(int rowIndex);
  //
  // /**
  // * Unhides one or more consecutive rows starting at the given index.
  // *
  // * @param rowIndex the index of the row to unhide
  // * @param numRows the number of rows to unhide
  // */
  // void showRows(int rowIndex, int numRows);

  // range ///////////////////////////////////////////////////////////////////
  /**
   * Returns the range with the top left cell at the given coordinates.
   *
   * @param row the row of the cell to return
   * @param column the column of the cell to return
   * @return a {@link Range} containing only this cell
   */
  Range getRange(int row, int column);

  /**
   * Returns the range with the top left cell at the given coordinates with the given number of rows
   * and columns.
   *
   * @param row the starting row of the range
   * @param column the starting column of the range
   * @param numRows the number of rows to return
   * @param numColumns the number of columns to return
   * @return a {@link Range} corresponding to the area specified
   */
  Range getRange(int row, int column, int numRows, int numColumns);

  /**
   * Returns the active range for the active sheet. Returns the range of cells that is currently
   * considered active. This generally means the range that a user has selected in the active sheet.
   * 
   * @return the active range
   */
  Range getActiveRange();

  /**
   * Sets the active range for the active sheet.
   * 
   * @param range the range to set as the active range
   * @return the newly active range
   */
  Range setActiveRange(Range range);

  /**
   * Returns the active cell in this sheet.
   * 
   * @return the current active cell
   */
  Range getActiveCell();

  /**
   * Returns the range containing all the cells.
   * 
   * @return a {@link Range} containing all the cells
   */
  Range getRange();

  // /**
  // * Returns the range as specified in A1 notation or R1C1 notation.
  // *
  // * @param a1Notation the range to return, as specified in A1 notation or R1C1 notation
  // * @return the range at the location designated
  // */
  // IRange getRange(String a1Notation);

  // sort ////////////////////////////////////////////////////////////////////

  /**
   * Sorts a sheet, by column and order specified.
   *
   * @param sortSpecObj
   * @return the sheet, useful for method chaining
   */
  CsvSheet sort(Object sortSpecObj);
}
