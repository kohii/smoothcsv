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
package command.csvsheet;

import com.smoothcsv.core.component.CsvPropertiesDialog;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.core.csvsheet.edits.ChangePropertyEdit;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.SCBundle;

/**
 * @author kohii
 */
public class ShowPropertiesCommand extends Command {

  @Override
  public void run() {
    CsvSheetView view = (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    if (view != null) {
      view.getGridSheetPane().getTable().stopCellEditing();
    }
    CsvSheetViewInfo viewInfo = view.getViewInfo();
    CsvPropertiesDialog propDialog = new CsvPropertiesDialog(SCApplication.components().getFrame(),
        SCBundle.get("key.properties"), false, false, false);
    propDialog.setCsvProperties(viewInfo.getCsvMeta());
    DialogOperation opt = propDialog.showDialog();
    if (opt == DialogOperation.OK) {
      CsvMeta newCsvMeta = propDialog.getCsvMeta();
      if (!viewInfo.getCsvMeta().equals(newCsvMeta)) {
        view.getGridSheetPane().getUndoManager().put(new ChangePropertyEdit(viewInfo.getCsvMeta(), newCsvMeta));
      }
      viewInfo.setCsvMeta(newCsvMeta);
      view.repaint();
    } else if (opt == DialogOperation.CANCEL) {
      // do nothing
    } else {
      throw new IllegalStateException(opt.toString());
    }
  }
}
