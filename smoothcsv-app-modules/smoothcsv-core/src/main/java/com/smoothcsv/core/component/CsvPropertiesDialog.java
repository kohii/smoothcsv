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
package com.smoothcsv.core.component;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.RegulatedTextField;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvPropertiesDialog extends DialogBase {

  private final boolean autoDeterminedOptionEnabled;
  private final boolean readMode;
  private final boolean showSizeOption;

  private CsvMetaPanel csvMetaPanel;
  private JLabel messageLabel;
  private RegulatedTextField rowNum;
  private RegulatedTextField colNum;

  public CsvPropertiesDialog(Dialog parent, String title, boolean autoDeterminedOptionEnabled,
                             boolean readMode, boolean showSizeOption) {
    super(parent, title);
    setAutoPack(true);
    this.autoDeterminedOptionEnabled = autoDeterminedOptionEnabled;
    this.readMode = readMode;
    this.showSizeOption = showSizeOption;
    initialize();
  }

  public CsvPropertiesDialog(Frame parent, String title, boolean autoDeterminedOptionEnabled,
                             boolean readMode, boolean showSizeOption) {
    super(parent, title);
    setAutoPack(true);
    this.autoDeterminedOptionEnabled = autoDeterminedOptionEnabled;
    this.readMode = readMode;
    this.showSizeOption = showSizeOption;
    initialize();
  }

  protected void initialize() {
    JPanel panel = getContentPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

    messageLabel = new JLabel();
    messageLabel.setAlignmentX(LEFT_ALIGNMENT);
    panel.add(messageLabel);
    if (showSizeOption) {
      panel.add(createTableSizePanel());
    }
    panel.add(createPropPanel());
  }

  private JPanel createTableSizePanel() {

    JPanel columnAndRowSizePanel = new JPanel();
    columnAndRowSizePanel
        .setBorder(BorderFactory.createTitledBorder(CoreBundle.get("key.gridSize")));

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    columnAndRowSizePanel.setLayout(gridBagLayout);

    JLabel label_4 = new JLabel("　");
    GridBagConstraints gbc_label_4 = new GridBagConstraints();
    gbc_label_4.insets = new Insets(0, 0, 5, 5);
    gbc_label_4.gridx = 0;
    gbc_label_4.gridy = 1;
    columnAndRowSizePanel.add(label_4, gbc_label_4);

    JLabel label_1 = new JLabel(CoreBundle.get("key.rowCount"));
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.WEST;
    gbc_label_1.insets = new Insets(0, 0, 5, 5);
    gbc_label_1.gridx = 1;
    gbc_label_1.gridy = 1;
    columnAndRowSizePanel.add(label_1, gbc_label_1);

    rowNum = new RegulatedTextField(RegulatedTextField.Type.NUMERIC, 8);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 2;
    gbc_textField.gridy = 1;
    columnAndRowSizePanel.add(rowNum, gbc_textField);
    rowNum.setColumns(10);

    JLabel label_2 = new JLabel(CoreBundle.get("key.columnCount"));
    GridBagConstraints gbc_label_2 = new GridBagConstraints();
    gbc_label_2.anchor = GridBagConstraints.WEST;
    gbc_label_2.insets = new Insets(0, 0, 5, 5);
    gbc_label_2.gridx = 1;
    gbc_label_2.gridy = 2;
    columnAndRowSizePanel.add(label_2, gbc_label_2);

    colNum = new RegulatedTextField(RegulatedTextField.Type.NUMERIC, 8);
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 5, 0);
    gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_1.gridx = 2;
    gbc_textField_1.gridy = 2;
    columnAndRowSizePanel.add(colNum, gbc_textField_1);
    colNum.setColumns(10);

    return columnAndRowSizePanel;
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      validateInput();
    }
    return super.processOperation(selectedOperation);
  }

  private JPanel createPropPanel() {
    JPanel propPanel = new JPanel();
    propPanel.setLayout(new BorderLayout());
    propPanel.setBorder(BorderFactory.createTitledBorder(SCBundle.get("key.properties")));

    csvMetaPanel = new CsvMetaPanel(autoDeterminedOptionEnabled, readMode);
    propPanel.add(csvMetaPanel, BorderLayout.CENTER);

    return propPanel;
  }

  private JPanel showReadOptionPanel() {
    JPanel propPanel = new JPanel();
    propPanel.setLayout(new BorderLayout());
    propPanel.setBorder(BorderFactory.createTitledBorder("Options"));

    csvMetaPanel = new CsvMetaPanel(autoDeterminedOptionEnabled);
    propPanel.add(csvMetaPanel, BorderLayout.CENTER);

    return propPanel;
  }


  public void setMessage(String msg) {
    messageLabel.setText(msg);
  }

  public void setGirdSize(int r, int c) {
    rowNum.setText(r);
    colNum.setText(c);
  }

  public void setCsvProperties(CsvMeta csvProperties) {
    csvMetaPanel.load(csvProperties);
  }

  public int getRowCount() {
    return Integer.parseInt(rowNum.getText());
  }

  public int getColumnCount() {
    return Integer.parseInt(colNum.getText());
  }

  public CsvMeta getCsvMeta() {
    CsvMeta csvMeta = new CsvMeta();
    csvMetaPanel.save(csvMeta);
    return csvMeta;
  }

  public void validateInput() {
    csvMetaPanel.validateInput();

    if (showSizeOption) {
      if (StringUtils.isEmpty(rowNum.getText())) {
        throw new AppException("WSCC0009", SCBundle.get("key.rowCount"));
      } else if (StringUtils.isEmpty(colNum.getText())) {
        throw new AppException("WSCC0009", SCBundle.get("key.columnCount"));
      }
    }
  }
}
