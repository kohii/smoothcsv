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
import com.smoothcsv.csv.CsvQuoteApplyRule;
import com.smoothcsv.framework.preference.PrefButtonGroup;
import com.smoothcsv.framework.preference.PrefCheckBox;
import com.smoothcsv.framework.preference.PrefTextField;
import com.smoothcsv.framework.preference.PrefTextValidator;
import com.smoothcsv.framework.preference.PrefTitleLabel;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.ExRadioButton;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class EditEditorPrefPanel extends JPanel {
  private PrefTextField textField;

  public EditEditorPrefPanel() {
    setBorder(null);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{22, 0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights =
        new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JLabel lblEdit = new PrefTitleLabel(SCBundle.get("key.pref.copy"));
    GridBagConstraints gbc_lblEdit = new GridBagConstraints();
    gbc_lblEdit.insets = new Insets(0, 0, 5, 0);
    gbc_lblEdit.gridwidth = 3;
    gbc_lblEdit.anchor = GridBagConstraints.NORTHWEST;
    gbc_lblEdit.gridx = 0;
    gbc_lblEdit.gridy = 0;
    add(lblEdit, gbc_lblEdit);

    ExRadioButton<CsvQuoteApplyRule> radioButton =
        new ExRadioButton<CsvQuoteApplyRule>(CsvQuoteApplyRule.QUOTES_ALL, "Quote all values");
    GridBagConstraints gbc_radioButton = new GridBagConstraints();
    gbc_radioButton.gridwidth = 2;
    gbc_radioButton.anchor = GridBagConstraints.WEST;
    gbc_radioButton.insets = new Insets(0, 0, 5, 0);
    gbc_radioButton.gridx = 1;
    gbc_radioButton.gridy = 1;
    add(radioButton, gbc_radioButton);

    ExRadioButton<CsvQuoteApplyRule> rdbtnNewRadioButton_3 = new ExRadioButton<CsvQuoteApplyRule>(
        CsvQuoteApplyRule.QUOTES_IF_NECESSARY, "Quote values only when necessary");
    GridBagConstraints gbc_rdbtnNewRadioButton_3 = new GridBagConstraints();
    gbc_rdbtnNewRadioButton_3.gridwidth = 2;
    gbc_rdbtnNewRadioButton_3.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton_3.insets = new Insets(0, 0, 5, 0);
    gbc_rdbtnNewRadioButton_3.gridx = 1;
    gbc_rdbtnNewRadioButton_3.gridy = 2;
    add(rdbtnNewRadioButton_3, gbc_rdbtnNewRadioButton_3);

    ExRadioButton<CsvQuoteApplyRule> rdbtnNewRadioButton_4 =
        new ExRadioButton<CsvQuoteApplyRule>(CsvQuoteApplyRule.NO_QUOTE, "Do not quote");
    GridBagConstraints gbc_rdbtnNewRadioButton_4 = new GridBagConstraints();
    gbc_rdbtnNewRadioButton_4.insets = new Insets(0, 0, 5, 0);
    gbc_rdbtnNewRadioButton_4.gridwidth = 2;
    gbc_rdbtnNewRadioButton_4.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton_4.gridx = 1;
    gbc_rdbtnNewRadioButton_4.gridy = 3;
    add(rdbtnNewRadioButton_4, gbc_rdbtnNewRadioButton_4);

    new PrefButtonGroup<CsvQuoteApplyRule>(CoreSettings.getInstance(),
        CoreSettings.QUOTE_RULE_FOR_COPYING, radioButton, rdbtnNewRadioButton_3,
        rdbtnNewRadioButton_4);

    JLabel lblPaste = new PrefTitleLabel(SCBundle.get("key.pref.paste"));
    GridBagConstraints gbc_lblPaste = new GridBagConstraints();
    gbc_lblPaste.insets = new Insets(0, 0, 5, 0);
    gbc_lblPaste.anchor = GridBagConstraints.WEST;
    gbc_lblPaste.gridwidth = 3;
    gbc_lblPaste.gridx = 0;
    gbc_lblPaste.gridy = 5;
    add(lblPaste, gbc_lblPaste);

    PrefCheckBox rdbtnNewRadioButton_5 =
        new PrefCheckBox(CoreSettings.getInstance(), CoreSettings.PASTE_REPEATEDLY,
            "Fill selected cells with the same data when pasting single data");
    GridBagConstraints gbc_rdbtnNewRadioButton_5 = new GridBagConstraints();
    gbc_rdbtnNewRadioButton_5.insets = new Insets(0, 0, 5, 0);
    gbc_rdbtnNewRadioButton_5.anchor = GridBagConstraints.WEST;
    gbc_rdbtnNewRadioButton_5.gridwidth = 2;
    gbc_rdbtnNewRadioButton_5.gridx = 1;
    gbc_rdbtnNewRadioButton_5.gridy = 6;
    add(rdbtnNewRadioButton_5, gbc_rdbtnNewRadioButton_5);

    JLabel lblUndoredo = new PrefTitleLabel(SCBundle.get("key.pref.undoAndRedo"));
    GridBagConstraints gbc_lblUndoredo = new GridBagConstraints();
    gbc_lblUndoredo.insets = new Insets(0, 0, 5, 0);
    gbc_lblUndoredo.gridwidth = 3;
    gbc_lblUndoredo.anchor = GridBagConstraints.WEST;
    gbc_lblUndoredo.gridx = 0;
    gbc_lblUndoredo.gridy = 8;
    add(lblUndoredo, gbc_lblUndoredo);

    JLabel lblTheMaximumNumber = new JLabel("The maximum number of changes that can be undone:");
    GridBagConstraints gbc_lblTheMaximumNumber = new GridBagConstraints();
    gbc_lblTheMaximumNumber.anchor = GridBagConstraints.WEST;
    gbc_lblTheMaximumNumber.insets = new Insets(0, 0, 5, 0);
    gbc_lblTheMaximumNumber.gridwidth = 2;
    gbc_lblTheMaximumNumber.gridx = 1;
    gbc_lblTheMaximumNumber.gridy = 9;
    add(lblTheMaximumNumber, gbc_lblTheMaximumNumber);

    textField = new PrefTextField(CoreSettings.getInstance(), CoreSettings.SIZE_OF_UNDOING,
        PrefTextField.Type.NUMERIC, 2);
    textField.addValidator(PrefTextValidator.NOT_NULL);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.gridwidth = 2;
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 10;
    add(textField, gbc_textField);
    textField.setColumns(10);

    // PrefButtonGroup<String> buttonGroup =
    // new PrefButtonGroup<String>("file.howToDetectProperties", rdbtnNewRadioButton,
    // rdbtnNewRadioButton_1, rdbtnNewRadioButton_2);
  }
}
