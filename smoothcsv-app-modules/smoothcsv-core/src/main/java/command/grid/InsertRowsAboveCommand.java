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

import com.smoothcsv.commons.functions.IntRangeConsumer;
import com.smoothcsv.commons.utils.ArrayUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;

/**
 * @author kohii
 */
public class InsertRowsAboveCommand extends GridCommand {


  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    run(gridSheetPane, null);
  }

  public void run(CsvGridSheetPane gridSheetPane, int[] selectedRowIndices) {
    gridSheetPane.stopCellEditingIfEditing();
    ;

    GridSheetModel model = gridSheetPane.getModel();

    try (EditTransaction tran = gridSheetPane.transaction()) {

      if (selectedRowIndices == null) {
        selectedRowIndices = gridSheetPane.getSelectionModel().getSelectedRows();
      }

      ArrayUtils.processIntArrayAsBlock(new IntRangeConsumer() {
        @Override
        public void accept(int start, int end) {
          model.insertRow(start, end - start + 1);
        }
      }, selectedRowIndices, true);

      gridSheetPane.getSelectionModel().clearHeaderSelection();
    }
  }
}
