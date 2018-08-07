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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.text.Document;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
public class ExTextField extends JTextField {

  private static final long serialVersionUID = 8778145030268480369L;

  @Getter
  @Setter
  private String placeholder;

  /**
   *
   */
  public ExTextField() {
    super();
  }

  /**
   * @param doc
   * @param text
   * @param columns
   */
  public ExTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
  }

  /**
   * @param columns
   */
  public ExTextField(int columns) {
    super(columns);
  }

  /**
   * @param text
   * @param columns
   */
  public ExTextField(String text, int columns) {
    super(text, columns);
  }

  /**
   * @param text
   */
  public ExTextField(String text) {
    super(text);
  }

  @Override
  protected void paintComponent(final Graphics pG) {
    super.paintComponent(pG);

    if (placeholder.length() == 0 || getText().length() > 0) {
      return;
    }

    final Graphics2D g = (Graphics2D) pG;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(getDisabledTextColor());
    g.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent()
        + getInsets().top);
  }
}
