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

/**
 * @author kohii
 */
public class DefaultGridSheetColorProvider implements GridSheetColorProvider {

  private static final Color SELECTION_BORDER_COLOR = new Color(110, 120, 222);
  private static final Color SELECTION_COLOR = new Color(40, 110, 255, 30);

  private static DefaultGridSheetColorProvider instance;

  public DefaultGridSheetColorProvider() {}

  public static DefaultGridSheetColorProvider getInstance() {
    if (instance == null) {
      instance = new DefaultGridSheetColorProvider();
    }
    return instance;
  }


  @Override
  public Color getRuleLineColor() {
    return Color.LIGHT_GRAY;
  }

  @Override
  public Color getFrozenLineColor() {
    return Color.BLACK;
  }

  @Override
  public Color getSelectionBorderColor() {
    return SELECTION_BORDER_COLOR;
  }

  @Override
  public Color getSelectionColor() {
    return SELECTION_COLOR;
  }

  // private static Color alpha(Color foreground, Color background, int alpha) {
  // double a = ((double) alpha) / ((double) 255);
  // int r = (int) (foreground.getRed() * a + background.getRed() * (1.00 - a));
  // int g = (int) (foreground.getGreen() * a + background.getGreen() * (1.00 - a));
  // int b = (int) (foreground.getBlue() * a + background.getBlue() * (1.00 - a));
  // return new Color(r, g, b);
  // }
}
