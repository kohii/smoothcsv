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
package com.smoothcsv.framework.component;

import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.menu.IMenu;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SCMenuBar extends JMenuBar {

  public SCMenuBar() {
  }

  @Override
  protected void processKeyEvent(KeyEvent e) {
    // do nothing
  }

  @Override
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
    // do nothing
    return false;
  }

  @Override
  public JMenu add(JMenu menu) {
    if (Env.isUsingMacSystemMenuBar()) {
      // HACK: If using mac screen menu bar, it will respond to key events and makes application slow.
      // So we disable accelerator key when the menu bar is closed so that it won't respond to key events.

      menu.addMenuListener(new MenuListener() {
        @Override
        public void menuSelected(MenuEvent e) {
          ((IMenu) e.getSource()).setAcceleratorEnabled(true);
        }

        @Override
        public void menuDeselected(MenuEvent e) {
          ((IMenu) e.getSource()).setAcceleratorEnabled(false);
        }

        @Override
        public void menuCanceled(MenuEvent e) {
          menuDeselected(e);
        }
      });
    }
    return super.add(menu);
  }

  public boolean isOpened() {
    Component[] topLevelMenus = getComponents();
    for (int i = 0; i < topLevelMenus.length; i++) {
      if (((JMenu) topLevelMenus[i]).isSelected()) {
        return true;
      }
    }
    return false;
  }
}
