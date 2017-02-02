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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.modular.ModuleManifest.Language;
import com.smoothcsv.framework.preference.PrefCheckBox;
import com.smoothcsv.framework.preference.PrefSelectBox;
import com.smoothcsv.framework.preference.PrefTitleLabel;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.icon.AwesomeIconConstants;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class GeneralPrefPanel extends JPanel {

  public GeneralPrefPanel() {
    setBorder(null);
    setLayout(new BorderLayout());
    add(new MainPanel(), BorderLayout.NORTH);
  }

  private static class MainPanel extends JPanel {
    public MainPanel() {
      setBorder(null);
      setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      JLabel langLabel = new PrefTitleLabel(SCBundle.get("key.pref.language"));
      langLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(langLabel);

      List<Language> langs = SCApplication.getApplication().getModuleManager().getAvailableLanguages();
      PrefSelectBox<Language> comboBox =
          new PrefSelectBox<>(CoreSettings.getInstance(), CoreSettings.LANGUAGE, langs, "id", "name");
      comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(comboBox);

      JLabel restartMsgLabel = new JLabel(MessageBundles.getString("ISCA0011"));
      restartMsgLabel.setIcon(AwesomeIcon.create(AwesomeIconConstants.FA_WARNING));
      restartMsgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      restartMsgLabel.setVisible(false);
      add(restartMsgLabel);

      comboBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          restartMsgLabel.setVisible(
              !Locale.getDefault().getLanguage().equals(((Language) e.getItem()).getId()));
        }
      });

      JLabel autoUpdateCheckLabel = new PrefTitleLabel(SCBundle.get("key.pref.autoUpdateCheck"));
      autoUpdateCheckLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 2, 0));
      autoUpdateCheckLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(autoUpdateCheckLabel);

      PrefCheckBox autoUpdateCheck = new PrefCheckBox(
          CoreSettings.getInstance(),
          CoreSettings.AUTO_UPDATE_CHECK,
          SCBundle.get("key.pref.autoUpdateCheckMessage"));
      autoUpdateCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(autoUpdateCheck);
    }
  }
}
