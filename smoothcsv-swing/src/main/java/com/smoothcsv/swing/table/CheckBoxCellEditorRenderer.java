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
package com.smoothcsv.swing.table;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CheckBoxCellEditorRenderer extends AbstractCellEditor implements TableCellRenderer {

  private JCheckBox checkbox = new JCheckBox();

  public CheckBoxCellEditorRenderer() {
    checkbox.setFocusable(false);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    if (value instanceof Boolean) {
      checkbox.setSelected((Boolean) value);
    }
    return checkbox;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                               int row, int column) {
    if (value instanceof Boolean) {
      checkbox.setSelected((Boolean) value);
    }
    return checkbox;
  }

  public Object getCellEditorValue() {
    return checkbox.isSelected();
  }
}
