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

import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.framework.command.CommandKeymap;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CommandInputMap extends InputMap {

  private static final Logger LOG = LoggerFactory.getLogger(CommandInputMap.class);

  private final SmoothComponent component;

  /**
   * @param component
   */
  protected CommandInputMap(SmoothComponent component) {
    this.component = component;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.InputMap#get(javax.swing.KeyStroke)
   */
  @Override
  public Object get(KeyStroke keyStroke) {
    if (keyStroke.getKeyEventType() != KeyEvent.KEY_PRESSED) {
      Object retValue = super.get(keyStroke);
      if (retValue != null) {
        return retValue;
      }
      if (keyStroke.getKeyEventType() == KeyEvent.KEY_TYPED
          && keyStroke.getKeyChar() != KeyEvent.CHAR_UNDEFINED
          && component instanceof JTextComponent) {
        return DefaultEditorKit.defaultKeyTypedAction;
      }
    }
    CommandKeymap keymap = CommandKeymap.getDefault();
    String commandId = keymap.findCommand(keyStroke, component);
    LOG.debug("Search command. key:{}, context:{} -> command:{}", keyStroke,
        component.getComponentType(), commandId);
    if (commandId != null) {
      return commandId;
    }
    return super.get(keyStroke);
  }

  // @Override
  // public Object get(KeyStroke keyStroke) {
  // if (keyStroke.getKeyEventType() != KeyEvent.KEY_PRESSED) {
  // return null;
  // }
  // CommandKeymap keymap = CommandKeymap.getDefault();
  //
  // Container c = (Container) component;
  // do {
  // if (c instanceof SmoothComponent) {
  // String commandId = keymap.findCommand(keyStroke, (SmoothComponent) c);
  // LOG.debug("Search command. key:{}, context:{} -> command:{}", keyStroke,
  // component.getStyleClasses(), commandId);
  // if (commandId != null || (c instanceof SCContentPane)) {
  // return commandId;
  // }
  // }
  // } while ((c = c.getParent()) != null);
  // return null;
  // }
}
