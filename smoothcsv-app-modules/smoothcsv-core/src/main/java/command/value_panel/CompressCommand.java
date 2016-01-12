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
package command.value_panel;

import com.smoothcsv.core.command.ValuePanelCommandBase;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.setting.Settings;

/**
 * @author kohii
 *
 */
public class CompressCommand extends ValuePanelCommandBase {

  @Override
  protected void run(CsvGridSheetCellValuePanel valuePanel) {
    Settings setting = CoreSettings.getInstance();
    int h = setting.getInteger(CoreSettings.VALUE_PANEL_HEIGHT);
    h--;
    if (h < 1) {
      abort();
    }
    setting.save(CoreSettings.VALUE_PANEL_HEIGHT, h);
    valuePanel.reloadPanelHeight();
  }
}
