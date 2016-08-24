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

import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel;
import com.smoothcsv.core.csvsheet.CsvSheetTextPaneConfig;
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
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class AppearenceEditorPrefPanel extends JPanel {

  public AppearenceEditorPrefPanel() {

    Consumer<Boolean> repaintFunc = e -> SCApplication.components().getFrame().repaint();

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
    chckbxShowEOL.onChange(repaintFunc);
    GridBagConstraints gbc_chckbxShowEOL = new GridBagConstraints();
    gbc_chckbxShowEOL.insets = new Insets(0, 10, 0, 0);
    gbc_chckbxShowEOL.gridwidth = 3;
    gbc_chckbxShowEOL.anchor = GridBagConstraints.WEST;
    gbc_chckbxShowEOL.gridx = 1;
    gbc_chckbxShowEOL.gridy = 1;
    add(chckbxShowEOL, gbc_chckbxShowEOL);

    PrefCheckBox chckbxShowEOF = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.SHOW_EOF, SCBundle.get("key.pref.appearance.showEOF"));
    chckbxShowEOF.onChange(repaintFunc);
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

    JLabel lblTextArea = new PrefTitleLabel(SCBundle.get("key.pref.textArea"));
    GridBagConstraints gbc_lblTextArea = new GridBagConstraints();
    gbc_lblTextArea.insets = new Insets(10, 0, 5, 0);
    gbc_lblTextArea.gridwidth = 4;
    gbc_lblTextArea.anchor = GridBagConstraints.WEST;
    gbc_lblTextArea.gridx = 0;
    gbc_lblTextArea.gridy = 9;
    add(lblTextArea, gbc_lblTextArea);

    Consumer<Boolean> repaintTextAreaFunc = selected -> {
      CsvSheetTextPaneConfig.getInstance().loadFromPreferences();
      SCApplication.components().getFrame().repaint();
    };

    PrefCheckBox chkTextAreaShowEOL = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.TEXT_AREA_SHOW_EOL, SCBundle.get("key.pref.textArea.showEOL"));
    chkTextAreaShowEOL.onChange(repaintTextAreaFunc);
    GridBagConstraints gbc_chkTextAreaShowEOL = new GridBagConstraints();
    gbc_chkTextAreaShowEOL.insets = new Insets(0, 10, 0, 0);
    gbc_chkTextAreaShowEOL.gridwidth = 3;
    gbc_chkTextAreaShowEOL.anchor = GridBagConstraints.WEST;
    gbc_chkTextAreaShowEOL.gridx = 1;
    gbc_chkTextAreaShowEOL.gridy = 10;
    add(chkTextAreaShowEOL, gbc_chkTextAreaShowEOL);

    PrefCheckBox chkTextAreaShowTab = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.TEXT_AREA_SHOW_TAB, SCBundle.get("key.pref.textArea.showTab"));
    chkTextAreaShowTab.onChange(repaintTextAreaFunc);
    GridBagConstraints gbc_chkTextAreaShowTab = new GridBagConstraints();
    gbc_chkTextAreaShowTab.insets = new Insets(0, 10, 0, 0);
    gbc_chkTextAreaShowTab.gridwidth = 3;
    gbc_chkTextAreaShowTab.anchor = GridBagConstraints.WEST;
    gbc_chkTextAreaShowTab.gridx = 1;
    gbc_chkTextAreaShowTab.gridy = 11;
    add(chkTextAreaShowTab, gbc_chkTextAreaShowTab);

    PrefCheckBox chkTextAreaShowSpace = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.TEXT_AREA_SHOW_SPACE, SCBundle.get("key.pref.textArea.showSpace"));
    chkTextAreaShowSpace.onChange(repaintTextAreaFunc);
    GridBagConstraints gbc_chkTextAreaShowSpace = new GridBagConstraints();
    gbc_chkTextAreaShowSpace.insets = new Insets(0, 10, 0, 0);
    gbc_chkTextAreaShowSpace.gridwidth = 3;
    gbc_chkTextAreaShowSpace.anchor = GridBagConstraints.WEST;
    gbc_chkTextAreaShowSpace.gridx = 1;
    gbc_chkTextAreaShowSpace.gridy = 12;
    add(chkTextAreaShowSpace, gbc_chkTextAreaShowSpace);

    PrefCheckBox chkTextAreaWrap = new PrefCheckBox(CoreSettings.getInstance(),
        CoreSettings.TEXT_AREA_WRAP, SCBundle.get("key.pref.textArea.wrap"));
    chkTextAreaWrap.onChange(selected -> {
      CsvGridSheetCellValuePanel.getInstance().getEditorPanel().getScrollPane().setHorizontalScrollBarPolicy(
          selected ? JScrollPane.HORIZONTAL_SCROLLBAR_NEVER : JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
      );
      repaintTextAreaFunc.accept(selected);
    });
    GridBagConstraints gbc_chkTextAreaWrap = new GridBagConstraints();
    gbc_chkTextAreaWrap.insets = new Insets(0, 10, 0, 0);
    gbc_chkTextAreaWrap.gridwidth = 3;
    gbc_chkTextAreaWrap.anchor = GridBagConstraints.WEST;
    gbc_chkTextAreaWrap.gridx = 1;
    gbc_chkTextAreaWrap.gridy = 13;
    add(chkTextAreaWrap, gbc_chkTextAreaWrap);
  }
}
