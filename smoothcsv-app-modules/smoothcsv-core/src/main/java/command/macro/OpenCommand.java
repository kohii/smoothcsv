/*
 * Copyright 2015 kohii.
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

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.component.SmoothCsvComponentManager;
import com.smoothcsv.core.macro.component.JsFileChooser;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.dialog.BasicFileChooser;
import com.smoothcsv.framework.exception.AppException;

/**
 * @author kohii
 *
 */
public class OpenCommand extends Command {

  @Override
  public void run() {
    BasicFileChooser fileChooser = JsFileChooser.getInstance();
    switch (fileChooser.showOpenDialog()) {
      case JFileChooser.APPROVE_OPTION:
        File file = fileChooser.getSelectedFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) {
          throw new AppException("WSCC0001", file);
        }
        try {
          SmoothCsvComponentManager componentManager =
              (SmoothCsvComponentManager) SCApplication.components();
          componentManager.getMacroTools().getMacroEditor().getTextArea()
              .setText(FileUtils.readAll(file, "UTF-8"));
        } catch (IOException e) {
          throw new UnexpectedException(e);
        }
      case JFileChooser.CANCEL_OPTION:
        throw new CancellationException();
      default:
        throw new UnexpectedException();
    }
  }
}
