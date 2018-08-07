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
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.smoothcsv.commons.utils.HtmlUtils;


public class LinkLabel extends JLabel {

  private static final long serialVersionUID = 2915717306820064388L;

  public LinkLabel(String text, final String url) {
    super(HtmlUtils.createLinkHtml(url, text));
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        Component p = (Component) e.getSource();
        p.setCursor(c);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        Cursor c = Cursor.getDefaultCursor();
        Component p = (Component) e.getSource();
        p.setCursor(c);
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          try {
            Desktop.getDesktop().browse(new URI(url));
          } catch (IOException | URISyntaxException e1) {
            throw new RuntimeException(e1);
          }
        }
      }
    });
  }
}
