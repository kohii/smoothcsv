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
package com.smoothcsv.swing.components;

import java.awt.Color;

import javax.swing.JButton;

import com.smoothcsv.swing.icon.AwesomeIcon;

/**
 * @author kohii
 */
public class AwesomeIconButton extends JButton {

  private static final long serialVersionUID = 2994762677433260023L;

  private AwesomeIcon icon;

  private boolean isDisabledIconSet;

  public AwesomeIconButton(char code, int size) {
    this(null, AwesomeIcon.create(code, size));
  }

  public AwesomeIconButton(char code) {
    this(null, AwesomeIcon.create(code));
  }

  public AwesomeIconButton(String text, char code) {
    this(text, AwesomeIcon.create(code));
  }

  public AwesomeIconButton(AwesomeIcon icon) {
    this(null, icon);
  }

  public AwesomeIconButton(String text, AwesomeIcon icon) {
    super(text, icon);
    this.icon = icon;
    Color rolloverColor = icon.getColor().brighter();
    setRolloverIcon(icon.create(rolloverColor));
    setBorder(null);
    setFocusable(false);
  }

  @Override
  public void setEnabled(boolean b) {
    if (!b && !isDisabledIconSet) {
      setDisabledIcon(icon.create(Color.LIGHT_GRAY));
      isDisabledIconSet = true;
    }
    super.setEnabled(b);
  }
}
