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

import com.smoothcsv.core.component.CsvPropertiesDialog;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.framework.util.SCBundle;

/**
 *
 * @author kohii
 */
public class NewFileAsCommand extends Command {

  @Override
  public void run() {
    CsvPropertiesDialog dialog =
        new CsvPropertiesDialog(SCApplication.components().getFrame(), "Open", false, false, true);
    dialog.setMessage(SCBundle.get("key.newFileAs.message"));

    Settings settings = SettingManager.getSettings(AppSettingKeys.File.$);
    String r = settings.get(AppSettingKeys.File.DEFAULT_ROW_SIZE, "5");
    String c = settings.get(AppSettingKeys.File.DEFAULT_COLUMN_SIZE, "5");
    dialog.setGirdSize(Integer.parseInt(r), Integer.parseInt(c));
    dialog.setCsvProperties(CsvSheetSupport.getDefaultCsvMeta());

    dialog.pack();
    if (dialog.showDialog() == DialogOperation.OK) {
      int row = dialog.getRowCount();
      int column = dialog.getColumnCount();
      CsvMeta csvMeta = dialog.getCsvMeta();
      new NewFileCommand().run(row, column, csvMeta);
    }
  }
}
