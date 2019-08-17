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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.SmoothCsvReader;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.csv.prop.QuoteApplyRule;
import com.smoothcsv.swing.gridsheet.model.CellConsumer;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.utils.ClipboardUtils;

/**
 * @author kohii
 */
public class PasteCommand extends GridCommand {

  private static CsvMeta singleTsvMeta = new CsvMeta();

  static {
    singleTsvMeta.setDelimiter('\t');
    singleTsvMeta.setEscape('\0');
    singleTsvMeta.setQuote('"');
    singleTsvMeta.setQuoteOption(QuoteApplyRule.QUOTES_ALL);
  }

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    String text = ClipboardUtils.readText();
    if (StringUtils.isEmpty(text)) {
      return;
    }
    paste(gridSheetPane, text, singleTsvMeta, false);
  }

  public static void paste(CsvGridSheetPane gridSheetPane, String text, CsvMeta csvMeta,
                           boolean doNotChangeSelection) {
    List<List<String>> values = parse(text, csvMeta);
    DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    PasteRange range = new PasteRange() {

      @Override
      public int getRow() {
        return sm.getMainMinRowSelectionIndex();
      }

      @Override
      public int getColumn() {
        return sm.getMainMinColumnSelectionIndex();
      }

      @Override
      public void forEach(CellConsumer callback) {
        sm.forEachSelectedCell(callback);
      }
    };

    paste(gridSheetPane, range, values, doNotChangeSelection);
  }

  public static void paste(CsvGridSheetPane gridSheetPane, PasteRange range, List<List<String>> values,
                           boolean doNotChangeSelection) {
    if (values.isEmpty()) {
      return;
    }

    boolean isSingleValue = values.size() == 1 && values.get(0).size() == 1;

    try (EditTransaction tran = gridSheetPane.transaction()) {

      int leftTopRow = range.getRow();
      int leftTopColumn = range.getColumn();
      int valuesMaxColumnSize = values.stream().mapToInt(v -> v.size()).max().getAsInt();

      if (!isSingleValue) {
        int rowSize = gridSheetPane.getRowCount();
        int columnSize = gridSheetPane.getColumnCount();

        if (leftTopRow + values.size() > rowSize) {
          gridSheetPane.addRow(leftTopRow + values.size() - rowSize);
        }
        if (leftTopColumn + valuesMaxColumnSize > columnSize) {
          gridSheetPane.addColumn(leftTopColumn + valuesMaxColumnSize - columnSize);
        }

        int currentRow = leftTopRow;
        for (List<String> rowValues : values) {
          int len = rowValues.size();
          for (int j = 0; j < len; j++) {
            String v = rowValues.get(j);
            gridSheetPane.setValueAt(v, currentRow, j + leftTopColumn);
          }
          currentRow++;
        }
      } else {
        String dataToPaste = values.get(0).get(0);
        if (!CoreSettings.getInstance().getBoolean(CoreSettings.PASTE_REPEATEDLY)) {
          gridSheetPane.setValueAt(dataToPaste, leftTopRow, leftTopColumn);
        } else {
          range.forEach(new CellConsumer() {
            @Override
            public void accept(int row, int column) {
              gridSheetPane.setValueAt(dataToPaste, row, column);
            }
          });
        }
      }

      if (!isSingleValue || !CoreSettings.getInstance().getBoolean(CoreSettings.PASTE_REPEATEDLY)) {
        if (!doNotChangeSelection) {
          GridSheetSelectionModel selectionModel = gridSheetPane.getSelectionModel();
          selectionModel.clearHeaderSelection();
          selectionModel.setSelectionIntervalNoChangeAnchor(leftTopRow, leftTopColumn,
              leftTopRow + values.size() - 1, leftTopColumn + valuesMaxColumnSize - 1);
        }
      }
    }
  }

  private static List<List<String>> parse(String text, CsvMeta csvMeta) {
    List<List<String>> data = new ArrayList<>();
    try (SmoothCsvReader csvReader = new SmoothCsvReader(new StringReader(text), csvMeta.toCsvProperties())) {
      List<String> rowData;
      while ((rowData = csvReader.readRow()) != null) {
        if (rowData.isEmpty()) {
          data.add(Collections.singletonList(""));
        } else {
          data.add(rowData);
        }
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
    return data;
  }

  public interface PasteRange {

    int getRow();

    int getColumn();

    void forEach(CellConsumer callback);
  }
}
