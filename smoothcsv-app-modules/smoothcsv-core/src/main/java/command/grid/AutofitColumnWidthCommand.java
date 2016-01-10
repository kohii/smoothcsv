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
package command.grid;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;

/**
 * @author kohii
 *
 */
public class AutofitColumnWidthCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    run(gridSheetPane, 0, gridSheetPane.getColumnCount() - 1);
  }

  public static void run(CsvGridSheetPane gridSheetPane, int columnIndexFrom, int columnIndexTo) {
    gridSheetPane.getTable().stopCellEditing();

    Settings settings = CoreSettings.getInstance();

    int maxWidth;
    if (settings.getBoolean(CoreSettings.LIMIT_WIDTH_WHEN_AUTO_FITTING)) {
      int percentage =
          settings.getInteger(CoreSettings.MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING);
      maxWidth = SCApplication.components().getFrame().getWidth() * percentage / 100;
    } else {
      maxWidth = Integer.MAX_VALUE;
    }

    int rowNumsToScan;
    if (settings.getBoolean(CoreSettings.AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE)) {
      rowNumsToScan = settings.getInteger(CoreSettings.ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING);
    } else {
      rowNumsToScan = Integer.MAX_VALUE;
    }

    // if (onlySelectedColumns) {
    // GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    // sm.forEachSelectedColumns(columnIndex -> {
    // // TODO
    // GridSheetUtils.sizeWidthToFit(gridSheetPane, columnIndex, 1000, Integer.MAX_VALUE);
    // });
    // } else {
    for (int columnIndex = columnIndexFrom; columnIndex <= columnIndexTo; columnIndex++) {
      // TODO
      GridSheetUtils.sizeWidthToFit(gridSheetPane, columnIndex, maxWidth, rowNumsToScan);
    }
    // }
  }
}
