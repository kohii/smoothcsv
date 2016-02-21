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
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class AutofitColumnWidthToSelectedCellCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getTable().stopCellEditing();

    Settings editorSettings = CoreSettings.getInstance();

    int maxWidth;
    if (editorSettings.getBoolean(CoreSettings.LIMIT_WIDTH_WHEN_AUTO_FITTING)) {
      int percentage =
          editorSettings
              .getInteger(CoreSettings.MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING);
      maxWidth = SCApplication.components().getFrame().getWidth() * percentage / 100;
    } else {
      maxWidth = Integer.MAX_VALUE;
    }

    int rowNumsToScan;
    if (editorSettings
        .getBoolean(CoreSettings.AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE)) {
      rowNumsToScan =
          editorSettings.getInteger(CoreSettings.ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING);
    } else {
      rowNumsToScan = Integer.MAX_VALUE;
    }

    DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    sm.forEachSelectedColumns(columnIndex -> {
      GridSheetUtils.sizeWidthToFitSelectedCells(gridSheetPane, columnIndex, maxWidth,
          rowNumsToScan);
    });
  }
}
