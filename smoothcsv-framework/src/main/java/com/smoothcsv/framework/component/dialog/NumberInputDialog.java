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
package com.smoothcsv.framework.component.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.swing.components.RegulatedTextField;

public class NumberInputDialog extends DialogBase {

  private static final long serialVersionUID = -5662546861883880815L;

  private static NumberInputDialog defaultInstance = new NumberInputDialog(
      SCApplication.components().getFrame(), SCApplication.getApplication().getName(), "");

  public static NumberInputDialog getInstance() {
    return defaultInstance;
  }

  public static Integer showDialog(String msg) {
    NumberInputDialog dialog = getInstance();
    dialog.setMessage(msg);
    dialog.textField.setText("");
    dialog.pack();
    if (dialog.showDialog() != DialogOperation.OK) {
      return null;
    }
    return dialog.getResult();
  }

  private JTextField textField;

  private Integer result;

  private JLabel msgLabel;

  public NumberInputDialog(Dialog owner, String title, String msg, int maxLen) {
    super(owner, title);
    init(msg, maxLen);
  }

  public NumberInputDialog(Frame owner, String title, String msg, int maxLen) {
    super(owner, title);
    init(msg, maxLen);
  }

  public NumberInputDialog(Dialog owner, String title, String msg) {
    this(owner, title, msg, 5);
  }

  /**
   * @wbp.parser.constructor
   */
  public NumberInputDialog(Frame owner, String title, String msg) {
    this(owner, title, msg, 5);
  }

  private void init(String msg, int maxLen) {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0};
    gridBagLayout.rowHeights = new int[] {0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    getContentPanel().setLayout(gridBagLayout);

    msgLabel = new JLabel(msg);
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    getContentPanel().add(msgLabel, gbc_lblNewLabel);

    textField = new RegulatedTextField(RegulatedTextField.Type.NUMERIC, 6);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 0;
    gbc_textField.gridy = 1;
    getContentPanel().add(textField, gbc_textField);
    textField.setColumns(8);
    pack();
  }

  public void setMessage(String msg) {
    this.msgLabel.setText(msg);
  }

  @Override
  protected boolean processOperation(DialogOperation operation) {
    if (operation == DialogOperation.OK) {
      this.result = Integer.parseInt(textField.getText());
    }
    return super.processOperation(operation);
  }

  public Integer getResult() {
    return result;
  }
}
