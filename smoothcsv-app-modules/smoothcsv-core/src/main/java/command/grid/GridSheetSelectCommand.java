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
package command.grid;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 */
public class GridSheetSelectCommand extends GridCommand {

  public static final int DECREMENT = -1;
  public static final int INCREMENT = +1;
  public static final int TO_FIRST = Integer.MIN_VALUE;
  public static final int TO_LAST = Integer.MAX_VALUE;

  protected final int dx;
  protected final int dy;
  protected final boolean extend;

  public GridSheetSelectCommand(int dx, int dy, boolean extend) {
    this.dx = dx;
    this.dy = dy;
    this.extend = extend;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.smoothcsv.core.command.GridSheetCommandAction#run(com.smoothcsv.core.component.csvgridsheet
   * .CsvGridSheetPane)
   */
  @Override
  public final void run(CsvGridSheetPane gridSheetPane) {
    GridSheetTable table = gridSheetPane.getTable();
    if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
      return;
    }

    GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    int anchorRow = sm.getRowAnchorIndex();
    int anchorColumn = sm.getColumnAnchorIndex();
    changeSelection(gridSheetPane, sm, dx, dy, extend, anchorRow, anchorColumn);
  }

  /**
   * @param gridSheetPane
   * @param sm
   * @param dx
   * @param dy
   * @param extend
   * @param anchorRow
   * @param anchorColumn
   */
  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm,
                                 int dx, int dy, boolean extend, int anchorRow, int anchorColumn) {
    GridSheetTable table = gridSheetPane.getTable();

    // Unfortunately, this strategy introduces bugs because
    // of the asynchronous nature of requestFocus() call below.
    // Introducing a delay with invokeLater() makes this work
    // in the typical case though race conditions then allow
    // focus to disappear altogether. The right solution appears
    // to be to fix requestFocus() so that it queues a request
    // for the focus regardless of who owns the focus at the
    // time the call to requestFocus() is made. The optimisation
    // to ignore the call to requestFocus() when the component
    // already has focus may ligitimately be made as the
    // request focus event is dequeued, not before.
    // boolean wasEditingWithFocus = table.isEditing() &&
    // table.getEditorComponent().isFocusOwner();

    int rowCount = gridSheetPane.getRowCount();
    int columnCount = gridSheetPane.getColumnCount();

    if (rowCount <= 0 || columnCount <= 0) {
      // bail - don't try to move selection on an empty table
      return;
    }

    if (extend) {
      int leadRowDirection = dy > 0 ? 1 : dy < 0 ? -1 : 0;
      int leadColumnDirection = dx > 0 ? 1 : dx < 0 ? -1 : 0;
      int minR = sm.getMinRowSelectionIndex();
      int minC = sm.getMinColumnSelectionIndex();
      int maxR = sm.getMaxRowSelectionIndex();
      int maxC = sm.getMaxColumnSelectionIndex();
      if (dy != 0) {
        if (minR == anchorRow) {
          maxR = moveWithinTableRange(maxR, dy, rowCount);
          if (maxR < anchorRow) {
            minR -= anchorRow - maxR;
            maxR = anchorRow;
          }
          if (dy < 0) {
            leadRowDirection *= -1;
          }
        } else if (maxR == anchorRow) {
          minR = moveWithinTableRange(minR, dy, rowCount);
          if (anchorRow < minR) {
            maxR -= anchorRow - minR;
            minR = anchorRow;
          }
          if (0 < dy) {
            leadRowDirection *= -1;
          }
        } else {
          if (dy > 0) {
            maxR = moveWithinTableRange(maxR, dy, rowCount);
          } else if (dy < 0) {
            minR = moveWithinTableRange(minR, dy, rowCount);
          }
        }
      }
      if (dx != 0) {
        if (minC == anchorColumn) {
          maxC = moveWithinTableRange(maxC, dx, columnCount);
          if (maxC < anchorColumn) {
            minC -= anchorColumn - maxC;
            maxC = anchorColumn;
          }
          if (dx < 0) {
            leadColumnDirection *= -1;
          }
        } else if (maxC == anchorColumn) {
          minC = moveWithinTableRange(minC, dx, columnCount);
          if (anchorColumn < minC) {
            maxC -= anchorColumn - minC;
            minC = anchorColumn;
          }
          if (0 < dx) {
            leadColumnDirection *= -1;
          }
        } else {
          if (dx > 0) {
            maxC = moveWithinTableRange(maxC, dx, columnCount);
            ;
          } else if (dx < 0) {
            minC = moveWithinTableRange(minC, dx, columnCount);
            ;
          }
        }
      }
      // minR = clipToRange(minR, rowCount);
      // minC = clipToRange(minC, columnCount);
      // maxR = clipToRange(maxR, rowCount);
      // maxC = clipToRange(maxC, columnCount);
      if (sm.isColumnHeaderSelected() && (minR != 0 || maxR != rowCount - 1)) {
        sm.setValueIsAdjusting(false);
        sm.setColumnHeaderSelected(false);
      }
      if (sm.isRowHeaderSelected() && (minC != 0 || maxC != columnCount - 1)) {
        sm.setValueIsAdjusting(false);
        sm.setRowHeaderSelected(false);
      }
      sm.setSelectionIntervalNoChangeAnchor(minR, minC, maxR, maxC);
      if (sm.getValueIsAdjusting()) {
        sm.setValueIsAdjusting(false);
      }
      table.scrollRectToVisible(leadRowDirection < 0 ? minR : maxR, leadColumnDirection < 0 ? minC
          : maxC);
    } else {
      anchorRow = moveWithinTableRange(anchorRow, dy, rowCount);
      anchorColumn = moveWithinTableRange(anchorColumn, dx, columnCount);
      sm.clearHeaderSelection();
      sm.setSelectionInterval(anchorRow, anchorColumn, anchorRow, anchorColumn);
      table.scrollRectToVisible(anchorRow, anchorColumn);
    }
  }

  private static int moveWithinTableRange(int index, int direction, int size) {
    if (direction == TO_FIRST) {
      return 0;
    } else if (direction == TO_LAST) {
      return size - 1;
    }
    return clipToRange(index + direction, size);
  }

  private static int clipToRange(int i, int size) {
    if (i < 0) {
      return 0;
    } else if (size <= i) {
      return size - 1;
    }
    return i;
  }
}
