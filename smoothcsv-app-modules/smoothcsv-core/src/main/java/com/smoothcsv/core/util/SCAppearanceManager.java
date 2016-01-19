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
package com.smoothcsv.core.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 * @author kohii
 *
 */
public class SCAppearanceManager {

  private static final JPanel basicPanel = new JPanel();
  private static Color basicBorderColor = Color.LIGHT_GRAY;
  private static final Color basicBackground = basicPanel.getBackground();
  private static final Color basicForeground = basicPanel.getForeground();
  private static final Font basicFont = basicPanel.getFont();

  // -- defaults --
  @Getter
  private static Font defaultFont = basicFont;
  @Getter
  private static Color defaultBorderColor = basicBorderColor;
  @Getter
  private static Color defaultBackground = basicBackground;
  @Getter
  private static Color defaultForeground = basicForeground;

  // -- statusbar --
  @Getter
  private static Font statusbarFont;

  // -- grid --
  @Getter
  private static Font gridFont;
  @Getter
  private static Color gridBackground = Color.WHITE;
  @Getter
  private static Color gridForeground = Color.BLACK;
  @Getter
  private static Color gridLineColor = new Color(200, 200, 200);
  @Getter
  private static Color gridFrozenLineColor = Color.BLACK;
  @Getter
  private static Color gridSelectionBackground = new Color(40, 110, 255, 30);
  @Getter
  private static Color gridSelectionBorderColor = new Color(110, 120, 222);
  @Getter
  private static Color gridFindhilightColor = new Color(250, 250, 180);

  // -- grid-header --
  @Getter
  private static Color gridHeaderBackground = new Color(230, 230, 230);
  @Getter
  private static Color gridHeaderForeground = Color.BLACK;
  @Getter
  private static Color gridHeaderSelectedBackground = new Color(210, 210, 210);
  @Getter
  private static Color gridHeaderSelectedForeground = Color.BLACK;
  @Getter
  private static Color gridHeaderFocusedBackground = new Color(110, 120, 222);
  @Getter
  private static Color gridHeaderFocusedForeground = Color.WHITE;
  @Getter
  private static Color gridHeaderLineColor = Color.LIGHT_GRAY;

  // -- celleditor --
  @Getter
  private static Font celleditorFont;
  // @Getter
  // private static Color celleditorBackground = basicBackground;
  // @Getter
  // private static Color celleditorForeground = basicForeground;
  // @Getter
  // private static Color celleditorCaretColor = basicBorderColor;

  // -- inline-celleditor --
  @Getter
  private static Font inlineCelleditorFont;
  // @Getter
  // private static Color inlineCelleditorBackground = basicBackground;
  // @Getter
  // private static Color inlineCelleditorForeground = basicForeground;
  // @Getter
  // private static Color inlineCelleditorCaretColor = basicBorderColor;

  private static boolean isFontChanged = false;
  private static boolean isBackgroundChanged = false;
  private static boolean isForegroundChanged = false;

  public static void init() {
    UISettings settings = UISettings.getInstance();

    // default font
    defaultFont = createFont(settings.get(UISettings.DEFAULT_FONT_NAME),
        settings.getInteger(UISettings.DEFAULT_FONT_SIZE), defaultFont);

    // default background color
    String defaultBg = settings.get(UISettings.DEFAULT_BACKGROUND);
    if (StringUtils.isNotEmpty(defaultBg)) {
      defaultBackground = Color.decode(defaultBg);
    }

    // default foreground color
    String defaultFg = settings.get(UISettings.DEFAULT_FOREGROUND);
    if (StringUtils.isNotEmpty(defaultFg)) {
      defaultForeground = Color.decode(defaultFg);
    }

    // statusbar font
    statusbarFont = createFont(settings.get(UISettings.STATUSBAR_FONT_NAME),
        settings.getInteger(UISettings.STATUSBAR_FONT_SIZE), defaultFont);

    // grid font
    gridFont = createFont(settings.get(UISettings.GRID_FONT_NAME),
        settings.getInteger(UISettings.GRID_FONT_SIZE), defaultFont);

    // celeditor font
    celleditorFont = createFont(settings.get(UISettings.CELLEDITOR_FONT_NAME),
        settings.getInteger(UISettings.CELLEDITOR_FONT_SIZE), defaultFont);

    // inline-celeditor font
    inlineCelleditorFont = createFont(settings.get(UISettings.INLINE_CELLEDITOR_FONT_NAME),
        settings.getInteger(UISettings.INLINE_CELLEDITOR_FONT_SIZE), gridFont);

    // set grid ui
    UIManager.put("Grid.font", gridFont);
    UIManager.put("GridHeader.font", gridFont);

    // set defaults
    putDefaultFont(defaultFont);
    putDefaultForeground(defaultBackground);
    putDefaultBackground(defaultForeground);
  }


  private static void putDefaultFont(Font font) {
    if (!isFontChanged && font.equals(basicFont)) {
      return;
    }
    isFontChanged = true;
    UIManager.put("Button.font", font);
    UIManager.put("ToggleButton.font", font);
    UIManager.put("RadioButton.font", font);
    UIManager.put("CheckBox.font", font);
    UIManager.put("ColorChooser.font", font);
    UIManager.put("ComboBox.font", font);
    UIManager.put("Label.font", font);
    UIManager.put("List.font", font);
    UIManager.put("MenuBar.font", font);
    UIManager.put("MenuItem.font", font);
    UIManager.put("RadioButtonMenuItem.font", font);
    UIManager.put("CheckBoxMenuItem.font", font);
    UIManager.put("Menu.font", font);
    UIManager.put("PopupMenu.font", font);
    UIManager.put("OptionPane.font", font);
    UIManager.put("Panel.font", font);
    UIManager.put("ProgressBar.font", font);
    UIManager.put("ScrollPane.font", font);
    UIManager.put("Viewport.font", font);
    UIManager.put("TabbedPane.font", font);
    UIManager.put("Table.font", font);
    UIManager.put("TableHeader.font", font);
    UIManager.put("TextField.font", font);
    UIManager.put("PasswordField.font", font);
    UIManager.put("TextArea.font", font);
    UIManager.put("TextPane.font", font);
    UIManager.put("EditorPane.font", font);
    UIManager.put("TitledBorder.font", font);
    UIManager.put("ToolBar.font", font);
    UIManager.put("ToolTip.font", font);
    UIManager.put("Tree.font", font);
  }

  private static void putDefaultForeground(Color color) {
    if (!isForegroundChanged && color.equals(basicForeground)) {
      return;
    }
    isForegroundChanged = true;
    // TODO
  }


  private static void putDefaultBackground(Color color) {
    if (!isBackgroundChanged && color.equals(basicBackground)) {
      return;
    }
    isBackgroundChanged = true;
    // TODO
  }

  private static Font createFont(String name, Integer size, Font base) {
    Font ret = base;
    if (StringUtils.isNotEmpty(name)) {
      ret = new Font(name, base.getStyle(), size == null ? base.getSize() : size);
    } else {
      if (size != null) {
        ret = base.deriveFont(size.floatValue());
      }
    }
    return ret;
  }
}
