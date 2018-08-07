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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetRowHeader;
import com.smoothcsv.swing.gridsheet.GridSheetScrollPane;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import com.smoothcsv.swing.utils.SwingUtils;
import sun.swing.SwingUtilities2;

public class GridSheetRowHeaderUI extends AbstractGridUI {

  private static final StringBuilder BASELINE_COMPONENT_KEY =
      new StringBuilder("GridRowHeader.baselineComponent");

  //
  // Instance Variables
  //
  // The GridSheetRowHeader that is delegating the painting to this UI.
  protected GridSheetRowHeader header;
  protected CellRendererPane rendererPane;

  // Listeners that are attached to the GridSheetTable
  protected MouseInputListener mouseInputListener;

  private Handler handler;

  //
  // Helper class for keyboard actions
  //
  //
  // The Table's mouse and mouse motion listeners
  //

  /**
   * This class should be treated as a &quot;protected&quot; inner class. Instantiate it only within
   * subclasses of BasicTableUI.
   */
  public class MouseInputHandler implements MouseInputListener {
    // NOTE: This class exists only for backward compatability. All
    // its functionality has been moved into Handler. If you need to add
    // new functionality add it to the Handler, but make sure this
    // class calls into the Handler.

    public void mouseClicked(MouseEvent e) {
      getHandler().mouseClicked(e);
    }

    public void mousePressed(MouseEvent e) {
      getHandler().mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
      getHandler().mouseReleased(e);
    }

    public void mouseEntered(MouseEvent e) {
      getHandler().mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
      getHandler().mouseExited(e);
    }

    public void mouseMoved(MouseEvent e) {
      getHandler().mouseMoved(e);
    }

    public void mouseDragged(MouseEvent e) {
      getHandler().mouseDragged(e);
    }
  }

  private class Handler implements MouseInputListener, PropertyChangeListener // ,BeforeDrag
  {

    // MouseInputListener
    public void mouseClicked(MouseEvent e) {}

    private void setValueIsAdjusting(boolean flag) {
      getGridSheetPane().getSelectionModel().setValueIsAdjusting(flag);
    }

    // The row and column where the press occurred and the
    // press event itself
    private int pressedRow = -1;
    private int leadRow = -1;
    private MouseEvent pressedEvent;

    public void mousePressed(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, header)) {
        return;
      }

      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
      if (scrollPane != null && scrollPane.isFrozen()) {
        scrollPane.translateToOriginalViewPoint(p, false, true);
      }
      pressedRow = leadRow = header.rowAtPoint(p);

      if (pressedRow != -1) {
        setValueIsAdjusting(true);
        GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
        sm.setRowHeaderSelected(true);
        sm.setColumnHeaderSelected(false);
        adjustSelection(e);
        getGridSheetPane().getTable().requestFocusInWindow();
      }
    }

    // private void mousePressedDND(MouseEvent e) {
    // pressedEvent = e;
    // boolean grabFocus = true;
    // dragStarted = false;
    //
    // if (canStartDrag() && DragRecognitionSupport.mousePressed(e)) {
    //
    // dragPressDidSelection = false;
    //
    // if (!e.isShiftDown()
    // && table.isCellSelected(pressedRow, pressedCol)) {
    // // clicking on something that's already selected
    // // and need to make it the lead now
    // table.getSelectionModel().addSelectionInterval(pressedRow,
    // pressedRow);
    // table.getColumnModel().getSelectionModel()
    // .addSelectionInterval(pressedCol, pressedCol);
    //
    // return;
    // }
    //
    // dragPressDidSelection = true;
    //
    // // could be a drag initiating event - don't grab focus
    // grabFocus = false;
    // } else {
    // // When drag can't happen, mouse drags might change the
    // // selection in the table
    // // so we want the isAdjusting flag to be set
    // setValueIsAdjusting(true);
    // }
    //
    // if (grabFocus) {
    // SwingUtilities2.adjustFocus(table);
    // }
    //
    // adjustSelection(e);
    // }
    private void adjustSelection(MouseEvent e) {
      // The autoscroller can generate drag events outside the
      // table's range.
      if (pressedRow == -1) {
        return;
      }

      GridSheetPane gridSheetPane = getGridSheetPane();

      GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      if (SwingUtils.isMenuShortcutKeyDown(e)) {
        sm.addSelectionInterval(pressedRow, 0, pressedRow, gridSheetPane.getColumnCount() - 1);
      } else if (e.isShiftDown()) {
        sm.changeLeadSelection(pressedRow, gridSheetPane.getColumnCount() - 1,
            GridSheetSelectionModel.CHANGE_ONLY_VERTICAL);
      } else {
        sm.setSelectionInterval(pressedRow, 0, pressedRow, gridSheetPane.getColumnCount() - 1);
      }
      header.scrollRectToVisible(pressedRow);
    }

    public void mouseReleased(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, header)) {
        return;
      }

      // if (table.getDragEnabled()) {
      // mouseReleasedDND(e);
      // } else {
      // }
      pressedEvent = null;
      setValueIsAdjusting(false);
    }

    // private void mouseReleasedDND(MouseEvent e) {
    // MouseEvent me = DragRecognitionSupport.mouseReleased(e);
    // if (me != null) {
    // SwingUtilities2.adjustFocus(table);
    // if (!dragPressDidSelection) {
    // adjustSelection(me);
    // }
    // }
    //
    // if (!dragStarted) {
    //
    // Point p = e.getPoint();
    //
    // if (pressedEvent != null
    // && table.rowAtPoint(p) == pressedRow
    // && table.columnAtPoint(p) == pressedCol
    // && table.editCellAt(pressedRow, pressedCol,
    // pressedEvent)) {
    //
    // setDispatchComponent(pressedEvent);
    // repostEvent(pressedEvent);
    //
    // // This may appear completely odd, but must be done for
    // // backward
    // // compatibility reasons. Developers have been known to rely
    // // on
    // // a call to shouldSelectCell after editing has begun.
    // CellEditor ce = table.getCellEditor();
    // if (ce != null) {
    // ce.shouldSelectCell(pressedEvent);
    // }
    // }
    // }
    // }
    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, header)) {
        return;
      }

      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
      if (scrollPane != null && scrollPane.isFrozen()) {
        Point frozenPoint = scrollPane.getFrozenPoint();
        p.y += frozenPoint.y;
      }
      int row = header.rowAtPoint(p, 0, header.getRowCount() - 1);
      // The autoscroller can generate drag events outside the
      // table's range.
      if (row == leadRow) {
        return;
      }
      leadRow = row;
      GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
      sm.changeLeadSelection(row, 0, GridSheetSelectionModel.CHANGE_ONLY_VERTICAL);
      scrollToRow(row);
    }

    // PropertyChangeListener
    public void propertyChange(PropertyChangeEvent event) {
      String changeName = event.getPropertyName();
    }
  }

  /**
   * Used by selectRow to scroll horizontally, if necessary, to ensure that the newly selected row
   * is visible.
   */
  private void scrollToRow(int row) {
    GridSheetPane gridSheetPane = getGridSheetPane();

    // Now scroll, if necessary.
    Rectangle vis = gridSheetPane.getVisibleRect();
    Rectangle cellBounds = gridSheetPane.getTable().getCellRect(row, 0, true);
    vis.y = cellBounds.y - 5;
    vis.height = cellBounds.height + 10;
    gridSheetPane.scrollRectToVisible(vis);
  }

  //
  // Factory methods for the Listeners
  //
  private Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }

  /**
   * Creates the key listener for handling keyboard navigation in the GridSheetTable.
   */
  protected KeyListener createKeyListener() {
    return null;
  }

  /**
   * Creates the mouse listener for the GridSheetTable.
   */
  protected MouseInputListener createMouseInputListener() {
    return getHandler();
  }

  //
  // The installation/uninstall procedures and support
  //
  public static ComponentUI createUI(JComponent c) {
    return new GridSheetRowHeaderUI();
  }

  // Installation
  public void installUI(JComponent c) {
    header = (GridSheetRowHeader) c;

    rendererPane = new CellRendererPane();
    header.add(rendererPane);
    installDefaults();
    // installDefaults2();
    installListeners();
  }

  /**
   * Initialize GridSheetTable properties, e.g. font, foreground, and background. The font,
   * foreground, and background properties are only set if their current value is either null or a
   * UIResource, other properties are set if the current value is null.
   *
   * @see #installUI
   */
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(header, "GridHeader.background", "GridHeader.foreground",
        "GridHeader.font");
    LookAndFeel.installProperty(header, "opaque", Boolean.TRUE);

    // // install the scrollpane border
    // Container parent = table.getParent(); // should be viewport
    // if (parent != null) {
    // parent = parent.getParent(); // should be the scrollpane
    // if (parent != null && parent instanceof JScrollPane) {
    // LookAndFeel.installBorder((JScrollPane) parent,
    // "Table.scrollPaneBorder");
    // }
    // }
  }

  private void installDefaults2() {
    TransferHandler th = header.getTransferHandler();
    if (th == null || th instanceof UIResource) {
      header.setTransferHandler(defaultTransferHandler);
      // default TransferHandler doesn't support drop
      // so we don't want drop handling
      if (header.getDropTarget() instanceof UIResource) {
        header.setDropTarget(null);
      }
    }
  }

  /**
   * Attaches listeners to the GridSheetTable.
   */
  protected void installListeners() {
    mouseInputListener = createMouseInputListener();

    header.addMouseListener(mouseInputListener);
    header.addMouseMotionListener(mouseInputListener);
    header.addPropertyChangeListener(getHandler());
  }

  // Uninstallation
  public void uninstallUI(JComponent c) {
    uninstallDefaults();
    uninstallListeners();

    header.remove(rendererPane);
    rendererPane = null;
    header = null;
  }

  protected void uninstallDefaults() {
    if (header.getTransferHandler() instanceof UIResource) {
      header.setTransferHandler(null);
    }
  }

  protected void uninstallListeners() {
    header.removeMouseListener(mouseInputListener);
    header.removeMouseMotionListener(mouseInputListener);
    header.removePropertyChangeListener(getHandler());

    mouseInputListener = null;
    handler = null;
  }

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
    UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();
    Component renderer = (Component) lafDefaults.get(BASELINE_COMPONENT_KEY);
    if (renderer == null) {
      GridSheetHeaderRenderer tcr = new DefaultGridSheetHeaderCellRenderer();
      renderer = tcr.getGridCellRendererComponent(header, "a", false, false, -1);
      lafDefaults.put(BASELINE_COMPONENT_KEY, renderer);
    }
    renderer.setFont(header.getFont());
    int rowMargin = 1;
    return renderer.getBaseline(Integer.MAX_VALUE,
        getGridSheetPane().getModel().getDefaultRowHeight() - rowMargin) + rowMargin / 2;
  }

  /**
   * Returns an enum indicating how the baseline of the component changes as the size changes.
   *
   * @throws NullPointerException {@inheritDoc}
   * @see javax.swing.JComponent#getBaseline(int, int)
   * @since 1.6
   */
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
    super.getBaselineResizeBehavior(c);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }

  //
  // Size Methods
  //
  private int getHeaderWidth() {
    return 60;
  }

  private Dimension createHeaderSize(long height) {
    // None of the callers include the intercell spacing, do it here.
    if (height > Integer.MAX_VALUE) {
      height = Integer.MAX_VALUE;
    }
    return new Dimension(getHeaderWidth(), (int) height);
  }

  /**
   * Return the minimum size of the table. The minimum height is the row height times the number of
   * rows. The minimum width is the sum of the minimum widths of each column.
   */
  public Dimension getMinimumSize(JComponent c) {
    int height = header.getMinHeight();
    return createHeaderSize(height);
  }

  /**
   * Return the preferred size of the table. The preferred height is the row height times the number
   * of rows. The preferred width is the sum of the preferred widths of each column.
   */
  public Dimension getPreferredSize(JComponent c) {
    int height = header.getHeight();
    Dimension d = createHeaderSize(height);
    GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(header);
    if (scrollPane.isFrozen()) {
      Point frozenPoint = scrollPane.getFrozenPoint();
      d.height -= frozenPoint.y;
    }
    return d;
  }

  /**
   * Return the maximum size of the table. The maximum height is the row heighttimes the number of
   * rows. The maximum width is the sum of the maximum widths of each column.
   */
  public Dimension getMaximumSize(JComponent c) {
    int height = header.getMaxHeight();
    return createHeaderSize(height);
  }

  //
  // Paint methods and support
  //
  public void paint(Graphics g, JComponent c) {
    if (getGridSheetPane().getRowCount() <= 0) {
      return;
    }

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
      paintGridAndCells(g, clip, 0);
    } else {
      // frozen

      Rectangle viewportRect = scrollPane.getRowHeader().getBounds();
      Point frozenPoint = scrollPane.getFrozenPoint();
      Point dPoint = scrollPane.getDivisionPoint();
      Point scrolledDistance = scrollPane.getRowHeader().getViewPosition();

      int frozenAreaHeight = dPoint.y - frozenPoint.y;

      Rectangle upperRect = new Rectangle(0, frozenPoint.y, viewportRect.width, frozenAreaHeight);
      Rectangle lowerRect = new Rectangle(0, dPoint.y + scrolledDistance.y + frozenPoint.y,
          viewportRect.width, viewportRect.height - frozenAreaHeight);

      // Paint cells.
      paintFrozenGridAndCells(g, lowerRect, clip, scrolledDistance, frozenPoint, false);
      paintFrozenGridAndCells(g, upperRect, clip, scrolledDistance, frozenPoint, true);

      // paint line.
      Rectangle horizontalLineRect = new Rectangle(scrolledDistance.x,
          dPoint.y + scrolledDistance.y - 1 - frozenPoint.y, viewportRect.width, 1);
      paintFrozenLine(g, clip, horizontalLineRect);
    }
  }

  private void paintGridAndCells(Graphics g, Rectangle drawRect, int correction) {
    Point top = drawRect.getLocation();
    Point bottom = new Point(drawRect.x, drawRect.y + drawRect.height - 1);
    int rMin = header.rowAtPoint(top);
    int rMax = header.rowAtPoint(bottom);
    // This should never happen.
    if (rMin == -1) {
      rMin = 0;
    }
    // If the table does not have enough columns to fill the view we'll get
    // -1.
    // Replace this with the index of the last column.
    if (rMax == -1) {
      rMax = header.getRowCount() - 1;
    }

    // Paint the cells.
    paintCells(g, rMin, rMax, correction);

    // Paint the grid.
    paintRule(g, rMin, rMax, correction);

  }

  private void paintFrozenGridAndCells(Graphics g, Rectangle drawRect, Rectangle clip,
                                       Point scrollDistance, Point frozenPoint, boolean freeze) {
    int correction = freeze ? scrollDistance.y : -frozenPoint.y;

    drawRect.y += correction - frozenPoint.y;
    drawRect = drawRect.intersection(clip);
    if (!drawRect.isEmpty()) {
      g.setClip(drawRect);
      correction += (freeze ? 0 : frozenPoint.y);
      drawRect.y -= correction - frozenPoint.y;
      correction -= frozenPoint.y;
      paintGridAndCells(g, drawRect, correction);
    }
  }

  /*
   * Paints the grid lines within <I>aRect</I>, using the grid color set with <I>setGridColor</I>.
   * Paints vertical lines if <code>getShowVerticalLines()</code> returns true and paints horizontal
   * lines if <code>getShowHorizontalLines()</code> returns true.
   */
  private void paintRule(Graphics g, int rMin, int rMax, int correction) {
    GridSheetPane gridSheetPane = getGridSheetPane();
    g.setColor(gridSheetPane.getColorProvider().getRuleLineColor());

    Rectangle minCell = header.getHeaderRect(rMin);
    Rectangle maxCell = header.getHeaderRect(rMax);
    Rectangle damagedArea = minCell.union(maxCell);
    damagedArea.y += correction;

    int tableWidth = damagedArea.x + damagedArea.width;
    int y = damagedArea.y;
    for (int row = rMin; row <= rMax; row++) {
      y += gridSheetPane.getRow(row).getHeight();
      g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
    }
    g.drawLine(tableWidth - 1, damagedArea.y, tableWidth - 1, damagedArea.y + damagedArea.height);
  }

  private void paintCells(Graphics g, int rMin, int rMax, int correction) {
    GridSheetPane gridSheetPane = getGridSheetPane();
    int cellMargin = 1;
    Rectangle cellRect = header.getHeaderRect(rMin);
    cellRect.y += correction;
    for (int row = rMin; row <= rMax; row++) {
      int rowHeaight = gridSheetPane.getRow(row).getHeight();
      cellRect.height = rowHeaight - cellMargin;
      paintCell(g, cellRect, row);
      cellRect.y += rowHeaight;
    }

    // Remove all components in the rendererPane.
    rendererPane.removeAll();
  }

  private void paintCell(Graphics g, Rectangle cellRect, int row) {
    GridSheetHeaderRenderer renderer = header.getDefaultRenderer();
    Component component = header.prepareRenderer(renderer, row);
    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width,
        cellRect.height, true);
  }

  private static final TransferHandler defaultTransferHandler = new TableTransferHandler();

  @SuppressWarnings("serial")
  static class TableTransferHandler extends TransferHandler implements UIResource {

    // /**
    // * Create a Transferable to use as the source for a data transfer.
    // *
    // * @param c
    // * The component holding the data to be transfered. This
    // * argument is provided to enable sharing of TransferHandlers
    // * by multiple components.
    // * @return The representation of the data to be transfered.
    // *
    // */
    // protected Transferable createTransferable(JComponent c) {
    // if (c instanceof GridSheetTable) {
    // GridSheetTable table = (GridSheetTable) c;
    // int[] rows;
    // int[] cols;
    //
    // rows = table.getSelectedRows();
    //
    // cols = table.getSelectedColumns();
    //
    // if (rows == null || cols == null || rows.length == 0
    // || cols.length == 0) {
    // return null;
    // }
    //
    // StringBuilder plainBuf = new StringBuilder();
    // StringBuilder htmlBuf = new StringBuilder();
    //
    // htmlBuf.append("<html>\n<body>\n<table>\n");
    //
    // for (int row = 0; row < rows.length; row++) {
    // htmlBuf.append("<tr>\n");
    // for (int col = 0; col < cols.length; col++) {
    // Object obj = table.getValueAt(rows[row], cols[col]);
    // String val = ((obj == null) ? "" : obj.toString());
    // plainBuf.append(val + "\t");
    // htmlBuf.append(" <td>" + val + "</td>\n");
    // }
    // // we want a newline at the end of each line and not a tab
    // plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
    // htmlBuf.append("</tr>\n");
    // }
    //
    // // remove the last newline
    // plainBuf.deleteCharAt(plainBuf.length() - 1);
    // htmlBuf.append("</table>\n</body>\n</html>");
    //
    // return new ExBasicTransferable(plainBuf.toString(),
    // htmlBuf.toString());
    // }
    //
    // return null;
    // }
    public int getSourceActions(JComponent c) {
      return COPY;
    }

  }

  @Override
  protected GridSheetPane getGridSheetPane() {
    return header.getGridSheetPane();
  }
}
