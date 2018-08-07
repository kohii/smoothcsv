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

import lombok.Getter;

/**
 * @author kohii
 */
public class CellRect {

  @Getter
  private int row;
  @Getter
  private int column;
  @Getter
  private int lastRow;
  @Getter
  private int lastColumn;

  /**
   * @param row
   * @param column
   * @param lastRow
   * @param lastColumn
   */
  public CellRect(int row, int column, int lastRow, int lastColumn) {
    this.row = row;
    this.column = column;
    this.lastRow = lastRow;
    this.lastColumn = lastColumn;
  }

  public int getNumRows() {
    return lastRow - row + 1;
  }

  public int getNumColumns() {
    return lastColumn - column + 1;
  }
}
