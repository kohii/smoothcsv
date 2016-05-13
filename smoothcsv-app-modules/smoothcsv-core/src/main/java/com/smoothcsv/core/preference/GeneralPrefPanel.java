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
import com.smoothcsv.framework.modular.ModuleManifest.Language;
import com.smoothcsv.framework.preference.PrefSelectBox;
import com.smoothcsv.framework.util.MessageBundles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class GeneralPrefPanel extends JPanel {

  public GeneralPrefPanel() {
    setBorder(null);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JLabel lblNewLabel = new JLabel("Language");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.gridwidth = 2;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    List<Language> langs =
        SCApplication.getApplication().getModuleManager().getAvailableLanguages();
    PrefSelectBox<Language> comboBox =
        new PrefSelectBox<>(CoreSettings.getInstance(), CoreSettings.LANGUAGE, langs, "id", "name");
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.insets = new Insets(0, 0, 5, 0);
    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_comboBox.gridx = 1;
    gbc_comboBox.gridy = 1;
    add(comboBox, gbc_comboBox);

    JLabel restartMsgLabel = new JLabel(MessageBundles.getString("ISCA0011"));
    restartMsgLabel.setVisible(false);
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.gridx = 1;
    gbc_lblNewLabel_1.gridy = 2;
    add(restartMsgLabel, gbc_lblNewLabel_1);

    comboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        restartMsgLabel.setVisible(true);
      }
    });
  }
}
