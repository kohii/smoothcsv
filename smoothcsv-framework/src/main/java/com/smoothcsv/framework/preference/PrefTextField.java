/*
 * Copyright 2014 kohii.
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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.swing.components.RegulatedTextField;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class PrefTextField extends RegulatedTextField {

  private String oldText;

  private List<PrefTextValidator> validators = new ArrayList<>();

  public PrefTextField(String prefKey, Type type, int length) {
    super(type, length);

    addFocusListener(new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        String text = getText();
        for (PrefTextValidator validator : validators) {
          if (!validator.validate(text)) {
            setText(oldText);
            return;
          }
        }
        if (getType() == Type.NUMERIC) {
          if (StringUtils.isNotEmpty(text)) {
            text = Integer.toString(Integer.parseInt(text));
            setText(text);
          }
        }
        SettingManager.save(prefKey, text);
      }

      @Override
      public void focusGained(FocusEvent e) {
        oldText = getText();
      }
    });

    addHierarchyListener(new HierarchyListener() {
      @Override
      public void hierarchyChanged(HierarchyEvent e) {

      }
    });

    String value = SettingManager.get(prefKey);
    setText(value);
  }

  public void addValidator(PrefTextValidator validator) {
    validators.add(validator);
  }
}
