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

import com.smoothcsv.core.component.CsvMetaPanel;
import com.smoothcsv.core.sql.model.SqlTableInfo;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlCsvPropertiesPanel extends AbstractSqlTableDetailsPanel {

  private CsvMetaPanel csvMetaPanel;
  private JCheckBox autoDetectChk;

  public SqlCsvPropertiesPanel() {
    setLayout(new BorderLayout());
    csvMetaPanel = new CsvMetaPanel(false, true);
    JScrollPane scrollPane = new JScrollPane(csvMetaPanel);
    scrollPane.setBorder(null);
    add(scrollPane);

    autoDetectChk = new JCheckBox("Detect automatically");
    autoDetectChk.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (autoDetectChk.isEnabled()) {
          csvMetaPanel.setEnabled(!autoDetectChk.isSelected());
        }
      }
    });
    add(autoDetectChk, BorderLayout.NORTH);
  }

  @Override
  public void setEnabled(boolean enabled) {
    autoDetectChk.setEnabled(enabled);
    csvMetaPanel.setEnabled(!autoDetectChk.isSelected() && enabled);
    super.setEnabled(enabled);
  }

  @Override
  protected void load(SqlTableInfo tableInfo) {

  }
}
