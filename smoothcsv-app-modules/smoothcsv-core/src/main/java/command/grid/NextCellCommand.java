/**
 *
 */
package command.grid;

import java.util.Map;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class NextCellCommand extends GridCommand {

  public static final int PREVIOUS = -1;
  public static final int NEXT = +1;

  protected final int dx;
  protected final int dy;

  public NextCellCommand(Map<String, Object> options) {
    this(getDirectionX(options), getDirectionY(options));
  }

  private NextCellCommand(int dx, int dy) {

    assert (-1 <= dx && dx <= 1 && -1 <= dy && dy <= 1);

    // make sure one is zero, but not both
    assert (dx == 0 || dy == 0) && !(dx == 0 && dy == 0);

    this.dx = dx;
    this.dy = dy;
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
    changeSelection(gridSheetPane, sm, dx, dy);
  }

  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm, int dx,
      int dy) {
    GridSheetTable table = gridSheetPane.getTable();

    boolean stayInSelection = !sm.isSingleCellSelected();
    GridSheetUtils.moveAnchor(table.getGridSheetPane(),
        dx != 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL, dx < 0 || dy < 0, stayInSelection,
        true);
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
}
