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
package command.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.command.CsvSheetCommandBase;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.SmoothCsvWriter;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;

/**
 * @author kohii
 *
 */
public class SaveCommand extends CsvSheetCommandBase {

  @Override
  public void run(CsvSheetView view) {
    File file = view.getViewInfo().getFile();
    if (file == null || !file.canWrite()) {
      new SaveAsCommand().run(view);
    } else {
      save(view, file);
    }
  }

  public static void save(CsvSheetView view, File file) {
    view.getGridSheetPane().stopCellEditingIfEditing();
    CsvMeta csvMeta = view.getViewInfo().getCsvMeta();
    try (SmoothCsvWriter writer =
        new SmoothCsvWriter(
            new OutputStreamWriter(new FileOutputStream(file), csvMeta.getCharset()), csvMeta)) {
      GridSheetModel model = view.getGridSheetPane().getModel();
      int rowCount = model.getRowCount();
      if (rowCount > 0) {
        int columnCount = model.getColumnCount();
        for (int rowIndex = 0; rowIndex < rowCount - 1; rowIndex++) {
          writer.writeRow(model.getRowDataAt(rowIndex));
        }
        writer.setWriteLineSeparater(false);
        writer.writeRow(model.getRowDataAt(rowCount - 1));
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
    view.getViewInfo().setFile(file);
    view.getGridSheetPane().getUndoManager().save();
  }
}
