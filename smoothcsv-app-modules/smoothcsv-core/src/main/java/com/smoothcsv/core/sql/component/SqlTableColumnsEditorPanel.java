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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.smoothcsv.core.sql.model.SqlColumnInfo;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlTableColumnsEditorPanel extends AbstractSqlTableDetailsPanel {
  private ExTable<SqlColumnInfo> table;
  private JCheckBox chckbxUseTheFirst;

  public SqlTableColumnsEditorPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    chckbxUseTheFirst = new JCheckBox("Use The First Line As Column Names");
    GridBagConstraints gbc_chckbxUseTheFirst = new GridBagConstraints();
    gbc_chckbxUseTheFirst.insets = new Insets(0, 0, 5, 0);
    gbc_chckbxUseTheFirst.anchor = GridBagConstraints.WEST;
    gbc_chckbxUseTheFirst.gridwidth = 2;
    gbc_chckbxUseTheFirst.gridx = 0;
    gbc_chckbxUseTheFirst.gridy = 0;
    add(chckbxUseTheFirst, gbc_chckbxUseTheFirst);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(null);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 2;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

    List<ExTableColumn> columns = new ArrayList<>();
    columns.add(new ExTableColumn("Index", "columnIndex", false));
    columns.add(new ExTableColumn("Name", "name"));
    columns.add(new ExTableColumn("Data Type", "type"));
    // columns.add(new ExTableColumn("Scale", "scale"));
    // columns.add(new ExTableColumn("Precision", "precision"));
    ExTableModel<SqlColumnInfo> model = new ExTableModel<>(new ArrayList<>(), columns);
    table = new ExTable<>(model);
    table.setAutoResizeMode(ExTable.AUTO_RESIZE_OFF);
    table.getTableHeader().setReorderingAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setBorder(null);
    table.getColumnModel().getColumn(0).setPreferredWidth(24);
    table.getColumnModel().getColumn(1).setPreferredWidth(50);
    table.getColumnModel().getColumn(2).setPreferredWidth(70);
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
  }

  @Override
  protected void load(SqlTableInfo tableInfo) {
    if (table.isEditing()) {
      table.getCellEditor().stopCellEditing();
    }
    ExTableModel<SqlColumnInfo> tableModel = table.getModel();
    table.getSelectionModel().clearSelection();
    tableModel.setData(tableInfo == null ? Collections.emptyList() : tableInfo.getColumns());
  }

  @Override
  public void setEnabled(boolean enabled) {
    chckbxUseTheFirst.setEnabled(enabled);
    super.setEnabled(enabled);
  }
}
