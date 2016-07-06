package command.grid;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ishikawa kohei
 */
public class RemoveDuplicateCommand extends GridCommand {
  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
    int minR = selectionModel.getMinRowSelectionIndex();
    int maxR = selectionModel.getMaxRowSelectionIndex();
    int minC = selectionModel.getMinColumnSelectionIndex();
    int maxC = selectionModel.getMaxColumnSelectionIndex();

    List<List<Object>> lines = new ArrayList<>();

    try (EditTransaction tran = gridSheetPane.transaction()) {

      for (int rowIndex = minR; rowIndex <= maxR; rowIndex++) {
        if (!selectionModel.isRowSelected(rowIndex)) {
          continue;
        }
        List<Object> line = new ArrayList<>(maxC - minC + 1);
        for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
          if (!selectionModel.isCellSelected(rowIndex, columnIndex)) {
            continue;
          }
          Object value = gridSheetPane.getValueAt(rowIndex, columnIndex);
          if (value == null) {
            break;
          }
          line.add(value);
        }
        if (contains(lines, line)) {
          for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
            if (!selectionModel.isCellSelected(rowIndex, columnIndex)) {
              continue;
            }
            gridSheetPane.setValueAt("", rowIndex, columnIndex);
          }
        } else {
          lines.add(line);
        }
      }
    }
  }

  private static boolean contains(List<List<Object>> lines, List<Object> line) {
    for (List<Object> l : lines) {
      if (l.equals(line)) {
        return true;
      }
    }
    return false;
  }
}
