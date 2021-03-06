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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JCheckBox;

import com.smoothcsv.framework.setting.Settings;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class PrefCheckBox extends JCheckBox {

  private List<Consumer<Boolean>> listeners = new ArrayList<>();

  public PrefCheckBox(Settings settings, String prefKey, String text) {
    super(text);

    String value = settings.get(prefKey);
    setSelected(Boolean.valueOf(value));

    addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        settings.save(prefKey, isSelected());
        for (Consumer<Boolean> listener : listeners) {
          listener.accept(isSelected());
        }
      }
    });
  }

  public void onChange(Consumer<Boolean> listener) {
    listeners.add(listener);
  }
}
