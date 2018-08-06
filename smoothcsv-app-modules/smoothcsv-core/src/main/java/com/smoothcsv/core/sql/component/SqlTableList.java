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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlTableDefinitions;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.event.HoveredCellChangeEvent;
import com.smoothcsv.swing.event.TableCellHoverListener;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableCellValueExtracter;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;
import com.smoothcsv.swing.table.ReadOnlyExTableCellValueExtracter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlTableList extends JPanel implements SmoothComponent, ListSelectionListener {

  private static final int FILE_NAME_COLUMN_INDEX = 0;
  private static final int INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX = 2;

  @Getter
  private final SmoothComponentSupport componentSupport = new SmoothComponentSupport(this,
      "sql-tablelist");

  private final ExTable<CsvSheetView> csvSheetList;

  @Setter
  private BiConsumer<SqlTableInfo, SqlTableInfo> selectionChangeListener;

  @Setter
  private Consumer<String> tableNameInsertButtonListener;

  private SqlTableInfo currentSqlTableInfo;

  public SqlTableList() {
    setLayout(new BorderLayout());
    setBorder(null);

    ExTableModel<CsvSheetView> csvSheetListModel = new ExTableModel<>(new ArrayList<>(),
        Arrays.asList(
            new ExTableColumn("File", new ReadOnlyExTableCellValueExtracter<CsvSheetView>() {
              @Override
              public Object getValue(CsvSheetView rowData, ExTableColumn column, int rowIndex, int columnIndex) {
                return rowData.getViewInfo().getShortTitle();
              }
            }),
            new ExTableColumn("Table Name", new ExTableCellValueExtracter<CsvSheetView>() {
              @Override
              public void setValue(Object value, CsvSheetView rowData, ExTableColumn column, int rowIndex, int columnIndex) {
                if (value == null || "".equals(value)) {
                  return;
                }
                SqlTableDefinitions.getInstance().getTableInfoByViewId(rowData.getViewId()).setName(value.toString());
              }

              @Override
              public Object getValue(CsvSheetView rowData, ExTableColumn column, int rowIndex, int columnIndex) {
                return SqlTableDefinitions.getInstance().getTableInfoByViewId(rowData.getViewId()).getName();
              }
            }),
            new ExTableColumn("", new ReadOnlyExTableCellValueExtracter<CsvSheetView>() {
              @Override
              public Object getValue(CsvSheetView rowData, ExTableColumn column, int rowIndex, int columnIndex) {
                return "<html><font color=\"#4ebfa0\">&gt;&gt;</font><html>";
              }
            })
        ));
    csvSheetList = new ExTable<>(csvSheetListModel);
    csvSheetList.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    csvSheetList.sizeColumnsToFit(0);
    csvSheetList.getTableHeader().setReorderingAllowed(false);
    csvSheetList.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    csvSheetList.setRowHeight(20);

    csvSheetList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    csvSheetList.getSelectionModel().addListSelectionListener(this);

    csvSheetList.getColumnModel().getColumn(1).setPreferredWidth(80);
    csvSheetList.getColumnModel().getColumn(INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX).setPreferredWidth(22);
    csvSheetList.getColumnModel().getColumn(INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX).setMaxWidth(22);

    TableCellHoverListener hoverListener = new TableCellHoverListener() {
      @Override
      public void hoveredCellChanged(HoveredCellChangeEvent event) {
        if (!event.isOutOfTableBounds()) {
          if (event.getNewColumn() == INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX) {
            csvSheetList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          } else {
            csvSheetList.setCursor(Cursor.getDefaultCursor());
          }
        }
        csvSheetList.repaint();
      }
    };
    hoverListener.installTo(csvSheetList);

    csvSheetList.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableCellRenderer r = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        boolean isHovered = hoverListener.getHoveredRowIndex() == row;

        Icon icon;
        String tooltip;
        int alignment;
        if (column == INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX) {
          alignment = JLabel.RIGHT;
          tooltip = "Insert Table Name To SQL";
          if (isHovered) {
            if (isSelected) {
              icon = SqlComponentConstants.SQL_INSERT_TEXT_ICON_SELECTED_HOVER;
            } else {
              icon = SqlComponentConstants.SQL_INSERT_TEXT_ICON_HOVER;
            }
          } else {
            icon = SqlComponentConstants.SQL_INSERT_TEXT_ICON;
          }
        } else {
          alignment = JLabel.LEFT;
          if (column == FILE_NAME_COLUMN_INDEX) {
            CsvSheetView csvSheet = csvSheetList.getModel().getRowDataAt(row);
            tooltip = csvSheet.getViewInfo().getFullTitle();
            icon = SqlComponentConstants.TABLE_ICON;
          } else {
            tooltip = null;
            icon = null;
          }
        }
        r.setIcon(icon);
        r.setToolTipText(tooltip);
        r.setHorizontalAlignment(alignment);

        if (isSelected) {
          r.setForeground(Color.WHITE);
          r.setBackground(SqlComponentConstants.SELECTED_CELL_BACKGROUND);
        } else if (isHovered) {
          r.setForeground(Color.BLACK);
          r.setBackground(SqlComponentConstants.HOVERED_CELL_BACKGROUND);
        } else {
          r.setForeground(Color.BLACK);
          r.setBackground(Color.WHITE);
        }

        return r;
      }
    });

    csvSheetList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int col = csvSheetList.columnAtPoint(e.getPoint());
        if (col != INSERT_TABLE_NAME_BUTTON_COLUMN_INDEX) {
          return;
        }

        int row = csvSheetList.rowAtPoint(e.getPoint());
        if (row < 0) {
          return;
        }

        CsvSheetView csvSheetView = csvSheetList.getModel().getRowDataAt(row);
        String tableName = SqlTableDefinitions.getInstance().getTableInfoByViewId(csvSheetView.getViewId()).getName();
        tableNameInsertButtonListener.accept(tableName);
      }
    });

    JScrollPane scrollPane = new JScrollPane(csvSheetList);
    scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
        UIConstants.getDefaultBorderColor()));
    add(scrollPane, BorderLayout.CENTER);

    JLabel label = new JLabel("Tables");
    label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(label, BorderLayout.NORTH);
  }

  public SqlTableInfo getSelectedTableInfo() {
    int selectedRow = csvSheetList.getSelectedRow();
    if (selectedRow < 0) {
      return null;
    }
    CsvSheetView csvSheetView = csvSheetList.getModel().getData().get(selectedRow);
    return SqlTableDefinitions.getInstance().getTableInfoByViewId(csvSheetView.getViewId());
  }

  public void loadCsvSheetTables() {
    ExTableModel<CsvSheetView> csvSheetListModel = csvSheetList.getModel();
    while (csvSheetListModel.getRowCount() > 0) {
      csvSheetListModel.removeRow(0);
    }

    List<SqlCsvSheetTableInfo> tableInfoList = SqlTableDefinitions.getInstance().getTableInfoList();
    for (SqlCsvSheetTableInfo tableInfo : tableInfoList) {
      csvSheetListModel.addRow(tableInfo.getCsvSheet());
    }

    selectFirstRow();
  }

  public void stopEditiong() {
    if (csvSheetList.isEditing()) {
      csvSheetList.getCellEditor().stopCellEditing();
    }
  }

  private void selectFirstRow() {
    int rowCount = csvSheetList.getRowCount();
    if (rowCount == 0) {
      return;
    }
    csvSheetList.selecteRowAt(0);
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getFirstIndex() <= e.getLastIndex()) {
      SqlTableInfo oldSqlTableInfo = this.currentSqlTableInfo;
      this.currentSqlTableInfo = getSelectedTableInfo();
      selectionChangeListener.accept(oldSqlTableInfo, getSelectedTableInfo());
    } else {
      SqlTableInfo oldSqlTableInfo = this.currentSqlTableInfo;
      this.currentSqlTableInfo = null;
      selectionChangeListener.accept(oldSqlTableInfo, null);
    }
  }
}
