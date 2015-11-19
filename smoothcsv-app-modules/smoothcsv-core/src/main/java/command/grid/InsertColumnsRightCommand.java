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
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.Transaction;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;

/**
 * @author kohii
 *
 */
public class InsertColumnsRightCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    run(gridSheetPane, null);
  }

  public void run(CsvGridSheetPane gridSheetPane, int[] selectedColumnIndices) {
    gridSheetPane.stopCellEditingIfEditing();;

    GridSheetModel model = gridSheetPane.getModel();

    try (Transaction tran = gridSheetPane.transaction()) {
      if (selectedColumnIndices == null) {
        selectedColumnIndices = gridSheetPane.getSelectionModel().getSelectedColumns();
      }

      ArrayUtils.processIntArrayAsBlock(new IntRangeConsumer() {
        @Override
        public void accept(int start, int end) {
          model.insertColumn(start + 1, end - start + 1);
        }
      }, selectedColumnIndices, true);

      gridSheetPane.getSelectionModel().clearHeaderSelection();
    }
  }
}
