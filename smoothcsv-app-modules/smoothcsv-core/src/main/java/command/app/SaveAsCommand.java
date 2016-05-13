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
package command.app;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.command.CsvSheetCommandBase;
import com.smoothcsv.core.csvsheet.CsvFileChooser;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.framework.exception.AppException;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 * @author kohii
 */
public class SaveAsCommand extends CsvSheetCommandBase {

  @Override
  public void run(CsvSheetView view) {
    view.getGridSheetPane().stopCellEditingIfEditing();
    File file = chooseFile(view.getViewInfo());
    SaveCommand.save(view, file);
  }

  public static File chooseFile(CsvSheetViewInfo viewInfo) {

    CsvFileChooser fileChooser = CsvFileChooser.getInstance();

    File file = viewInfo.getFile();
    if (file == null) {
      String name = "untitled";
      switch (viewInfo.getCsvMeta().getDelimiter()) {
        case ',':
          name += ".csv";
          break;
        case '\t':
          name += ".tsv";
          break;
        default:
          name += ".txt";
          break;
      }
      file = new File(fileChooser.getCurrentDirectory(), name);
    }
    fileChooser.setSelectedFile(file);

    switch (fileChooser.showSaveDialog()) {
      case JFileChooser.APPROVE_OPTION:
        file = fileChooser.getSelectedFile();
        if (file.exists() && !file.canWrite()) {
          try {
            throw new AppException("WSCA0002", file.getCanonicalPath());
          } catch (IOException e) {
            throw new UnexpectedException();
          }
        }
        return file;
      case JFileChooser.CANCEL_OPTION:
        throw new CancellationException();
      default:
        throw new UnexpectedException();
    }
  }
}
