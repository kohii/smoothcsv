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

import com.smoothcsv.commons.constants.Direction;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class GridSheetCellRange {

  int firstRow;
  int lastRow;

  int firstColumn;
  int lastColumn;

  public GridSheetCellRange(int firstRow, int lastRow, int firstColumn, int lastColumn) {
    this.firstRow = firstRow;
    this.lastRow = lastRow;
    this.firstColumn = firstColumn;
    this.lastColumn = lastColumn;
  }

  public boolean containsRowAt(int index) {
    return (firstRow <= index) && (index <= lastRow);
  }

  public boolean containsColumnAt(int index) {
    return (firstColumn <= index) && (index <= lastColumn);
  }

  public boolean contains(int row, int column) {
    return containsRowAt(row) && containsColumnAt(column);
  }

  public int getFirstRow() {
    return firstRow;
  }

  public int getLastRow() {
    return lastRow;
  }

  public int getFirstColumn() {
    return firstColumn;
  }

  public int getLastColumn() {
    return lastColumn;
  }

  public int getNumRows() {
    return lastRow - firstRow + 1;
  }

  public int getNumColumns() {
    return lastColumn - firstColumn + 1;
  }

  public GridSheetCellRange extendHorizontallyTo(int column) {
    return new GridSheetCellRange(firstRow, lastRow, Math.min(firstColumn, column),
        Math.max(lastColumn, column));
  }

  public GridSheetCellRange extendVerticallyTo(int row) {
    return new GridSheetCellRange(Math.min(firstRow, row), Math.max(lastRow, row), firstColumn,
        lastColumn);
  }

  public GridSheetCellRange extend(Direction direction, int num) {
    GridSheetCellRange range = new GridSheetCellRange(firstRow, lastRow, firstColumn, lastColumn);
    switch (direction) {
      case UP:
        range.firstRow -= num;
        break;
      case DOWN:
        range.lastRow += num;
        break;
      case LEFT:
        range.firstColumn -= num;
        break;
      case RIGHT:
        range.lastColumn += num;
        break;
      default:
        throw new IllegalArgumentException();
    }
    return range;
  }

  @Override
  public String toString() {
    return "row:" + firstRow + "-" + lastRow + " column:" + firstColumn + "-" + lastColumn;
  }
}
