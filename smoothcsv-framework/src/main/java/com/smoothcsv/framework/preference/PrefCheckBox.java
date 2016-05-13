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
package com.smoothcsv.framework.preference;

import com.smoothcsv.framework.setting.Settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class PrefCheckBox extends JCheckBox {

  public PrefCheckBox(Settings settings, String prefKey, String text) {
    super(text);

    String value = settings.get(prefKey);
    setSelected(Boolean.valueOf(value));

    addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        settings.save(prefKey, isSelected());
      }
    });
  }
}
