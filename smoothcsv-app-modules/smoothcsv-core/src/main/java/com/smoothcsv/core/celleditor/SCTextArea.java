/*
 * Copyright 2015 kohii.
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

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.components.ExTextArea;

import lombok.Getter;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class SCTextArea extends ExTextArea implements SmoothComponent {

  // /**
  // * @see #getUIClassID
  // * @see #readObject
  // */
  // private static final String uiClassID = "SCTextAreaUI";

  @Getter
  private final SmoothComponentSupport componentSupport;

  private final ActionMap originalAm;

  public SCTextArea(String componentTypeName) {
    this.originalAm = getActionMap();
    this.componentSupport = new SmoothComponentSupport(this, componentTypeName);
  }

  // @Override
  // public String getUIClassID() {
  // return uiClassID;
  // }

  public void invokeOriginalAction(String key) {
    ActionEvent e = new ActionEvent(this, 0, key);
    Action action = originalAm.get(key);
    action.actionPerformed(e);
  }
}
