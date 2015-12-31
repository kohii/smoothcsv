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
package com.smoothcsv.core.preference;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandKeymap.Keybinding;
import com.smoothcsv.swing.components.ExTextField;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;
import com.smoothcsv.swing.table.ExTableRowFilter;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class KeyBindingsPrefPanel extends JPanel {

  public KeyBindingsPrefPanel() {
    setLayout(new BorderLayout());

    ExTextField txtFilter = new ExTextField();
    txtFilter.setPlaceholder("Search keybindings");
    add(txtFilter, BorderLayout.NORTH);

    List<RowData> data = new ArrayList<>();
    CommandKeymap.getDefault().getAll().entrySet().stream().forEach(entry -> {
      String source = entry.getKey().toString();
      List<Keybinding> keybindingList = entry.getValue();
      for (Keybinding kb : keybindingList) {
        data.add(new RowData(source, kb.getKeyS(), kb.getCommand()));
      }
    });
    List<ExTableColumn> columns = new ArrayList<>();
    columns.add(new ExTableColumn("Source", "source", false));
    columns.add(new ExTableColumn("Binding", "binding", false));
    columns.add(new ExTableColumn("Command", "command", false));
    ExTableModel<RowData> model = new ExTableModel<>(data, columns);
    ExTable<RowData> table = new ExTable<>(model);

    SwingUtils.autofitColumnWidth(table);

    JScrollPane scrollPane =
        new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    add(scrollPane);

    txtFilter.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        String text = txtFilter.getText();
        if (StringUtils.isBlank(text)) {
          model.doFilter(null);
        } else {
          String[] searchKeywords = StringUtils.split(text.toLowerCase(), " 　\t");
          model.doFilter(new ExTableRowFilter<KeyBindingsPrefPanel.RowData>() {
            @Override
            public boolean include(RowData d, int index) {
              for (String keyword : searchKeywords) {
                if (!StringUtils.containsIgnoreCase(d.getSource(), keyword)
                    && !StringUtils.containsIgnoreCase(d.getBinding(), keyword)
                    && !StringUtils.containsIgnoreCase(d.getCommand(), keyword)) {
                  return false;
                }
              }
              return true;
            }
          });
        }
      }
    });
  }

  @AllArgsConstructor
  @Getter
  public static class RowData {
    private String source;
    private String binding;
    private String command;
  }
}
