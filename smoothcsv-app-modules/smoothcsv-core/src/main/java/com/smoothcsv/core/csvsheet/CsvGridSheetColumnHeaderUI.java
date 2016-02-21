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
package com.smoothcsv.core.csvsheet;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.ui.GridSheetColumnHeaderUI;

/**
 * @author kohii
 *
 */
public class CsvGridSheetColumnHeaderUI extends GridSheetColumnHeaderUI {

  public static ComponentUI createUI(JComponent c) {
    return new CsvGridSheetColumnHeaderUI();
  }

  @Override
  protected void autoFitColumnWidth(GridSheetPane gridSheetPane, int columnIndex) {
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
    GridSheetUtils.sizeWidthToFit(gridSheetPane, columnIndex, maxWidth, rowNumsToScan);
  }
}
