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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import com.smoothcsv.core.sql.model.SqlColumnInfo;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.swing.event.HoveredCellChangeEvent;
import com.smoothcsv.swing.event.TableCellHoverListener;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;
import com.smoothcsv.swing.table.ReadOnlyExTableCellValueExtracter;
import lombok.Setter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlTableColumnsEditorPanel extends AbstractSqlTableDetailsPanel {

  private static final int DATA_TYPE_COLUMN_INDEX = 2;
  private static final int INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX = 3;

  private ExTable<SqlColumnInfo> table;
//  private JCheckBox chckbxUseTheFirst;

  @Setter
  private Consumer<String> columnNameInsertButtonListener;

  public SqlTableColumnsEditorPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

//    chckbxUseTheFirst = new JCheckBox("Use The First Line As Column Names");
//    GridBagConstraints gbc_chckbxUseTheFirst = new GridBagConstraints();
//    gbc_chckbxUseTheFirst.insets = new Insets(0, 0, 5, 0);
//    gbc_chckbxUseTheFirst.anchor = GridBagConstraints.WEST;
//    gbc_chckbxUseTheFirst.gridwidth = 2;
//    gbc_chckbxUseTheFirst.gridx = 0;
//    gbc_chckbxUseTheFirst.gridy = 0;
//    add(chckbxUseTheFirst, gbc_chckbxUseTheFirst);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(null);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 2;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

    List<ExTableColumn> columns = new ArrayList<>();
    columns.add(new ExTableColumn("Index", new ReadOnlyExTableCellValueExtracter<SqlColumnInfo>() {
      @Override
      public Object getValue(SqlColumnInfo rowData, ExTableColumn column, int rowIndex, int columnIndex) {
        return rowData.getColumnId() + 1;
      }
    }));
    columns.add(new ExTableColumn("Name", "name"));
    columns.add(new ExTableColumn("Data Type", "type"));
    columns.add(new ExTableColumn("", new ReadOnlyExTableCellValueExtracter() {
      @Override
      public Object getValue(Object rowData, ExTableColumn column, int rowIndex, int columnIndex) {
        return "";
      }
    }));

    // columns.add(new ExTableColumn("Scale", "scale"));
    // columns.add(new ExTableColumn("Precision", "precision"));
    ExTableModel<SqlColumnInfo> model = new ExTableModel<>(new ArrayList<>(), columns);
    table = new ExTable<>(model);
    table.setAutoResizeMode(ExTable.AUTO_RESIZE_OFF);
    table.getTableHeader().setReorderingAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setBorder(null);
    table.getColumnModel().getColumn(0).setPreferredWidth(40);
    table.getColumnModel().getColumn(0).setMaxWidth(40);
    table.getColumnModel().getColumn(2).setPreferredWidth(70);
    table.getColumnModel().getColumn(INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX).setMaxWidth(22);
    table.getColumnModel().getColumn(INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX).setPreferredWidth(22);
    table.sizeColumnsToFit(1);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    table.setRowHeight(20);
    table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    // table.getColumnModel().getColumn(3).setPreferredWidth(30);
    // table.getColumnModel().getColumn(4).setPreferredWidth(30);
    JComboBox<JDBCType> typeCombo = new JComboBox<>();
    typeCombo.addItem(JDBCType.VARCHAR);
    typeCombo.addItem(JDBCType.BIGINT);
    typeCombo.addItem(JDBCType.INTEGER);
    typeCombo.addItem(JDBCType.DECIMAL);
    typeCombo.addItem(JDBCType.DATE);
    typeCombo.addItem(JDBCType.TIME);
    table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(typeCombo));

    scrollPane.setViewportView(table);
    scrollPane.setColumnHeaderView(table.getTableHeader());
    scrollPane.setViewportBorder(null);

    TableCellHoverListener hoverListener = new TableCellHoverListener() {
      @Override
      public void hoveredCellChanged(HoveredCellChangeEvent event) {
        if (!event.isOutOfTableBounds()) {
          if (event.getNewColumn() == INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          } else {
            table.setCursor(Cursor.getDefaultCursor());
          }

          if (event.getNewColumn() == DATA_TYPE_COLUMN_INDEX) {
            table.editCellAt(event.getNewRow(), 2);
          } else {
            TableCellEditor cellEditor = table.getCellEditor();
            if (cellEditor != null && table.isEditing() && table.getEditingColumn() == 2) {
              cellEditor.stopCellEditing();
            }
          }
        }

        table.repaint();
      }
    };
    hoverListener.installTo(table);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        isSelected = false;
        if (column != 1) {
          hasFocus = false;
        }

        DefaultTableCellRenderer r = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        boolean isHovered = hoverListener.getHoveredRowIndex() == row
            || (hoverListener.getHoveredRowIndex() < 0
            && table.getEditingColumn() == DATA_TYPE_COLUMN_INDEX
            && table.getEditingRow() == row);

        Icon icon;
        String tooltip;
        int alignment;
        if (column == INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX) {
          alignment = JLabel.RIGHT;
          tooltip = "Insert Column Name To SQL";
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
          tooltip = null;
          icon = null;
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

    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(e.getPoint());
        if (col != INSERT_COLUMN_NAME_BUTTON_COLUMN_INDEX) {
          return;
        }

        int row = table.rowAtPoint(e.getPoint());
        if (row < 0) {
          return;
        }

        SqlColumnInfo columnInfo = table.getModel().getRowDataAt(row);
        String columnName = columnInfo.getName();
        columnNameInsertButtonListener.accept(columnName);
      }
    });
  }

  public void stopEditiong() {
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }
  }

  @Override
  protected void load(SqlTableInfo tableInfo) {
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }
    ExTableModel<SqlColumnInfo> tableModel = table.getModel();
    table.getSelectionModel().clearSelection();
    tableModel.setData(tableInfo == null ? Collections.emptyList() : tableInfo.getColumns());
    table.revalidate();
    table.repaint();
  }

  @Override
  public void setEnabled(boolean enabled) {
//    chckbxUseTheFirst.setEnabled(enabled);
    super.setEnabled(enabled);
  }
}
