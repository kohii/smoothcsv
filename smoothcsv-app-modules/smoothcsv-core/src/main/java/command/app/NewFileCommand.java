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

import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.setting.Settings;

/**
 *
 * @author kohii
 */
public class NewFileCommand extends Command {

  @Override
  public void run() {
    Settings settings = CoreSettings.getInstance();
    int rows = settings.getInteger(CoreSettings.DEFAULT_ROW_SIZE);
    int columns = settings.getInteger(CoreSettings.DEFAULT_COLUMN_SIZE);
    CsvMeta csvMeta = CsvSheetSupport.getDefaultCsvMeta();
    run(rows, columns, csvMeta);
  }

  public void run(int rows, int columns, CsvMeta csvMeta) {
    boolean isAdjusting = SmoothComponentManager.isAdjusting();
    try {
      if (!isAdjusting) {
        SmoothComponentManager.startAdjustingComponents();
      }
      CsvSheetViewInfo viewInfo = new CsvSheetViewInfo(null, csvMeta, null);
      CsvGridSheetModel model = CsvSheetSupport.createModel(rows, columns, csvMeta);
      CsvSheetView csvGridSheetView = new CsvSheetView(viewInfo, model);
      SCApplication.components().getTabbedPane().addTab(csvGridSheetView);
    } finally {
      if (!isAdjusting) {
        SmoothComponentManager.stopAdjustingComponents();
      }
    }
  }
}
