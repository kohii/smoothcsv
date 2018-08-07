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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class AnchorLabel extends JLabel {

  private static final long serialVersionUID = 2915717306820064388L;

  private static final Cursor HAND_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

  private Runnable onClick;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public AnchorLabel() {
    Font font = getFont();
    Map attributes = font.getAttributes();
    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    setFont(font.deriveFont(attributes));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        Component p = (Component) e.getSource();
        p.setCursor(HAND_CURSOR);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        Cursor c = Cursor.getDefaultCursor();
        Component p = (Component) e.getSource();
        p.setCursor(c);
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (onClick != null && SwingUtilities.isLeftMouseButton(e) && HAND_CURSOR == getCursor()) {
          onClick.run();
        }
      }
    });
  }

  public void setOnClick(Runnable onClick) {
    this.onClick = onClick;
  }
}
