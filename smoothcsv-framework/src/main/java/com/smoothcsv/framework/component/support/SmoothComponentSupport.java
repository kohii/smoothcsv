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

import java.awt.Container;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.smoothcsv.commons.utils.ArrayUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCContentPane;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 *
 */
public class SmoothComponentSupport {

  private static final String[] EMPTY_ARRAY = new String[0];

  private final SmoothComponent component;
  private final JComponent jComponent;

  private final String type;

  private EventHandler handler = new EventHandler();

  private String[] styleClasses = EMPTY_ARRAY;
  private String[] pseudoClasses = EMPTY_ARRAY;

  /**
   * @param component
   */
  public SmoothComponentSupport(SmoothComponent component, String type) {
    this.component = component;
    this.jComponent = (JComponent) component;
    this.type = type;
    init();
  }

  private void init() {
    installCommandKeyBindings();
    jComponent.setFocusTraversalKeysEnabled(false);

    jComponent.addMouseListener(new PopupMenuMouseAdopter() {
      @Override
      protected boolean showPopup(JComponent c, MouseEvent e) {
        if (component.beforeShowPopupMenu(e)) {
          return super.showPopup(c, e);
        }
        return false;
      }
    });

    // jComponent.addFocusListener(handler);
    // jComponent.addComponentListener(handler);
    jComponent.addHierarchyListener(handler);
  }

  //
  // protected void beforeShowPopup() {
  //
  // }

  public String getComponentType() {
    return type;
  }

  public String[] getStyleClasses() {
    return styleClasses;
  }

  public void setStyleClasses(String[] styleClasses) {
    this.styleClasses = styleClasses;
  }

  public void addStyleClass(String styleClass) {
    this.styleClasses = ArrayUtils.add(styleClasses, styleClass);
  }

  public void removeStyleClass(String styleClass) {
    this.styleClasses = ArrayUtils.remove(styleClasses, styleClass);
  }

  public String[] getPseudoClasses() {
    return pseudoClasses;
  }

  public void addPseudoClass(String pseudoClasse) {
    this.pseudoClasses = ArrayUtils.add(pseudoClasses, pseudoClasse);
  }

  public void removePseudoClass(String pseudoClasse) {
    this.pseudoClasses = ArrayUtils.remove(pseudoClasses, pseudoClasse);
  }

  protected void installCommandKeyBindings() {
    CommandMapFactory commandMapFactory = SCApplication.getApplication().getCommandMapFactory();

    {
      CommandInputMap commandInputMap = commandMapFactory.createInputMap(component);
      jComponent.setInputMap(JComponent.WHEN_FOCUSED, commandInputMap);
    }

    {
      CommandInputMap commandInputMap = commandMapFactory.createInputMap(component);
      jComponent.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, commandInputMap);
    }

    CommandActionMap commandActionMap = commandMapFactory.createActionMap(component);
    commandActionMap.setParent(jComponent.getActionMap());
    jComponent.setActionMap(commandActionMap);
  }

  public boolean invokeKeyAction(KeyStroke ks, KeyEvent e) {
    InputMap map = jComponent.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    ActionMap am = jComponent.getActionMap();

    if (map != null && am != null && jComponent.isEnabled()) {
      Object binding = map.get(ks);
      Action action = (binding == null) ? null : am.get(binding);
      if (action != null) {
        return SwingUtilities.notifyAction(action, ks, e, this, e.getModifiers());
      }
    }
    return false;
  }

  private static boolean isComponentVisible(JComponent component) {
    Container c = component;
    do {
      if (!c.isVisible()) {
        return false;
      }
      if (c instanceof SCContentPane) {
        return !SwingUtils.isModalDialogShowing();
      }
      if (c instanceof DialogBase) {
        return c.isVisible() && ((DialogBase) c).getCanBeActive();
      }
    } while ((c = c.getParent()) != null);
    return false;
  }

  private class EventHandler implements
      // FocusListener,
      // ComponentListener,
      HierarchyListener {

    // ComponentListener------------------
    // @Override
    // public void componentShown(ComponentEvent e) {
    // if (isComponentVisible(jComponent)) {
    // SmoothComponentManager.addVisibleComponent(component);
    // }
    // }
    //
    // @Override
    // public void componentResized(ComponentEvent e) {}
    //
    // @Override
    // public void componentMoved(ComponentEvent e) {}
    //
    // @Override
    // public void componentHidden(ComponentEvent e) {
    // SmoothComponentManager.removeVisibleComponent(component);
    // }

    // HierarchyListener----------------
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
      if (isComponentVisible(jComponent)) {
        SmoothComponentManager.addVisibleComponent(component);
      } else {
        SmoothComponentManager.removeVisibleComponent(component);
      }
    }
  }
}
