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
package command.app;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.preference.PreferencesDialog;

/**
 *
 * @author kohii
 */
public class ShowSettingsCommand extends Command {

  @Override
  public void run() {
    CsvSheetView view = (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    if (view != null) {
      view.getGridSheetPane().stopCellEditingIfEditing();
    }
    PreferencesDialog dialog = new PreferencesDialog("Preferenes");
    dialog.setSize(700, 600);
    dialog.setLocationRelativeTo(SCApplication.components().getFrame());
    dialog.setVisible(true);
  }
}
