/*
 * Copyright 2015 kohii
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
import java.util.Arrays;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndableEdit;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;
import com.smoothcsv.core.csvsheet.edits.Transaction;
import com.smoothcsv.core.find.FindAndReplaceMatcher;
import com.smoothcsv.core.find.FindAndReplaceParams;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.swing.gridsheet.AbstractGridSheetHeaderComponent;
import com.smoothcsv.swing.gridsheet.GridSheetColumnHeader;
import com.smoothcsv.swing.gridsheet.GridSheetCornerHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetRowHeader;
import com.smoothcsv.swing.gridsheet.GridSheetScrollPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.event.GridSheetDataEvent;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetColorProvider;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CsvGridSheetPane extends GridSheetPane {

  @Getter
  private GridSheetUndoManager undoManager;

  private Color defaultHeaderBgColor = new Color(230, 230, 230);

  private Color selectedHeaderBgColor = new Color(200, 200, 200);

  private Color focusedHeaderBgColor = new Color(110, 120, 222);

  private Color viewportBgColor = new Color(237, 237, 237);

  private Color ruleLineColor = new Color(213, 213, 213);

  private Color selectionBorderColor = new Color(110, 120, 222);

  private Color selectionColor = new Color(40, 110, 255, 30);

  private Color frozenLineColor = Color.BLACK;

  @Getter
  private CsvSheetView csvSheetView;

  @Getter
  @Setter
  private boolean newlineCharsVisible = true;

  @Getter
  @Setter
  private Color newlineCharColor = new Color(110, 180, 232);

  private GridSheetHeaderRenderer headerRenderer;

  /**
   * @param gm
   */
  public CsvGridSheetPane(CsvSheetView csvSheetView, CsvGridSheetModel gm) {
    super(gm);

    this.csvSheetView = csvSheetView;

    undoManager = new GridSheetUndoManager(this,
        SettingManager.getInteger(AppSettingKeys.Editor.SIZE_OF_UNDOING));

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
    scrollPane.getRowHeader().setBackground(viewportBgColor);
    scrollPane.getColumnHeader().setBackground(viewportBgColor);
    scrollPane.getViewport().setBackground(viewportBgColor);

    gm.setUndableEditListener(new Consumer<GridSheetUndableEdit>() {
      @Override
      public void accept(GridSheetUndableEdit edit) {
        undoManager.put(edit);
      }
    });
  }

  @Override
  protected GridSheetColorProvider createColorProvider() {
    return new GridSheetColorProvider() {

      @Override
      public Color getRuleLineColor() {
        return ruleLineColor;
      }

      @Override
      public Color getSelectionColor() {
        return selectionColor;
      }

      @Override
      public Color getSelectionBorderColor() {
        return selectionBorderColor;
      }

      @Override
      public Color getFrozenLineColor() {
        return frozenLineColor;
      }
    };
  }

  @Override
  protected GridSheetTable createTable() {
    GridSheetTable table = new CsvGridSheetTable(this, createCellRenderer());
    return table;
  }

  @Override
  protected GridSheetCornerHeader createCornerHeader() {
    return new CsvGridSheetCornerHeader(this, createHeaderRenderer());
  }

  @Override
  protected GridSheetColumnHeader createColumnHeader() {
    return new CsvGridSheetColumnHeader(this, createHeaderRenderer());
  }

  @Override
  protected GridSheetRowHeader createRowHeader() {
    return new CsvGridSheetRowHeader(this, createHeaderRenderer());
  }

  protected GridSheetCellRenderer createCellRenderer() {
    return new CsvGridSheetCellRenderer() {
      private final Color HILIGHTED_CELL_COLOR = new Color(250, 250, 180);

      @Override
      public Component getGridCellRendererComponent(GridSheetTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        super.getGridCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
          if (value instanceof String) {
            setForeground(Color.BLACK);

            if (ApplicationStatus.getInstance().isFindAndReplacePanelVisible()) {
              if (value instanceof String) {
                if (FindAndReplaceMatcher.matches((String) value,
                    FindAndReplaceParams.getInstance())) {
                  setBackground(HILIGHTED_CELL_COLOR);
                } else {
                  setBackground(Color.WHITE);
                }
              }
            } else {
              setBackground(Color.WHITE);
            }
          } else {
            setForeground(newlineCharColor);
            setBackground(viewportBgColor);
          }
        } else {
          // the cell not exists
          setBackground(viewportBgColor);
        }

        return this;
      }
    };
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
      headerRenderer = new DefaultGridSheetHeaderRenderer() {
        @Override
        public Component getGridCellRendererComponent(AbstractGridSheetHeaderComponent header,
            Object value, boolean isSelected, boolean hasFocus, int index) {
          setValue(value);
          if (isSelected) {
            if (hasFocus) {
              setForeground(Color.WHITE);
              setBackground(focusedHeaderBgColor);
            } else {
              setForeground(Color.BLACK);
              setBackground(selectedHeaderBgColor);
            }
          } else {
            setForeground(Color.BLACK);
            setBackground(defaultHeaderBgColor);
          }
          return this;
        }
      };
    }
    return headerRenderer;
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    aValue = ObjectUtils.toString(aValue);
    Object oldVal = getValueAt(row, column);
    if (oldVal != null) {
      super.setValueAt(aValue, row, column);
      return;
    }
    GridSheetUndoManager um = getUndoManager();
    CsvGridSheetModel model = (CsvGridSheetModel) getModel();
    String[] data = new String[column - model.getColumnCountAt(row) + 1];
    Arrays.fill(data, "");
    if (um.isTransactionStarted()) {
      model.insertCell(row, model.getColumnCountAt(row), data);
      super.setValueAt(aValue, row, column);
    } else {
      try (Transaction tran = transaction()) {
        model.insertCell(row, model.getColumnCountAt(row), data);
        super.setValueAt(aValue, row, column);
      }
    }
  }

  @Override
  public int getTotalColumnWidth() {
    return super.getTotalColumnWidth() + getNewlineCharacterRectWidth();
  }

  public int getNewlineCharacterRectWidth() {
    return newlineCharsVisible ? 50 : 0;
  }

  public int getColumnCountAt(int rowIndex) {
    int size = model.getColumnCountAt(convertRowIndexToModel(rowIndex));
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

  public Transaction transaction() {
    return new Transaction(getUndoManager());
  }

  @Override
  public void cellValueChanged(GridSheetDataEvent e) {
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
}
