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

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class FillUpCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.stopCellEditingIfEditing();;

    GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
    int minR = selectionModel.getMinRowSelectionIndex();
    int maxR = selectionModel.getMaxRowSelectionIndex();

    // gridSheetPane.getEditManager().startMultiCellEdit();
    gridSheetPane.getSelectionModel().forEachSelectedColumns((column) -> {
      Object val = null;
      boolean first = true;
      for (int row = maxR; minR <= row; row--) {
        if (selectionModel.isCellSelected(row, column)) {
          if (first) {
            val = gridSheetPane.getValueAt(row, column);
            first = false;
          } else {
            gridSheetPane.setValueAt(val, row, column);
          }
        }
      }
    });
    // gridSheetPane.getEditManager().stopMultiCellEdit();
  }

}
