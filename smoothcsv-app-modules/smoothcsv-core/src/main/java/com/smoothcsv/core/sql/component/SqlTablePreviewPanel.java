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
package com.smoothcsv.core.sql.component;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.event.GridSheetColumnHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetCornerHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetRowHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionListener;
import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlTablePreviewPanel extends AbstractSqlTableDetailsPanel {
  private GridSheetPane previewGrid;

  public SqlTablePreviewPanel() {
    setLayout(new BorderLayout());
    GridSheetModel gm = new GridSheetModel(Arrays.asList(Arrays.asList("")));
    previewGrid = new GridSheetPane(gm) {
      @Override
      protected GridSheetSelectionModel createSelectionModel() {
        return new GridSheetNoSelectionModel();
      }

      @Override
      protected GridSheetTable createTable() {
        return new GridSheetTable(this) {
          @Override
          public boolean editCellAt(int row, int column, EventObject e) {
            return false;
          }

          @Override
          public String getToolTipText(MouseEvent e) {
            int r = rowAtPoint(e.getPoint());
            int c = columnAtPoint(e.getPoint());
            if (r < 0 || c < 0) {
              return null;
            }
            Object o = getValueAt(r, c);
            if (ObjectUtils.isEmpty(o)) {
              return null;
            }
            return ObjectUtils.toString(o);
          }
        };
      }
    };
    // gridSheetPane.setBackground(getBackground());
    add(previewGrid);
  }

  @Override
  protected void load(SqlTableInfo tableInfo) {
    if (tableInfo instanceof SqlCsvSheetTableInfo) {
      CsvGridSheetModel model =
          ((SqlCsvSheetTableInfo) tableInfo).getCsvSheet().getGridSheetPane().getModel();
      List<List> dataList = model.getDataList(0, 5);
      ((GridSheetModel) previewGrid.getModel()).setDataList(dataList);
    }
  }

  private static class GridSheetNoSelectionModel implements GridSheetSelectionModel {

    @Override
    public boolean isRowHeaderSelected() {
      return false;
    }

    @Override
    public boolean isColumnHeaderSelected() {
      return false;
    }

    @Override
    public void setRowHeaderSelected(boolean selected) {}

    @Override
    public void setColumnHeaderSelected(boolean selected) {}

    @Override
    public boolean isCellSelected(int row, int column) {
      return false;
    }

    @Override
    public boolean isRowSelected(int row) {
      return false;
    }

    @Override
    public boolean isColumnSelected(int column) {
      return false;
    }

    @Override
    public int getMinRowSelectionIndex() {
      return -1;
    }

    @Override
    public int getMinColumnSelectionIndex() {
      return -1;
    }

    @Override
    public int getMaxRowSelectionIndex() {
      return -1;
    }

    @Override
    public int getMaxColumnSelectionIndex() {
      return -1;
    }

    @Override
    public int getMainMinRowSelectionIndex() {
      return -1;
    }

    @Override
    public int getMainMinColumnSelectionIndex() {
      return -1;
    }

    @Override
    public int getMainMaxRowSelectionIndex() {
      return -1;
    }

    @Override
    public int getMainMaxColumnSelectionIndex() {
      return -1;
    }

    @Override
    public boolean getValueIsAdjusting() {
      return false;
    }

    @Override
    public void setValueIsAdjusting(boolean adjusting) {}

    @Override
    public void addSelectionInterval(int rowAnchor,
                                     int columnAnchor,
                                     int rowLead,
                                     int columnLead) {}

    @Override
    public void setSelectionInterval(int rowAnchor,
                                     int columnAnchor,
                                     int rowLead,
                                     int columnLead) {}

    @Override
    public void setSelectionIntervalNoChangeAnchor(int minRowIndex,
                                                   int minColumnIndex,
                                                   int maxRowIndex,
                                                   int maxColumnIndex) {}

    @Override
    public void changeLeadSelection(int row, int column, int option) {}

    @Override
    public void clearHeaderSelection() {}

    @Override
    public void clearSelection() {}

    @Override
    public boolean isSingleCellSelected() {
      return false;
    }

    @Override
    public int getRowAnchorIndex() {
      return -1;
    }

    @Override
    public int getColumnAnchorIndex() {
      return -1;
    }

    @Override
    public boolean isAnchor(int row, int column) {
      return false;
    }

    @Override
    public boolean isAdditionallySelected() {
      return false;
    }

    @Override
    public void updateAnchor(int row, int column) {}

    @Override
    public List<GridSheetCellRange> getAdditionalSelections() {
      return null;
    }

    @Override
    public void selectEntireRow() {}

    @Override
    public void selectEntireColumn() {}

    @Override
    public void addGridFocusListener(GridSheetFocusListener l) {}

    @Override
    public void removeGridFocusListener(GridSheetFocusListener l) {}

    @Override
    public void addGridSelectionListener(GridSheetSelectionListener l) {}

    @Override
    public void removeGridSelectionListener(GridSheetSelectionListener l) {}

    @Override
    public void addColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l) {}

    @Override
    public void removeColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l) {}

    @Override
    public void addRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l) {}

    @Override
    public void removeRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l) {}

    @Override
    public void addCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l) {}

    @Override
    public void removeCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l) {}

  }
}
