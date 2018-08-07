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

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.smoothcsv.swing.gridsheet.GridSheetColumnHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetScrollPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import com.smoothcsv.swing.utils.SwingUtils;
import sun.swing.SwingUtilities2;

public class GridSheetColumnHeaderUI extends AbstractGridUI {

  private static Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

  //
  // Instance Variables
  //
  /**
   * The GridSheetColumnHeader that is delegating the painting to this UI.
   */
  protected GridSheetColumnHeader header;
  protected CellRendererPane rendererPane;

  // Listeners that are attached to the GridSheetTable
  protected MouseInputListener mouseInputListener;

  // The column header over which the mouse currently is.
  private int rolloverColumn = -1;

  // The column that should be highlighted when the table header has the
  // focus.
  private int selectedColumnIndex = 0; // Read ONLY via

  // getSelectedColumnIndex!

  /**
   * This class should be treated as a &quot;protected&quot; inner class. Instantiate it only within
   * subclasses of {@code GridSheetColumnHeaderUI}.
   */
  public class MouseInputHandler implements MouseInputListener {

    private int mouseXOffset;
    private Cursor otherCursor = resizeCursor;
    private int pressedColumn = -1;
    private MouseEvent pressedEvent;
    private boolean dragged = false;

    private GridSheetColumn getResizingColumn(Point p) {
      return getResizingColumn(p, header.columnAtPoint(p));
    }

    private GridSheetColumn getResizingColumn(Point p, int column) {
      if (column == -1) {
        return null;
      }
      Rectangle r = header.getHeaderRect(column);
      r.grow(-3, 0);
      if (r.contains(p)) {
        return null;
      }
      int midPoint = r.x + r.width / 2;
      int columnIndex;
      if (header.getComponentOrientation().isLeftToRight()) {
        columnIndex = (p.x < midPoint) ? column - 1 : column;
      } else {
        columnIndex = (p.x < midPoint) ? column : column - 1;
      }
      if (columnIndex == -1) {
        return null;
      }
      return getGridSheetPane().getColumn(columnIndex);
    }

    public void mouseClicked(MouseEvent e) {
      if (!header.isEnabled()) {
        return;
      }
      if (e.getClickCount() % 2 == 0 && header.getCursor() == resizeCursor
          && SwingUtilities.isLeftMouseButton(e)) {
        getGridSheetPane().getTable().stopCellEditing();
        GridSheetPane gridSheetPane = getGridSheetPane();
        Point pt = new Point(e.getX() - 3, e.getY());
        int vc = header.columnAtPoint(pt);
        if (vc != -1) {
          GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
          boolean isColumnHeaderSelected = sm.isColumnHeaderSelected();
          int minIndex = sm.getMinColumnSelectionIndex();
          int maxIndex = sm.getMaxColumnSelectionIndex();
          if (maxIndex < minIndex || !(sm.isColumnSelected(vc) && isColumnHeaderSelected)) {
            autoFitColumnWidth(gridSheetPane, vc);
          } else if (isColumnHeaderSelected) {
            minIndex = Math.max(0, minIndex);
            maxIndex = Math.min(gridSheetPane.getColumnCount() - 1, maxIndex);
            for (int i = minIndex; i <= maxIndex; i++) {
              if (sm.isColumnSelected(i)) {
                autoFitColumnWidth(gridSheetPane, i);
              }
            }
          }
        }
      }
    }

    public void mousePressed(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, header)) {
        return;
      }
      dragged = false;
      header.setResizingColumn(null);

      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
      if (scrollPane != null && scrollPane.isFrozen()) {
        scrollPane.translateToOriginalViewPoint(p, true, false);
      }
      pressedColumn = header.columnAtPoint(p);
      pressedEvent = e;

      // First find which header cell was hit
      int index = header.columnAtPoint(p);

      if (index != -1) {
        // The last 3 pixels + 3 pixels of next column are for resizing
        GridSheetColumn resizingColumn = getResizingColumn(p, index);
        if (canResize(resizingColumn, header)) {
          header.setResizingColumn(resizingColumn);
          mouseXOffset = p.x - resizingColumn.getWidth();
        } else {
          setValueIsAdjusting(true);
          GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
          sm.setRowHeaderSelected(false);
          sm.setColumnHeaderSelected(true);
          adjustSelection(e);
        }
      }

      getGridSheetPane().getTable().requestFocusInWindow();
    }

    private void setValueIsAdjusting(boolean flag) {
      getGridSheetPane().getSelectionModel().setValueIsAdjusting(flag);
    }

    private void swapCursor() {
      Cursor tmp = header.getCursor();
      header.setCursor(otherCursor);
      otherCursor = tmp;
    }

    private void adjustSelection(MouseEvent e) {
      // The autoscroller can generate drag events outside the
      // table's range.
      if (pressedColumn == -1) {
        return;
      }

      GridSheetPane gridSheetPane = getGridSheetPane();
      GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      int rowCount = gridSheetPane.getRowCount();
      if (SwingUtils.isMenuShortcutKeyDown(e)) {
        sm.addSelectionInterval(0, pressedColumn, rowCount - 1, pressedColumn);
      } else if (e.isShiftDown()) {
        sm.changeLeadSelection(0, pressedColumn, GridSheetSelectionModel.CHANGE_ONLY_HORIZONTAL);
      } else {
        sm.setSelectionInterval(0, pressedColumn, rowCount - 1, pressedColumn);
      }
    }

    public void mouseMoved(MouseEvent e) {
      if (!header.isEnabled()) {
        return;
      }
      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
      if (scrollPane.isFrozen()) {
        scrollPane.translateToOriginalViewPoint(p, true, false);
      }
      if (canResize(getResizingColumn(p), header) != (header.getCursor() == resizeCursor)) {
        swapCursor();
      }
    }

    public void mouseDragged(MouseEvent e) {

      GridSheetColumn resizingColumn = header.getResizingColumn();

      boolean headerLeftToRight = header.getComponentOrientation().isLeftToRight();

      if (resizingColumn != null) {
        dragged = true;

        Point p = e.getPoint();
        GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
        if (scrollPane != null && scrollPane.isFrozen()) {
          Point frozenPoint = scrollPane.getFrozenPoint();
          p.x += frozenPoint.x;
        }

        int mouseX = p.x;

        int oldWidth = resizingColumn.getWidth();
        int newWidth;
        if (headerLeftToRight) {
          newWidth = mouseX - mouseXOffset;
        } else {
          newWidth = mouseXOffset - mouseX;
        }
        mouseXOffset += changeColumnWidth(resizingColumn, header, oldWidth, newWidth);
      } else {
        Point p = e.getPoint();
        GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
        if (scrollPane != null && scrollPane.isFrozen()) {
          Point frozenPoint = scrollPane.getFrozenPoint();
          p.x += frozenPoint.x;
        }
        int column = header.columnAtPoint(p);
        if (column == -1) {
          return;
        }
        GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
        sm.changeLeadSelection(0, column, GridSheetSelectionModel.CHANGE_ONLY_HORIZONTAL);
        scrollToColumn(column);
      }

    }

    public void mouseReleased(MouseEvent e) {
      GridSheetPane gridSheetPane = getGridSheetPane();
      if (SwingUtilities2.shouldIgnore(e, header)) {
        return;
      }

      GridSheetColumn resizingColumn = header.getResizingColumn();
      if (resizingColumn != null) {
        int width = resizingColumn.getWidth();

        GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
        int minIndex;
        int maxIndex;
        if (dragged && sm.isColumnHeaderSelected()
            && (minIndex = sm.getMinColumnSelectionIndex()) < (maxIndex =
            sm.getMaxColumnSelectionIndex())
            && sm.isColumnSelected(gridSheetPane.viewIndexForColumn(resizingColumn))) {
          minIndex = Math.max(0, minIndex);
          maxIndex = Math.min(header.getColumnCount() - 1, maxIndex);
          for (int i = minIndex; i <= maxIndex; i++) {
            if (sm.isColumnSelected(i)) {
              gridSheetPane.getColumn(i).setWidth(width);
            }
          }
        }
      }

      pressedEvent = null;
      setValueIsAdjusting(false);
      getGridSheetPane().getTable().requestFocusInWindow();
    }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    //
    // Protected & Private Methods
    //
  }

  //
  // Factory methods for the Listeners
  //

  /**
   * Creates the mouse listener for the GridSheetColumnHeader.
   */
  protected MouseInputListener createMouseInputListener() {
    return new MouseInputHandler();
  }

  //
  // The installation/uninstall procedures and support
  //
  public static ComponentUI createUI(JComponent h) {
    return new GridSheetColumnHeaderUI();
  }

  // Installation
  public void installUI(JComponent c) {
    header = (GridSheetColumnHeader) c;

    rendererPane = new CellRendererPane();
    header.add(rendererPane);

    installDefaults();
    installListeners();
  }

  /**
   * Initializes GridSheetColumnHeader properties such as font, foreground, and background. The
   * font, foreground, and background properties are only set if their current value is either null
   * or a UIResource, other properties are set if the current value is null.
   *
   * @see #installUI
   */
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(header, "GridHeader.background", "GridHeader.foreground",
        "GridHeader.font");
    LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);
  }

  /**
   * Attaches listeners to the GridSheetColumnHeader.
   */
  protected void installListeners() {
    mouseInputListener = createMouseInputListener();

    header.addMouseListener(mouseInputListener);
    header.addMouseMotionListener(mouseInputListener);
  }

  // Uninstall methods
  public void uninstallUI(JComponent c) {
    uninstallDefaults();
    uninstallListeners();

    header.remove(rendererPane);
    rendererPane = null;
    header = null;
  }

  protected void uninstallDefaults() {}

  protected void uninstallListeners() {
    header.removeMouseListener(mouseInputListener);
    header.removeMouseMotionListener(mouseInputListener);

    mouseInputListener = null;
  }

  //
  // Support for mouse rollover
  //

  /**
   * Returns the index of the column header over which the mouse currently is. When the mouse is not
   * over the table header, -1 is returned.
   *
   * @return the index of the current rollover column
   * @see #rolloverColumnUpdated(int, int)
   * @since 1.6
   */
  protected int getRolloverColumn() {
    return rolloverColumn;
  }

  // /**
  // * Selects the specified column in the table header. Repaints the affected
  // * header cells and makes sure the newly selected one is visible.
  // */
  // void selectColumn(int newColIndex) {
  // selectColumn(newColIndex, true);
  // }
  //
  // void selectColumn(int newColIndex, boolean doScroll) {
  // Rectangle repaintRect = header.getHeaderRect(selectedColumnIndex);
  // header.repaint(repaintRect);
  // selectedColumnIndex = newColIndex;
  // repaintRect = header.getHeaderRect(newColIndex);
  // header.repaint(repaintRect);
  // if (doScroll) {
  // scrollToColumn(newColIndex);
  // }
  // return;
  // }

  /**
   * Used by selectColumn to scroll horizontally, if necessary, to ensure that the newly selected
   * column is visible.
   */
  private void scrollToColumn(int col) {
    GridSheetTable table = getGridSheetPane().getTable();

    // Now scroll, if necessary.
    Rectangle vis = table.getVisibleRect();
    Rectangle cellBounds = table.getCellRect(0, col, true);
    vis.x = cellBounds.x - 5;
    vis.width = cellBounds.width + 10;
    table.scrollRectToVisible(vis);
  }

  private int getSelectedColumnIndex() {
    int numCols = getGridSheetPane().getColumnCount();
    if (selectedColumnIndex >= numCols && numCols > 0) {
      selectedColumnIndex = numCols - 1;
    }
    return selectedColumnIndex;
  }

  private static boolean canResize(GridSheetColumn column, GridSheetColumnHeader header) {
    return (column != null) && header.getResizingAllowed()
        && !header.getGridSheetPane().isEditing();
  }

  private int changeColumnWidth(GridSheetColumn resizingColumn, GridSheetColumnHeader th,
                                int oldWidth, int newWidth) {
    resizingColumn.setWidth(newWidth);
    return 0;
  }

  //
  // Baseline
  //

  /**
   * Returns the baseline.
   *
   * @throws NullPointerException     {@inheritDoc}
   * @throws IllegalArgumentException {@inheritDoc}
   * @see javax.swing.JComponent#getBaseline(int, int)
   * @since 1.6
   */
  public int getBaseline(JComponent c, int width, int height) {
    super.getBaseline(c, width, height);
    int baseline = -1;
    for (int column = 0; column < getGridSheetPane().getColumnCount(); column++) {
      Component comp = getHeaderRenderer(column);
      Dimension pref = comp.getPreferredSize();
      int columnBaseline = comp.getBaseline(pref.width, height);
      if (columnBaseline >= 0) {
        if (baseline == -1) {
          baseline = columnBaseline;
        } else if (baseline != columnBaseline) {
          baseline = -1;
          break;
        }
      }
    }
    return baseline;
  }

  //
  // Paint Methods and support
  //
  public void paint(Graphics g, JComponent c) {
    if (getGridSheetPane().getColumnCount() <= 0) {
      return;
    }
    boolean ltr = header.getComponentOrientation().isLeftToRight();

    Rectangle clip = g.getClipBounds();

    boolean isFrozen = false;
    GridSheetScrollPane scrollPane = null;
    Container parent = header.getParent(); // should be viewport
    if (parent != null) {
      parent = parent.getParent(); // should be the scrollpane
      if (parent != null && parent instanceof GridSheetScrollPane) {
        scrollPane = (GridSheetScrollPane) parent;
        isFrozen = scrollPane.isFrozen();
      }
    }

    if (!isFrozen) {
      paintRuleAndCells(g, clip, ltr, 0);
    } else {
      // frozen

      Rectangle viewportRect = scrollPane.getColumnHeader().getBounds();
      Point frozenPoint = scrollPane.getFrozenPoint();
      Point dPoint = scrollPane.getDivisionPoint();
      Point scrolledDistance = scrollPane.getColumnHeader().getViewPosition();

      int frozenAreaWidth = dPoint.x - frozenPoint.x;

      Rectangle leftRect = new Rectangle(frozenPoint.x, 0, frozenAreaWidth, viewportRect.height);
      Rectangle rightRect = new Rectangle(dPoint.x + scrolledDistance.x + frozenPoint.x, 0,
          viewportRect.width - frozenAreaWidth, viewportRect.height);

      // Paint cells.
      paintFrozenGridAndCells(g, rightRect, clip, ltr, scrolledDistance, frozenPoint, false);
      paintFrozenGridAndCells(g, leftRect, clip, ltr, scrolledDistance, frozenPoint, true);

      // paint line.
      Rectangle verticalLineRect = new Rectangle(dPoint.x + scrolledDistance.x - 1 - frozenPoint.x,
          scrolledDistance.y, 1, viewportRect.height);
      paintFrozenLine(g, clip, verticalLineRect);
    }
  }

  private void paintRuleAndCells(Graphics g, Rectangle drawRect, boolean ltr, int correction) {
    Point left = drawRect.getLocation();
    Point right = new Point(drawRect.x + drawRect.width - 1, drawRect.y);
    int cMin = header.columnAtPoint(ltr ? left : right);
    int cMax = header.columnAtPoint(ltr ? right : left);
    // This should never happen.
    if (cMin == -1) {
      cMin = 0;
    }
    // If the table does not have enough columns to fill the view we'll get
    // -1.
    // Replace this with the index of the last column.
    if (cMax == -1) {
      cMax = header.getColumnCount() - 1;
    }

    // Paint the cells.
    paintCells(g, cMin, cMax, correction);

    // Paint the grid.
    paintRule(g, cMin, cMax, correction);

    // Remove all components in the rendererPane.
    rendererPane.removeAll();

  }

  private void paintFrozenGridAndCells(Graphics g, Rectangle drawRect, Rectangle clip, boolean ltr,
                                       Point scrollDistance, Point frozenPoint, boolean freeze) {
    int correction = freeze ? scrollDistance.x : -frozenPoint.x;

    drawRect.x += correction - frozenPoint.x;
    drawRect = drawRect.intersection(clip);
    if (!drawRect.isEmpty()) {
      g.setClip(drawRect);
      correction += (freeze ? 0 : frozenPoint.x);
      drawRect.x -= correction - frozenPoint.x;
      correction -= frozenPoint.x;
      paintRuleAndCells(g, drawRect, ltr, correction);
    }
  }

  private Component getHeaderRenderer(int columnIndex) {
    GridSheetHeaderRenderer renderer = header.getDefaultRenderer();

    // TODO
    boolean hasFocus = !header.isPaintingForPrint() && (columnIndex == getSelectedColumnIndex())
        && header.hasFocus();
    return renderer.getGridCellRendererComponent(header, header.getHeaderValue(columnIndex), false,
        hasFocus, columnIndex);
  }

  /*
   * Paints the grid lines within <I>aRect</I>, using the grid color set with <I>setGridColor</I>.
   * Paints vertical lines if <code>getShowVerticalLines()</code> returns true and paints horizontal
   * lines if <code>getShowHorizontalLines()</code> returns true.
   */
  private void paintRule(Graphics g, int cMin, int cMax, int correction) {
    g.setColor(getGridSheetPane().getColorProvider().getRuleLineColor());

    Rectangle minCell = header.getHeaderRect(cMin);
    Rectangle maxCell = header.getHeaderRect(cMax);
    Rectangle damagedArea = minCell.union(maxCell);
    damagedArea.x += correction;

    int tableHeight = damagedArea.y + damagedArea.height;
    int x = damagedArea.x;
    for (int column = cMin; column <= cMax; column++) {
      x += getGridSheetPane().getColumn(column).getWidth();
      g.drawLine(x - 1, damagedArea.y, x - 1, tableHeight - 1);
    }
    g.drawLine(damagedArea.x, tableHeight - 1, damagedArea.x + damagedArea.width, tableHeight - 1);
  }

  private void paintCells(Graphics g, int cMin, int cMax, int correction) {
    int cellMargin = 1;
    Rectangle cellRect = header.getHeaderRect(cMin);
    cellRect.x += correction;
    for (int column = cMin; column <= cMax; column++) {
      int ColumnWidth = getGridSheetPane().getColumn(column).getWidth();
      cellRect.width = ColumnWidth - cellMargin;
      paintCell(g, cellRect, column);
      cellRect.x += ColumnWidth;
    }
  }

  private void paintCell(Graphics g, Rectangle cellRect, int column) {
    GridSheetHeaderRenderer renderer = header.getDefaultRenderer();
    Component component = header.prepareRenderer(renderer, column);
    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width,
        cellRect.height, true);
  }

  //
  // Size Methods
  //
  private int getHeaderHeight() {
    int height = getGridSheetPane().getModel().getDefaultRowHeight();
    return height;
  }

  private Dimension createHeaderSize(long width) {
    // None of the callers include the intercell spacing, do it here.
    if (width > Integer.MAX_VALUE) {
      width = Integer.MAX_VALUE;
    }
    return new Dimension((int) width, getHeaderHeight());
  }

  /**
   * Return the minimum size of the header. The minimum width is the sum of the minimum widths of
   * each column (plus inter-cell spacing).
   */
  public Dimension getMinimumSize(JComponent c) {
    long width = header.getMinWidth();
    return createHeaderSize(width);
  }

  /**
   * Return the preferred size of the header. The preferred height is the maximum of the preferred
   * heights of all of the components provided by the header renderers. The preferred width is the
   * sum of the preferred widths of each column (plus inter-cell spacing).
   */
  public Dimension getPreferredSize(JComponent c) {
    int width = header.getWidth();
    Dimension d = createHeaderSize(width);
    GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
    if (scrollPane.isFrozen()) {
      Point frozenPoint = scrollPane.getFrozenPoint();
      d.width -= frozenPoint.x;
    }
    return d;
  }

  protected void autoFitColumnWidth(GridSheetPane gridSheetPane, int columnIndex) {
    GridSheetUtils.sizeWidthToFit(gridSheetPane, columnIndex, 1000, Integer.MAX_VALUE);
  }

  /**
   * Return the maximum size of the header. The maximum width is the sum of the maximum widths of
   * each column (plus inter-cell spacing).
   */
  public Dimension getMaximumSize(JComponent c) {
    long width = header.getMaxWidth();
    return createHeaderSize(width);
  }

  @Override
  protected GridSheetPane getGridSheetPane() {
    return header.getGridSheetPane();
  }
}
