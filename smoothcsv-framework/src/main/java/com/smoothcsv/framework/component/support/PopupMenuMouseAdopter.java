/*
 * Copyright 2015 kohii
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import com.smoothcsv.framework.menu.ContextMenu;
import com.smoothcsv.framework.menu.ContextMenuManager;

public abstract class PopupMenuMouseAdopter extends MouseAdapter {

  @Override
  public void mousePressed(MouseEvent e) {
    mousePopup(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    mousePopup(e);
  }

  private void mousePopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      JComponent c = (JComponent) e.getSource();
      if (showPopup(c, e)) {
        e.consume();
      }
    }
  }

  protected boolean showPopup(JComponent c, MouseEvent e) {
    ContextMenu pmenu = ContextMenuManager.instance().getContextMenu((SmoothComponent) c);
    if (pmenu == null) {
      return false;
    }
    c.requestFocusInWindow();
    pmenu.show(c, e.getX(), e.getY());
    return true;
  }
}
