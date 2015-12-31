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
package command.app;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCTabbedPane;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.component.support.SmoothComponentManager;

/**
 * @author kohii
 *
 */
public class CloseCommand extends Command {

  @Override
  public void run() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    BaseTabView<?> view = tabbedPane.getSelectedView();
    close(view);
  }

  public static void close(BaseTabView<?> view) {
    close(view, true);
  }

  public static void close(BaseTabView<?> view, boolean askToSave) {
    CsvSheetView csvSheetView = (CsvSheetView) view;
    csvSheetView.getGridSheetPane().getTable().stopCellEditing();
    if (askToSave && !csvSheetView.getGridSheetPane().getUndoManager().isSavepoint()) {

      DialogOperation option =
          MessageDialogs.confirm2("ISCA0002", csvSheetView.getViewInfo().getShortTitle());
      if (option == DialogOperation.YES) {
        new SaveCommand().execute();
      } else if (option == DialogOperation.NO) {
        // do nothing
      } else if (option == DialogOperation.CANCEL) {
        throw new CancellationException();
      } else {
        throw new UnexpectedException();
      }
    }
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    try {
      SmoothComponentManager.startAdjustingComponents();
      tabbedPane.remove(csvSheetView);
    } finally {
      SmoothComponentManager.stopAdjustingComponents();
    }
  }
}
