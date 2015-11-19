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
package command.find;

import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
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
public class ReplaceNextCommand extends GridCommand {

  protected boolean reverse = false;

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    if (!ApplicationStatus.getInstance().isFindAndReplacePanelVisible()) {
      return;
    }

    boolean found = replaceNext(gridSheetPane);
    if (found) {
      SCApplication.components().getStatusBar()
          .showTemporaryMessage(MessageBundles.getString("ISCA0005", 1));
    } else {
      SwingUtils.beep();
      SCApplication.components().getStatusBar()
          .showTemporaryMessage(MessageBundles.getString("ISCA0006"));
    }
    FindAndReplacePanel.getInstance().initFocus();
  }

  boolean replaceNext(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getTable().stopCellEditing();

    FindAndReplacePanel.getInstance().saveFindWhatText();
    FindAndReplacePanel.getInstance().saveReplaceText();

    FindAndReplaceParams params = FindAndReplaceParams.getInstance();

    CellIterator itr =
        new CellIterator(gridSheetPane, params.isInSelection()
            && params.isInSelectionCheckboxEnabled(), true, reverse, params.getOrientation(), false);

    boolean replaced = false;
    boolean found = false;

    do {
      Object val = gridSheetPane.getValueAt(itr.getRow(), itr.getColumn());
      if (val != null) {
        boolean matches = FindAndReplaceMatcher.matches(val.toString(), params);
        if (matches) {
          if (replaced) {
            if (params.isInSelection() && params.isInSelectionCheckboxEnabled()) {
              gridSheetPane.getSelectionModel().updateAnchor(itr.getRow(), itr.getColumn());
            } else {
              gridSheetPane.getSelectionModel().setSelectionInterval(itr.getRow(), itr.getColumn(),
                  itr.getRow(), itr.getColumn());
            }
            found = true;
            break;
          } else {
            gridSheetPane.setValueAt(FindAndReplaceMatcher.replace(val.toString(), params),
                itr.getRow(), itr.getColumn());
            replaced = true;
          }
        }
      }

    } while (itr.next());

    return found;
  }
}
