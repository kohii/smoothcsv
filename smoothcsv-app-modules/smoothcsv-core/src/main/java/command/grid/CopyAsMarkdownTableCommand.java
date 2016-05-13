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

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.utils.ClipboardUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kohii
 */
public class CopyAsMarkdownTableCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    List<List<Object>> data = CopyAsHtmlTableCommand.getSelectedData(gridSheetPane);

    List<Integer> textWidths = new ArrayList<>();
    for (List<Object> rowData : data) {
      for (int i = 0; i < rowData.size(); i++) {
        int w = StringUtils.textWidth((String) rowData.get(i));
        if (textWidths.size() <= i) {
          textWidths.add(w);
        } else {
          textWidths.set(i, Math.max(textWidths.get(i), w));
        }
      }
    }

    final String lineSep = gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta()
        .getNewlineCharacter().stringValue();

    StringBuilder sb = new StringBuilder();
    int colSize = textWidths.size();
    for (int i = 0; i < data.size(); i++) {
      List<Object> rowData = data.get(i);
      int ln = rowData.size();
      sb.append('|');

      for (int j = 0; j < colSize; j++) {
        String val = j < ln ? (String) rowData.get(j) : "";
        int textW = StringUtils.textWidth(val);
        int colW = textWidths.get(j);
        sb.append(' ').append(val);
        if (textW < colW) {
          char[] pad = new char[colW - textW];
          Arrays.fill(pad, ' ');
          sb.append(pad);
        }
        sb.append(' ');
        sb.append('|');
      }
      sb.append(lineSep);

      if (i == 0) {
        sb.append('|');
        for (int j = 0; j < colSize; j++) {
          sb.append(' ');
          char[] line = new char[textWidths.get(j)];
          Arrays.fill(line, '-');
          sb.append(line);
          sb.append(' ');
          sb.append('|');
        }
        sb.append(lineSep);
      }
    }

    ClipboardUtils.writeText(sb.toString());
  }
}
