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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.function.Function;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class ComboBoxCellEditorRenderer extends AbstractCellEditor implements TableCellRenderer,
    TableCellEditor {

  private Function<Object, Object> displayValueSupplier;

  @Getter
  protected final ComboBoxPanel comboBoxPanel;

  public ComboBoxCellEditorRenderer(Object[] items) {
    comboBoxPanel = new ComboBoxPanel(items);
    comboBoxPanel.getComboBox().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
      }
    });
    comboBoxPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        fireEditingStopped();
      }
    });

    comboBoxPanel.getComboBox().setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        if (displayValueSupplier != null) {
          value = displayValueSupplier.apply(value);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });
  }

  @Override
  public Object getCellEditorValue() {
    return comboBoxPanel.getComboBox().getSelectedItem();
  }

  @Override
  public boolean stopCellEditing() {
    comboBoxPanel.getComboBox().actionPerformed(new ActionEvent(this, 0, ""));
    fireEditingStopped();
    return true;
  }


  @Override
  public boolean shouldSelectCell(EventObject anEvent) {
    if (anEvent instanceof MouseEvent) {
      MouseEvent e = (MouseEvent) anEvent;
      return e.getID() != MouseEvent.MOUSE_DRAGGED;
    }
    return true;
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                               int row, int column) {
    comboBoxPanel.setBackground(table.getSelectionBackground());
    comboBoxPanel.getComboBox().setSelectedItem(value);
    return comboBoxPanel;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                 boolean hasFocus, int row, int column) {
    comboBoxPanel
        .setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
    if (value != null) {
      comboBoxPanel.getComboBox().setSelectedItem(value);
    }
    return comboBoxPanel;
  }

  public static class ComboBoxPanel extends JPanel {
    @Getter
    private JComboBox<?> comboBox;

    public ComboBoxPanel(Object[] items) {
      super(new GridBagLayout());
      setOpaque(true);

      GridBagConstraints c = new GridBagConstraints();
      c.weightx = 1.0;
      c.insets = new Insets(0, 1, 0, 1);
      c.fill = GridBagConstraints.HORIZONTAL;

      comboBox = new JComboBox<Object>(items);
      comboBox.setFocusable(false);
      comboBox.setEditable(false);
      add(comboBox, c);
    }
  }
}
