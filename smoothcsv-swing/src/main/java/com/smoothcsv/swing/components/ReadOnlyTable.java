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
package com.smoothcsv.swing.components;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
public class ReadOnlyTable extends JTable {

  private static final long serialVersionUID = 6035940424424972939L;

  private final List<ActionListener> actions = new ArrayList<>();

  public ReadOnlyTable() {
    super();
    initialize();
  }

  public ReadOnlyTable(Object[][] rowData, Object[] columnNames) {
    super(rowData, columnNames);
    initialize();
  }

  public ReadOnlyTable(TableModel dm) {
    super(dm);
    initialize();
  }

  private void initialize() {
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    JTableHeader header = getTableHeader();
    header.setReorderingAllowed(false);
    header.setFocusCycleRoot(false);

    setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      private static final long serialVersionUID = 8824164463281799459L;

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(null);
        return this;
      }
    });
    ((JLabel) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 144L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });

    setDefaultEditor(Object.class, null);
  }

  public void pack() {
    SwingUtils.autofitColumnWidth(this);
  }

  public void pack(int buf) {
    SwingUtils.autofitColumnWidth(this, buf);
  }

  protected void processMouseEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_CLICKED && SwingUtilities.isLeftMouseButton(e)
        && e.getClickCount() % 2 == 0) {
      int row = rowAtPoint(e.getPoint());
      if (row < 0 || getSelectedRow() != row) {
        e.consume();
        return;
      }
      for (ActionListener al : actions) {
        al.actionPerformed(null);
      }
      e.consume();
    } else {
      super.processMouseEvent(e);
    }
  }

  public void addActionListener(ActionListener a) {
    actions.add(a);
  }
}
