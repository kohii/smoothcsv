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
package com.smoothcsv.swing.gridsheet.ui;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;

import com.smoothcsv.swing.gridsheet.GridSheetPane;

public abstract class AbstractGridUI extends ComponentUI {

  protected abstract GridSheetPane getGridSheetPane();

  protected void paintFrozenLine(Graphics g, Rectangle clip, Rectangle lineRect) {
    lineRect = lineRect.intersection(clip);
    if (!lineRect.isEmpty()) {
      g.setClip(clip);
      g.setColor(getGridSheetPane().getColorProvider().getFrozenLineColor());
      g.fillRect(lineRect.x, lineRect.y, lineRect.width, lineRect.height);
    }
  }
}
