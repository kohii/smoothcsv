/**
 * 
 */
package command.grid;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class GridSheetSelectEdgeCommand extends GridCommand {

  public static final int PREVIOUS = -1;
  public static final int NEXT = +1;

  protected final int dx;
  protected final int dy;
  protected final boolean extend;

  public GridSheetSelectEdgeCommand(String commandId, int dx, int dy, boolean extend) {

    assert (-1 <= dx && dx <= 1 && -1 <= dy && dy <= 1);

    // make sure one is zero, but not both
    assert (dx == 0 || dy == 0) && !(dx == 0 && dy == 0);

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

  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm,
      int dx, int dy, boolean extend, int anchorRow, int anchorColumn) {
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
