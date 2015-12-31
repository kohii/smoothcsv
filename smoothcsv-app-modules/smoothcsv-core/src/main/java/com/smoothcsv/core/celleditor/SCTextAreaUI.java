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
package com.smoothcsv.core.celleditor;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;

/**
 * @author kohii
 *
 */
public class SCTextAreaUI extends BasicTextAreaUI {

  public static ComponentUI createUI(JComponent c) {
    return new SCTextAreaUI();
  }

  transient SCTextArea editor;

  private ActionMap originalAm;

  @Override
  protected void installKeyboardActions() {
    super.installKeyboardActions();

    originalAm = editor.getActionMap();


  }

  @Override
  public void installUI(JComponent c) {
    if (c instanceof SCTextArea) {
      editor = (SCTextArea) c;
    }
  }

  public void invokeOriginalAction(String key, ActionEvent e) {
    originalAm.get(key).actionPerformed(e);
  }
}
