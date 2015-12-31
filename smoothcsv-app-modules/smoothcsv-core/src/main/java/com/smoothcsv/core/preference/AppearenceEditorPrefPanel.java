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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.smoothcsv.framework.preference.PrefCheckBox;
import com.smoothcsv.framework.preference.PrefTextField;
import com.smoothcsv.framework.preference.PrefTextValidator;
import com.smoothcsv.framework.preference.PrefTitleLabel;
import com.smoothcsv.swing.components.ExLabel;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class AppearenceEditorPrefPanel extends JPanel {

  public AppearenceEditorPrefPanel() {
    setBorder(null);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 22, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JLabel lblAutofitColumnWidth = new PrefTitleLabel("Auto-fit Column Width");
    GridBagConstraints gbc_lblAutofitColumnWidth = new GridBagConstraints();
    gbc_lblAutofitColumnWidth.insets = new Insets(0, 0, 5, 0);
    gbc_lblAutofitColumnWidth.gridwidth = 4;
    gbc_lblAutofitColumnWidth.anchor = GridBagConstraints.WEST;
    gbc_lblAutofitColumnWidth.gridx = 0;
    gbc_lblAutofitColumnWidth.gridy = 0;
    add(lblAutofitColumnWidth, gbc_lblAutofitColumnWidth);

    PrefCheckBox chckbxAutofitColumnWidth =
        new PrefCheckBox("editor.autoFitColumnWidthAfterOpeningFile",
            "Auto-fit column width after opening a file");
    GridBagConstraints gbc_chckbxAutofitColumnWidth = new GridBagConstraints();
    gbc_chckbxAutofitColumnWidth.insets = new Insets(0, 0, 5, 0);
    gbc_chckbxAutofitColumnWidth.anchor = GridBagConstraints.WEST;
    gbc_chckbxAutofitColumnWidth.gridwidth = 3;
    gbc_chckbxAutofitColumnWidth.gridx = 1;
    gbc_chckbxAutofitColumnWidth.gridy = 1;
    add(chckbxAutofitColumnWidth, gbc_chckbxAutofitColumnWidth);

    PrefCheckBox chckbxNewCheckBox_1 =
        new PrefCheckBox("editor.autoFitColumnWidthWithLimitedRowSize",
            "Limit the number of rows to scan");
    GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
    gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
    gbc_chckbxNewCheckBox_1.gridwidth = 3;
    gbc_chckbxNewCheckBox_1.gridx = 1;
    gbc_chckbxNewCheckBox_1.gridy = 2;
    add(chckbxNewCheckBox_1, gbc_chckbxNewCheckBox_1);

    PrefTextField txtRowsToScan =
        new PrefTextField("editor.rowSizeToScanWhenAutoFitting", PrefTextField.Type.NUMERIC, 8);
    txtRowsToScan.addValidator(PrefTextValidator.MORE_THAN_ZERO);
    ExLabel lblNewLabel =
        new ExLabel("Scan the first {} rows to auto-fit column width", txtRowsToScan);
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 2;
    gbc_lblNewLabel.gridy = 3;
    add(lblNewLabel, gbc_lblNewLabel);

    PrefCheckBox chckbxSetTheMaximum =
        new PrefCheckBox("editor.limitWidthWhenAutoFitting", "Set the maximum column width");
    GridBagConstraints gbc_chckbxSetTheMaximum = new GridBagConstraints();
    gbc_chckbxSetTheMaximum.gridwidth = 3;
    gbc_chckbxSetTheMaximum.anchor = GridBagConstraints.WEST;
    gbc_chckbxSetTheMaximum.gridx = 1;
    gbc_chckbxSetTheMaximum.gridy = 4;
    add(chckbxSetTheMaximum, gbc_chckbxSetTheMaximum);

    PrefTextField txtColWidth =
        new PrefTextField("editor.maxColumnWidthPerWindowWhenAutoFitting",
            PrefTextField.Type.NUMERIC, 3);
    txtColWidth.addValidator(PrefTextValidator.MORE_THAN_ZERO);
    ExLabel lblNewLabel_1 =
        new ExLabel("Each column width should be less than {} % of the window width", txtColWidth);
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 2;
    gbc_lblNewLabel_1.gridy = 5;
    add(lblNewLabel_1, gbc_lblNewLabel_1);
  }
}
