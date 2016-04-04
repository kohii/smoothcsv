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
package com.smoothcsv.core.sort;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.AwesomeIconButton;
import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.table.ComboBoxCellEditorRenderer;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class SortCriteriasDialog extends DialogBase {

  private JButton btnAdd;
  private JButton btnDelete;
  private JButton btnUp;
  private JButton btnDown;

  private List<ColumnInfo> availableColumns;
  private ExTable<SortCriteria> table;

  /**
   * @param title
   */
  public SortCriteriasDialog(String title, List<ColumnInfo> availableColumns,
      List<SortCriteria> defaultData) {
    super(SCApplication.components().getFrame(), title);
    this.availableColumns = availableColumns;

    SCToolBar toolBar = new SCToolBar();
    btnAdd = new AwesomeIconButton(SCBundle.get("key.add"), AwesomeIcon.FA_PLUS);
    btnAdd.addActionListener(this::addCriteria);
    toolBar.add(btnAdd);
    btnDelete = new AwesomeIconButton(SCBundle.get("key.delete"), AwesomeIcon.FA_MINUS);
    btnDelete.addActionListener(this::deleteSelectedCriteria);
    toolBar.add(btnDelete);
    toolBar.addSeparator();
    btnUp = new AwesomeIconButton(AwesomeIcon.FA_ANGLE_UP);
    btnUp.addActionListener(this::moveSelectedRowToAbove);
    toolBar.add(btnUp);
    btnDown = new AwesomeIconButton(AwesomeIcon.FA_ANGLE_DOWN);
    btnDown.addActionListener(this::moveSelectedRowToBelow);
    toolBar.add(btnDown);
    add(toolBar, BorderLayout.NORTH);

    List<SortCriteria> data = new ArrayList<>();
    data.addAll(defaultData);

    List<ExTableColumn> columns = new ArrayList<>();
    columns.add(new ExTableColumn(SCBundle.get("key.column"), "column"));
    columns.add(new ExTableColumn(SCBundle.get("key.sort.type"), "type"));
    columns.add(new ExTableColumn(SCBundle.get("key.sort.order"), "order"));
    columns.add(new ExTableColumn(SCBundle.get("key.sort.blanksOption"), "blanksOption"));
    ExTableModel<SortCriteria> model = new ExTableModel<SortCriteria>(data, columns);
    table = new ExTable<>(model);
    table.setRowHeight(34);
    table.setIntercellSpacing(new Dimension());
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    table.getColumnModel().getColumn(0)
        .setCellEditor(new SortCriteriaColumnEditorRenderer(availableColumns));
    table.getColumnModel().getColumn(0)
        .setCellRenderer(new SortCriteriaColumnEditorRenderer(availableColumns));

    table.getColumnModel().getColumn(1)
        .setCellEditor(new ComboBoxCellEditorRenderer(ValueType.values()));
    table.getColumnModel().getColumn(1)
        .setCellRenderer(new ComboBoxCellEditorRenderer(ValueType.values()));

    table.getColumnModel().getColumn(2)
        .setCellEditor(new ComboBoxCellEditorRenderer(Order.values()));
    table.getColumnModel().getColumn(2)
        .setCellRenderer(new ComboBoxCellEditorRenderer(Order.values()));

    table.getColumnModel().getColumn(3)
        .setCellEditor(new ComboBoxCellEditorRenderer(BlanksOption.values()));
    table.getColumnModel().getColumn(3)
        .setCellRenderer(new ComboBoxCellEditorRenderer(BlanksOption.values()));

    JScrollPane scrollPane = new JScrollPane(table);
    getContentPanel().add(scrollPane, BorderLayout.CENTER);

    table.getSelectionModel().addListSelectionListener(this::selectionChanged);
    table.selecteRowAt(0);

    setSize(580, 440);
    setLocationRelativeTo(getParent());
  }

  public List<SortCriteria> getSortCriterias() {
    return table.getModel().getData();
  }

  private void addCriteria(ActionEvent e) {
    int rowCount = table.getRowCount();
    table.getModel().addRow(new SortCriteria(availableColumns.get(0).getIndex()));
    table.selecteRowAt(rowCount);
  }

  private void deleteSelectedCriteria(ActionEvent e) {
    int index = table.getSelectedRow();
    int rowCount = table.getRowCount();
    if (index < 0 || rowCount <= index) {
      return;
    }
    table.getModel().removeRow(index);
    if (rowCount > 1) {
      int newSelIdx;
      if (index == rowCount - 1) {
        newSelIdx = rowCount - 2;
      } else {
        newSelIdx = index;
      }
      table.selecteRowAt(newSelIdx);
    }
  }

  private void moveSelectedRowToAbove(ActionEvent e) {
    int index = table.getSelectedRow();
    if (index <= 0) {
      return;
    }
    SortCriteria target = table.getModel().getRowDataAt(index);
    table.getModel().removeRow(index);
    table.getModel().insertRow(index - 1, target);
    table.selecteRowAt(index - 1);
  }

  private void moveSelectedRowToBelow(ActionEvent e) {
    int index = table.getSelectedRow();
    if (index < 0 || index == table.getRowCount() - 1) {
      return;
    }
    SortCriteria target = table.getModel().getRowDataAt(index);
    table.getModel().removeRow(index);
    table.getModel().insertRow(index + 1, target);
    table.selecteRowAt(index + 1);
  }

  public void selectionChanged(ListSelectionEvent e) {
    int index = table.getSelectedRow();
    btnDelete.setEnabled(index >= 0);
    btnUp.setEnabled(index > 0);
    btnDown.setEnabled(index >= 0 && index != table.getRowCount() - 1);
  }

  @AllArgsConstructor
  @Getter
  public static class ColumnInfo {
    private int index;
    private String text;
  }

  private static class SortCriteriaColumnEditorRenderer extends ComboBoxCellEditorRenderer {

    public SortCriteriaColumnEditorRenderer(List<ColumnInfo> items) {
      super(items.toArray());
      getComboBoxPanel().getComboBox().setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
          return super.getListCellRendererComponent(list, ((ColumnInfo) value).getText(), index,
              isSelected, cellHasFocus);
        }
      });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
      Component ret =
          super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (value != null) {
        selectValue((int) value);
      }
      return ret;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
        int row, int column) {
      Component ret = super.getTableCellEditorComponent(table, value, isSelected, row, column);
      selectValue((int) value);
      return ret;
    }

    private void selectValue(int value) {
      ComboBoxModel<?> model = comboBoxPanel.getComboBox().getModel();
      for (int i = 0; i < model.getSize(); i++) {
        if (((ColumnInfo) model.getElementAt(i)).getIndex() == value) {
          comboBoxPanel.getComboBox().setSelectedIndex(i);
        }
      }
    }

    @Override
    public Object getCellEditorValue() {
      return ((ColumnInfo) super.getCellEditorValue()).getIndex();
    }
  }
}
