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
