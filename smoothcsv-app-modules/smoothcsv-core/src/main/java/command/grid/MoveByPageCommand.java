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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;

import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class MoveByPageCommand extends GridSheetSelectCommand {

  public static final int SCROLL_DOWN = -1;
  public static final int SCROLL_UP = +1;


  public MoveByPageCommand(Map<String, Object> options) {
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

  private MoveByPageCommand(int dx, int dy, boolean extend) {
    super(dx, dy, extend);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.smoothcsv.core.command.gridsheet.GridSheetSelectCommand#changeSelection(com.smoothcsv.core
   * .component.csvgridsheet.CsvGridSheetPane,
   * com.smoothcsv.swing.components.gridsheet.model.GridSheetSelectionModel, int, int, boolean, int,
   * int)
   */
  @Override
  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm, int dx,
      int dy, boolean extend, int anchorRow, int anchorColumn) {
    GridSheetTable table = gridSheetPane.getTable();

    Dimension delta = table.getParent().getParent().getSize();

    if (dy != 0) { // vertically
      Rectangle r = table.getCellRect(anchorRow, 0, true);
      if (dy > 0) { // forwards
        // scroll by at least one cell
        r.y += Math.max(delta.height, r.height);
      } else {
        r.y -= delta.height;
      }

      dx = 0;
      int newRow = gridSheetPane.rowAtPoint(r.getLocation());
      if (newRow == -1 && dy > 0) {
        newRow = gridSheetPane.getRowCount();
      }
      dy = newRow - anchorRow;
    } else { // horizontally
      Rectangle r = table.getCellRect(0, anchorColumn, true);

      if (dx > 0) {// forwards
        // scroll by at least one cell
        r.x += Math.max(delta.width, r.width);
      } else {
        r.x -= delta.width;
      }

      int newColumn = gridSheetPane.columnAtPoint(r.getLocation());
      if (newColumn == -1) {
        boolean ltr = table.getComponentOrientation().isLeftToRight();

        newColumn = dx > 0 ? (ltr ? gridSheetPane.getColumnCount() : 0)
            : (ltr ? 0 : gridSheetPane.getColumnCount());

      }
      dx = newColumn - anchorColumn;
      dy = 0;
    }
    super.changeSelection(gridSheetPane, sm, dx, dy, extend, anchorRow, anchorColumn);
  }
}
