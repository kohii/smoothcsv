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
package command.macro;

import com.smoothcsv.core.component.SmoothCsvComponentManager;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.support.SmoothComponentManager;

/**
 * @author kohii
 *
 */
public class ToggleMacroToolsCommand extends Command {

  @Override
  public void run() {
    try {
      SmoothComponentManager.startAdjustingComponents();
      SmoothCsvComponentManager componentManager =
          (SmoothCsvComponentManager) SCApplication.components();
      componentManager.setMacroToolsVisible(!componentManager.isMacroToolsVisible());
    } finally {
      SmoothComponentManager.stopAdjustingComponents();
    }
  }
}
