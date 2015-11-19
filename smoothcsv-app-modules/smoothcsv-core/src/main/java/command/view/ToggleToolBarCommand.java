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
package command.view;

import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.framework.setting.Settings;

/**
 * @author kohii
 *
 */
public class ToggleToolBarCommand extends Command {

  @Override
  public void run() {
    Settings settings = SettingManager.getSettings(AppSettingKeys.Core.$);
    boolean oldVal = settings.getBoolean(AppSettingKeys.Core.TOOLBAR_VISIBLE);
    boolean newVal = !oldVal;
    settings.save(AppSettingKeys.Core.TOOLBAR_VISIBLE, newVal);
    SCApplication.components().getToolBar().setVisible(newVal);
  }
}
