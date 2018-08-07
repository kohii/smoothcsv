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

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 */
public class CellIterator {
  private final GridSheetPane grid;

  private final boolean inSelection;
  private final boolean wrap;
  private final boolean reverse;
  private final Orientation orientation;

  private int minRow;
  private int maxRow;
  private int minColumn;
  private int maxColumn;

  private int startRow;
  private int startColumn;
  private boolean turned;

  private int row;
  private int column;

  public CellIterator(GridSheetPane grid, boolean inSelection, boolean wrap, boolean reverse,
                      Orientation orientation, boolean startsFromFirst) {
    this.grid = grid;
    this.inSelection = inSelection;
    this.wrap = wrap;
    this.reverse = reverse;
    this.orientation = orientation;
    GridSheetSelectionModel selectionModel = grid.getSelectionModel();
    if (inSelection) {
      minRow = selectionModel.getMinRowSelectionIndex();
      maxRow = selectionModel.getMaxRowSelectionIndex();
      minColumn = selectionModel.getMinColumnSelectionIndex();
      maxColumn = selectionModel.getMaxColumnSelectionIndex();
    } else {
      minRow = 0;
      maxRow = grid.getRowCount() - 1;
      minColumn = 0;
      maxColumn = grid.getColumnCount() - 1;
    }
    if (minRow == maxRow && minColumn == maxColumn) {
      turned = true;
    }

    if (startsFromFirst) {
      if (reverse) {
        this.startRow = maxRow;
        this.startColumn = maxColumn;
      } else {
        this.startRow = minRow;
        this.startColumn = minColumn;
      }
      if (inSelection) {
        if (orientation == Orientation.HORIZONTAL) {
          while (!isCellSelected(startRow, startColumn)) {
            startRow += reverse ? -1 : 1;
          }
        } else {
          while (!isCellSelected(startRow, startColumn)) {
            startColumn += reverse ? -1 : 1;
          }
        }
      }
    } else {
      this.startRow = selectionModel.getRowAnchorIndex();
      this.startColumn = selectionModel.getColumnAnchorIndex();
    }

    this.row = startRow;
    this.column = startColumn;

    if (orientation == Orientation.VERTICAL) {
      int tmp;

      tmp = this.row;
      this.row = this.column;
      this.column = tmp;

      tmp = this.startRow;
      this.startRow = this.startColumn;
      this.startColumn = tmp;

      tmp = this.minRow;
      this.minRow = this.minColumn;
      this.minColumn = tmp;

      tmp = this.maxRow;
      this.maxRow = this.maxColumn;
      this.maxColumn = tmp;
    }
  }

  public boolean next() {
    if (!inSelection) {
      if (!reverse) {
        if (column == maxColumn) {
          if (row == maxRow) {
            if (!wrap) {
              return false;
            } else {
              if (turned) {
                return false;
              }
              turned = true;
              row = minRow;
            }
          } else {
            row++;
          }
          column = minColumn;
        } else {
          column++;
        }
      } else {
        if (column == minColumn) {
          if (row == minRow) {
            if (!wrap) {
              return false;
            } else {
              if (turned) {
                return false;
              }
              turned = true;
              row = maxRow;
            }
          } else {
            row--;
          }
          column = maxColumn;
        } else {
          column--;
        }
      }
    } else {
      // In selection
      if (!reverse) {
        for (int j = column + 1; j <= maxColumn; j++) {
          if (isCellSelected(row, j)) {
            column = j;
            return true;
          }
        }
        for (int i = row + 1; i <= maxRow; i++) {
          for (int j = minColumn; j <= maxColumn; j++) {
            if (isCellSelected(i, j)) {
              row = i;
              column = j;
              return true;
            }
          }
        }
        if (turned || !wrap) {
          return false;
        }
        turned = true;
        row = minRow;
        for (int j = minColumn; j <= maxColumn; j++) {
          if (isCellSelected(row, j)) {
            column = j;
            break;
          }
        }
      } else {
        for (int j = column - 1; minColumn <= j; j--) {
          if (isCellSelected(row, j)) {
            column = j;
            return true;
          }
        }
        for (int i = row - 1; minRow <= i; i--) {
          for (int j = maxColumn; minColumn <= j; j--) {
            if (isCellSelected(i, j)) {
              row = i;
              column = j;
              return true;
            }
          }
        }
        if (turned || !wrap) {
          return false;
        }
        turned = true;
        row = maxRow;
        for (int j = maxColumn; minColumn <= j; j--) {
          if (isCellSelected(row, j)) {
            column = j;
            break;
          }
        }
      }
    }

    if (turned
        && ((!reverse & startRow <= row && startColumn <= column) || (reverse & row <= startRow && column <= startColumn))) {
      return false;
    }
    return true;
  }

  private boolean isCellSelected(int row, int column) {
    int r, c;
    if (orientation == Orientation.VERTICAL) {
      c = row;
      r = column;
    } else {
      r = row;
      c = column;
    }
    return grid.getSelectionModel().isCellSelected(r, c);
  }

  public int getRow() {
    return orientation == Orientation.VERTICAL ? column : row;
  }

  /**
   * @return the column
   */
  public int getColumn() {
    return orientation == Orientation.VERTICAL ? row : column;
  }

  @Override
  public String toString() {
    return getRow() + "," + getColumn();
  }
}
