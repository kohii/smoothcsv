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
package com.smoothcsv.swing.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

import com.smoothcsv.commons.exception.UnexpectedException;

/**
 * Create icon from Font-Awesome 4.7.0
 *
 * @author kohii
 * @see http://fortawesome.github.io/Font-Awesome/
 */
public class AwesomeIcon implements Icon, AwesomeIconConstants {

  private static final String FONTAWESOME_TTF = "/font-awesome-4.7.0/fonts/fontawesome-webfont.ttf";

  private static final Font AWESOME;

  private static int defaultIconSize = 16;
  private static Color defaultIconColor = new Color(80, 80, 80);

  private static Font defaultFont;

  private BufferedImage buffer;

  private final char code;
  private final int size;
  private final Color color;
  private final Font font;

  static {
    try (InputStream stream = AwesomeIcon.class.getResourceAsStream(FONTAWESOME_TTF)) {
      AWESOME = Font.createFont(Font.TRUETYPE_FONT, stream);
      defaultFont = AWESOME.deriveFont(Font.PLAIN, defaultIconSize);
    } catch (FontFormatException | IOException ex) {
      throw new UnexpectedException(ex);
    }
  }

  /**
   * @param defaultIconSize the defaultIconSize to set
   */
  public static void setDefaultIconSize(int defaultIconSize) {
    AwesomeIcon.defaultIconSize = defaultIconSize;
    defaultFont = AWESOME.deriveFont(Font.PLAIN, defaultIconSize);
  }

  /**
   * @param defaultIconColor the defaultIconColor to set
   */
  public static void setDefaultIconColor(Color defaultIconColor) {
    AwesomeIcon.defaultIconColor = defaultIconColor;
  }

  /**
   * @param code
   * @return
   * @see http://fortawesome.github.io/Font-Awesome/cheatsheet/
   */
  public static AwesomeIcon create(char code) {
    return create(code, defaultIconSize, defaultIconColor);
  }

  /**
   * @param code
   * @param color
   * @return
   * @see http://fortawesome.github.io/Font-Awesome/cheatsheet/
   */
  public static AwesomeIcon create(char code, Color color) {
    return new AwesomeIcon(code, defaultIconSize, color);
  }

  /**
   * @param code
   * @param size
   * @return
   * @see http://fortawesome.github.io/Font-Awesome/cheatsheet/
   */
  public static AwesomeIcon create(char code, int size) {
    return new AwesomeIcon(code, size, defaultIconColor);
  }

  /**
   * @param code
   * @param size
   * @param color
   * @return
   * @see http://fortawesome.github.io/Font-Awesome/cheatsheet/
   */
  public static AwesomeIcon create(char code, int size, Color color) {
    return new AwesomeIcon(code, size, color);
  }

  private AwesomeIcon(char code, int size, Color color) {
    this.code = code;
    this.size = size;
    this.color = color;
    if (size == defaultIconSize) {
      font = defaultFont;
    } else {
      font = AWESOME.deriveFont(Font.PLAIN, size);
    }
  }

  public AwesomeIcon create(Color color) {
    return create(code, size, color);
  }

  @Override
  public synchronized void paintIcon(Component c, Graphics g, int x, int y) {

    if (buffer == null) {
      buffer = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);

      Graphics2D graphics = (Graphics2D) buffer.getGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      graphics.setFont(font);
      graphics.setColor(color);

      int stringY = getIconHeight() - (getIconHeight() / 4) + 1;
      graphics.drawString(String.valueOf(code), 0, stringY);

      graphics.dispose();
    }

    g.drawImage(buffer, x, y, null);
  }

  @Override
  public int getIconHeight() {
    return size;
  }

  @Override
  public int getIconWidth() {
    return size;
  }

  /**
   * @return the code
   */
  public char getCode() {
    return code;
  }

  /**
   * @return the color
   */
  public Color getColor() {
    return color;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * @return the defaultFont
   */
  public static Font getDefaultFont() {
    return defaultFont;
  }

  /**
   * @return the defaultIconColor
   */
  public static Color getDefaultIconColor() {
    return defaultIconColor;
  }

  /**
   * @return the defaultIconSize
   */
  public static int getDefaultIconSize() {
    return defaultIconSize;
  }
}
