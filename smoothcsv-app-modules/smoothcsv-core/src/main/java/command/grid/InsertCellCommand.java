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

import java.util.Arrays;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;

/**
 * @author kohii
 */
public class InsertCellCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getTable().stopCellEditing();

    try (EditTransaction tran = gridSheetPane.transaction()) {
      CsvGridSheetModel model = (CsvGridSheetModel) gridSheetPane.getModel();
      DefaultGridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
      selectionModel.forEachSelectedRows((rowIndex) -> {
        selectionModel.forEachSelectedColumnsAsBlock(rowIndex,
            (fromColumnIndex, toColumnIndex) -> {
              while (gridSheetPane.getValueAt(rowIndex, toColumnIndex) == null) {
                toColumnIndex--;
                if (toColumnIndex < fromColumnIndex) {
                  return;
                }
              }
              String[] data = new String[toColumnIndex - fromColumnIndex + 1];
              Arrays.fill(data, "");
              model.insertCell(rowIndex, fromColumnIndex, data);
            }, false);
      });
    }
  }
}
