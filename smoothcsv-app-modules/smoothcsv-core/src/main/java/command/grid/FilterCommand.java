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

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.core.filter.FilterConditionGroup;
import com.smoothcsv.core.filter.FilterConditions;
import com.smoothcsv.core.filter.FilterDialog;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import command.app.OpenFileCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kohii
 */
public class FilterCommand extends GridCommand {

  private static FilterDialog filterDialog = new FilterDialog();

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {

    filterDialog.setVisible(true);
    if (filterDialog.getSelectedOperation() != DialogOperation.OK) {
      return;
    }
    FilterConditions conditions = filterDialog.getFilterConditions();
    int ope = conditions.getOperation();
    FilterConditionGroup cond = conditions.getCondition();

    gridSheetPane.stopCellEditingIfEditing();

    CsvGridSheetModel model = gridSheetPane.getModel();
    int rc = model.getRowCount();

    if (ope == FilterConditions.FILTER_OPERATION_DELETE_UNMATCH
        || ope == FilterConditions.FILTER_OPERATION_DELETE_MATCH) {
      // Delete rows

      try (EditTransaction tran = gridSheetPane.transaction()) {
        int numDeleted = 0;
        for (int i = 0; i < rc; i++) {
          @SuppressWarnings("unchecked")
          boolean match = cond.matches(model.getRowDataAt(i - numDeleted));
          if (ope == FilterConditions.FILTER_OPERATION_DELETE_UNMATCH && !match
              || ope == FilterConditions.FILTER_OPERATION_DELETE_MATCH && match) {
            model.deleteRow(i - numDeleted);
            numDeleted++;
          }
        }
        if (model.getRowCount() == 0) {
          // insert empty row if there is no row
          model.insertRow(0, 1);
          model.deleteCell(0, 0, model.getColumnCount() - 1);
        }

        DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
        sm.clearHeaderSelection();
        sm.correctSelectionIfInvalid();
      }
    } else {
      // Open in new tab

      List<List> dataList = new ArrayList<>();
      for (int i = rc - 1; i >= 0; i--) {
        @SuppressWarnings("unchecked")
        boolean match = cond.matches(model.getRowDataAt(i));
        if (ope == FilterConditions.FILTER_OPERATION_NEW_TAB_UNMATCH && !match
            || ope == FilterConditions.FILTER_OPERATION_NEW_TAB_MATCH && match) {
          dataList.add(model.getRowDataAt(i));
        }
      }

      if (dataList.isEmpty()) {
        MessageDialogs.alert("ISCA0010");
        return;
      }

      CsvSheetViewInfo viewInfo =
          new CsvSheetViewInfo(null, CsvSheetSupport.getDefaultCsvMeta(), null);
      CsvGridSheetModel csvSheetModel = new CsvGridSheetModel(dataList);
      new OpenFileCommand().run(viewInfo, csvSheetModel);
    }
  }
}
