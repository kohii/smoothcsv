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

import javax.swing.JToggleButton;

import com.smoothcsv.swing.icon.AwesomeIcon;

/**
 * @author kohii
 */
public class AwesomeIconToggleButton extends JToggleButton {

  private static final long serialVersionUID = 2994762677433260023L;
  private static final Color selectedIconForegroundColor = new Color(57, 172, 230);

  private AwesomeIcon icon;

  private boolean isDisabledIconSet;

  public AwesomeIconToggleButton(char code, int size) {
    this(AwesomeIcon.create(code, size));
  }

  public AwesomeIconToggleButton(char code) {
    this(AwesomeIcon.create(code));
  }

  public AwesomeIconToggleButton(AwesomeIcon icon) {
    super(icon);
    this.icon = icon;
    Color rolloverColor = icon.getColor().brighter();
    setRolloverIcon(icon.create(rolloverColor));
    setSelectedIcon(icon.create(selectedIconForegroundColor));
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
