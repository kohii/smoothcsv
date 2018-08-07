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

import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class GridSheetDataEvent extends java.util.EventObject {

  /**
   * Identifies the header.
   */
  public static final int HEADER_INDEX = -1;

  /**
   * Specifies all cells.
   */
  public static final int ALL_CELLS = -2;

  /**
   * Specifies all cells.
   */
  public static final int TO_THE_END = Integer.MAX_VALUE;

  @Getter
  private int firstRow;
  @Getter
  private int lastRow;
  @Getter
  private int firstColumn;
  @Getter
  private int lastColumn;
  @Getter
  private boolean structureChanged;

  public GridSheetDataEvent(Object source, int firstRow, int firstColumn, int lastRow,
                            int lastColumn, boolean structureChanged) {
    super(source);
    this.firstRow = firstRow;
    this.firstColumn = firstColumn;
    this.lastRow = lastRow;
    this.lastColumn = lastColumn;
    this.structureChanged = structureChanged;
  }

  public GridSheetDataEvent(Object source, int firstRow, int firstColumn, int lastRow,
                            int lastColumn) {
    this(source, firstRow, firstColumn, lastRow, lastColumn, false);
  }

  public GridSheetDataEvent(Object source, int firstRow, int firstColumn) {
    super(source);
    this.firstRow = firstRow;
    this.firstColumn = firstColumn;
    this.lastRow = TO_THE_END;
    this.lastColumn = TO_THE_END;
  }

  public GridSheetDataEvent(Object source) {
    super(source);
    this.firstRow = ALL_CELLS;
  }
}
