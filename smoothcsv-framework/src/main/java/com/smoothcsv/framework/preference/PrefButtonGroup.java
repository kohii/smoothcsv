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

import java.util.Objects;

import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.swing.components.ExButtonGroup;
import com.smoothcsv.swing.components.ExRadioButton;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class PrefButtonGroup<V> extends ExButtonGroup<V> {

  @SuppressWarnings("unchecked")
  public PrefButtonGroup(Settings settings, String prefKey, ExRadioButton<V>... radioButtons) {
    super(radioButtons);

    String v = settings.get(prefKey);
    for (ExRadioButton<V> rb : radioButtons) {
      if (Objects.equals(rb.getValue().toString(), v)) {
        rb.setSelected(true);
        break;
      }
    }

    addSelectionListener(rb -> settings.save(prefKey, rb.getValue()));
  }
}
