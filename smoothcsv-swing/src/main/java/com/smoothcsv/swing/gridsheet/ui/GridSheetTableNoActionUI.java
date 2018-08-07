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
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.CellEditor;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.smoothcsv.commons.constants.Direction;
import com.smoothcsv.swing.gridsheet.GridSheetCellEditor;
import com.smoothcsv.swing.gridsheet.GridSheetColumnHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetScrollPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.model.IGridSheetModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetColorProvider;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetColorProvider;
import com.smoothcsv.swing.utils.SwingUtils;
import sun.swing.SwingUtilities2;

public class GridSheetTableNoActionUI extends AbstractGridUI {

  private static final StringBuilder BASELINE_COMPONENT_KEY =
      new StringBuilder("Grid.baselineComponent");

  private static final Cursor AUTOFILL_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

  //
  // Instance Variables
  //
  // The GridSheetTable that is delegating the painting to this UI.
  private GridSheetTable table;
  protected CellRendererPane rendererPane;
  protected CellRendererPane frozenCellRendererPane;

  protected MouseInputListener mouseInputListener;

  private Handler handler;

  private Cursor otherCursor = AUTOFILL_CURSOR;

  //
  // Helper class for keyboard actions
  //

  //
  // The Table's focus listener
  //
  // /**
  // * This class should be treated as a &quot;protected&quot; inner class.
  // * Instantiate it only within subclasses of {@code BasicTableUI}.
  // */
  // public class FocusHandler implements FocusListener {
  // // NOTE: This class exists only for backward compatability. All
  // // its functionality has been moved into Handler. If you need to add
  // // new functionality add it to the Handler, but make sure this
  // // class calls into the Handler.
  // public void focusGained(FocusEvent e) {
  // getHandler().focusGained(e);
  // }
  //
  // public void focusLost(FocusEvent e) {
  // getHandler().focusLost(e);
  // }
  // }
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

  protected class Handler implements MouseInputListener, PropertyChangeListener // ,BeforeDrag
  {

    // FocusListener
    // private void repaintLeadCell() {
    // int lr = table.getSelectionSupport().getLeadRowSelectionIndex();
    // int lc = table.getSelectionSupport().getLeadColumnSelectionIndex();
    //
    // if (lr < 0 || lc < 0) {
    // return;
    // }
    //
    // Rectangle dirtyRect = table.getCellRect(lr, lc, false);
    // table.repaint(dirtyRect);
    // }
    //
    // public void focusGained(FocusEvent e) {
    // repaintLeadCell();
    // }
    //
    // public void focusLost(FocusEvent e) {
    // repaintLeadCell();
    // }


    // MouseInputListener
    // Component receiving mouse events during editing.
    // May not be editorComponent.
    private Component dispatchComponent;

    public void mouseClicked(MouseEvent e) {
      if (!table.isEnabled()) {
        return;
      }
      if (e.getClickCount() % 2 == 0 && table.getCursor() == AUTOFILL_CURSOR
          && SwingUtilities.isLeftMouseButton(e)) {
        // autofill to the last row
        GridSheetSelectionModel sm = table.getGridSheetPane().getSelectionModel();
        GridSheetCellRange autofillBaseRange = new GridSheetCellRange(
            sm.getMainMinRowSelectionIndex(), sm.getMainMaxRowSelectionIndex(),
            sm.getMainMinColumnSelectionIndex(), sm.getMainMaxColumnSelectionIndex());
        table.autofill(autofillBaseRange, Direction.DOWN,
            table.getGridSheetPane().getRowCount() - autofillBaseRange.getLastRow() - 1);
        mouseMoved(e);
        e.consume();
      }
    }

    private void setDispatchComponent(MouseEvent e) {
      GridSheetCellEditor editor = table.getCellEditor();
      JComponent editorComponent = editor.getEditorComponent();
      Point p = e.getPoint();
      Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
      dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, p2.x, p2.y);
      SwingUtilities2.setSkipClickCount(dispatchComponent, e.getClickCount() - 1);
    }

    private boolean repostEvent(MouseEvent e) {
      // Check for isEditing() in case another event has
      // caused the editor to be removed. See bug #4306499.
      if (dispatchComponent == null || !table.isEditing()) {
        return false;
      }
      MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e, dispatchComponent);
      dispatchComponent.dispatchEvent(e2);
      return true;
    }

    private void setValueIsAdjusting(boolean flag) {
      getGridSheetPane().getSelectionModel().setValueIsAdjusting(flag);
    }

    // The row and column where the press occurred and the
    // press event itself
    private int pressedRow;
    private int pressedCol;
    private MouseEvent pressedEvent;

    // Whether or not the mouse press (which is being considered as part
    // of a drag sequence) also caused the selection change to be fully
    // processed.
    private boolean dragPressDidSelection;

    // Set to true when a drag gesture has been fully recognized and DnD
    // begins. Use this to ignore further mouse events which could be
    // delivered if DnD is cancelled (via ESCAPE for example)
    private boolean dragStarted;

    // To cache the return value of pointOutsidePrefSize since we use
    // it multiple times.
    private boolean outsidePrefSize;

    private GridSheetCellRange autofillBaseRange;
    private Rectangle autofillBaseRect;

    private boolean canStartDrag() {
      if (pressedRow == -1 || pressedCol == -1) {
        return false;
      }
      return getGridSheetPane().isCellSelected(pressedRow, pressedCol);
    }

    private void swapCursor() {
      Cursor tmp = table.getCursor();
      table.setCursor(otherCursor);
      otherCursor = tmp;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, table)) {
        return;
      }
      autofillBaseRange = null;
      autofillBaseRect = null;
      if (table.getCursor() == AUTOFILL_CURSOR) {
        // start autofill
        GridSheetSelectionModel sm = table.getGridSheetPane().getSelectionModel();
        autofillBaseRange = new GridSheetCellRange(sm.getMainMinRowSelectionIndex(),
            sm.getMainMaxRowSelectionIndex(), sm.getMainMinColumnSelectionIndex(),
            sm.getMainMaxColumnSelectionIndex());
        autofillBaseRect = table.getCellRect(autofillBaseRange);
      } else {
        Point p = e.getPoint();
        GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(table);
        if (scrollPane != null && scrollPane.isFrozen()) {
          scrollPane.translateToOriginalViewPoint(p);
        }
        GridSheetPane gridSheetPane = getGridSheetPane();
        pressedRow = gridSheetPane.rowAtPoint(p);
        pressedCol = gridSheetPane.columnAtPoint(p);
        outsidePrefSize = pointOutsidePrefSize(pressedRow, pressedCol, p);

        if (pressedRow == -1 || pressedCol == -1) {
          return;
        }

        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
          GridSheetCellEditor editor = table.getCellEditor();
          JComponent editorComponent = editor.getEditorComponent();
          if (editorComponent != null && !editorComponent.hasFocus()) {
            SwingUtilities2.compositeRequestFocus(editorComponent);
          }
          return;
        }

        // if (table.getDragEnabled()) {
        // mousePressedDND(e);
        // } else {
        SwingUtilities2.adjustFocus(table);
        setValueIsAdjusting(true);
        adjustSelection(e);
        // }
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
      // Fix for 4835633
      GridSheetPane gridSheetPane = getGridSheetPane();
      if (outsidePrefSize) {
        // If shift is down in multi-select, we should just return.
        // For single select or non-shift-click, clear the selection
        if (e.getID() == MouseEvent.MOUSE_PRESSED && !e.isShiftDown()) {
          gridSheetPane.getSelectionModel().clearSelection();
          GridSheetCellEditor tce = table.getCellEditor();
          if (tce != null) {
            tce.stopCellEditing();
          }
        }
        return;
      }
      // The autoscroller can generate drag events outside the
      // table's range.
      if ((pressedCol == -1) || (pressedRow == -1)) {
        return;
      }

      // boolean dragEnabled = table.getDragEnabled();
      if (table.editCellAt(pressedRow, pressedCol, e)) {
        setDispatchComponent(e);
        repostEvent(e);
      }

      CellEditor editor = table.getCellEditor();
      if (editor == null || editor.shouldSelectCell(e)) {
        GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
        sm.clearHeaderSelection();
        if (SwingUtils.isMenuShortcutKeyDown(e)) {
          sm.addSelectionInterval(pressedRow, pressedCol, pressedRow, pressedCol);
        } else if (e.isShiftDown()) {
          sm.changeLeadSelection(pressedRow, pressedCol, GridSheetSelectionModel.DEFAULT);
        } else {
          sm.setSelectionInterval(pressedRow, pressedCol, pressedRow, pressedCol);
        }
      }
      table.scrollRectToVisible(pressedRow, pressedCol);
    }

    // public void actionPerformed(ActionEvent ae) {
    // table.editCellAt(pressedRow, pressedCol, null);
    // Component editorComponent = table.getEditorComponent();
    // if (editorComponent != null && !editorComponent.hasFocus()) {
    // SwingUtilities2.compositeRequestFocus(editorComponent);
    // }
    // return;
    // }

    public void mouseReleased(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, table)) {
        return;
      }

      // if (table.getDragEnabled()) {
      // mouseReleasedDND(e);
      // } else {
      // }
      pressedEvent = null;
      repostEvent(e);
      dispatchComponent = null;
      setValueIsAdjusting(false);
      if (autofillBaseRange != null) {
        GridSheetCellRange autofillRange = table.getAutofillRange();
        if (autofillRange != null && !autofillBaseRange.equals(autofillRange)) {
          Direction direction;
          int num;
          if (autofillBaseRange.getLastRow() < autofillRange.getLastRow()) {
            direction = Direction.DOWN;
            num = autofillRange.getLastRow() - autofillBaseRange.getLastRow();
          } else if (autofillBaseRange.getFirstRow() > autofillRange.getFirstRow()) {
            direction = Direction.UP;
            num = autofillBaseRange.getFirstRow() - autofillRange.getFirstRow();
          } else if (autofillBaseRange.getLastColumn() < autofillRange.getLastColumn()) {
            direction = Direction.RIGHT;
            num = autofillRange.getLastColumn() - autofillBaseRange.getLastColumn();
          } else if (autofillBaseRange.getFirstColumn() > autofillRange.getFirstColumn()) {
            direction = Direction.LEFT;
            num = autofillBaseRange.getFirstColumn() - autofillRange.getFirstColumn();
          } else {
            throw new IllegalStateException();
          }
          table.autofill(autofillBaseRange, direction, num);
        }
        table.setAutofillRange(null);
        autofillBaseRange = null;
        autofillBaseRect = null;
      }
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

    public void mouseMoved(MouseEvent e) {
      if (!table.isEnabled()) {
        return;
      }
      GridSheetSelectionModel sm = table.getGridSheetPane().getSelectionModel();
      if (sm.isAdditionallySelected()) {
        return;
      }
      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(table);
      if (scrollPane.isFrozen()) {
        scrollPane.translateToOriginalViewPoint(p, true, false);
      }
      int rightBottomSelectionRow = sm.getMainMaxRowSelectionIndex();
      int rightBottomSelectionColumn = sm.getMainMaxColumnSelectionIndex();
      Rectangle rightBottomCellRect =
          table.getCellRect(rightBottomSelectionRow, rightBottomSelectionColumn, false);
      Rectangle autofillRect = new Rectangle(rightBottomCellRect.x + rightBottomCellRect.width - 5,
          rightBottomCellRect.y + rightBottomCellRect.height - 5, 6, 6);
      if ((autofillRect.contains(p)
          && !table.isEditing()) != (table.getCursor() == AUTOFILL_CURSOR)) {
        swapCursor();
      }
    }

    public void dragStarting(MouseEvent me) {
      dragStarted = true;
      pressedEvent = null;
    }

    public void mouseDragged(MouseEvent e) {
      if (SwingUtilities2.shouldIgnore(e, table)) {
        return;
      }

      repostEvent(e);

      if (table.isEditing()) {
        return;
      }

      Point p = e.getPoint();
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(table);
      if (scrollPane != null && scrollPane.isFrozen()) {
        Point frozenPoint = scrollPane.getFrozenPoint();
        p.x += frozenPoint.x;
        p.y += frozenPoint.y;
      }
      GridSheetPane gridSheetPane = getGridSheetPane();
      int row = gridSheetPane.rowAtPoint(p);
      int column = gridSheetPane.columnAtPoint(p);

      if (autofillBaseRange != null) {
        // autofilling
        if (autofillBaseRect.contains(p)) {
          table.setAutofillRange(null);
          return;
        } else {
          int distanceX = 0;
          int distanceY = 0;
          if (p.x < autofillBaseRect.x) {
            distanceX = autofillBaseRect.x - p.x;
          } else if (autofillBaseRect.x + autofillBaseRect.width < p.x) {
            distanceX = p.x - (autofillBaseRect.x + autofillBaseRect.width);
          }
          if (p.y < autofillBaseRect.y) {
            distanceY = autofillBaseRect.y - p.y;
          } else if (autofillBaseRect.y + autofillBaseRect.height < p.y) {
            distanceY = p.y - (autofillBaseRect.y + autofillBaseRect.height);
          }
          if (distanceX == 0 && distanceY == 0) {
            table.setAutofillRange(null);
            return;
          }
          GridSheetCellRange autofillRange;
          if (distanceX < distanceY) {
            int r = gridSheetPane.rowAtPoint(p, 0, gridSheetPane.getRowCount() - 1);
            autofillRange = autofillBaseRange.extendVerticallyTo(r);
          } else {
            int c = gridSheetPane.columnAtPoint(p, 0, gridSheetPane.getColumnCount() - 1);
            autofillRange = autofillBaseRange.extendHorizontallyTo(c);
          }
          table.setAutofillRange(autofillRange);
          table.scrollRectToVisible(row, column);
        }
      } else {
        // The autoscroller can generate drag events outside the
        // table's range.
        if ((column == -1) || (row == -1)) {
          return;
        }

        gridSheetPane.getSelectionModel().changeLeadSelection(row, column,
            GridSheetSelectionModel.DEFAULT);
        table.scrollRectToVisible(row, column);
      }
    }

    // PropertyChangeListener
    public void propertyChange(PropertyChangeEvent event) {
      String changeName = event.getPropertyName();

      if ("componentOrientation" == changeName) {

        GridSheetColumnHeader header = getGridSheetPane().getColumnHeader();
        if (header != null) {
          header.setComponentOrientation((ComponentOrientation) event.getNewValue());
        }
      }
      // else if ("dropLocation" == changeName) {
      // GridSheetTable.DropLocation oldValue =
      // (GridSheetTable.DropLocation) event
      // .getOldValue();
      // repaintDropLocation(oldValue);
      // repaintDropLocation(table.getDropLocation());
      // }
    }

    private void repaintDropLocation(GridSheetTable.DropLocation loc) {
      if (loc == null) {
        return;
      }

      if (!loc.isInsertRow() && !loc.isInsertColumn()) {
        Rectangle rect = table.getCellRect(loc.getRow(), loc.getColumn(), false);
        if (rect != null) {
          table.repaint(rect);
        }
        return;
      }

      if (loc.isInsertRow()) {
        Rectangle rect = extendRect(getHDropLineRect(loc), true);
        if (rect != null) {
          table.repaint(rect);
        }
      }

      if (loc.isInsertColumn()) {
        Rectangle rect = extendRect(getVDropLineRect(loc), false);
        if (rect != null) {
          table.repaint(rect);
        }
      }
    }
  }

  /*
   * Returns true if the given point is outside the preferredSize of the item at the given row of
   * the table. (Column must be 0). Returns false if the "Table.isFileList" client property is not
   * set.
   */
  private boolean pointOutsidePrefSize(int row, int column, Point p) {
    return false;
  }

  //
  // Factory methods for the Listeners
  //
  protected Handler getHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
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
    return new GridSheetTableNoActionUI();
  }

  // Installation
  public void installUI(JComponent c) {
    table = (GridSheetTable) c;

    rendererPane = new CellRendererPane();
    table.add(rendererPane);
    installDefaults();
    installDefaults2();
    installListeners();

    frozenCellRendererPane = new CellRendererPane();
    // table.getRootPane().getLayeredPane()
    // .add(frozenCellRendererPane, JLayeredPane.POPUP_LAYER);
  }

  /**
   * Initialize GridSheetTable properties, e.g. font, foreground, and background. The font,
   * foreground, and background properties are only set if their current value is either null or a
   * UIResource, other properties are set if the current value is null.
   *
   * @see #installUI
   */
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(table, "Grid.background", "Grid.foreground", "Grid.font");
    // GridSheetTable's original row height is 16. To correctly display the
    // contents on Linux we should have set it to 18, Windows 19 and
    // Solaris 20. As these values vary so much it's too hard to
    // be backward compatable and try to update the row height, we're
    // therefor NOT going to adjust the row height based on font. If the
    // developer changes the font, it's there responsability to update
    // the row height.

    LookAndFeel.installProperty(table, "opaque", Boolean.TRUE);

    GridSheetPane gridSheetPane = getGridSheetPane();
    GridSheetColorProvider colorProvider = gridSheetPane.getColorProvider();
    if (colorProvider == null || colorProvider instanceof UIResource) {
      colorProvider = (GridSheetColorProvider) UIManager.get("Grid.colorProvider");
      gridSheetPane.setColorProvider(
          colorProvider != null ? colorProvider : DefaultGridSheetColorProvider.getInstance());
    }

    // Color sbg = table.getSelectionBackground();
    // if (sbg == null || sbg instanceof UIResource) {
    // sbg = UIManager.getColor("Table.selectionBackground");
    // table.setSelectionBackground(sbg != null ? sbg : UIManager
    // .getColor("textHighlight"));
    // }
    //
    // Color sfg = table.getSelectionForeground();
    // if (sfg == null || sfg instanceof UIResource) {
    // sfg = UIManager.getColor("Table.selectionForeground");
    // table.setSelectionForeground(sfg != null ? sfg : UIManager
    // .getColor("textHighlightText"));
    // }
    // install the scrollpane border
    Container parent = table.getParent(); // should be viewport
    if (parent != null) {
      parent = parent.getParent(); // should be the scrollpane
      if (parent != null && parent instanceof JScrollPane) {
        LookAndFeel.installBorder((JScrollPane) parent, "Grid.scrollPaneBorder");
      }
    }
  }

  private void installDefaults2() {
    TransferHandler th = table.getTransferHandler();
    if (th == null || th instanceof UIResource) {
      table.setTransferHandler(defaultTransferHandler);
      // default TransferHandler doesn't support drop
      // so we don't want drop handling
      if (table.getDropTarget() instanceof UIResource) {
        table.setDropTarget(null);
      }
    }
  }

  /**
   * Attaches listeners to the GridSheetTable.
   */
  protected void installListeners() {
    mouseInputListener = createMouseInputListener();

    table.addMouseListener(mouseInputListener);
    table.addMouseMotionListener(mouseInputListener);
    table.addPropertyChangeListener(getHandler());
  }

  // Uninstallation
  public void uninstallUI(JComponent c) {
    uninstallDefaults();
    uninstallListeners();

    table.remove(rendererPane);
    rendererPane = null;
    table.getRootPane().getLayeredPane().remove(frozenCellRendererPane);

    table = null;
    frozenCellRendererPane = null;
  }

  protected void uninstallDefaults() {
    if (table.getTransferHandler() instanceof UIResource) {
      table.setTransferHandler(null);
    }
  }

  protected void uninstallListeners() {
    table.removeMouseListener(mouseInputListener);
    table.removeMouseMotionListener(mouseInputListener);
    table.removePropertyChangeListener(getHandler());

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
      DefaultGridSheetCellRenderer tcr = new DefaultGridSheetCellRenderer();
      renderer = tcr.getGridCellRendererComponent(table, "a", false, false, -1, -1);
      lafDefaults.put(BASELINE_COMPONENT_KEY, renderer);
    }
    renderer.setFont(table.getFont());
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
  private Dimension createTableSize(long width) {
    int height = 0;
    GridSheetPane gridSheetPane = getGridSheetPane();
    int rowCount = gridSheetPane.getRowCount();
    if (rowCount > 0 && gridSheetPane.getColumnCount() > 0) {
      Rectangle r = table.getCellRect(rowCount - 1, 0, true);
      height = r.y + r.height;
    }
    // Width is always positive. The call to abs() is a workaround for
    // a bug in the 1.1.6 JIT on Windows.
    long tmp = Math.abs(width);
    if (tmp > Integer.MAX_VALUE) {
      tmp = Integer.MAX_VALUE;
    }
    return new Dimension((int) tmp, height);
  }

  /**
   * Return the minimum size of the table. The minimum height is the row height times the number of
   * rows. The minimum width is the sum of the minimum widths of each column.
   */
  public Dimension getMinimumSize(JComponent c) {
    GridSheetPane gridSheetPane = getGridSheetPane();
    IGridSheetModel structure = gridSheetPane.getModel();
    int width = gridSheetPane.getColumnCount() * structure.getMinColumnWidth();
    int height = gridSheetPane.getRowCount() * structure.getMinRowHeight();
    return new Dimension(width, height);
  }

  /**
   * Return the preferred size of the table. The preferred height is the row height times the number
   * of rows. The preferred width is the sum of the preferred widths of each column.
   */
  public Dimension getPreferredSize(JComponent c) {
    Dimension d = new Dimension(getGridSheetPane().getTotalColumnWidth(),
        getGridSheetPane().getTotalRowHeight());
    GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(table);
    if (scrollPane.isFrozen()) {
      Point frozenPoint = scrollPane.getFrozenPoint();
      d.width -= frozenPoint.x;
      d.height -= frozenPoint.y;
    }
    return d;
  }

  /**
   * Return the maximum size of the table. The maximum height is the row heighttimes the number of
   * rows. The maximum width is the sum of the maximum widths of each column.
   */
  public Dimension getMaximumSize(JComponent c) {
    GridSheetPane gridSheetPane = getGridSheetPane();
    IGridSheetModel structure = gridSheetPane.getModel();
    int width = structure.getColumnCount() * structure.getMaxColumnWidth();
    int height = structure.getRowCount() * structure.getMaxRowHeight();
    return new Dimension(width, height);
  }

  //
  // Paint methods and support
  //

  /**
   * Paint a representation of the <code>table</code> instance that was set in installUI().
   */
  @Override
  public void paint(Graphics g, JComponent c) {
    Rectangle clip = g.getClipBounds();

    Rectangle bounds = table.getBounds();
    // account for the fact that the graphics has already been translated
    // into the table's bounds
    bounds.x = bounds.y = 0;

    GridSheetPane gridSheetPane = getGridSheetPane();

    if (gridSheetPane.getRowCount() <= 0 || gridSheetPane.getColumnCount() <= 0
        || !bounds.intersects(clip)) {

      // paintDropLines(g);
      return;
    }

    boolean isFrozen = false;
    GridSheetScrollPane scrollPane = null;
    Container parent = table.getParent(); // should be viewport
    if (parent != null) {
      parent = parent.getParent(); // should be the scrollpane
      if (parent != null && parent instanceof GridSheetScrollPane) {
        scrollPane = (GridSheetScrollPane) parent;
        isFrozen = scrollPane.isFrozen();
      }
    }

    boolean ltr = table.getComponentOrientation().isLeftToRight();

    if (!isFrozen) {
      paintRuleAndCells(g, clip, ltr, 0, 0);
    } else {
      // frozen

      Rectangle viewportRect = scrollPane.getViewport().getBounds();
      Point frozenPoint = scrollPane.getFrozenPoint();
      Point dPoint = scrollPane.getDivisionPoint();
      Point scrolledDistance = scrollPane.getViewport().getViewPosition();

      int frozenAreaWidth = dPoint.x - frozenPoint.x;
      int frozenAreaHeight = dPoint.y - frozenPoint.y;

      Rectangle upperLeftRect =
          new Rectangle(frozenPoint.x, frozenPoint.y, frozenAreaWidth, frozenAreaHeight);
      Rectangle lowerRightRect = new Rectangle(dPoint.x + scrolledDistance.x + frozenPoint.x,
          dPoint.y + scrolledDistance.y + frozenPoint.y, viewportRect.width - frozenAreaWidth,
          viewportRect.height - frozenAreaHeight);
      Rectangle upperRightRect =
          new Rectangle(lowerRightRect.x, upperLeftRect.y, lowerRightRect.width, frozenAreaHeight);
      Rectangle lowerLeftRect =
          new Rectangle(upperLeftRect.x, lowerRightRect.y, frozenAreaWidth, lowerRightRect.height);

      // scrolledDistance.x -= frozenPoint.x;
      // scrolledDistance.y -= frozenPoint.y;
      // Paint cells.
      paintFrozenGridAndCells(g, lowerRightRect, clip, ltr, scrolledDistance, frozenPoint, false,
          false);
      paintFrozenGridAndCells(g, upperRightRect, clip, ltr, scrolledDistance, frozenPoint, false,
          true);
      paintFrozenGridAndCells(g, lowerLeftRect, clip, ltr, scrolledDistance, frozenPoint, true,
          false);
      paintFrozenGridAndCells(g, upperLeftRect, clip, ltr, scrolledDistance, frozenPoint, true,
          true);

      // clip.x -= frozenPoint.x;
      // clip.y -= frozenPoint.y;
      // paint line.
      Rectangle horizontalLineRect = new Rectangle(scrolledDistance.x,
          dPoint.y + scrolledDistance.y - 1 - frozenPoint.y, viewportRect.width, 1);
      Rectangle verticalLineRect = new Rectangle(dPoint.x + scrolledDistance.x - 1 - frozenPoint.x,
          scrolledDistance.y, 1, viewportRect.height);
      paintFrozenLine(g, clip, horizontalLineRect);
      paintFrozenLine(g, clip, verticalLineRect);

      // g.drawLine(clip.x, clip.y, clip.x + clip.width, clip.y +
      // clip.height);
    }
  }

  // private void paintDropLines(Graphics g) {
  // GridSheetTable.DropLocation loc = table.getDropLocation();
  // if (loc == null) {
  // return;
  // }
  //
  // Color color = UIManager.getColor("Table.dropLineColor");
  // Color shortColor = UIManager.getColor("Table.dropLineShortColor");
  // if (color == null && shortColor == null) {
  // return;
  // }
  //
  // Rectangle rect;
  //
  // rect = getHDropLineRect(loc);
  // if (rect != null) {
  // int x = rect.x;
  // int w = rect.width;
  // if (color != null) {
  // extendRect(rect, true);
  // g.setColor(color);
  // g.fillRect(rect.x, rect.y, rect.width, rect.height);
  // }
  // if (!loc.isInsertColumn() && shortColor != null) {
  // g.setColor(shortColor);
  // g.fillRect(x, rect.y, w, rect.height);
  // }
  // }
  //
  // rect = getVDropLineRect(loc);
  // if (rect != null) {
  // int y = rect.y;
  // int h = rect.height;
  // if (color != null) {
  // extendRect(rect, false);
  // g.setColor(color);
  // g.fillRect(rect.x, rect.y, rect.width, rect.height);
  // }
  // if (!loc.isInsertRow() && shortColor != null) {
  // g.setColor(shortColor);
  // g.fillRect(rect.x, y, rect.width, h);
  // }
  // }
  // }

  private Rectangle getHDropLineRect(GridSheetTable.DropLocation loc) {
    if (!loc.isInsertRow()) {
      return null;
    }

    GridSheetPane gridSheetPane = getGridSheetPane();

    int row = loc.getRow();
    int col = loc.getColumn();
    if (col >= gridSheetPane.getColumnCount()) {
      col--;
    }

    Rectangle rect = table.getCellRect(row, col, true);

    if (row >= gridSheetPane.getRowCount()) {
      row--;
      Rectangle prevRect = table.getCellRect(row, col, true);
      rect.y = prevRect.y + prevRect.height;
    }

    if (rect.y == 0) {
      rect.y = -1;
    } else {
      rect.y -= 2;
    }

    rect.height = 3;

    return rect;
  }

  private Rectangle getVDropLineRect(GridSheetTable.DropLocation loc) {
    if (!loc.isInsertColumn()) {
      return null;
    }

    boolean ltr = table.getComponentOrientation().isLeftToRight();
    int col = loc.getColumn();
    Rectangle rect = table.getCellRect(loc.getRow(), col, true);

    if (col >= getGridSheetPane().getColumnCount()) {
      col--;
      rect = table.getCellRect(loc.getRow(), col, true);
      if (ltr) {
        rect.x = rect.x + rect.width;
      }
    } else if (!ltr) {
      rect.x = rect.x + rect.width;
    }

    if (rect.x == 0) {
      rect.x = -1;
    } else {
      rect.x -= 2;
    }

    rect.width = 3;

    return rect;
  }

  private Rectangle extendRect(Rectangle rect, boolean horizontal) {
    if (rect == null) {
      return rect;
    }

    if (horizontal) {
      rect.x = 0;
      rect.width = table.getWidth();
    } else {
      rect.y = 0;

      GridSheetPane gridSheetPane = getGridSheetPane();

      if (gridSheetPane.getRowCount() != 0) {
        Rectangle lastRect = table.getCellRect(gridSheetPane.getRowCount() - 1, 0, true);
        rect.height = lastRect.y + lastRect.height;
      } else {
        rect.height = table.getHeight();
      }
    }

    return rect;
  }

  private void paintRuleAndCells(Graphics g, Rectangle drawRect, boolean ltr, int correctionX,
                                 int correctionY) {
    Point upperLeft = drawRect.getLocation();
    Point lowerRight = new Point(drawRect.x + drawRect.width - 1, drawRect.y + drawRect.height - 1);

    GridSheetPane gridSheetPane = getGridSheetPane();

    int rMin = gridSheetPane.rowAtPoint(upperLeft);
    int rMax = gridSheetPane.rowAtPoint(lowerRight);
    // This should never happen (as long as our bounds intersect the
    // clip,
    // which is why we bail above if that is the case).
    if (rMin == -1) {
      rMin = 0;
    }
    // If the table does not have enough rows to fill the view we'll get
    // -1.
    // (We could also get -1 if our bounds don't intersect the clip,
    // which is why we bail above if that is the case).
    // Replace this with the index of the last row.
    if (rMax == -1) {
      rMax = gridSheetPane.getRowCount() - 1;
    }

    int cMin = gridSheetPane.columnAtPoint(ltr ? upperLeft : lowerRight);
    int cMax = gridSheetPane.columnAtPoint(ltr ? lowerRight : upperLeft);
    // This should never happen.
    if (cMin == -1) {
      cMin = 0;
    }
    // If the table does not have enough columns to fill the view we'll
    // get
    // -1.
    // Replace this with the index of the last column.
    if (cMax == -1) {
      cMax = gridSheetPane.getColumnCount() - 1;
    }

    // Paint the grid.
    paintRule(g, rMin, rMax, cMin, cMax, correctionX, correctionY);
    // Paint the cells.
    paintCells(g, rMin, rMax, cMin, cMax, correctionX, correctionY);

    paintSelection(g, drawRect, correctionX, correctionY);
  }

  private void paintFrozenGridAndCells(Graphics g, Rectangle drawRect, Rectangle clip, boolean ltr,
                                       Point scrollDistance, Point frozenPoint, boolean freezeX, boolean freezeY) {
    int correctionX = freezeX ? scrollDistance.x : -frozenPoint.x;
    int correctionY = freezeY ? scrollDistance.y : -frozenPoint.y;

    drawRect.x += correctionX - frozenPoint.x;
    drawRect.y += correctionY - frozenPoint.y;
    drawRect = drawRect.intersection(clip);
    if (!drawRect.isEmpty()) {
      g.setClip(drawRect);
      correctionX += (freezeX ? 0 : frozenPoint.x);
      correctionY += (freezeY ? 0 : frozenPoint.y);
      drawRect.x -= correctionX - frozenPoint.x;
      drawRect.y -= correctionY - frozenPoint.y;
      correctionX -= frozenPoint.x;
      correctionY -= frozenPoint.y;
      paintRuleAndCells(g, drawRect, ltr, correctionX, correctionY);
    }
  }

  /*
   * Paints the grid lines within <I>aRect</I>, using the grid color set with <I>setGridColor</I>.
   * Paints vertical lines if <code>getShowVerticalLines()</code> returns true and paints horizontal
   * lines if <code>getShowHorizontalLines()</code> returns true.
   */
  private void paintRule(Graphics g, int rMin, int rMax, int cMin, int cMax, int correctionX,
                         int correctionY) {
    GridSheetPane gridSheetPane = getGridSheetPane();
    g.setColor(gridSheetPane.getColorProvider().getRuleLineColor());

    Rectangle minCell = table.getCellRect(rMin, cMin, true);
    Rectangle maxCell = table.getCellRect(rMax, cMax, true);
    Rectangle damagedArea = minCell.union(maxCell);
    damagedArea.x += correctionX;
    damagedArea.y += correctionY;

    int tableWidth = damagedArea.x + damagedArea.width;
    int y = damagedArea.y;
    // g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);

    for (int row = rMin; row <= rMax; row++) {
      y += gridSheetPane.getRow(row).getHeight();
      g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
    }

    int tableHeight = damagedArea.y + damagedArea.height;
    int x;
    if (table.getComponentOrientation().isLeftToRight()) {
      x = damagedArea.x;
      // g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
      for (int column = cMin; column <= cMax; column++) {
        int w = gridSheetPane.getColumn(column).getWidth();
        x += w;
        g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
      }
    } else {
      x = damagedArea.x;
      g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
      for (int column = cMax; column >= cMin; column--) {
        int w = gridSheetPane.getColumn(column).getWidth();
        x += w;
        g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
      }
    }
  }

  // private int viewIndexForColumn(TableColumn aColumn) {
  // GridSheetModel model = table.getModel();
  // for (int column = 0; column < model.getColumnCount(); column++) {
  // if (cm.getColumn(column) == aColumn) {
  // return column;
  // }
  // }
  // return -1;
  // }
  protected void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax, int correctionX,
                            int correctionY) {
    int cellMargin = 1;
    GridSheetPane gridSheetPane = getGridSheetPane();
    IGridSheetModel model = gridSheetPane.getModel();

    Rectangle cellRect;
    GridSheetColumn aColumn;
    int columnWidth;
    if (table.getComponentOrientation().isLeftToRight()) {

      // Paint scrollable cells.
      for (int row = rMin; row <= rMax; row++) {
        cellRect = table.getCellRect(row, cMin, false);
        cellRect.height -= cellMargin;
        cellRect.x += correctionX;
        cellRect.y += correctionY;
        for (int column = cMin; column <= cMax; column++) {
          aColumn = gridSheetPane.getColumn(column);
          columnWidth = aColumn.getWidth();
          cellRect.width = columnWidth - cellMargin;
          paintCell(g, cellRect, row, column);
          cellRect.x += columnWidth;
        }
      }
    } else {
      for (int row = rMin; row <= rMax; row++) {
        cellRect = table.getCellRect(row, cMin, false);
        aColumn = gridSheetPane.getColumn(cMin);
        columnWidth = aColumn.getWidth();
        cellRect.width = columnWidth - cellMargin;
        paintCell(g, cellRect, row, cMin);

        for (int column = cMin + 1; column <= cMax; column++) {
          aColumn = gridSheetPane.getColumn(column);
          columnWidth = aColumn.getWidth();
          cellRect.width = columnWidth - cellMargin;
          cellRect.x -= columnWidth;
          paintCell(g, cellRect, row, column);
        }
      }
    }

    // Remove any renderers that may be left in the rendererPane.
    rendererPane.removeAll();
  }

  // private void paintDraggedArea(Graphics g, int rMin, int rMax,
  // TableColumn draggedColumn, int distance) {
  // int draggedColumnIndex = viewIndexForColumn(draggedColumn);
  //
  // Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
  // Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);
  //
  // Rectangle vacatedColumnRect = minCell.union(maxCell);
  //
  // // Paint a gray well in place of the moving column.
  // g.setColor(table.getParent().getBackground());
  // g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
  // vacatedColumnRect.width, vacatedColumnRect.height);
  //
  // // Move to the where the cell has been dragged.
  // vacatedColumnRect.x += distance;
  //
  // // Fill the background.
  // g.setColor(table.getBackground());
  // g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
  // vacatedColumnRect.width, vacatedColumnRect.height);
  //
  // // Paint the vertical grid lines if necessary.
  // if (table.getShowVerticalLines()) {
  // g.setColor(table.getGridColor());
  // int x1 = vacatedColumnRect.x;
  // int y1 = vacatedColumnRect.y;
  // int x2 = x1 + vacatedColumnRect.width - 1;
  // int y2 = y1 + vacatedColumnRect.height - 1;
  // // Left
  // g.drawLine(x1 - 1, y1, x1 - 1, y2);
  // // Right
  // g.drawLine(x2, y1, x2, y2);
  // }
  //
  // for (int row = rMin; row <= rMax; row++) {
  // // Render the cell value
  // Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
  // r.x += distance;
  // paintCell(g, r, row, draggedColumnIndex);
  //
  // // Paint the (lower) horizontal grid line if necessary.
  // if (table.getShowHorizontalLines()) {
  // g.setColor(table.getGridColor());
  // Rectangle rcr = table
  // .getCellRect(row, draggedColumnIndex, true);
  // rcr.x += distance;
  // int x1 = rcr.x;
  // int y1 = rcr.y;
  // int x2 = x1 + rcr.width - 1;
  // int y2 = y1 + rcr.height - 1;
  // g.drawLine(x1, y2, x2, y2);
  // }
  // }
  // }
  protected void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
    if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
      GridSheetCellEditor editor = table.getCellEditor();
      JComponent component = editor.getOuterEditorComponent();
      component.setBounds(cellRect);
      component.validate();
    } else {
      GridSheetCellRenderer renderer = table.getCellRenderer(row, column);
      Component component = table.prepareRenderer(renderer, row, column);
      rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width,
          cellRect.height, true);
    }
  }

  private void paintFrozenCell(Graphics g, Rectangle cellRect, int row, int column) {
    if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
      GridSheetCellEditor editor = table.getCellEditor();
      JComponent component = editor.getOuterEditorComponent();
      component.setBounds(cellRect);
      component.validate();
    } else {
      GridSheetCellRenderer renderer = table.getCellRenderer(row, column);
      Component component = table.prepareRenderer(renderer, row, column);
      frozenCellRendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y,
          cellRect.width, cellRect.height, true);
    }
  }


  /**
   * @param g
   * @param rMin
   * @param rMax
   * @param cMin
   * @param cMax
   * @param correctionX
   * @param correctionY
   */
  private void paintSelection(Graphics g, Rectangle drawRect, int correctionX, int correctionY) {
    // GridSheetPane gridSheetPane = getGridSheetPane();
    // GridSheetSelectionModel selModel = gridSheetPane.getSelectionModel();
    //
    // if (!selModel.isAdditionallySelected()) {
    // int rMinSel = selModel.getMainMinRowSelectionIndex();
    // int cMinSel = selModel.getMainMinColumnSelectionIndex();
    // int rMaxSel = selModel.getMainMaxRowSelectionIndex();
    // int cMaxSel = selModel.getMainMaxColumnSelectionIndex();
    //
    // if (rMinSel < 0 || cMinSel < 0 || rMaxSel < 0 || cMaxSel < 0) {
    // return;
    // }
    //
    // Rectangle minCell = table.getCellRect(rMinSel, cMinSel, true);
    // Rectangle maxCell = table.getCellRect(rMaxSel, cMaxSel, true);
    // Rectangle mainSelectionArea = minCell.union(maxCell);
    // mainSelectionArea.x += correctionX;
    // mainSelectionArea.y += correctionY;
    //
    // int lowerRightX = mainSelectionArea.x + mainSelectionArea.width;
    // int lowerRightY = mainSelectionArea.y + mainSelectionArea.height;
    //
    //
    // if (mainSelectionArea.x > drawRect.x + drawRect.width
    // || mainSelectionArea.y > drawRect.y + drawRect.height || lowerRightX < drawRect.x
    // || lowerRightY < drawRect.y) {
    // return;
    // }
    //
    // g.setColor(gridSheetPane.getColorProvider().getSelectionBorderColor());
    // g.drawRect(mainSelectionArea.x - 1, mainSelectionArea.y - 1, mainSelectionArea.width,
    // mainSelectionArea.height);
    //
    // // int[] xPoints = new int[3];
    // // int[] yPoints = new int[3];
    // // xPoints[0] = lowerRightX;
    // // yPoints[0] = lowerRightY;
    // // xPoints[1] = lowerRightX - 8;
    // // yPoints[1] = lowerRightY;
    // // xPoints[2] = lowerRightX;
    // // yPoints[2] = lowerRightY - 8;
    // // g.setColor(Color.BLACK);
    // // g.fillPolygon(xPoints, yPoints, 3);
    // //
    // // g.fillRect(mainSelectionArea.x + mainSelectionArea.width - 4, mainSelectionArea.y
    // // + mainSelectionArea.height - 4, 4, 4);
    // }
    //
    // int rFocus = selModel.getRowAnchorIndex();
    // int cFocus = selModel.getColumnAnchorIndex();
    // Rectangle focusCellRect = table.getCellRect(rFocus, cFocus, true);
    //
    // if (!gridSheetPane.isEditing()) {
    // g.setColor(Color.BLACK);
    // g.drawRect(focusCellRect.x - 1, focusCellRect.y - 1, focusCellRect.width,
    // focusCellRect.height);
    // g.drawRect(focusCellRect.x, focusCellRect.y, focusCellRect.width - 2,
    // focusCellRect.height - 2);
    // }

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

  public GridSheetTable getTable() {
    return table;
  }

  @Override
  protected GridSheetPane getGridSheetPane() {
    return table.getGridSheetPane();
  }

  public static class BasicColorProvider extends DefaultGridSheetColorProvider
      implements UIResource {
  }
}
