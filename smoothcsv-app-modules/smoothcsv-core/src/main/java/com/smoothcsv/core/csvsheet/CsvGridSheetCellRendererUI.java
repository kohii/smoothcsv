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
package com.smoothcsv.core.csvsheet;

import com.smoothcsv.core.util.CoreSettings;
import sun.swing.SwingUtilities2;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.text.View;

/**
 * @author kohii
 */
public class CsvGridSheetCellRendererUI extends BasicLabelUI {

  private Rectangle paintIconR = new Rectangle();
  private Rectangle paintTextR = new Rectangle();

  public static ComponentUI createUI(JComponent c) {
    return new CsvGridSheetCellRendererUI();
  }

  public void paint(Graphics g, JComponent c) {
    CsvGridSheetCellRenderer label = (CsvGridSheetCellRenderer) c;
    Object value = label.getValue();

    if (value == CsvGridSheetTable.END_OF_LINE) {
      if (CoreSettings.getInstance().getBoolean(CoreSettings.SHOW_EOL)) {
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        int height = fm.getHeight();
        int width = (int) (height * 0.7);
        paintEndOfLine(c, g, 0, 0, width, height);
      }
    } else if (value == CsvGridSheetTable.END_OF_FILE) {
      if (CoreSettings.getInstance().getBoolean(CoreSettings.SHOW_EOF)) {
        paintEndOfFile(label, g);
      }
    } else {
      if (value != null) {
        paintImpl(g, c);
      }
    }
  }

  public void paintImpl(Graphics g, JComponent c) {
    CsvGridSheetCellRenderer label = (CsvGridSheetCellRenderer) c;
    String text = label.getText();
    Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

    if ((icon == null) && (text == null)) {
      return;
    }

    FontMetrics fm = SwingUtilities2.getFontMetrics(label, g);
    String clippedText = layout(label, fm, c.getWidth(), c.getHeight());

    if (icon != null) {
      icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
    }

    if (text != null) {
      View v = (View) c.getClientProperty(BasicHTML.propertyKey);
      if (v != null) {
        v.paint(g, paintTextR);
      } else {
        int textX = paintTextR.x;
        int textY = paintTextR.y + fm.getAscent();

        if (label.isEnabled()) {
          paintEnabledText(label, g, clippedText, textX, textY);
        } else {
          paintDisabledText(label, g, clippedText, textX, textY);
        }
      }
    }
  }

  private Rectangle createComponentRect(CsvGridSheetCellRenderer label, int width, int height) {
    Insets insets = label.getInsets(null);

    Rectangle paintViewR = new Rectangle();
    paintViewR.x = insets.left;
    paintViewR.y = insets.top;
    paintViewR.width = width - (insets.left + insets.right);
    paintViewR.height = height - (insets.top + insets.bottom);
    paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
    paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;
    return paintViewR;
  }

  private String layout(CsvGridSheetCellRenderer label, FontMetrics fm, int width, int height) {
    String text = label.getText();
    Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
    Rectangle paintViewR = createComponentRect(label, width, height);
    return layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);
  }

  protected void paintEndOfLine(JComponent comp, Graphics g, int x, int y, int width, int height) {

    g.setColor(comp.getForeground());

    int arrowWidth = width / 4;
    int top = y + height / 3;
    int baseline = y + height / 2;
    int left = x + height / 10;
    int right = x + width;

    g.drawLine(right, top, right, baseline);
    g.drawLine(left, baseline, right, baseline);
    g.drawLine(left, baseline, left + arrowWidth, baseline + arrowWidth);
    g.drawLine(left, baseline, left + arrowWidth, baseline - arrowWidth);
  }

  protected void paintEndOfFile(CsvGridSheetCellRenderer label, Graphics g) {
    FontMetrics fm = SwingUtilities2.getFontMetrics(label, g);

    createComponentRect(label, label.getWidth(), label.getHeight());

    int textX = paintTextR.x;
    int textY = paintTextR.y + fm.getAscent();

    paintEnabledText(label, g, "[EOF]", textX, textY);
  }
}
