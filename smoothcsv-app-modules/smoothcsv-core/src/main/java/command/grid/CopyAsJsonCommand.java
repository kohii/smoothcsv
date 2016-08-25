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
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author kohii
 */
public class CopyAsJsonCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {

    if (gridSheetPane.getSelectionModel().isAdditionallySelected()) {
      throw new AppException("WSCA0004");
    }

    List<List<Object>> data = CopyAsHtmlTableCommand.getSelectedData(gridSheetPane);

    final String lineSep = gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta()
        .getNewlineCharacter().stringValue();

    List<Object> headerData = data.get(0);

    final String indent = "  ";

    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = 1; i < data.size(); i++) {
      if (i > 1) {
        sb.append(", ");
      }

      List<Object> rowData = data.get(i);

      sb.append('{').append(lineSep);
      for (int j = 0; j < headerData.size(); j++) {
        if (j > 0) {
          sb.append(',').append(lineSep);
        }
        sb.append(indent);
        if (headerData.get(j) != null) {
          sb.append('"').append(escapeJson(headerData.get(j).toString())).append('"');
        } else {
          sb.append("null");
        }
        sb.append(": ");
        if (rowData.get(j) != null) {
          String value = rowData.get(j).toString();
          if (com.smoothcsv.commons.utils.StringUtils.isDecimal(value)) {
            sb.append(value);
          } else {
            sb.append('"').append(escapeJson(value)).append('"');
          }
        } else {
          sb.append("null");
        }
      }
      sb.append(lineSep).append('}');
    }
    sb.append(']');

    ClipboardUtils.writeText(sb.toString());
  }

  private static String escapeJson(String s) {
    return StringUtils.replace(s, "\"", "\\\"");
  }
}
