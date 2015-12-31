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
package com.smoothcsv.framework.util;

import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

/**
 *
 * @author kohii
 */
public class ImageUtils {

  public static ImageIcon getImageIcon(String name) {
    return getImageIcon(name, ImageUtils.class);
  }

  public static ImageIcon getImageIcon(String name, Class<?> baseClass) {
    return new ImageIcon(baseClass.getResource("/img/" + name));
  }

  public static Image getImage(String name) {
    return getImage(name, ImageUtils.class);
  }

  public static Image getImage(String name, Class<?> baseClass) {
    Toolkit tk = Toolkit.getDefaultToolkit();
    return tk.createImage(baseClass.getResource(name));
  }

}
