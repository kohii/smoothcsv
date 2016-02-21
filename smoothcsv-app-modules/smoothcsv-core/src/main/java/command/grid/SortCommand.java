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
import java.util.Arrays;
import java.util.List;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.sort.SortCriteria;
import com.smoothcsv.core.sort.SortCriteriasDialog;
import com.smoothcsv.core.sort.SortCriteriasDialog.ColumnInfo;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;

/**
 * @author kohii
 *
 */
public class SortCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.stopCellEditingIfEditing();;
    List<SortCriteria> criterias = chooseSortCriteria(gridSheetPane);
    if (criterias != null) {
      ((CsvGridSheetModel) gridSheetPane.getModel()).sort(criterias);
    }
  }

  public static List<SortCriteria> chooseSortCriteria(CsvGridSheetPane gridSheetPane) {
    ColumnInfo[] columns = new ColumnInfo[gridSheetPane.getColumnCount()];
    for (int i = 0; i < columns.length; i++) {
      columns[i] = new ColumnInfo(i, "Column ".concat(String.valueOf(i + 1)));
    }

    List<SortCriteria> defaultSortCriterias = null;
    DefaultGridSheetSelectionModel selModel = gridSheetPane.getSelectionModel();
    if (selModel.isColumnHeaderSelected()) {
      defaultSortCriterias = new ArrayList<>();
      int[] cols = selModel.getSelectedColumns();
      for (int c : cols) {
        defaultSortCriterias.add(new SortCriteria(c));
      }
    }

    return chooseSortCriteria(gridSheetPane, Arrays.asList(columns), defaultSortCriterias);
  }

  public static List<SortCriteria> chooseSortCriteria(CsvGridSheetPane gridSheetPane,
      List<ColumnInfo> columns, List<SortCriteria> defaultSortCriterias) {

    if (defaultSortCriterias == null || defaultSortCriterias.isEmpty()) {
      defaultSortCriterias = Arrays.asList(new SortCriteria(columns.get(0).getIndex()));
    }

    SortCriteriasDialog dialog =
        new SortCriteriasDialog(SCBundle.get("key.sort"), columns, defaultSortCriterias);
    dialog.setVisible(true);
    if (dialog.getSelectedOperation() == DialogOperation.OK) {
      return dialog.getSortCriterias();
    }
    return null;
  }
}
