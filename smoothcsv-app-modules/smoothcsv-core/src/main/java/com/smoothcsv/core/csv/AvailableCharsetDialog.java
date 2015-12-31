/*
 * Copyright 2014 kohii.
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
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;

/**
 *
 * @author kohii
 */
@SuppressWarnings("serial")
public class AvailableCharsetDialog extends DialogBase {

  private JTable table;

  private String charset = null;

  public String getCharset() {
    return charset;
  }

  public AvailableCharsetDialog(Frame frame) {
    super(frame, null);
    setUp();
  }

  /**
   * Create the dialog.
   */
  public AvailableCharsetDialog(JDialog parent) {
    super(parent, null);
    setUp();
  }

  private void setUp() {
    JPanel contentPanel = getContentPanel();
    setBounds(100, 100, 600, 400);
    contentPanel.setLayout(new BorderLayout(0, 0));
    {
      JLabel label = new JLabel(CoreBundle.get("key.availableCharsetDialog.description"));
      contentPanel.add(label, BorderLayout.NORTH);
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, BorderLayout.CENTER);
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // table.getTableHeader().getDefaultRenderer().setHorizontalAlignment(SwingConstants.LEFT);
        table.getTableHeader().setReorderingAllowed(false);
        table.setModel(new DefaultTableModel(new Object[][] {},
            new String[] {CoreBundle.get("key.encoding"), CoreBundle.get("key.alias")}) {
          @Override
          public boolean isCellEditable(int row, int column) {
            return false;
          }
        });
        scrollPane.setViewportView(table);
      }
    }

    setTableItems();

  }

  private void setTableItems() {
    SortedMap<String, Charset> csm = Charset.availableCharsets();
    DefaultTableModel model = (DefaultTableModel) table.getModel();
    for (Map.Entry<String, Charset> cse : csm.entrySet()) {
      Charset cs = cse.getValue();
      model.addRow(new String[] {cs.name(), toStr(cs.aliases())});
    }

    table.getSelectionModel().setSelectionInterval(0, 0);

    sizeWidthToFitData(0);
    sizeWidthToFitData(1);

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

  public void sizeWidthToFitData(int vc) {
    TableColumn tc = table.getColumnModel().getColumn(vc);

    int max = table.getTableHeader().getDefaultRenderer()
        .getTableCellRendererComponent(table, tc.getHeaderValue(), false, false, 0, vc)
        .getPreferredSize().width;

    int vrows = table.getRowCount();
    for (int i = 0; i < vrows; i++) {
      TableCellRenderer r = table.getCellRenderer(i, vc);
      Object value = table.getValueAt(i, vc);
      Component c = r.getTableCellRendererComponent(table, value, false, false, i, vc);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
    }

    tc.setPreferredWidth(max + 1);
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      int row = table.getSelectedRow();
      if (row >= 0) {
        charset = (String) table.getValueAt(row, 0);
      }
    }
    return super.processOperation(selectedOperation);
  }
}
