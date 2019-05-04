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
package com.smoothcsv.core.csv;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.smoothcsv.commons.encoding.FileEncoding;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class AvailableEncodingDialog extends DialogBase {

  private JTable table;

  @Getter
  private FileEncoding encoding = null;

  public AvailableEncodingDialog(Frame frame) {
    super(frame, null);
    setUp();
  }

  /**
   * Create the dialog.
   */
  public AvailableEncodingDialog(JDialog parent) {
    super(parent, null);
    setUp();
  }

  private void setUp() {
    JPanel contentPanel = getContentPanel();
    setBounds(100, 100, 600, 400);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JLabel label = new JLabel(CoreBundle.get("key.availableEncodingDialog.description"));
      contentPanel.add(label, BorderLayout.NORTH);
    }
    {
      {
        table = new JTable() {
          @Override
          protected void processMouseEvent(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getID() == MouseEvent.MOUSE_CLICKED
                && SwingUtilities.isLeftMouseButton(e)) {
              e.consume();
              invokeOperationAction(DialogOperation.OK);
              return;
            }
            super.processMouseEvent(e);
          }
        };
        table.setFocusable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        // table.getTableHeader().getDefaultRenderer().setHorizontalAlignment(SwingConstants.LEFT);
        table.getTableHeader().setReorderingAllowed(false);
        table.setModel(new DefaultTableModel(new Object[][]{},
            new String[]{CoreBundle.get("key.encoding"), CoreBundle.get("key.alias")}) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        });
      }
      JScrollPane scrollPane = new JScrollPane(table);
      contentPanel.add(scrollPane, BorderLayout.CENTER);
    }

    setTableItems();
  }

  private void setTableItems() {
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    for (FileEncoding encoding : FileEncoding.getAvailableEncodings()) {
      model.addRow(new String[]{encoding.getName(), toStr(encoding.getAliases())});
    }

    table.getSelectionModel().setSelectionInterval(0, 0);

    table.getColumnModel().getColumn(0).setMinWidth(180);
    table.getColumnModel().getColumn(0).setMaxWidth(180);
  }

  private String toStr(Set<String> strSet) {
    StringBuilder sb = new StringBuilder();
    for (String string : strSet) {
      sb.append(string).append(", ");
    }

    String ret = sb.toString();

    if (ret.length() != 0) {
      ret = ret.substring(0, ret.length() - 2);
    }
    return ret;
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      int row = table.getSelectedRow();
      if (row >= 0) {
        String name = (String) table.getValueAt(row, 0);
        encoding = FileEncoding.forName(name).orElse(null);
      }
    }
    return super.processOperation(selectedOperation);
  }
}
