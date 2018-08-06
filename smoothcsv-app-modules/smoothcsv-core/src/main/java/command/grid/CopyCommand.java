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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.SmoothCsvWriter;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.csv.prop.QuoteApplyRule;
import com.smoothcsv.csv.writer.CsvWriteOption;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.utils.ClipboardUtils;

/**
 * @author kohii
 */
public class CopyCommand extends GridCommand {

  private static CsvMeta singleTsvMeta = new CsvMeta();

  static {
    singleTsvMeta.setDelimiter('\t');
    singleTsvMeta.setEscape('\0');
    singleTsvMeta.setQuote('"');
  }

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    QuoteApplyRule quoteRule = QuoteApplyRule
        .valueOf(CoreSettings.getInstance().get(CoreSettings.QUOTE_RULE_FOR_COPYING));
    singleTsvMeta.setQuoteOption(quoteRule);
    String text = copy(gridSheetPane, singleTsvMeta);
    ClipboardUtils.writeText(text);
  }

  public static String copy(CsvGridSheetPane gridSheetPane, CsvMeta csvMeta) {
    GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
    int minR = selectionModel.getMinRowSelectionIndex();
    int maxR = selectionModel.getMaxRowSelectionIndex();
    int minC = selectionModel.getMinColumnSelectionIndex();
    int maxC = selectionModel.getMaxColumnSelectionIndex();

    maxR = Math.min(maxR, gridSheetPane.getRowCount() - 1);
    maxC = Math.min(maxC, gridSheetPane.getColumnCount() - 1);

    StringWriter sw = new StringWriter();
    CsvWriteOption opt = CsvWriteOption.of(csvMeta.getQuoteOption());
    try (SmoothCsvWriter writer = new SmoothCsvWriter(sw, csvMeta, opt)) {

      for (int rowIndex = minR; rowIndex <= maxR; rowIndex++) {
        if (!selectionModel.isRowSelected(rowIndex)) {
          continue;
        }
        List<String> values = new ArrayList<>(maxC - minC + 1);
        for (int columnIndex = minC; columnIndex <= maxC; columnIndex++) {
          if (!selectionModel.isCellSelected(rowIndex, columnIndex)) {
            continue;
          }
          Object value = gridSheetPane.getValueAt(rowIndex, columnIndex);
          if (value == null) {
            break;
          }
          values.add(value.toString());
        }
        if (maxR == rowIndex) {
          writer.setWriteLineSeparator(false);
        }
        writer.writeRow(values);
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
    return sw.toString();
  }

}
