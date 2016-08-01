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
package com.smoothcsv.core.preference;

import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.preference.PrefCheckBox;
import com.smoothcsv.framework.preference.PrefTextField;
import com.smoothcsv.framework.preference.PrefTextValidator;
import com.smoothcsv.framework.preference.PrefTitleLabel;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.ExLabel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class AppearenceEditorPrefPanel extends JPanel {

  public AppearenceEditorPrefPanel() {

    ItemListener repaintFunc = e -> SCApplication.components().getFrame().repaint();

    setBorder(null);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 22, 0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JLabel lblAppearance = new PrefTitleLabel(SCBundle.get("key.pref.appearance"));
    GridBagConstraints gbc_lblAppearance = new GridBagConstraints();
    gbc_lblAppearance.insets = new Insets(0, 0, 5, 0);
    gbc_lblAppearance.gridwidth = 4;
    gbc_lblAppearance.anchor = GridBagConstraints.WEST;
    gbc_lblAppearance.gridx = 0;
    gbc_lblAppearance.gridy = 0;
    add(lblAppearance, gbc_lblAppearance);

    PrefCheckBox chckbxShowEOL = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.SHOW_EOL, SCBundle.get("key.pref.appearance.showEOL"));
    chckbxShowEOL.addItemListener(repaintFunc);
    GridBagConstraints gbc_chckbxShowEOL = new GridBagConstraints();
    gbc_chckbxShowEOL.insets = new Insets(0, 10, 0, 0);
    gbc_chckbxShowEOL.gridwidth = 3;
    gbc_chckbxShowEOL.anchor = GridBagConstraints.WEST;
    gbc_chckbxShowEOL.gridx = 1;
    gbc_chckbxShowEOL.gridy = 1;
    add(chckbxShowEOL, gbc_chckbxShowEOL);

    PrefCheckBox chckbxShowEOF = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.SHOW_EOF, SCBundle.get("key.pref.appearance.showEOF"));
    chckbxShowEOF.addItemListener(repaintFunc);
    GridBagConstraints gbc_chckbxShowEOF = new GridBagConstraints();
    gbc_chckbxShowEOF.insets = new Insets(0, 10, 0, 0);
    gbc_chckbxShowEOF.gridwidth = 3;
    gbc_chckbxShowEOF.anchor = GridBagConstraints.WEST;
    gbc_chckbxShowEOF.gridx = 1;
    gbc_chckbxShowEOF.gridy = 2;
    add(chckbxShowEOF, gbc_chckbxShowEOF);

    JLabel lblAutofitColumnWidth = new PrefTitleLabel(SCBundle.get("key.pref.autoFitColumnWidth"));
    GridBagConstraints gbc_lblAutofitColumnWidth = new GridBagConstraints();
    gbc_lblAutofitColumnWidth.insets = new Insets(10, 0, 5, 0);
    gbc_lblAutofitColumnWidth.gridwidth = 4;
    gbc_lblAutofitColumnWidth.anchor = GridBagConstraints.WEST;
    gbc_lblAutofitColumnWidth.gridx = 0;
    gbc_lblAutofitColumnWidth.gridy = 3;
    add(lblAutofitColumnWidth, gbc_lblAutofitColumnWidth);

    PrefCheckBox chckbxAutofitColumnWidth = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE,
        SCBundle.get("key.pref.autofitAfterOpening"));
    GridBagConstraints gbc_chckbxAutofitColumnWidth = new GridBagConstraints();
    gbc_chckbxAutofitColumnWidth.insets = new Insets(0, 10, 5, 0);
    gbc_chckbxAutofitColumnWidth.anchor = GridBagConstraints.WEST;
    gbc_chckbxAutofitColumnWidth.gridwidth = 3;
    gbc_chckbxAutofitColumnWidth.gridx = 1;
    gbc_chckbxAutofitColumnWidth.gridy = 4;
    add(chckbxAutofitColumnWidth, gbc_chckbxAutofitColumnWidth);

    PrefCheckBox chckbxNewCheckBox_1 = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE,
        SCBundle.get("key.pref.limitRowsToScan"));
    GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
    gbc_chckbxNewCheckBox_1.insets = new Insets(0, 10, 5, 0);
    gbc_chckbxNewCheckBox_1.anchor = GridBagConstraints.WEST;
    gbc_chckbxNewCheckBox_1.gridwidth = 3;
    gbc_chckbxNewCheckBox_1.gridx = 1;
    gbc_chckbxNewCheckBox_1.gridy = 5;
    add(chckbxNewCheckBox_1, gbc_chckbxNewCheckBox_1);

    PrefTextField txtRowsToScan = new PrefTextField(CoreSettings.getInstance(),
        CoreSettings.ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING, PrefTextField.Type.NUMERIC, 8);
    txtRowsToScan.addValidator(PrefTextValidator.MORE_THAN_ZERO);
    ExLabel lblNewLabel =
        new ExLabel(SCBundle.get("key.pref.limitRowsToScan.numRows"), txtRowsToScan);
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.insets = new Insets(0, 15, 5, 0);
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.gridx = 2;
    gbc_lblNewLabel.gridy = 6;
    add(lblNewLabel, gbc_lblNewLabel);

    PrefCheckBox chckbxSetTheMaximum = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.LIMIT_WIDTH_WHEN_AUTO_FITTING, SCBundle.get("key.pref.setMaxColumnWidth"));
    GridBagConstraints gbc_chckbxSetTheMaximum = new GridBagConstraints();
    gbc_chckbxSetTheMaximum.insets = new Insets(0, 10, 5, 0);
    gbc_chckbxSetTheMaximum.gridwidth = 3;
    gbc_chckbxSetTheMaximum.anchor = GridBagConstraints.WEST;
    gbc_chckbxSetTheMaximum.gridx = 1;
    gbc_chckbxSetTheMaximum.gridy = 7;
    add(chckbxSetTheMaximum, gbc_chckbxSetTheMaximum);

    PrefTextField txtColWidth = new PrefTextField(CoreSettings.getInstance(),
        CoreSettings.MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING,
        PrefTextField.Type.NUMERIC, 3);
    txtColWidth.addValidator(PrefTextValidator.MORE_THAN_ZERO);
    ExLabel lblNewLabel_1 =
        new ExLabel(SCBundle.get("key.pref.maxColumnWidthPerWindow"), txtColWidth);
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.insets = new Insets(0, 10, 5, 5);
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.gridx = 2;
    gbc_lblNewLabel_1.gridy = 8;
    add(lblNewLabel_1, gbc_lblNewLabel_1);
  }
}
