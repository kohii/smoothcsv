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
package command.find;

import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.Transaction;
import com.smoothcsv.core.find.FindAndReplaceMatcher;
import com.smoothcsv.core.find.FindAndReplacePanel;
import com.smoothcsv.core.find.FindAndReplaceParams;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.swing.gridsheet.CellIterator;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 *
 */
public class ReplaceAllCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    if (!ApplicationStatus.getInstance().isFindAndReplacePanelVisible()) {
      return;
    }

    int replaced = replaceAll(gridSheetPane);
    if (replaced != 0) {
      SCApplication
          .components()
          .getStatusBar()
          .showTemporaryMessage(
              MessageBundles.getString("ISCA0005" + (replaced <= 1 ? "" : "P"), replaced));
    } else {
      SwingUtils.beep();
      SCApplication.components().getStatusBar()
          .showTemporaryMessage(MessageBundles.getString("ISCA0006"));
    }
  }

  int replaceAll(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getTable().stopCellEditing();

    FindAndReplacePanel.getInstance().saveFindWhatText();
    FindAndReplacePanel.getInstance().saveReplaceText();

    int replaced = 0;

    FindAndReplaceParams params = FindAndReplaceParams.getInstance();
    CellIterator itr =
        new CellIterator(gridSheetPane, params.isInSelection()
            && params.isInSelectionCheckboxEnabled(), true, false, params.getOrientation(), true);

    try (Transaction tran = gridSheetPane.transaction()) {

      do {
        int row = itr.getRow();
        int column = itr.getColumn();
        Object val = gridSheetPane.getValueAt(row, column);
        if (val != null) {
          boolean matches = FindAndReplaceMatcher.matches(val.toString(), params);
          if (matches) {
            gridSheetPane.setValueAt(FindAndReplaceMatcher.replace(val.toString(), params), row,
                column);
            replaced++;
          }
        }

      } while (itr.next());

      if (replaced == 0) {
        tran.rollback();
      }
    }

    return replaced;
  }
}
