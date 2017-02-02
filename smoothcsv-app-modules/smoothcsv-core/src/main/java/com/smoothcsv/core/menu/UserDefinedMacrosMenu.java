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
package com.smoothcsv.core.menu;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import com.smoothcsv.core.macro.MacroInfo;
import com.smoothcsv.core.macro.UserDefinedMacroList;
import com.smoothcsv.framework.menu.CommandMenuItem;
import com.smoothcsv.framework.menu.IMenu;
import com.smoothcsv.framework.menu.IParentMenu;
import com.smoothcsv.framework.util.SCBundle;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class UserDefinedMacrosMenu extends JMenu implements IMenu {

  private final String caption;

  private boolean valid = false;

  public UserDefinedMacrosMenu(String caption) {
    this.caption = caption;
    IMenu.setCaption(this, caption);

    UserDefinedMacroList.getInstance().addListener(macroInfos -> invalidate());
  }

  /**
   * @return the caption
   */
  @Override
  public String getCaption() {
    return caption;
  }

  @Override
  public void setAcceleratorEnabled(boolean enabled) {
    Component[] children = getMenuComponents();
    for (Component child : children) {
      if (child instanceof IMenu) {
        ((IMenu) child).setAcceleratorEnabled(enabled);
      }
    }
  }

  public void invalidate() {
    valid = false;
  }

  @Override
  public void onAddedToParent(IParentMenu parent) {
    ((JMenu) parent).addMenuListener(new MenuListener() {
      @Override
      public void menuSelected(MenuEvent e) {
        prepareChildrenMenuItems();
      }

      @Override
      public void menuDeselected(MenuEvent e) {}

      @Override
      public void menuCanceled(MenuEvent e) {}
    });
  }

  private void prepareChildrenMenuItems() {
    if (valid) {
      return;
    }

    removeAll();

    int count = 0;
    for (MacroInfo macroInfo : UserDefinedMacroList.getInstance().getMacroInfoList()) {
      if (count > 20) {
        break;
      }
      add(new RunMacroMenuItem(macroInfo.getFile()));
      count++;
    }
    if (!UserDefinedMacroList.getInstance().getMacroInfoList().isEmpty()) {
      addSeparator();
    }
    add(new CommandMenuItem(SCBundle.get("key.editMacroList"), "macro:ShowList"));
    valid = true;
  }
}
