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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.commons.utils.HtmlUtils;
import com.smoothcsv.core.component.CsvPropertiesDialog;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.preference.PrefButtonGroup;
import com.smoothcsv.framework.preference.PrefCheckBox;
import com.smoothcsv.framework.preference.PrefTextField;
import com.smoothcsv.framework.preference.PrefTextValidator;
import com.smoothcsv.framework.preference.PrefTitleLabel;
import com.smoothcsv.framework.preference.PrefUtils;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.swing.components.ExLabel;
import com.smoothcsv.swing.components.ExRadioButton;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class FileEditorPrefPanel extends JPanel {

  private JLabel defaultPropLabel;

  public FileEditorPrefPanel() {
    setBorder(null);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {22, 25, 0, 0};
    gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JLabel lblOpen = new PrefTitleLabel("Open File");
    GridBagConstraints gbc_lblOpen = new GridBagConstraints();
    gbc_lblOpen.anchor = GridBagConstraints.WEST;
    gbc_lblOpen.gridwidth = 3;
    gbc_lblOpen.insets = new Insets(0, 0, 5, 0);
    gbc_lblOpen.gridx = 0;
    gbc_lblOpen.gridy = 0;
    add(lblOpen, gbc_lblOpen);

    ExRadioButton<String> rdbtnNewRadioButton =
        new ExRadioButton<>("manual", "Choose properties manually");
    GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
    gbc_rdbtnNewRadioButton.gridwidth = 2;
    gbc_rdbtnNewRadioButton.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton.gridx = 1;
    gbc_rdbtnNewRadioButton.gridy = 1;
    add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);

    ExRadioButton<String> rdbtnNewRadioButton_1 =
        new ExRadioButton<>("auto", "Detect properties automatically");
    GridBagConstraints gbc_rdbtnNewRadioButton_1 = new GridBagConstraints();
    gbc_rdbtnNewRadioButton_1.gridwidth = 2;
    gbc_rdbtnNewRadioButton_1.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton_1.gridx = 1;
    gbc_rdbtnNewRadioButton_1.gridy = 2;
    add(rdbtnNewRadioButton_1, gbc_rdbtnNewRadioButton_1);

    ExRadioButton<String> rdbtnNewRadioButton_2 =
        new ExRadioButton<>("default", "Use default properties");
    GridBagConstraints gbc_rdbtnNewRadioButton_2 = new GridBagConstraints();
    gbc_rdbtnNewRadioButton_2.gridwidth = 2;
    gbc_rdbtnNewRadioButton_2.insets = new Insets(0, 0, 5, 0);
    gbc_rdbtnNewRadioButton_2.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton_2.gridx = 1;
    gbc_rdbtnNewRadioButton_2.gridy = 3;
    add(rdbtnNewRadioButton_2, gbc_rdbtnNewRadioButton_2);

    new PrefButtonGroup<String>(CoreSettings.getInstance(),
        CoreSettings.HOW_TO_DETECT_PROPERTIES, rdbtnNewRadioButton, rdbtnNewRadioButton_1,
        rdbtnNewRadioButton_2);

    PrefCheckBox chckbxNewCheckBox = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.ALERT_ON_OPENING_HUGE_FILE, "Alert before opening a huge file");
    GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
    gbc_chckbxNewCheckBox.insets = new Insets(10, 0, 0, 0);
    gbc_chckbxNewCheckBox.gridwidth = 2;
    gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
    gbc_chckbxNewCheckBox.gridx = 1;
    gbc_chckbxNewCheckBox.gridy = 4;
    add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

    PrefTextField txtAlertThresholdFileSize = new PrefTextField(CoreSettings.getInstance(),
        CoreSettings.ALERT_THRESHOLD, PrefTextField.Type.NUMERIC, 6);
    txtAlertThresholdFileSize.addValidator(PrefTextValidator.MORE_THAN_ZERO);
    ExLabel lblNewLabel_1 =
        new ExLabel("When the size is more than {} MB", txtAlertThresholdFileSize);
    FlowLayout flowLayout = (FlowLayout) lblNewLabel_1.getLayout();
    flowLayout.setAlignment(FlowLayout.LEADING);
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
    gbc_lblNewLabel_1.gridx = 2;
    gbc_lblNewLabel_1.gridy = 5;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    JLabel lblNewLabel = new PrefTitleLabel("New File");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.gridwidth = 3;
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 7;
    add(lblNewLabel, gbc_lblNewLabel);

    JLabel lblDefaultProperties = new JLabel("Default Properties:");
    GridBagConstraints gbc_lblDefaultProperties = new GridBagConstraints();
    gbc_lblDefaultProperties.insets = new Insets(0, 0, 5, 0);
    gbc_lblDefaultProperties.gridwidth = 2;
    gbc_lblDefaultProperties.anchor = GridBagConstraints.WEST;
    gbc_lblDefaultProperties.gridx = 1;
    gbc_lblDefaultProperties.gridy = 8;
    add(lblDefaultProperties, gbc_lblDefaultProperties);

    defaultPropLabel = new JLabel();
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.insets = new Insets(0, 0, 5, 0);
    gbc_label.anchor = GridBagConstraints.WEST;
    gbc_label.gridwidth = 2;
    gbc_label.gridx = 1;
    gbc_label.gridy = 9;
    add(defaultPropLabel, gbc_label);
    displayDefaultProperties();

    JButton btnEdit = new JButton("Edit...");
    GridBagConstraints gbc_btnEdit = new GridBagConstraints();
    gbc_btnEdit.insets = new Insets(0, 0, 5, 0);
    gbc_btnEdit.anchor = GridBagConstraints.WEST;
    gbc_btnEdit.gridwidth = 2;
    gbc_btnEdit.gridx = 1;
    gbc_btnEdit.gridy = 10;
    add(btnEdit, gbc_btnEdit);

    // add events

    chckbxNewCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        txtAlertThresholdFileSize.setEnabled(chckbxNewCheckBox.isSelected());
      }
    });
    PrefUtils.invokeItemStateChanged(chckbxNewCheckBox);

    btnEdit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CsvPropertiesDialog dialog = new CsvPropertiesDialog(SCApplication.components().getFrame(),
            "Properties", false, false, true);

        Settings settings = CoreSettings.getInstance();
        String r = settings.get(CoreSettings.DEFAULT_ROW_SIZE);
        String c = settings.get(CoreSettings.DEFAULT_COLUMN_SIZE);
        dialog.setGirdSize(Integer.parseInt(r), Integer.parseInt(c));
        dialog.setCsvProperties(CsvSheetSupport.getDefaultCsvMeta());
        dialog.pack();

        if (dialog.showDialog() == DialogOperation.OK) {
          int row = dialog.getRowCount();
          int column = dialog.getColumnCount();
          CsvMeta csvMeta = dialog.getCsvMeta();
          CsvSheetSupport.setDefaultCsvMeta(csvMeta);
          Settings fileSettings = CoreSettings.getInstance();
          Map<String, Object> map = new HashMap<>();
          map.put(CoreSettings.DEFAULT_ROW_SIZE, row);
          map.put(CoreSettings.DEFAULT_COLUMN_SIZE, column);
          fileSettings.saveAll(map);

          displayDefaultProperties();
        }
      }
    });
  }

  private void displayDefaultProperties() {
    Settings settings = CoreSettings.getInstance();
    String r = settings.get(CoreSettings.DEFAULT_ROW_SIZE);
    String c = settings.get(CoreSettings.DEFAULT_COLUMN_SIZE);
    String s = CsvSheetSupport.getDefaultCsvMeta().toDisplayString(Integer.parseInt(r),
        Integer.parseInt(c));
    defaultPropLabel.setText("<html>" + HtmlUtils.escapeHtml(s) + "</html>");
  }
}
