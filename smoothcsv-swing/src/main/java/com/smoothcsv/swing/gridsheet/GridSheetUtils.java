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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.ui.GridSheetColumnHeaderUI;
import com.smoothcsv.swing.gridsheet.ui.GridSheetRowHeaderUI;
import com.smoothcsv.swing.gridsheet.ui.GridSheetTableNoActionUI;

public class GridSheetUtils {

  public static void moveAnchor(GridSheetPane gridSheetPane, Orientation orientation,
                                boolean reverse, boolean stayInSelection, boolean scroll) {
    CellIterator itr =
        new CellIterator(gridSheetPane, stayInSelection, true, reverse, orientation, false);
    itr.next();
    if (stayInSelection) {
      gridSheetPane.getSelectionModel().updateAnchor(itr.getRow(), itr.getColumn());
    } else {
      gridSheetPane.getSelectionModel().setSelectionInterval(itr.getRow(), itr.getColumn(),
          itr.getRow(), itr.getColumn());
    }
    gridSheetPane.getTable().scrollRectToVisible(itr.getRow(), itr.getColumn());
  }


  public static void sizeWidthToFitHeadr(GridSheetPane gridSheetPane, int vc, int maxWidth) {
    GridSheetColumnHeader header = gridSheetPane.getColumnHeader();
    int headerWidth = gridSheetPane.getColumnHeader().getDefaultRenderer()
        .getGridCellRendererComponent(header, header.getHeaderValue(vc), false, false, vc)
        .getPreferredSize().width;

    gridSheetPane.getColumn(vc).setWidth(Math.min(maxWidth, headerWidth + 3));
  }

  public static void sizeWidthToFitData(GridSheetPane gridSheetPane, int vc, int maxWidth,
                                        int maxRowsToScan) {

    int max = 0;
    int limit = maxWidth;
    int maxRow = maxRowsToScan;

    int vrows = gridSheetPane.getRowCount();

    for (int i = 0; i < vrows; i++) {
      GridSheetCellRenderer r = gridSheetPane.getTable().getDefaultRenderer();
      Object value = gridSheetPane.getValueAt(i, vc);
      Component c =
          r.getGridCellRendererComponent(gridSheetPane.getTable(), value, false, false, i, vc);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
      if (limit <= max || maxRow <= i) {
        break;
      }
    }

    gridSheetPane.getColumn(vc).setWidth(Math.min(maxWidth, max + 3));
  }

  public static void sizeWidthToFit(GridSheetPane gridSheetPane, int vc, int maxWidth,
                                    int maxRowsToScan) {
    GridSheetColumnHeader header = gridSheetPane.getColumnHeader();
    int max = gridSheetPane.getColumnHeader().getDefaultRenderer()
        .getGridCellRendererComponent(header, header.getHeaderValue(vc), false, false, vc)
        .getPreferredSize().width;
    int limit = maxWidth;
    int maxRow = maxRowsToScan;

    int vrows = gridSheetPane.getRowCount();

    for (int i = 0; i < vrows; i++) {
      GridSheetCellRenderer r = gridSheetPane.getTable().getDefaultRenderer();
      Object value = gridSheetPane.getValueAt(i, vc);
      Component c =
          r.getGridCellRendererComponent(gridSheetPane.getTable(), value, false, false, i, vc);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
      if (limit <= max || maxRow <= i) {
        break;
      }
    }

    gridSheetPane.getColumn(vc).setWidth(Math.min(maxWidth, max + 3));
  }

  public static void sizeWidthToFitSelectedCells(GridSheetPane gridSheetPane, int vc, int maxWidth,
                                                 int maxRowsToScan) {
    GridSheetTable table = gridSheetPane.getTable();

    int vrows = gridSheetPane.getRowCount();
    if (vrows == 0) {
      return;
    }

    int max = 0;
    int limit = maxWidth;

    GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    int start = sm.getMinRowSelectionIndex();
    int end = sm.getMaxRowSelectionIndex();
    int maxCount = maxRowsToScan;

    for (int i = start; i <= end; i++) {
      if (sm.isCellSelected(i, vc)) {
        GridSheetCellRenderer r = table.getDefaultRenderer();
        Object value = gridSheetPane.getValueAt(i, vc);
        Component c = r.getGridCellRendererComponent(table, value, false, false, i, vc);
        int w = c.getPreferredSize().width;
        if (max < w) {
          max = w;
        }
        if (limit <= max || maxCount <= i) {
          break;
        }
      }
    }
    gridSheetPane.getColumn(vc).setWidth(Math.min(maxWidth, max + 3));
  }

  public static void initializeUI() {
    UIDefaults uiDefaults = UIManager.getDefaults();
    uiDefaults.put("GridSheetTableUI", GridSheetTableNoActionUI.class.getName());
    uiDefaults.put("GridSheetColumnHeaderUI", GridSheetColumnHeaderUI.class.getName());
    uiDefaults.put("GridSheetRowHeaderUI", GridSheetRowHeaderUI.class.getName());
    // uiDefaults.put("", new GridSheetTableUI.BasicColorProvider());
  }

  private static void sizeHeightToFit(GridSheetTable table, int vc) {
    throw new UnsupportedOperationException();
  }

  public static GridSheetScrollPane getAncestorScrollPane(JComponent table) {
    Container p = table.getParent();
    if (p != null && p instanceof JViewport) {
      p = p.getParent();
      if (p instanceof GridSheetScrollPane) {
        return (GridSheetScrollPane) p;
      }
    }
    return null;
  }

  public static GridSheetScrollPane getAncestorScrollPane(GridSheetTable table) {
    Container p = table.getParent();
    if (p != null) {
      p = p.getParent();
      if (p != null) {
        p = p.getParent();
        return (GridSheetScrollPane) p;
      }
    }
    return null;
  }

  public static boolean pointOutsidePrefSize(GridSheetPane gridSheetPane, int row, int column,
                                             Point p) {
    if (gridSheetPane.convertColumnIndexToModel(column) != 0 || row == -1) {
      return true;
    }
    GridSheetTable table = gridSheetPane.getTable();
    GridSheetCellRenderer tcr = table.getCellRenderer(row, column);
    Object value = gridSheetPane.getValueAt(row, column);
    Component cell = tcr.getGridCellRendererComponent(table, value, false, false, row, column);
    Dimension itemSize = cell.getPreferredSize();
    Rectangle cellBounds = table.getCellRect(row, column, false);
    cellBounds.width = itemSize.width;
    cellBounds.height = itemSize.height;

    // See if coords are inside
    // ASSUME: mouse x,y will never be < cell's x,y
    assert (p.x >= cellBounds.x && p.y >= cellBounds.y);
    if (p.x > cellBounds.x + cellBounds.width || p.y > cellBounds.y + cellBounds.height) {
      return true;
    }
    return false;
  }

  public static String escapeCellValue(Object o) {
    if (o == null) {
      return "";
    }

    String text = o.toString();
    int len = text.length();
    StringBuilder sb = null;
    boolean cr = false;
    for (int i = 0; i < len; i++) {
      char c = text.charAt(i);
      if (cr) {
        if (c == '\n') {
          continue;
        }
        cr = false;
      }
      if (c == '\t') {
        if (sb == null) {
          sb = new StringBuilder(len + 3);
          for (int j = 0; j < i; j++) {
            sb.append(text.charAt(j));
          }
        }
        for (int j = 0; j < 4; j++) {
          sb.append(' ');
        }
      } else if (c == '\r' || c == '\n') {
        if (sb == null) {
          sb = new StringBuilder(len);
          for (int j = 0; j < i; j++) {
            sb.append(text.charAt(j));
          }
        }
        sb.append('↓');
        if (c == '\r') {
          cr = true;
        }
      } else {
        if (sb != null) {
          sb.append(c);
        }
      }
    }
    if (sb == null) {
      return text;
    } else {
      return sb.toString();

    }
  }

}
