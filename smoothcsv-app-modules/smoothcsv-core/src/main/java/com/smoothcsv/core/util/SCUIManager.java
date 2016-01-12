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

import java.awt.Font;

import javax.swing.UIManager;

import com.smoothcsv.core.constants.UIConstants;

import lombok.Getter;

/**
 * @author kohii
 *
 */
public class SCUIManager {

  @Getter
  private static SCUIManager instance = new SCUIManager();

  @Getter
  private Font defaultFont = UIConstants.getDefaultFont();

  @Getter
  private Font gridFont = defaultFont;

  @Getter
  private Font cellEditorFont = gridFont;

  @Getter
  private Font valuePanelFont = cellEditorFont;

  @Getter
  private Font statusBarFont = defaultFont;

  private SCUIManager() {}

  public void init() {}

  private void putDefaultFonts(Font font) {
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
}
