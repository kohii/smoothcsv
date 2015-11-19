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

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;

/**
 * @author kohii
 *
 */
public class PrefUtils {

  public static void invokeItemStateChanged(AbstractButton component) {
    ItemListener[] listeners = component.getItemListeners();
    for (ItemListener l : listeners) {
      int stateChange = component.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED;
      l.itemStateChanged(new ItemEvent(component, -1, component, stateChange));
    }
  }

  public static void invokeFocusLost(Component component) {
    FocusListener[] fls = component.getFocusListeners();
    for (FocusListener focusListener : fls) {
      focusListener.focusLost(new FocusEvent(component, FocusEvent.FOCUS_LOST));
    }
  }
}
