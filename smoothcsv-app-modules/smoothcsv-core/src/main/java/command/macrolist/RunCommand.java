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
package command.macrolist;

import com.smoothcsv.core.command.VisibleComponentCommandBase;
import com.smoothcsv.core.macro.Macro;
import com.smoothcsv.core.macro.SCAppMacroRuntime;
import com.smoothcsv.core.macro.component.MacroListPanel;
import com.smoothcsv.framework.exception.AbortionException;

import java.io.File;

/**
 * @author kohii
 */
public class RunCommand extends VisibleComponentCommandBase<MacroListPanel> {

  public RunCommand() {
    super("macro-list");
  }

  @Override
  public void run(MacroListPanel component) {
    File macroFile = component.getSelectedMacroFile();
    if (macroFile == null) {
      throw new AbortionException();
    }
    SCAppMacroRuntime.getMacroRuntime().execute(new Macro(macroFile));
  }
}
