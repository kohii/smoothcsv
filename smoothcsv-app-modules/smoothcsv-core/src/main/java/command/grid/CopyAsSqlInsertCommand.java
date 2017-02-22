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
package command.grid;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.swing.utils.ClipboardUtils;

import java.util.List;

/**
 * @author kohii
 */
public class CopyAsSqlInsertCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {

    if (gridSheetPane.getSelectionModel().isAdditionallySelected()) {
      throw new AppException("WSCA0004");
    }

    List<List<Object>> data = CopyAsHtmlTableCommand.getSelectedData(gridSheetPane);

    final String lineSep = gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta()
        .getNewlineCharacter().stringValue();
    final String indent = "  ";

    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO `").append(gridSheetPane.getCsvSheetView().getViewInfo().getShortTitle()).append("` ");

    boolean hasHeaderRow = data.size() > 1;
    if (hasHeaderRow) {
      sb.append('(');
      List<Object> headerRowData = data.get(0);
      for (int i = 0; i < headerRowData.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append('`').append(headerRowData.get(i)).append('`');
      }
      sb.append(')');
    }
    sb.append(lineSep);
    sb.append(indent).append("VALUES").append(lineSep);

    boolean isFirstRow = true;
    for (int i = hasHeaderRow ? 1 : 0; i < data.size(); i++) {
      List<Object> rowData = data.get(i);

      if (isFirstRow) {
        isFirstRow = false;
      } else {
        sb.append(",").append(lineSep);
      }

      sb.append(indent).append('(');
      for (int j = 0; j < rowData.size(); j++) {
        if (j > 0) {
          sb.append(", ");
        }
        sb.append('\'').append(rowData.get(j)).append('\'');
      }
      sb.append(')');
    }
    ClipboardUtils.writeText(sb.toString());
  }
}
