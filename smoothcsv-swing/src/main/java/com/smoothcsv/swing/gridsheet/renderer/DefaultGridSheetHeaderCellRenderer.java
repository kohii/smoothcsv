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
package com.smoothcsv.swing.gridsheet.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.smoothcsv.swing.gridsheet.AbstractGridSheetHeaderComponent;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class DefaultGridSheetHeaderCellRenderer extends JLabel implements GridSheetHeaderRenderer {

  private static Color DEFAULT_COLOR = new Color(237, 237, 237);

  public DefaultGridSheetHeaderCellRenderer() {
    setOpaque(true);
    setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    setHorizontalAlignment(JLabel.CENTER);
  }

  @Override
  public Component getGridCellRendererComponent(AbstractGridSheetHeaderComponent header,
                                                Object value, boolean isSelected, boolean hasFocus, int index) {

    setValue(value);
    setBackground(DEFAULT_COLOR);
    return this;
  }

  protected void setValue(Object value) {
    setText((value == null) ? "" : value.toString());
  }
}
