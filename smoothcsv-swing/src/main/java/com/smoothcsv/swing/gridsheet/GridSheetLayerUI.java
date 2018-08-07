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
package com.smoothcsv.swing.gridsheet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;

import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

/**
 * @author kohii
 */
public class GridSheetLayerUI extends LayerUI<GridSheetTable> {

  private static final long serialVersionUID = 239102108049872926L;

  private GridSheetPane gridSheetPane;

  /**
   * @param gridSheetPane
   */
  public GridSheetLayerUI(GridSheetPane gridSheetPane) {
    this.gridSheetPane = gridSheetPane;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.plaf.LayerUI#paint(java.awt.Graphics, javax.swing.JComponent)
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    super.paint(g, c);

    Rectangle clip = g.getClipBounds();
    Graphics2D g2 = (Graphics2D) g;
    GridSheetTable table = gridSheetPane.getTable();

    Rectangle editingCellRect = null;
    if (gridSheetPane.isEditing()) {
      editingCellRect = table.getOuterCellRect(table.getEditingRow(), table.getEditingColumn());
    }

    // int correctionX;
    // int correctionY;

    // Paint selections
    GridSheetSelectionModel selModel = gridSheetPane.getSelectionModel();

    int rMinSel = selModel.getMainMinRowSelectionIndex();
    int cMinSel = selModel.getMainMinColumnSelectionIndex();
    int rMaxSel = selModel.getMainMaxRowSelectionIndex();
    int cMaxSel = selModel.getMainMaxColumnSelectionIndex();

    boolean isAdditionallySelected = selModel.isAdditionallySelected();

    Rectangle minCell = table.getCellRect(rMinSel, cMinSel, true);
    Rectangle maxCell = table.getCellRect(rMaxSel, cMaxSel, true);
    Rectangle mainSelectionArea = minCell.union(maxCell);
    // mainSelectionArea.x += correctionX;
    // mainSelectionArea.y += correctionY;

    mainSelectionArea.x -= 1;
    mainSelectionArea.y -= 1;

    int mainSelectionLowerRightX = mainSelectionArea.x + mainSelectionArea.width;
    int mainSelectionLowerRightY = mainSelectionArea.y + mainSelectionArea.height;

    if (!isAdditionallySelected) {
      GridSheetCellRange autofillRange = table.getAutofillRange();
      if (autofillRange != null) {
        // Draw auto fill borders
        g2.setColor(Color.BLACK);
        Rectangle rect = table.getCellRect(autofillRange);
        rect.x -= 1;
        rect.y -= 1;
        Stroke defaultStroke = g2.getStroke();
        Stroke dashed =
            new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
        g2.setStroke(dashed);
        g2.drawRect(rect.x, rect.y, rect.width, rect.height);
        g2.setStroke(defaultStroke);
      }
    }

    if (mainSelectionArea.x <= clip.x + clip.width && mainSelectionArea.y <= clip.y + clip.height
        && mainSelectionLowerRightX >= clip.x && mainSelectionLowerRightY >= clip.y) {
      if (!isAdditionallySelected) {
        // Draw selection borders
        g.setColor(gridSheetPane.getColorProvider().getSelectionBorderColor());
        g.drawRect(mainSelectionArea.x, mainSelectionArea.y, mainSelectionArea.width,
            mainSelectionArea.height);

        if (!table.isEditing()) {
          // Draw autofill cover
          g.fillRect(mainSelectionArea.x + mainSelectionArea.width - 5,
              mainSelectionArea.y + mainSelectionArea.height - 5, 6, 6);
        }
      }

      g.setColor(gridSheetPane.getColorProvider().getSelectionColor());
      if (editingCellRect != null) {
        g2.fill(subtract(mainSelectionArea, editingCellRect));
      } else {
        g.fillRect(mainSelectionArea.x, mainSelectionArea.y, mainSelectionArea.width,
            mainSelectionArea.height);
      }
    }

    if (isAdditionallySelected) {
      g.setColor(gridSheetPane.getColorProvider().getSelectionColor());
      List<GridSheetCellRange> selections = selModel.getAdditionalSelections();
      for (int i = 0; i < selections.size(); i++) {
        GridSheetCellRange sel = selections.get(i);

        int rMinSelA = sel.getFirstRow();
        int cMinSelA = sel.getFirstColumn();
        int rMaxSelA = sel.getLastRow();
        int cMaxSelA = sel.getLastColumn();

        Rectangle minCellA = table.getCellRect(rMinSelA, cMinSelA, true);
        Rectangle maxCellA = table.getCellRect(rMaxSelA, cMaxSelA, true);
        Rectangle selectionArea = minCellA.union(maxCellA);

        selectionArea.x -= 1;
        selectionArea.y -= 1;

        int selLowerRightX = selectionArea.x + selectionArea.width;
        int selLowerRightY = selectionArea.y + selectionArea.height;

        if (selectionArea.x <= clip.x + clip.width && selectionArea.y <= clip.y + clip.height
            && selLowerRightX >= clip.x && selLowerRightY >= clip.y) {
          if (editingCellRect != null) {
            g2.fill(subtract(selectionArea, editingCellRect));
          } else {
            g.fillRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
          }
        }
      }
    }

    int rFocus = selModel.getRowAnchorIndex();
    int cFocus = selModel.getColumnAnchorIndex();
    Rectangle focusCellRect = table.getCellRect(rFocus, cFocus, true);
    focusCellRect.x -= 1;
    focusCellRect.y -= 1;

    g.setColor(gridSheetPane.getColorProvider().getSelectionBorderColor());
    g.drawRect(focusCellRect.x, focusCellRect.y, focusCellRect.width, focusCellRect.height);
    g.drawRect(focusCellRect.x + 1, focusCellRect.y + 1, focusCellRect.width - 2,
        focusCellRect.height - 2);
  }

  private Shape subtract(Rectangle rect, Rectangle arg) {
    if (!rect.intersects(arg)) {
      return rect;
    }
    Area area = new Area(rect);
    area.subtract(new Area(arg));
    return area;
  }
}
