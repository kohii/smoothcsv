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
public class DeleteColumnsCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    run(gridSheetPane, null);
  }

  public void run(CsvGridSheetPane gridSheetPane, int[] selectedColumnIndices) {

    gridSheetPane.stopCellEditingIfEditing();;

    if (selectedColumnIndices == null) {
      selectedColumnIndices = gridSheetPane.getSelectionModel().getSelectedColumns();
    }

    try (Transaction tran = gridSheetPane.transaction()) {
      CsvGridSheetModel model = gridSheetPane.getModel();

      ArrayUtils.processIntArrayAsBlock(new IntRangeConsumer() {
        @Override
        public void accept(int start, int end) {
          model.deleteColumn(start, end - start + 1);
        }
      }, selectedColumnIndices, true);

      if (model.getColumnCount() == 0) {
        // insert empty column if there is no column
        model.insertColumn(0, 1);
        for (int i = 0; i < model.getRowCount(); i++) {
          model.deleteCell(i, 0, 0);
        }
      }

      DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      sm.clearHeaderSelection();
      sm.correctSelectionIfInvalid();
    }
  }
}
