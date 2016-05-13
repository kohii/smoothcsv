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
import com.smoothcsv.core.sort.SortCriteria;
import com.smoothcsv.core.sort.SortCriteriasDialog.ColumnInfo;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.swing.gridsheet.model.CellRect;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author kohii
 */
public class SortSelectedRangeCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    if (sm.isAdditionallySelected()) {
      throw new AppException("WSCA0004");
    }
    CellRect range =
        new CellRect(sm.getMainMinRowSelectionIndex(), sm.getMainMinColumnSelectionIndex(),
            sm.getMainMaxRowSelectionIndex(), sm.getMainMaxColumnSelectionIndex());
    ColumnInfo[] columns = new ColumnInfo[range.getNumColumns()];
    for (int i = 0; i < columns.length; i++) {
      int colIdx = i + range.getColumn();
      columns[i] = new ColumnInfo(i, String.valueOf(colIdx + 1));
    }
    List<SortCriteria> criterias =
        SortCommand.chooseSortCriteria(gridSheetPane, Arrays.asList(columns), null);
    if (criterias != null) {
      gridSheetPane.getModel().sort(criterias, range);
    }
  }

  public static void sort(CsvGridSheetPane gridSheetPane, List<SortCriteria> criterias,
                          CellRect range) {
    if (range.getColumn() <= 0 && gridSheetPane.getColumnCount() - 1 <= range.getLastColumn()) {
      // entire columns

      if (range.getRow() <= 0 && gridSheetPane.getRowCount() - 1 <= range.getLastRow()) {
        // all cells
        new SortCommand().execute();
      } else {
        new SortSelectedRowsCommand().execute();
      }
    } else {
      gridSheetPane.getModel().sort(criterias, range);
    }
  }
}
