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

import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

import com.smoothcsv.csv.NewlineCharacter;
import sun.swing.SwingUtilities2;

/**
 * @author kohii
 */
public class CsvGridUIUtils {

  static void paintNewlineChar(JComponent comp, Graphics g, int x, int y, NewlineCharacter newlineCharacter) {

    FontMetrics fm = SwingUtilities2.getFontMetrics(comp, g);
    int height = fm.getHeight();
    int width = (int) (height * 0.7);
    int arrowWidth = width / 3;

    int top;
    int baseline;
    int left;
    int right;

    switch (newlineCharacter) {
      case LF:
        top = y + height / 5;
        int middle = x + 1 + arrowWidth;
        int bottom = y + height - height / 5;

        g.drawLine(middle, top, middle, bottom);
        g.drawLine(middle, bottom, middle - arrowWidth, bottom - arrowWidth);
        g.drawLine(middle, bottom, middle + arrowWidth, bottom - arrowWidth);

        break;
      case CRLF:

        top = y + height / 3;
        baseline = y + height / 2;
        left = x + height / 10;
        right = x + width;

        g.drawLine(right, top, right, baseline);
        g.drawLine(left, baseline, right, baseline);
        g.drawLine(left, baseline, left + arrowWidth, baseline + arrowWidth);
        g.drawLine(left, baseline, left + arrowWidth, baseline - arrowWidth);
        break;
      case CR:

        baseline = y + height / 2;
        left = x + height / 10;
        right = x + width;

        g.drawLine(left, baseline, right, baseline);
        g.drawLine(left, baseline, left + arrowWidth, baseline + arrowWidth);
        g.drawLine(left, baseline, left + arrowWidth, baseline - arrowWidth);

        break;
      default:
        throw new IllegalArgumentException(newlineCharacter.toString());
    }
  }
}
