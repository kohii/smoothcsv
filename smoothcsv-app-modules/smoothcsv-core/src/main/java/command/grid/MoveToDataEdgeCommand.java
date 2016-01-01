/*
 * Copyright 2015 kohii
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

import java.util.Map;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class MoveToDataEdgeCommand extends GridCommand {

  public static final int PREVIOUS = -1;
  public static final int NEXT = +1;

  protected final int dx;
  protected final int dy;
  protected final boolean extend;

  public MoveToDataEdgeCommand(Map<String, Object> options) {
    this(getDirectionX(options), getDirectionY(options), getExtend(options));
  }

  private static int getDirectionX(Map<String, Object> options) {
    Object d = options.get("direction");
    if (d != null) {
      if (d.equals("left")) {
        return -1;
      } else if (d.equals("right")) {
        return +1;
      }
    }
    return 0;
  }

  private static int getDirectionY(Map<String, Object> options) {
    Object d = options.get("direction");
    if (d != null) {
      if (d.equals("up")) {
        return -1;
      } else if (d.equals("down")) {
        return +1;
      }
    }
    return 0;
  }

  private static boolean getExtend(Map<String, Object> options) {
    Object e = options.get("extend");
    if (e != null) {
      if (e instanceof Boolean) {
        return (boolean) e;
      } else {
        return Boolean.valueOf(e.toString());
      }
    }
    return false;
  }

  private MoveToDataEdgeCommand(int dx, int dy, boolean extend) {
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

  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm, int dx,
      int dy, boolean extend, int anchorRow, int anchorColumn) {
    GridSheetTable table = gridSheetPane.getTable();

    if (dy != 0) {
      // vertical

      boolean forwards = dy > 0;

      int rowCount = gridSheetPane.getRowCount();
      int limitFrom;
      int limitTo;
      if (forwards) {
        limitFrom = 0;
        limitTo = rowCount - 1;
      } else {
        limitFrom = rowCount - 1;
        limitTo = 0;
      }

      int focusedRow = sm.getRowAnchorIndex();
      int focusedColumn = sm.getColumnAnchorIndex();
      Object current = gridSheetPane.getValueAt(focusedRow, focusedColumn);
      boolean isEmpty = ObjectUtils.isEmpty(current);
      int d = forwards ? 1 : -1;
      if (!isEmpty && focusedRow != limitTo
          && ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow + d, focusedColumn))) {
        isEmpty = true;
        focusedRow += d;
      }
      int row = limitTo;
      int limitMin = Math.min(limitFrom, limitTo);
      int limitMax = Math.max(limitFrom, limitTo);
      for (int i = focusedRow; limitMin <= i && i <= limitMax; i += d) {
        boolean b = ObjectUtils.isEmpty(gridSheetPane.getValueAt(i, focusedColumn));
        if (isEmpty) {
          if (!b) {
            row = i;
            break;
          }
        } else {
          if (b) {
            row = i - d;
            break;
          }
        }
      }

      sm.setValueIsAdjusting(true);
      sm.clearHeaderSelection();
      if (extend) {
        sm.changeLeadSelection(row, focusedColumn, GridSheetSelectionModel.CHANGE_ONLY_VERTICAL);
      } else {
        sm.setSelectionInterval(row, focusedColumn, row, focusedColumn);
      }
      table.scrollRectToVisible(row, focusedColumn);

      sm.setValueIsAdjusting(false);

    } else {
      // horizontal

      boolean forwards = dx > 0;

      int columnCount = gridSheetPane.getColumnCount();
      int limitFrom;
      int limitTo;
      if (forwards) {
        limitFrom = 0;
        limitTo = columnCount - 1;
      } else {
        limitFrom = columnCount - 1;
        limitTo = 0;
      }

      int focusedRow = sm.getRowAnchorIndex();
      int focusedColumn = sm.getColumnAnchorIndex();
      Object current = gridSheetPane.getValueAt(focusedRow, focusedColumn);
      boolean isEmpty = ObjectUtils.isEmpty(current);
      int d = forwards ? 1 : -1;
      if (!isEmpty && focusedColumn != limitTo
          && ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow, focusedColumn + d))) {
        isEmpty = true;
        focusedColumn += d;
      }
      int column = limitTo;
      int limitMin = Math.min(limitFrom, limitTo);
      int limitMax = Math.max(limitFrom, limitTo);
      for (int i = focusedColumn; limitMin <= i && i <= limitMax; i += d) {
        boolean b = ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow, i));
        if (isEmpty) {
          if (!b) {
            column = i;
            break;
          }
        } else {
          if (b) {
            column = i - d;
            break;
          }
        }
      }
      sm.setValueIsAdjusting(true);
      sm.clearHeaderSelection();
      if (extend) {
        sm.changeLeadSelection(focusedRow, column, GridSheetSelectionModel.CHANGE_ONLY_HORIZONTAL);
      } else {
        sm.setSelectionInterval(focusedRow, column, focusedRow, column);
      }
      table.scrollRectToVisible(focusedRow, column);
      sm.setValueIsAdjusting(false);

    }
  }
}
