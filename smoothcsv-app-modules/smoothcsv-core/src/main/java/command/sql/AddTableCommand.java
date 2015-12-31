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
package command.sql;

import java.io.File;

import javax.swing.JFileChooser;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.command.SqlTableListCommandBase;
import com.smoothcsv.core.csvsheet.CsvFileChooser;
import com.smoothcsv.core.sql.component.SqlTableList;
import com.smoothcsv.core.sql.model.SqlCsvFileTables;
import com.smoothcsv.framework.exception.AppException;

/**
 * @author kohii
 *
 */
public class AddTableCommand extends SqlTableListCommandBase {

  @Override
  public void run(SqlTableList component) {
    File file = chooseFile();
    if (!SqlCsvFileTables.getInstance().contains(file)) {
      SqlCsvFileTables.getInstance().addTable(file);
      component.reloadCsvFileTables();
    }
  }

  private File chooseFile() {
    CsvFileChooser fileChooser = CsvFileChooser.getInstance();
    switch (fileChooser.showOpenDialog()) {
      case JFileChooser.APPROVE_OPTION:
        File file = fileChooser.getSelectedFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) {
          throw new AppException("WSCC0001", file);
        }
        return file;
      case JFileChooser.CANCEL_OPTION:
        throw new CancellationException();
      default:
        throw new UnexpectedException();
    }
  }
}
