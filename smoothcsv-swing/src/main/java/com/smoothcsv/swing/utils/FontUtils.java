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
package com.smoothcsv.swing.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * @author kohii
 */
public class FontUtils {

  public static boolean isAvailableFont(String fontName) {
    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for (Font font : fonts) {
      if (font.getName().equals(fontName)) {
        return true;
      }
    }
    return false;
  }

  public static void setDefaultFont(Font font) {
    FontUIResource fontUIResource = new FontUIResource(font);
    for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults().entrySet()) {
      if (entry.getKey().toString().toLowerCase().endsWith("font")) {
        UIManager.put(entry.getKey(), fontUIResource);
      }
    }
  }

  public static boolean canDisplay2ByteChar(Font font) {
    return font.canDisplay('　');
  }

  public static String[] getAvailableFontNames() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fs = ge.getAvailableFontFamilyNames();
    return fs;
  }

}
