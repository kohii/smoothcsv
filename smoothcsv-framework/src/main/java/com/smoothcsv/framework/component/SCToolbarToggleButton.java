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
package com.smoothcsv.framework.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author kohii
 */
public class SCToolbarToggleButton extends JToggleButton {

  private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
  private static final Color DEFAULT_FOREGROUND = Color.DARK_GRAY;
  private static final Color SELECTED_FOREGROUND = new Color(0, 192, 192);

  private static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
  private static final Border SELECTED_BORDER = BorderFactory.createLineBorder(SELECTED_FOREGROUND, 1);

  private final Border padding;
  private boolean isMouseOn = false;

  public SCToolbarToggleButton(String text) {
    super(text);
    FontMetrics fm = getFontMetrics(FONT);
    int textWidth = fm.stringWidth(text);

    final int preferedWidth = fm.stringWidth("1234") + 2;
    int paddingLR = Math.max(0, (preferedWidth - textWidth) / 2);
    this.padding = BorderFactory.createEmptyBorder(1, paddingLR, 1, paddingLR);
    init();
  }

  private void init() {
    setFont(FONT);
    setFocusable(false);

    addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateAppearance();
      }
    });
    addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        isMouseOn = true;
        updateAppearance();
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        isMouseOn = false;
        updateAppearance();
      }
    });
    updateAppearance();
  }

  private void updateAppearance() {
    boolean isSelected = isSelected();

    if (isMouseOn || isSelected) {
      setForeground(SELECTED_FOREGROUND);
    } else {
      setForeground(DEFAULT_FOREGROUND);
    }

    if (isSelected()) {
      setBorder(BorderFactory.createCompoundBorder(
          SELECTED_BORDER,
          padding
      ));
    } else {
      setBorder(BorderFactory.createCompoundBorder(
          DEFAULT_BORDER,
          padding
      ));
    }
  }
}
