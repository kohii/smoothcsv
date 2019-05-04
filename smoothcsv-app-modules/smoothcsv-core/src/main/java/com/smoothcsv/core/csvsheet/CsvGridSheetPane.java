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
package com.smoothcsv.core.csvsheet;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoableEdit;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;
import com.smoothcsv.core.find.FindAndReplaceMatcher;
import com.smoothcsv.core.find.FindAndReplaceParams;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.swing.gridsheet.GridSheetColumnHeader;
import com.smoothcsv.swing.gridsheet.GridSheetCornerHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetRowHeader;
import com.smoothcsv.swing.gridsheet.GridSheetScrollPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.event.GridSheetDataEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetStructureEvent;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetColorProvider;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import com.smoothcsv.swing.utils.SwingUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvGridSheetPane extends GridSheetPane {

  @Getter
  private GridSheetUndoManager undoManager;

  @Getter
  private CsvSheetView csvSheetView;

  @Getter
  @Setter
  private boolean newlineCharsVisible = true;

  @Getter
  @Setter
  private Color newlineCharColor = new Color(110, 180, 232);

  private DefaultGridSheetHeaderCellRenderer headerRenderer;

  private int lineHeight;

  /**
   * @param gm
   */
  public CsvGridSheetPane(CsvSheetView csvSheetView, CsvGridSheetModel gm) {
    super(gm);
    setFont(SCAppearanceManager.getGridFont());

    this.csvSheetView = csvSheetView;

    undoManager = new GridSheetUndoManager(this,
        CoreSettings.getInstance().getInteger(CoreSettings.SIZE_OF_UNDOING));

    GridSheetScrollPane scrollPane = getScrollPane();
    // Color viewportBorderColor = getColorProvider().getRuleLineColor();
    // scrollPane.getColumnHeader().setBorder(
    // BorderFactory.createMatteBorder(0, 0, 1, 0, viewportBorderColor));;
    // scrollPane.getRowHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
    // viewportBorderColor));
    // ((JComponent) scrollPane.getCorner(GridSheetScrollPane.UPPER_LEFT_CORNER))
    // .setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, viewportBorderColor));
    // scrollPane.setViewportBorder(BorderFactory.createMatteBorder(1, 1, 0, 0,
    // viewportBorderColor));
    Color bg = SCAppearanceManager.getDefaultBackground();
    scrollPane.getRowHeader().setBackground(bg);
    scrollPane.getColumnHeader().setBackground(bg);
    scrollPane.getViewport().setBackground(bg);

    gm.setUndableEditListener(new Consumer<GridSheetUndoableEdit>() {
      @Override
      public void accept(GridSheetUndoableEdit edit) {
        undoManager.put(edit);
      }
    });

    addPropertyChangeListener("font", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        lineHeight = SwingUtils.getLineHeight(CsvGridSheetPane.this);
        getModel().setDefaultRowHeight(lineHeight);
      }
    });
    lineHeight = SwingUtils.getLineHeight(getTable());
    getModel().setDefaultRowHeight(lineHeight);
    // recalcRowHeights();
  }

  @Override
  protected GridSheetColorProvider createColorProvider() {
    return new GridSheetColorProvider() {

      @Override
      public Color getRuleLineColor() {
        return SCAppearanceManager.getGridLineColor();
      }

      @Override
      public Color getSelectionColor() {
        return SCAppearanceManager.getGridSelectionBackground();
      }

      @Override
      public Color getSelectionBorderColor() {
        return SCAppearanceManager.getGridSelectionBorderColor();
      }

      @Override
      public Color getFrozenLineColor() {
        return SCAppearanceManager.getGridFrozenLineColor();
      }
    };
  }

  @Override
  protected GridSheetTable createTable() {
    GridSheetTable table = new CsvGridSheetTable(this, createCellRenderer());
    table.setFont(SCAppearanceManager.getGridFont());
    return table;
  }

  @Override
  protected GridSheetCornerHeader createCornerHeader() {
    return new CsvGridSheetCornerHeader(this, createHeaderRenderer());
  }

  @Override
  protected GridSheetColumnHeader createColumnHeader() {
    CsvGridSheetColumnHeader header = new CsvGridSheetColumnHeader(this, createHeaderRenderer());
    header.setFont(SCAppearanceManager.getGridFont());
    return header;
  }

  @Override
  protected GridSheetRowHeader createRowHeader() {
    GridSheetRowHeader header = new CsvGridSheetRowHeader(this, createHeaderRenderer());
    header.setFont(SCAppearanceManager.getGridFont());
    return header;
  }

  protected GridSheetCellRenderer createCellRenderer() {
    CsvGridSheetCellRenderer renderer = new CsvGridSheetCellRenderer() {

      @Override
      public Component getGridCellRendererComponent(GridSheetTable table, Object value,
                                                    boolean isSelected, boolean hasFocus, int row, int column) {
        super.getGridCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
          if (value instanceof String) {
            setForeground(SCAppearanceManager.getGridForeground());

            if (ApplicationStatus.getInstance().isFindAndReplacePanelVisible()) {
              if (value instanceof String) {
                if (FindAndReplaceMatcher.matches((String) value,
                    FindAndReplaceParams.getInstance())) {
                  setBackground(SCAppearanceManager.getGridFindhilightColor());
                } else {
                  setBackground(SCAppearanceManager.getGridBackground());
                }
              }
            } else {
              setBackground(SCAppearanceManager.getGridBackground());
            }
          } else {
            setForeground(newlineCharColor);
            setBackground(SCAppearanceManager.getDefaultBackground());
          }
        } else {
          // the cell not exists
          setBackground(SCAppearanceManager.getDefaultBackground());
        }

        return this;
      }
    };
    renderer.setFont(SCAppearanceManager.getGridFont());
    return renderer;
  }

  @Override
  public CsvGridSheetModel getModel() {
    return (CsvGridSheetModel) super.getModel();
  }

  @Override
  public CsvGridSheetTable getTable() {
    return (CsvGridSheetTable) super.getTable();
  }

  protected GridSheetHeaderRenderer createHeaderRenderer() {
    if (headerRenderer == null) {
      headerRenderer = new CsvGridSheetHeaderCellRenderer();
    }
    return headerRenderer;
  }

  @Override
  public void setValueAt(String aValue, int row, int column) {
    String oldVal = getValueAt(row, column);
    if (oldVal != null) {
      super.setValueAt(aValue, row, column);
      return;
    }
    CsvGridSheetModel model = getModel();
    String[] data = new String[column - model.getColumnCountAt(row) + 1];
    Arrays.fill(data, "");
    try (EditTransaction tran = transaction()) {
      model.insertCell(row, model.getColumnCountAt(row), data);
      super.setValueAt(aValue, row, column);
    }
  }

  @Override
  public void setValuesAt(List<List<String>> valuesList, int row, int column) {
    CsvGridSheetModel model = getModel();
    try (EditTransaction tran = transaction()) {
      for (int i = 0; i < valuesList.size(); i++) {
        List<String> values = valuesList.get(i);
        int columnCount = model.getColumnCountAt(row + i);
        if (columnCount < column + values.size()) {
          String[] data = new String[column + values.size() - columnCount];
          Arrays.fill(data, "");
          model.insertCell(row, columnCount, data);
        }
      }
      model.setValuesAt(valuesList, row, column);
    }
  }

  @Override
  public int getTotalColumnWidth() {
    return super.getTotalColumnWidth() + getNewlineCharacterRectWidth();
  }

  @Override
  public int getTotalRowHeight() {
    return super.getTotalRowHeight() + getNewlineCharacterRectHeight();
  }

  public int getNewlineCharacterRectWidth() {
    return newlineCharsVisible ? 50 : 0;
  }

  public int getNewlineCharacterRectHeight() {
    return newlineCharsVisible ? lineHeight : 0;
  }

  public int getColumnCountAt(int rowIndex) {
    int size = getModel().getColumnCountAt(convertRowIndexToModel(rowIndex));
    if (areAllColumnsVisivle()) {
      return size;
    }
    int ret = 0;
    for (int i = 0; i < size; i++) {
      if (model.getColumn(i).isVisible()) {
        ret++;
      }
    }
    return ret;
  }

  @Override
  public DefaultGridSheetSelectionModel getSelectionModel() {
    return (DefaultGridSheetSelectionModel) super.getSelectionModel();
  }

  public void autoResizeColumn(int c) {
    int winW = SCApplication.components().getFrame().getWidth();
    GridSheetUtils.sizeWidthToFit(this, c, winW, Integer.MAX_VALUE);
  }

  public void stopCellEditingIfEditing() {
    if (isEditing()) {
      getTable().stopCellEditing();
    }
  }

  public EditTransaction transaction() {
    return new EditTransaction(getUndoManager());
  }

  @Override
  public void structureChanged(GridSheetStructureEvent e) {
    // switch (e.getType()) {
    // case GridSheetStructureEvent.INSERT_ROW:
    // int r = e.getIndex();
    // for (int i = 0, ln = e.getNumRows(); i < ln; i++) {
    // getModel().getRow(r).setHeight(calcRowHeightAt(r));
    // }
    // break;
    // case GridSheetStructureEvent.INSERT_COLUMN:
    // case GridSheetStructureEvent.REMOVE_COLUMN:
    // case GridSheetStructureEvent.UPDATE_VISIBLE_COLUMNS:
    // case GridSheetStructureEvent.CHANGE_DATALIST:
    // recalcRowHeights();
    // break;
    // }
    super.structureChanged(e);
  }

  @Override
  public void cellValueChanged(GridSheetDataEvent e) {
    if (getUndoManager().isTransactionStarted() || !getUndoManager().isCollecting()) {
      return;
    }
    // if (!e.isStructureChanged()) {
    // if (e.getFirstRow() == GridSheetDataEvent.ALL_CELLS) {
    // recalcRowHeights();
    // } else {
    // int rowIndexTo;
    // if (e.getLastRow() != GridSheetDataEvent.TO_THE_END) {
    // rowIndexTo = getRowCount() - 1;
    // } else {
    // rowIndexTo = e.getLastRow();
    // }
    // for (int i = 0; i <= rowIndexTo; i++) {
    // getRow(i).setHeight(calcRowHeightAt(i));
    // }
    // }
    // }
    super.cellValueChanged(e);
    getCsvSheetView().showCellValueOnValuePanel();
  }

  @Override
  public boolean requestFocusInWindow() {
    return getTable().requestFocusInWindow();
  }

  @Override
  public void requestFocus() {
    getTable().requestFocus();
  }

  // private int calcRowHeightAt(int row) {
  // int columnCount = getColumnCountAt(row);
  // int maxLineCount = 1;
  // for (int i = 0; i < columnCount; i++) {
  // int lc = StringUtils.calcLineCount((String) getValueAt(row, i));
  // if (maxLineCount < lc) {
  // maxLineCount = lc;
  // }
  // }
  // return maxLineCount * lineHeight;
  // }
  //
  // private void recalcRowHeights() {
  // int rowCount = getRowCount();
  // for (int i = 0; i < rowCount; i++) {
  // getRow(i).setHeight(calcRowHeightAt(i));
  // }
  // }
}
