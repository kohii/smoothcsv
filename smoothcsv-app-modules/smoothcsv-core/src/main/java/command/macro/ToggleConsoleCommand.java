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

import com.smoothcsv.core.command.VisibleComponentCommandBase;
import com.smoothcsv.core.macro.component.MacroToolsPanel;

/**
 * @author kohii
 */
public class ToggleConsoleCommand extends VisibleComponentCommandBase<MacroToolsPanel> {

  /**
   * @param cssSelector
   */
  public ToggleConsoleCommand() {
    super("macro-tools");
  }

  @Override
  public void run(MacroToolsPanel component) {
    component.setConsoleAlwaysVisible(!component.isConsoleAlwaysVisible());
  }
}
