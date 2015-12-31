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
package com.smoothcsv.core.csvsheet;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.smoothcsv.swing.gridsheet.ui.GridSheetTableNoActionUI;

public class CsvGridSheetTableUI extends GridSheetTableNoActionUI {

  private CsvGridSheetTable table;

  @Override
  public void installUI(JComponent c) {
    super.installUI(c);
    this.table = (CsvGridSheetTable) c;
  }

  @Override
  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    this.table = null;
  }

  public static ComponentUI createUI(JComponent c) {
    return new CsvGridSheetTableUI();
  }

  @Override
  public void paint(Graphics g, JComponent c) {
    super.paint(g, c);
  }

  @Override
  protected void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax, int correctionX,
      int correctionY) {

    super.paintCells(g, rMin, rMax, cMin, cMax, correctionX, correctionY);

    CsvGridSheetPane gridSheetPane = (CsvGridSheetPane) getGridSheetPane();
    if (gridSheetPane.isNewlineCharsVisible() && cMax == gridSheetPane.getColumnCount() - 1) {
      // if paints a edge of columns, also paints line feed codes.

      int cellMargin = 1;

      g.setColor(gridSheetPane.getNewlineCharColor());

      Rectangle lineFeedCharRect = table.getCellRect(rMin, cMax, false);
      lineFeedCharRect.height -= cellMargin;
      lineFeedCharRect.x += lineFeedCharRect.width + cellMargin;
      lineFeedCharRect.width = gridSheetPane.getNewlineCharacterRectWidth();
      for (int row = rMin; row <= rMax; row++) {
        int rowHeight = gridSheetPane.getRow(row).getHeight();
        lineFeedCharRect.height = rowHeight - cellMargin;
        paintEndOfLine(g, lineFeedCharRect, row);
        lineFeedCharRect.y += rowHeight;
      }
    }
  }

  private void paintEndOfLine(Graphics g, Rectangle cellRect, int rowIndex) {
    super.paintCell(g, cellRect, rowIndex, getGridSheetPane().getColumnCount());
  }
}
