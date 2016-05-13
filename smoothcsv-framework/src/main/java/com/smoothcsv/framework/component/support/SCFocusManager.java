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
package com.smoothcsv.framework.component.support;

import lombok.Getter;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author kohii
 */
public class SCFocusManager {

  // private static final Logger LOG = LoggerFactory.getLogger(SCFocusManager.class);

  public static class FocusOwnerChangeEvent {
    @Getter
    private Component oldFocusOwner;
    @Getter
    private Component newFocusOwner;

    /**
     * @param oldFocusOwner
     * @param newFocusOwner
     */
    public FocusOwnerChangeEvent(Component oldFocusOwner, Component newFocusOwner) {
      this.oldFocusOwner = oldFocusOwner;
      this.newFocusOwner = newFocusOwner;
    }
  }

  private static List<Consumer<FocusOwnerChangeEvent>> focusChangeListeners = new ArrayList<>();

  private static Component focusOwner;

  public static void init() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
        "permanentFocusOwner", new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() != null) {
              setFocusOwner((Component) evt.getNewValue());
            }
          }
        });
  }

  public static void addListener(Consumer<FocusOwnerChangeEvent> listener) {
    focusChangeListeners.add(listener);
  }

  /**
   * @return
   */
  public static Component getFocusOwner() {
    return focusOwner;
  }

  private static void setFocusOwner(Component focusOwner) {
    if (SCFocusManager.focusOwner == focusOwner) {
      return;
    }
    Component old = focusOwner;
    SCFocusManager.focusOwner = focusOwner;
    FocusOwnerChangeEvent e = new FocusOwnerChangeEvent(old, focusOwner);
    for (int i = 0; i < focusChangeListeners.size(); i++) {
      focusChangeListeners.get(i).accept(e);
    }
  }
}
