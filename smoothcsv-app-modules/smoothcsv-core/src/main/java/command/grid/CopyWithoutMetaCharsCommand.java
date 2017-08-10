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
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.utils.ClipboardUtils;

/**
 * @author kohii
 */
public class CopyWithoutMetaCharsCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    String text = copy(gridSheetPane);
    ClipboardUtils.writeText(text);
  }

  private String copy(CsvGridSheetPane gridSheetPane) {
    GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
    int minR = selectionModel.getMinRowSelectionIndex();
    int maxR = selectionModel.getMaxRowSelectionIndex();
    int minC = selectionModel.getMinColumnSelectionIndex();
    int maxC = selectionModel.getMaxColumnSelectionIndex();

    maxR = Math.min(maxR, gridSheetPane.getRowCount() - 1);
    maxC = Math.min(maxC, gridSheetPane.getColumnCount() - 1);

    final String lineSeparator = gridSheetPane.getCsvSheetView()
        .getViewInfo().getCsvMeta()
        .getLineSeparator().stringValue();

    StringBuilder sb = new StringBuilder();

    for (int rowIndex = minR; rowIndex <= maxR; rowIndex++) {
      if (!selectionModel.isRowSelected(rowIndex)) {
        continue;
      }
      for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
        if (!selectionModel.isCellSelected(rowIndex, columnIndex)) {
          continue;
        }
        Object value = gridSheetPane.getValueAt(rowIndex, columnIndex);
        if (value == null) {
          break;
        }
        sb.append(value);
      }
      if (maxR != rowIndex) {
        sb.append(lineSeparator);
      }
    }
    return sb.toString();
  }

}
