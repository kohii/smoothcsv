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
package command.macrolist;

import java.io.File;

import javax.swing.JFileChooser;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.command.VisibleComponentCommandBase;
import com.smoothcsv.core.macro.component.JsFileChooser;
import com.smoothcsv.core.macro.component.MacroListPanel;
import com.smoothcsv.framework.component.dialog.BasicFileChooser;

/**
 * @author kohii
 *
 */
public class AddCommand extends VisibleComponentCommandBase<MacroListPanel> {

  /**
   * @param cssSelector
   */
  public AddCommand() {
    super("macro-list");
  }

  @Override
  public void run(MacroListPanel component) {
    BasicFileChooser fileChooser = JsFileChooser.getInstance();
    try {
      fileChooser.setMultiSelectionEnabled(true);
      switch (fileChooser.showOpenDialog()) {
        case JFileChooser.APPROVE_OPTION:
          File[] files = fileChooser.getSelectedFiles();
          component.addMacroFiles(files);
        case JFileChooser.CANCEL_OPTION:
          throw new CancellationException();
        default:
          throw new UnexpectedException();
      }
    } finally {
      fileChooser.setMultiSelectionEnabled(false);
    }
  }
}
