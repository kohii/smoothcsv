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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.utils.ClipboardUtils;

/**
 * @author kohii
 *
 */
public class CopyAsHtmlTableCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    List<List<Object>> data = getSelectedData(gridSheetPane);

    final String lineSep = gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta()
        .getNewlineCharacter().stringValue();
    final String indent = "\t";

    StringBuilder sb = new StringBuilder();
    sb.append("<table>").append(lineSep);
    for (int i = 0; i < data.size(); i++) {
      List<Object> rowData = data.get(i);

      if (i == 0) {
        sb.append(indent).append("<thead>").append(lineSep);
      } else if (i == 1) {
        sb.append(indent).append("<tbody>").append(lineSep);
      }

      sb.append(indent).append(indent).append("<tr>").append(lineSep);
      for (int j = 0; j < rowData.size(); j++) {
        sb.append(indent).append(indent).append(indent);
        sb.append(i == 0 ? "<th>" : "<td>");
        sb.append(StringEscapeUtils.escapeHtml4((String) rowData.get(j)));
        sb.append(i == 0 ? "</th>" : "</td>").append(lineSep);
      }
      sb.append(indent).append(indent).append("</tr>").append(lineSep);

      if (i == 0) {
        sb.append(indent).append("</thead>").append(lineSep);
      } else if (i == data.size() - 1) {
        sb.append(indent).append("</tbody>").append(lineSep);
      }
    }
    sb.append("</table>");
    ClipboardUtils.writeText(sb.toString());
  }

  public static List<List<Object>> getSelectedData(CsvGridSheetPane gridSheetPane) {
    GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
    int minR = selectionModel.getMinRowSelectionIndex();
    int maxR = selectionModel.getMaxRowSelectionIndex();
    int minC = selectionModel.getMinColumnSelectionIndex();
    int maxC = selectionModel.getMaxColumnSelectionIndex();

    maxR = Math.min(maxR, gridSheetPane.getRowCount() - 1);
    maxC = Math.min(maxC, gridSheetPane.getColumnCount() - 1);

    List<List<Object>> ret = new ArrayList<>();

    List<Object> headerData = new ArrayList<>();
    for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
      if (!selectionModel.isColumnSelected(columnIndex)) {
        continue;
      }
      headerData.add(gridSheetPane.getModel().getColumnName(columnIndex));
    }

    ret.add(headerData);

    for (int rowIndex = minR; rowIndex <= maxR; rowIndex++) {
      if (!selectionModel.isRowSelected(rowIndex)) {
        continue;
      }
      List<Object> values = new ArrayList<>(maxC - minC + 1);
      for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
        if (!selectionModel.isCellSelected(rowIndex, columnIndex)) {
          continue;
        }
        Object value = gridSheetPane.getValueAt(rowIndex, columnIndex);
        if (value == null) {
          break;
        }
        values.add(value);
      }
      ret.add(values);
    }
    return ret;
  }
}
