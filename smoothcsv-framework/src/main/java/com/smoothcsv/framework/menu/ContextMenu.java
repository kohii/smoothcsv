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
package com.smoothcsv.framework.menu;

import java.awt.Component;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * @author kohii
 */
public class ContextMenu extends JPopupMenu implements IParentMenu {
  private static final long serialVersionUID = -2561700947475373524L;

  public ContextMenu() {
  }

  @Override
  public void addChild(IMenu menu) {
    super.add((JMenuItem) menu);
    menu.onAddedToParent(this);
  }

  @Override
  public String getCaption() {
    return null;
  }

  @Override
  public void setAcceleratorEnabled(boolean enabled) {
    for (Component child : getComponents()) {
      if (child instanceof IMenu) {
        ((IMenu) child).setAcceleratorEnabled(enabled);
      }
    }
  }
}
