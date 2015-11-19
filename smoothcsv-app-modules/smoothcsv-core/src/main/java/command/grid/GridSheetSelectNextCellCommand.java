/**
 * 
 */
package command.grid;

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
public class GridSheetSelectNextCellCommand extends GridCommand {

  public static final int PREVIOUS = -1;
  public static final int NEXT = +1;

  protected final int dx;
  protected final int dy;

  public GridSheetSelectNextCellCommand(int dx, int dy) {

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

  protected void changeSelection(CsvGridSheetPane gridSheetPane, GridSheetSelectionModel sm,
      int dx, int dy) {
    GridSheetTable table = gridSheetPane.getTable();

    boolean stayInSelection = !sm.isSingleCellSelected();
    GridSheetUtils.moveAnchor(table.getGridSheetPane(), dx != 0 ? Orientation.HORIZONTAL
        : Orientation.VERTICAL, dx < 0 || dy < 0, stayInSelection, true);
  }
}
