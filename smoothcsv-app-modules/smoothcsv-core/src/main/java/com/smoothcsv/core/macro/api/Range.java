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

/**
 *
 * @author kohii
 */
public interface Range {

  /**
   * Make this range the active range.
   * 
   * @return the range for chaining
   */
  Range activate();

  /**
   * Clears the range of contents, formats, and data-validation rules.
   *
   * @return the range for chaining
   */
  Range clear();

  /**
   * Returns a given cell within a range.
   *
   * @param row the row of the cell relative to the range
   * @param column the column of the cell relative to the range
   * @return a range containing a single cell at the specified coordinates
   */
  Range getCell(int row, int column);

  /**
   * Returns the starting row position for this range.
   *
   * @return the range's starting row position in the sheet
   */
  int getRow();

  /**
   * Returns the starting column position for this range.
   *
   * @return the range's starting column position in the sheet
   */
  int getColumn();

  /**
   * Returns the end row position.
   *
   * @return the range's ending row position in the sheet
   */
  int getLastRow();

  /**
   * Returns the end column position.
   *
   * @return the range's ending column position in the sheet
   */
  int getLastColumn();

  /**
   * Returns the number of rows in this range.
   *
   * @return the number of rows in this range
   */
  int getNumRows();

  /**
   * Returns the number of columns in this range.
   *
   * @return the number of columns in this range
   */
  int getNumColumns();

  /**
   * Returns the sheet this range belongs to.
   *
   * @return the sheet that this range belongs to
   */
  CsvSheet getSheet();

  /**
   * Returns the value of the top-left cell in the range.
   *
   * @return the value in this cell
   */
  Object getValue();

  /**
   * Returns the rectangular sheet of values for this range. Returns a two-dimensional array of
   * values, indexed by row, then by column.
   *
   * @return a two-dimensional array of values
   *
   */
  Object[][] getValues();

  /**
   * Returns true if the range is totally empty.
   *
   * @return whether the range is blank or not
   */
  boolean isBlank();

  /**
   * Copies the data from a range of cells to another range of cells.
   *
   * @param destination a destination range to copy to; only the top-left cell position is relevant
   */
  void copyTo(Range destination);

  // /**
  // * Copy the content of the range to the given location. If the destination is larger or smaller
  // * than the source range then the source will be repeated or truncated accordingly.
  // *
  // * @param row the first row of the target range
  // * @param column the first column of the target range
  // * @param rowEnd the end row of the target range
  // * @param columnEnd the end column of the target range
  // * @param sheet the target sheet
  // */
  // void copyTo(int row, int column, int rowEnd, int columnEnd, CsvSheet sheet);

  /**
   * Cut and paste the data from this range to the target range.
   *
   * @param destination a destination range to copy to; only the top-left cell position is relevant
   */
  void moveTo(Range destination);

  // /**
  // * Cut and paste the content of the range to the given location. If the destination is larger or
  // * smaller than the source range then the source will be repeated or truncated accordingly.
  // *
  // * @param row the first row of the target range
  // * @param column the first column of the target range
  // * @param rowEnd the end row of the target range
  // * @param columnEnd the end column of the target range
  // * @param sheet the target sheet
  // */
  // void moveTo(int row, int column, int rowEnd, int columnEnd, CsvSheet sheet);

  /**
   * Returns a new range that is offset from this range by the given number of rows and columns
   * (which can be negative). The new range will be the same size as the original range.
   *
   * @param rowOffset number of rows down from the range's top-left cell; negative values represent
   *        rows up from the range's top-left cell
   * @param columnOffset number of columns right from the range's top-left cell; negative values
   *        represent columns left from the range's top-left cell
   * @return the range for chaining
   */
  Range offset(int rowOffset, int columnOffset);

  /**
   * Returns a new range that is relative to the current range, whose upper left point is offset
   * from the current range by the given rows and columns, and with the given height and width in
   * cells.
   *
   * @param rowOffset number of rows down from the range's top-left cell; negative values represent
   *        rows up from the range's top-left cell
   * @param columnOffset number of columns right from the range's top-left cell; negative values
   *        represent columns left from the range's top-left cell
   * @param numRows the height in rows of the new range
   * @param numColumns the width in columns of the new range
   * @return
   */
  Range offset(int rowOffset, int columnOffset, int numRows, int numColumns);

  /**
   * Sets the value of the range.
   *
   * @param value the value for the range
   * @return the range for chaining
   */
  Range setValue(Object value);

  /**
   * Sets a rectangular sheet of values (must match dimensions of this range).
   *
   * @param values a two-dimensional array of values
   * @return the range for chaining
   */
  Range setValues(Object[][] values);

  /**
   * Sorts the cells in a given range, by column and order specified.
   *
   * @param sortSpecObj
   * @return the range, for chaining
   */
  Range sort(Object sortSpecObj);
}
