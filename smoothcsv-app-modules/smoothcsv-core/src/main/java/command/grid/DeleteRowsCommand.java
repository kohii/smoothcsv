/*
 * Copyright 2014 kohii.
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
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.Transaction;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class DeleteRowsCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    run(gridSheetPane, null);
  }

  public void run(CsvGridSheetPane gridSheetPane, int[] selectedRowIndices) {

    gridSheetPane.stopCellEditingIfEditing();

    try (Transaction tran = gridSheetPane.transaction()) {

      CsvGridSheetModel model = gridSheetPane.getModel();

      if (selectedRowIndices == null) {
        selectedRowIndices = gridSheetPane.getSelectionModel().getSelectedRows();
      }

      ArrayUtils.processIntArrayAsBlock(new IntRangeConsumer() {
        @Override
        public void accept(int start, int end) {
          model.deleteRow(start, end - start + 1);
        }
      }, selectedRowIndices, true);

      if (model.getRowCount() == 0) {
        // insert empty row if there is no row
        model.insertRow(0, 1);
        model.deleteCell(0, 0, model.getColumnCount() - 1);
      }

      DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      sm.clearHeaderSelection();
      sm.correctSelectionIfInvalid();
    }
  }
}
