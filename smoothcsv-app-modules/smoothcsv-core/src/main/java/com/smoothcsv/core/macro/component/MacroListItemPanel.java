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
package com.smoothcsv.core.macro.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;

import lombok.Getter;

import com.smoothcsv.core.macro.MacroInfo;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.swing.icon.AwesomeIcon;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class MacroListItemPanel extends JPanel {

  private static final Color SELECTED_ITEM_BG = new Color(60, 118, 221);

  @Getter
  private MacroInfo macroInfo;
  private JLabel lblFilepath;
  private JLabel lblMacrofilename;

  public MacroListItemPanel() {
    this(new MacroInfo("/test/test/test/test/test/test/test/test/test/test/test/test.js"));
  }

  public MacroListItemPanel(MacroInfo macroInfo) {
    this.macroInfo = macroInfo;

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0};
    gridBagLayout.rowHeights = new int[] {0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    lblMacrofilename = new JLabel();
    GridBagConstraints gbc_lblMacrofilename = new GridBagConstraints();
    gbc_lblMacrofilename.anchor = GridBagConstraints.WEST;
    gbc_lblMacrofilename.insets = new Insets(3, 3, 0, 3);
    gbc_lblMacrofilename.gridx = 0;
    gbc_lblMacrofilename.gridy = 0;
    add(lblMacrofilename, gbc_lblMacrofilename);

    lblFilepath = new JLabel();
    Font font = lblFilepath.getFont();
    font = font.deriveFont((float) font.getSize() * 8 / 10);
    lblFilepath.setFont(font);
    GridBagConstraints gbc_lblFilepath = new GridBagConstraints();
    gbc_lblFilepath.anchor = GridBagConstraints.WEST;
    gbc_lblFilepath.insets = new Insets(0, 7, 4, 3);
    gbc_lblFilepath.gridx = 0;
    gbc_lblFilepath.gridy = 1;
    add(lblFilepath, gbc_lblFilepath);

    setSelected(false);
    render();
  }

  public void render() {
    lblMacrofilename.setText(macroInfo.getFileName());
    lblFilepath.setText(macroInfo.getFilePath());
    File f = macroInfo.getFile();
    if (!f.exists() || !f.isFile()) {
      lblFilepath.setIcon(AwesomeIcon.create(AwesomeIcon.FA_WARNING, new Color(230, 170, 0)));
      setToolTipText(MessageBundles.getString("WSCC0002", macroInfo.getFilePath()));
    }
  }

  public void setSelected(boolean b) {
    if (b) {
      lblMacrofilename.setForeground(Color.WHITE);
      lblFilepath.setForeground(Color.WHITE);
      setBackground(SELECTED_ITEM_BG);
    } else {
      lblMacrofilename.setForeground(Color.BLACK);
      lblFilepath.setForeground(Color.DARK_GRAY);
      setBackground(null);
    }
  }
}
