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

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.UIResource;

import com.smoothcsv.commons.constants.Direction;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionListener;
import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetRow;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import com.smoothcsv.swing.gridsheet.ui.GridSheetTableNoActionUI;
import lombok.Getter;
import sun.swing.PrintingStatus;

@SuppressWarnings("serial")
public class GridSheetTable extends AbstractGridSheetComponent
    implements Scrollable, CellEditorListener, GridSheetSelectionListener {
  //
  // Static Constants
  //

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "GridSheetTableUI";

  public enum PrintMode {

    NORMAL, FIT_WIDTH
  }

  //
  // Instance Variables
  //

  //
  // Private state
  //

  /**
   * The last value of getValueIsAdjusting from the selection models valueChanged notification. Used
   * to test if a repaint is needed.
   */
  private boolean selectionAdjusting;

  /**
   * Parent GridSheetPane.
   */
  @Getter
  private GridSheetPane gridSheetPane;

  // structures
  // protected int[] rowHeights;
  // protected int[] columnHeights;
  protected boolean autoResize;

  protected boolean showMetaChars;

  protected int[] rowIndexConverters;
  protected int[] columnIndexConverters;

  // /**
  // * If editing, the <code>Component</code> that is handling the editing.
  // */
  // transient protected JComponent editorComp;

  /**
   * The active cell editor object, that overwrites the screen real estate occupied by the current
   * cell and allows the user to change its contents. {@code null} if the table isn't currently
   * editing.
   */
  transient protected GridSheetCellEditor cellEditor;

  /**
   * Identifies the column of the cell being edited.
   */
  transient protected int editingColumn;

  /**
   * Identifies the row of the cell being edited.
   */
  transient protected int editingRow;

  /**
   * A table of objects that display the contents of a cell, indexed by class as declared in
   * <code>getColumnClass</code> in the <code>GridSheetModel</code> interface.
   */
  transient protected GridSheetCellRenderer defaulRenderer;

  /**
   * A table of objects that display and edit the contents of a cell, indexed by class as declared
   * in <code>getColumnClass</code> in the <code>GridSheetModel</code> interface.
   */
  transient protected Map<Class<?>, GridSheetCellEditor> defaultEditorsByColumnClass;

  //
  // Private state
  //
  private PropertyChangeListener editorRemover = null;

  /**
   * To communicate errors between threads during printing.
   */
  private Throwable printError;

  /**
   * Whether or not the table always fills the viewport height.
   *
   * @see #setFillsViewportHeight
   * @see #getScrollableTracksViewportHeight
   */
  private boolean fillsViewportHeight;

  /**
   * The drop mode for this component.
   */
  private DropMode dropMode = DropMode.USE_SELECTION;

  /**
   * The autofill range. Null when not autofilling.
   */
  private GridSheetCellRange autofillRange;

  /**
   * The drop location.
   */
  private transient DropLocation dropLocation;

  /**
   * A subclass of <code>TransferHandler.DropLocation</code> representing a drop location for a
   * <code>JTable</code>.
   *
   * @see #getDropLocation
   * @since 1.6
   */
  public static final class DropLocation extends TransferHandler.DropLocation {

    private final int row;
    private final int col;
    private final boolean isInsertRow;
    private final boolean isInsertCol;

    private DropLocation(Point p, int row, int col, boolean isInsertRow, boolean isInsertCol) {

      super(p);
      this.row = row;
      this.col = col;
      this.isInsertRow = isInsertRow;
      this.isInsertCol = isInsertCol;
    }

    /**
     * Returns the row index where a dropped item should be placed in the table. Interpretation of
     * the value depends on the return of <code>isInsertRow()</code>. If that method returns
     * <code>true</code> this value indicates the index where a new row should be inserted.
     * Otherwise, it represents the value of an existing row on which the data was dropped. This
     * index is in terms of the view.
     * <p>
     * <code>-1</code> indicates that the drop occurred over empty space, and no row could be
     * calculated.
     *
     * @return the drop row
     */
    public int getRow() {
      return row;
    }

    /**
     * Returns the column index where a dropped item should be placed in the table. Interpretation
     * of the value depends on the return of <code>isInsertColumn()</code>. If that method returns
     * <code>true</code> this value indicates the index where a new column should be inserted.
     * Otherwise, it represents the value of an existing column on which the data was dropped. This
     * index is in terms of the view.
     * <p>
     * <code>-1</code> indicates that the drop occurred over empty space, and no column could be
     * calculated.
     *
     * @return the drop row
     */
    public int getColumn() {
      return col;
    }

    /**
     * Returns whether or not this location represents an insert of a row.
     *
     * @return whether or not this is an insert row
     */
    public boolean isInsertRow() {
      return isInsertRow;
    }

    /**
     * Returns whether or not this location represents an insert of a column.
     *
     * @return whether or not this is an insert column
     */
    public boolean isInsertColumn() {
      return isInsertCol;
    }

    /**
     * Returns a string representation of this drop location. This method is intended to be used for
     * debugging purposes, and the content and format of the returned string may vary between
     * implementations.
     *
     * @return a string representation of this drop location
     */
    public String toString() {
      return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "row=" + row + ","
          + "column=" + col + "," + "insertRow=" + isInsertRow + "," + "insertColumn=" + isInsertCol
          + "]";
    }
  }

  //
  // Constructors
  //
  public GridSheetTable(GridSheetPane gridSheetPane) {
    this(gridSheetPane, null);
  }

  /**
   * Constructs a <code>JTable</code> that is initialized with <code>dm</code> as the data model,
   * <code>cm</code> as the column model, and <code>sm</code> as the selection model. If any of the
   * parameters are <code>null</code> this method will initialize the table with the corresponding
   * default model. The <code>autoCreateColumnsFromModel</code> flag is set to false if
   * <code>cm</code> is non-null, otherwise it is set to true and the column model is populated with
   * suitable <code>GridColumns</code> for the columns in <code>dm</code>.
   *
   * @param renderer
   * @param gridSheetPane
   */
  public GridSheetTable(GridSheetPane gridSheetPane, GridSheetCellRenderer renderer) {
    super();
    setLayout(null);
    setFillsViewportHeight(true);

    this.gridSheetPane = gridSheetPane;

    setDefaultRenderer(renderer != null ? renderer : createDefaultRenderers());

    initializeLocalVars();
    updateUI();
  }

  /**
   * Calls the <code>configureEnclosingScrollPane</code> method.
   *
   * @see #configureEnclosingScrollPane
   */
  public void addNotify() {
    super.addNotify();
    // configureEnclosingScrollPane();
  }

  /**
   * If this <code>JTable</code> is the <code>viewportView</code> of an enclosing
   * <code>JScrollPane</code> (the usual situation), configure this <code>ScrollPane</code> by,
   * amongst other things, installing the table's <code>tableHeader</code> as the
   * <code>columnHeaderView</code> of the scroll pane. When a <code>JTable</code> is added to a
   * <code>JScrollPane</code> in the usual way, using <code>new JScrollPane(myTable)</code>,
   * <code>addNotify</code> is called in the <code>JTable</code> (when the table is added to the
   * viewport). <code>JTable</code>'s <code>addNotify</code> method in turn calls this method, which
   * is protected so that this default installation procedure can be overridden by a subclass.
   *
   * @see #addNotify
   */
  protected void configureEnclosingScrollPane() {
    Container parent = SwingUtilities.getUnwrappedParent(this);
    if (parent instanceof JViewport) {
      JViewport port = (JViewport) parent;
      Container gp = port.getParent();
      if (gp instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) gp;
        // Make certain we are the viewPort's view and not, for
        // example, the rowHeaderView of the scrollPane -
        // an implementor of fixed columns might do this.
        JViewport viewport = scrollPane.getViewport();
        if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
          return;
        }
        scrollPane.setColumnHeaderView(gridSheetPane.getColumnHeader());
        // configure the scrollpane for any LAF dependent settings
        configureEnclosingScrollPaneUI();
      }
    }
  }

  /**
   * This is a sub-part of configureEnclosingScrollPane() that configures anything on the scrollpane
   * that may change when the look and feel changes. It needed to be split out from
   * configureEnclosingScrollPane() so that it can be called from updateUI() when the LAF changes
   * without causing the regression found in bug 6687962. This was because updateUI() is called from
   * the constructor which then caused configureEnclosingScrollPane() to be called by the
   * constructor which changes its contract for any subclass that overrides it. So by splitting it
   * out in this way configureEnclosingScrollPaneUI() can be called both from
   * configureEnclosingScrollPane() and updateUI() in a safe manor.
   */
  private void configureEnclosingScrollPaneUI() {
    Container parent = SwingUtilities.getUnwrappedParent(this);
    if (parent instanceof JViewport) {
      JViewport port = (JViewport) parent;
      Container gp = port.getParent();
      if (gp instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) gp;
        // Make certain we are the viewPort's view and not, for
        // example, the rowHeaderView of the scrollPane -
        // an implementor of fixed columns might do this.
        JViewport viewport = scrollPane.getViewport();
        if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
          return;
        }
        // scrollPane.getViewport().setBackingStoreEnabled(true);
        Border border = scrollPane.getBorder();
        if (border == null || border instanceof UIResource) {
          Border scrollPaneBorder = UIManager.getBorder("Table.scrollPaneBorder");
          if (scrollPaneBorder != null) {
            scrollPane.setBorder(scrollPaneBorder);
          }
        }
        // add JScrollBar corner component if available from LAF and not
        // already set by the user
        Component corner = scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER);
        if (corner == null || corner instanceof UIResource) {
          corner = null;
          try {
            corner = (Component) UIManager.get("Table.scrollPaneCornerComponent");
          } catch (Exception e) {
            // just ignore and don't set corner
          }
          scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, corner);
        }
      }
    }
  }

  /**
   * Calls the <code>unconfigureEnclosingScrollPane</code> method.
   *
   * @see #unconfigureEnclosingScrollPane
   */
  public void removeNotify() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .removePropertyChangeListener("permanentFocusOwner", editorRemover);
    editorRemover = null;
    // unconfigureEnclosingScrollPane();
    super.removeNotify();
  }

  /**
   * Reverses the effect of <code>configureEnclosingScrollPane</code> by replacing the
   * <code>columnHeaderView</code> of the enclosing scroll pane with <code>null</code>.
   * <code>JTable</code>'s <code>removeNotify</code> method calls this method, which is protected so
   * that this default uninstallation procedure can be overridden by a subclass.
   *
   * @see #removeNotify
   * @see #configureEnclosingScrollPane
   * @since 1.3
   */
  protected void unconfigureEnclosingScrollPane() {
    Container parent = SwingUtilities.getUnwrappedParent(this);
    if (parent instanceof JViewport) {
      JViewport port = (JViewport) parent;
      Container gp = port.getParent();
      if (gp instanceof JScrollPane) {
        JScrollPane scrollPane = (JScrollPane) gp;
        // Make certain we are the viewPort's view and not, for
        // example, the rowHeaderView of the scrollPane -
        // an implementor of fixed columns might do this.
        JViewport viewport = scrollPane.getViewport();
        if (viewport == null || SwingUtilities.getUnwrappedView(viewport) != this) {
          return;
        }
        // remove ScrollPane corner if one was added by the LAF
        Component corner = scrollPane.getCorner(JScrollPane.UPPER_TRAILING_CORNER);
        if (corner instanceof UIResource) {
          scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, null);
        }
      }
    }
  }

  public void setDefaultRenderer(GridSheetCellRenderer renderer) {
    this.defaulRenderer = renderer;
  }

  public GridSheetCellRenderer getDefaultRenderer() {
    return defaulRenderer;
  }

  public void setDefaultEditor(Class<?> columnClass, GridSheetCellEditor editor) {
    if (editor != null) {
      defaultEditorsByColumnClass.put(columnClass, editor);
    } else {
      defaultEditorsByColumnClass.remove(columnClass);
    }
  }

  public GridSheetCellEditor getDefaultEditor(Class<?> columnClass) {
    if (columnClass == null) {
      return null;
    } else {
      Object editor = defaultEditorsByColumnClass.get(columnClass);
      if (editor != null) {
        return (GridSheetCellEditor) editor;
      } else {
        return getDefaultEditor(columnClass.getSuperclass());
      }
    }
  }

  // /**
  // * Turns on or off automatic drag handling. In order to enable automatic
  // * drag handling, this property should be set to {@code true}, and the
  // * table's {@code TransferHandler} needs to be {@code non-null}. The
  // default
  // * value of the {@code dragEnabled} property is {@code false}.
  // * <p>
  // * The job of honoring this property, and recognizing a user drag gesture,
  // * lies with the look and feel implementation, and in particular, the
  // * table's {@code GridSheetTableUI}. When automatic drag handling is
  // enabled,
  // most
  // * look and feels (including those that subclass {@code BasicLookAndFeel})
  // * begin a drag and drop operation whenever the user presses the mouse
  // * button over an item (in single selection mode) or a selection (in other
  // * selection modes) and then moves the mouse a few pixels. Setting this
  // * property to {@code true} can therefore have a subtle effect on how
  // * selections behave.
  // * <p>
  // * If a look and feel is used that ignores this property, you can still
  // * begin a drag and drop operation by calling {@code exportAsDrag} on the
  // * table's {@code TransferHandler}.
  // *
  // * @param b
  // * whether or not to enable automatic drag handling
  // * @exception HeadlessException
  // * if <code>b</code> is <code>true</code> and
  // * <code>GraphicsEnvironment.isHeadless()</code> returns
  // * <code>true</code>
  // * @see java.awt.GraphicsEnvironment#isHeadless
  // * @see #getDragEnabled
  // * @see #setTransferHandler
  // * @see TransferHandler
  // * @since 1.4
  // *
  // * @beaninfo description: determines whether automatic drag handling is
  // * enabled bound: false
  // */
  // public void setDragEnabled(boolean b) {
  // if (b && GraphicsEnvironment.isHeadless()) {
  // throw new HeadlessException();
  // }
  // dragEnabled = b;
  // }
  //
  // /**
  // * Returns whether or not automatic drag handling is enabled.
  // *
  // * @return the value of the {@code dragEnabled} property
  // * @see #setDragEnabled
  // * @since 1.4
  // */
  // public boolean getDragEnabled() {
  // return dragEnabled;
  // }
  // /**
  // * Sets the drop mode for this component. For backward compatibility, the
  // * default for this property is <code>DropMode.USE_SELECTION</code>. Usage
  // * of one of the other modes is recommended, however, for an improved user
  // * experience. <code>DropMode.ON</code>, for instance, offers similar
  // * behavior of showing items as selected, but does so without affecting
  // the
  // * actual selection in the table.
  // * <p>
  // * <code>JTable</code> supports the following drop modes:
  // * <ul>
  // * <li><code>DropMode.USE_SELECTION</code></li>
  // * <li><code>DropMode.ON</code></li>
  // * <li><code>DropMode.INSERT</code></li>
  // * <li><code>DropMode.INSERT_ROWS</code></li>
  // * <li><code>DropMode.INSERT_COLS</code></li>
  // * <li><code>DropMode.ON_OR_INSERT</code></li>
  // * <li><code>DropMode.ON_OR_INSERT_ROWS</code></li>
  // * <li><code>DropMode.ON_OR_INSERT_COLS</code></li>
  // * </ul>
  // * <p>
  // * The drop mode is only meaningful if this component has a
  // * <code>TransferHandler</code> that accepts drops.
  // *
  // * @param dropMode
  // * the drop mode to use
  // * @throws IllegalArgumentException
  // * if the drop mode is unsupported or <code>null</code>
  // * @see #getDropMode
  // * @see #getDropLocation
  // * @see #setTransferHandler
  // * @see TransferHandler
  // * @since 1.6
  // */
  // public final void setDropMode(DropMode dropMode) {
  // if (dropMode != null) {
  // switch (dropMode) {
  // case USE_SELECTION:
  // case ON:
  // case INSERT:
  // case INSERT_ROWS:
  // case INSERT_COLS:
  // case ON_OR_INSERT:
  // case ON_OR_INSERT_ROWS:
  // case ON_OR_INSERT_COLS:
  // this.dropMode = dropMode;
  // return;
  // }
  // }
  //
  // throw new IllegalArgumentException(dropMode
  // + ": Unsupported drop mode for table");
  // }
  //
  // /**
  // * Returns the drop mode for this component.
  // *
  // * @return the drop mode for this component
  // * @see #setDropMode
  // * @since 1.6
  // */
  // public final DropMode getDropMode() {
  // return dropMode;
  // }
  //
  // /**
  // * Calculates a drop location in this component, representing where a drop
  // * at the given point should insert data.
  // *
  // * @param p
  // * the point to calculate a drop location for
  // * @return the drop location, or <code>null</code>
  // */
  // DropLocation dropLocationForPoint(Point p) {
  // DropLocation location = null;
  //
  // int row = rowAtPoint(p);
  // int col = columnAtPoint(p);
  // boolean outside = Boolean.TRUE == getClientProperty("Table.isFileList")
  // && GridSheetUtils.pointOutsidePrefSize(this, row, col, p);
  //
  // Rectangle rect = getCellRect(row, col, true);
  // Section xSection, ySection;
  // boolean between = false;
  // boolean ltr = getComponentOrientation().isLeftToRight();
  //
  // switch (dropMode) {
  // case USE_SELECTION:
  // case ON:
  // if (row == -1 || col == -1 || outside) {
  // location = new DropLocation(p, -1, -1, false, false);
  // } else {
  // location = new DropLocation(p, row, col, false, false);
  // }
  // break;
  // case INSERT:
  // if (row == -1 && col == -1) {
  // location = new DropLocation(p, 0, 0, true, true);
  // break;
  // }
  //
  // xSection = SwingUtilities2.liesInHorizontal(rect, p, ltr, true);
  //
  // if (row == -1) {
  // if (xSection == LEADING) {
  // location = new DropLocation(p, getRowCount(), col, true,
  // true);
  // } else if (xSection == TRAILING) {
  // location = new DropLocation(p, getRowCount(), col + 1,
  // true, true);
  // } else {
  // location = new DropLocation(p, getRowCount(), col, true,
  // false);
  // }
  // } else if (xSection == LEADING || xSection == TRAILING) {
  // ySection = SwingUtilities2.liesInVertical(rect, p, true);
  // if (ySection == LEADING) {
  // between = true;
  // } else if (ySection == TRAILING) {
  // row++;
  // between = true;
  // }
  //
  // location = new DropLocation(p, row,
  // xSection == TRAILING ? col + 1 : col, between, true);
  // } else {
  // if (SwingUtilities2.liesInVertical(rect, p, false) == TRAILING) {
  // row++;
  // }
  //
  // location = new DropLocation(p, row, col, true, false);
  // }
  //
  // break;
  // case INSERT_ROWS:
  // if (row == -1 && col == -1) {
  // location = new DropLocation(p, -1, -1, false, false);
  // break;
  // }
  //
  // if (row == -1) {
  // location = new DropLocation(p, getRowCount(), col, true, false);
  // break;
  // }
  //
  // if (SwingUtilities2.liesInVertical(rect, p, false) == TRAILING) {
  // row++;
  // }
  //
  // location = new DropLocation(p, row, col, true, false);
  // break;
  // case ON_OR_INSERT_ROWS:
  // if (row == -1 && col == -1) {
  // location = new DropLocation(p, -1, -1, false, false);
  // break;
  // }
  //
  // if (row == -1) {
  // location = new DropLocation(p, getRowCount(), col, true, false);
  // break;
  // }
  //
  // ySection = SwingUtilities2.liesInVertical(rect, p, true);
  // if (ySection == LEADING) {
  // between = true;
  // } else if (ySection == TRAILING) {
  // row++;
  // between = true;
  // }
  //
  // location = new DropLocation(p, row, col, between, false);
  // break;
  // case INSERT_COLS:
  // if (row == -1) {
  // location = new DropLocation(p, -1, -1, false, false);
  // break;
  // }
  //
  // if (col == -1) {
  // location = new DropLocation(p, getColumnCount(), col, false,
  // true);
  // break;
  // }
  //
  // if (SwingUtilities2.liesInHorizontal(rect, p, ltr, false) == TRAILING) {
  // col++;
  // }
  //
  // location = new DropLocation(p, row, col, false, true);
  // break;
  // case ON_OR_INSERT_COLS:
  // if (row == -1) {
  // location = new DropLocation(p, -1, -1, false, false);
  // break;
  // }
  //
  // if (col == -1) {
  // location = new DropLocation(p, row, getColumnCount(), false,
  // true);
  // break;
  // }
  //
  // xSection = SwingUtilities2.liesInHorizontal(rect, p, ltr, true);
  // if (xSection == LEADING) {
  // between = true;
  // } else if (xSection == TRAILING) {
  // col++;
  // between = true;
  // }
  //
  // location = new DropLocation(p, row, col, false, between);
  // break;
  // case ON_OR_INSERT:
  // if (row == -1 && col == -1) {
  // location = new DropLocation(p, 0, 0, true, true);
  // break;
  // }
  //
  // xSection = SwingUtilities2.liesInHorizontal(rect, p, ltr, true);
  //
  // if (row == -1) {
  // if (xSection == LEADING) {
  // location = new DropLocation(p, getRowCount(), col, true,
  // true);
  // } else if (xSection == TRAILING) {
  // location = new DropLocation(p, getRowCount(), col + 1,
  // true, true);
  // } else {
  // location = new DropLocation(p, getRowCount(), col, true,
  // false);
  // }
  //
  // break;
  // }
  //
  // ySection = SwingUtilities2.liesInVertical(rect, p, true);
  // if (ySection == LEADING) {
  // between = true;
  // } else if (ySection == TRAILING) {
  // row++;
  // between = true;
  // }
  //
  // location = new DropLocation(p, row, xSection == TRAILING ? col + 1
  // : col, between, xSection != MIDDLE);
  //
  // break;
  // default:
  // assert false : "Unexpected drop mode";
  // }
  //
  // return location;
  // }
  //
  // /**
  // * Called to set or clear the drop location during a DnD operation. In
  // some
  // * cases, the component may need to use it's internal selection
  // temporarily
  // * to indicate the drop location. To help facilitate this, this method
  // * returns and accepts as a parameter a state object. This state object
  // can
  // * be used to store, and later restore, the selection state. Whatever this
  // * method returns will be passed back to it in future calls, as the state
  // * parameter. If it wants the DnD system to continue storing the same
  // state,
  // * it must pass it back every time. Here's how this is used:
  // * <p>
  // * Let's say that on the first call to this method the component decides
  // to
  // * save some state (because it is about to use the selection to show a
  // drop
  // * index). It can return a state object to the caller encapsulating any
  // * saved selection state. On a second call, let's say the drop location is
  // * being changed to something else. The component doesn't need to restore
  // * anything yet, so it simply passes back the same state object to have
  // the
  // * DnD system continue storing it. Finally, let's say this method is
  // * messaged with <code>null</code>. This means DnD is finished with this
  // * component for now, meaning it should restore state. At this point, it
  // can
  // * use the state parameter to restore said state, and of course return
  // * <code>null</code> since there's no longer anything to store.
  // *
  // * @param location
  // * the drop location (as calculated by
  // * <code>dropLocationForPoint</code>) or <code>null</code> if
  // * there's no longer a valid drop location
  // * @param state
  // * the state object saved earlier for this component, or
  // * <code>null</code>
  // * @param forDrop
  // * whether or not the method is being called because an actual
  // * drop occurred
  // * @return any saved state for this component, or <code>null</code> if
  // none
  // */
  // private Object setDropLocation(TransferHandler.DropLocation location,
  // Object state, boolean forDrop) {
  //
  // Object retVal = null;
  // DropLocation tableLocation = (DropLocation) location;
  //
  // if (dropMode == DropMode.USE_SELECTION) {
  // if (tableLocation == null) {
  // if (!forDrop && state != null) {
  // clearSelection();
  //
  // int[] rows = ((int[][]) state)[0];
  // int[] cols = ((int[][]) state)[1];
  // int[] anchleads = ((int[][]) state)[2];
  //
  // for (int row : rows) {
  // addRowSelectionInterval(row, row);
  // }
  //
  // for (int col : cols) {
  // addColumnSelectionInterval(col, col);
  // }
  //
  // SwingUtilities2.setLeadAnchorWithoutSelection(
  // selectionSupport.getRowSelectionModel(),
  // anchleads[1], anchleads[0]);
  //
  // SwingUtilities2.setLeadAnchorWithoutSelection(
  // selectionSupport.getColumnSelectionModel(),
  // anchleads[3], anchleads[2]);
  // }
  // } else {
  // if (dropLocation == null) {
  // retVal = new int[][] {
  // getSelectedRows(),
  // getSelectedColumns(),
  // {
  // getAdjustedIndex(selectionSupport
  // .getRowSelectionModel()
  // .getAnchorSelectionIndex(), true),
  // getAdjustedIndex(selectionSupport
  // .getRowSelectionModel()
  // .getLeadSelectionIndex(), true),
  // getAdjustedIndex(selectionSupport
  // .getColumnSelectionModel()
  // .getAnchorSelectionIndex(), false),
  // getAdjustedIndex(selectionSupport
  // .getColumnSelectionModel()
  // .getLeadSelectionIndex(), false) } };
  // } else {
  // retVal = state;
  // }
  //
  // if (tableLocation.getRow() == -1) {
  // clearSelection();
  // } else {
  // setRowSelectionInterval(tableLocation.getRow(),
  // tableLocation.getRow());
  // setColumnSelectionInterval(tableLocation.getColumn(),
  // tableLocation.getColumn());
  // }
  // }
  // }
  //
  // DropLocation old = dropLocation;
  // dropLocation = tableLocation;
  // firePropertyChange("dropLocation", old, dropLocation);
  //
  // return retVal;
  // }
  // /**
  // * Returns the location that this component should visually indicate as
  // the
  // * drop location during a DnD operation over the component, or {@code
  // null}
  // * if no location is to currently be shown.
  // * <p>
  // * This method is not meant for querying the drop location from a
  // * {@code TransferHandler}, as the drop location is only set after the
  // * {@code TransferHandler}'s <code>canImport</code> has returned and has
  // * allowed for the location to be shown.
  // * <p>
  // * When this property changes, a property change event with name
  // * "dropLocation" is fired by the component.
  // *
  // * @return the drop location
  // * @see #setDropMode
  // * @see TransferHandler#canImport(TransferHandler.TransferSupport)
  // * @since 1.6
  // */
  // public final DropLocation getDropLocation() {
  // return dropLocation;
  // }

  public void scrollRectToVisible(int rowIndex, int columnIndex) {
    Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
    if (cellRect != null) {
      // cellRect.y -= 5;
      // cellRect.x -= 5;
      // cellRect.width += 10;
      // cellRect.height += 10;
      scrollRectToVisible(cellRect);
    }
  }

  // /**
  // * Returns the foreground color for selected cells.
  // *
  // * @return the <code>Color</code> object for the foreground property
  // * @see #setSelectionForeground
  // * @see #setSelectionBackground
  // */
  // public Color getSelectionForeground() {
  // return selectionForeground;
  // }
  //
  // /**
  // * Sets the foreground color for selected cells. Cell renderers can use
  // this
  // * color to render text and graphics for selected cells.
  // * <p>
  // * The default value of this property is defined by the look and feel
  // * implementation.
  // * <p>
  // * This is a <a href=
  // *
  // "http://java.sun.com/docs/books/tutorial/javabeans/properties/bound.html"
  // * >JavaBeans</a> bound property.
  // *
  // * @param selectionForeground
  // * the <code>Color</code> to use in the foreground for selected
  // * list items
  // * @see #getSelectionForeground
  // * @see #setSelectionBackground
  // * @see #setForeground
  // * @see #setBackground
  // * @see #setFont
  // * @beaninfo bound: true description: A default foreground color for
  // * selected cells.
  // */
  // public void setSelectionForeground(Color selectionForeground) {
  // Color old = this.selectionForeground;
  // this.selectionForeground = selectionForeground;
  // firePropertyChange("selectionForeground", old, selectionForeground);
  // repaint();
  // }
  // /**
  // * Returns the background color for selected cells.
  // *
  // * @return the <code>Color</code> used for the background of selected list
  // * items
  // * @see #setSelectionBackground
  // * @see #setSelectionForeground
  // */
  // public Color getSelectionBackground() {
  // return selectionBackground;
  // }
  //
  // /**
  // * Sets the background color for selected cells. Cell renderers can use
  // this
  // * color to the fill selected cells.
  // * <p>
  // * The default value of this property is defined by the look and feel
  // * implementation.
  // * <p>
  // * This is a <a href=
  // *
  // "http://java.sun.com/docs/books/tutorial/javabeans/properties/bound.html"
  // * >JavaBeans</a> bound property.
  // *
  // * @param selectionBackground
  // * the <code>Color</code> to use for the background of selected
  // * cells
  // * @see #getSelectionBackground
  // * @see #setSelectionForeground
  // * @see #setForeground
  // * @see #setBackground
  // * @see #setFont
  // * @beaninfo bound: true description: A default background color for
  // * selected cells.
  // */
  // public void setSelectionBackground(Color selectionBackground) {
  // Color old = this.selectionBackground;
  // this.selectionBackground = selectionBackground;
  // firePropertyChange("selectionBackground", old, selectionBackground);
  // repaint();
  // }

  /**
   * Returns a rectangle for the cell that lies at the intersection of <code>row</code> and
   * <code>column</code>. If <code>includeSpacing</code> is true then the value returned has the
   * full height and width of the row and column specified. If it is false, the returned rectangle
   * is inset by the intercell spacing to return the true bounds of the rendering or editing
   * component as it will be set during rendering.
   * <p>
   * If the column index is valid but the row index is less than zero the method returns a rectangle
   * with the <code>y</code> and <code>height</code> values set appropriately and the <code>x</code>
   * and <code>width</code> values both set to zero. In general, when either the row or column
   * indices indicate a cell outside the appropriate range, the method returns a rectangle depicting
   * the closest edge of the closest cell that is within the table's range. When both row and column
   * indices are out of range the returned rectangle covers the closest point of the closest cell.
   * <p>
   * In all cases, calculations that use this method to calculate results along one axis will not
   * fail because of anomalies in calculations along the other axis. When the cell is not valid the
   * <code>includeSpacing</code> parameter is ignored.
   *
   * @param row            the row index where the desired cell is located
   * @param column         the column index where the desired cell is located in the display; this is not
   *                       necessarily the same as the column index in the data model for the table; the
   *                       {@link #convertColumnIndexToView(int)} method may be used to convert a data model column
   *                       index to a display column index
   * @param includeSpacing if false, return the true cell bounds - computed by subtracting the
   *                       intercell spacing from the height and widths of the column and row models
   * @return the rectangle containing the cell at location <code>row</code>, <code>column</code>
   * @see #getIntercellSpacing
   */
  public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
    Rectangle r = new Rectangle();
    if (row < 0) {
      // y = height = 0;
    } else if (row >= gridSheetPane.getRowCount()) {
      r.height = gridSheetPane.getRow(0).getHeight(); // FIXME
      r.y = row * r.height;
    } else {
      r.height = gridSheetPane.getRow(row).getHeight();
      r.y = row * r.height; // FIXME
    }

    if (column < 0) {
      if (!getComponentOrientation().isLeftToRight()) {
        r.x = getWidth();
      }
      // otherwise, x = width = 0;
    } else if (column >= gridSheetPane.getColumnCount()) {
      if (getComponentOrientation().isLeftToRight()) {
        r.x = getWidth();
      }
      // otherwise, x = width = 0;
    } else {
      if (getComponentOrientation().isLeftToRight()) {
        for (int i = 0; i < column; i++) {
          r.x += gridSheetPane.getColumn(i).getWidth();
        }
      } else {
        for (int i = gridSheetPane.getColumnCount() - 1; i > column; i--) {
          r.x += gridSheetPane.getColumn(i).getWidth();
        }
      }
      r.width = gridSheetPane.getColumn(column).getWidth();
    }

    // if (valid && !includeSpacing) {
    // // Bound the margins by their associated dimensions to prevent
    // // returning bounds with negative dimensions.
    // int rm = Math.min(getRowMargin(), r.height);
    // int cm = Math.min(getColumnMargin(), r.width);
    // // This is not the same as grow(), it rounds differently.
    // r.setBounds(r.x + cm / 2, r.y + rm / 2, r.width - cm, r.height - rm);
    // }

    // if(outer){
    // r.x -= 1;
    // r.y -= 1;
    // }else{
    // r.width -= 2;
    // r.height -= 2;
    // }
    return r;
  }

  public Rectangle getCellRect(GridSheetCellRange range) {
    Rectangle rect0 = getCellRect(range.getFirstRow(), range.getFirstColumn(), false);
    Rectangle rect1 = getCellRect(range.getLastRow(), range.getLastColumn(), false);
    return rect0.union(rect1);
  }

  public Rectangle getOuterCellRect(int row, int column) {
    Rectangle r = getCellRect(row, column, false);
    r.x -= 1;
    r.y -= 1;
    r.width += 1;
    r.height += 1;
    return r;
  }


  /**
   * Causes this table to lay out its rows and columns. Overridden so that columns can be resized to
   * accomodate a change in the size of a containing parent. Resizes one or more of the columns in
   * the table so that the total width of all of this <code>JTable</code>'s columns is equal to the
   * width of the table.
   * <p>
   * Before the layout begins the method gets the <code>resizingColumn</code> of the
   * <code>tableHeader</code>. When the method is called as a result of the resizing of an enclosing
   * window, the <code>resizingColumn</code> is <code>null</code>. This means that resizing has
   * taken place "outside" the <code>JTable</code> and the change - or "delta" - should be
   * distributed to all of the columns regardless of this <code>JTable</code>'s automatic resize
   * mode.
   * <p>
   * If the <code>resizingColumn</code> is not <code>null</code>, it is one of the columns in the
   * table that has changed size rather than the table itself. In this case the auto-resize modes
   * govern the way the extra (or deficit) space is distributed amongst the available columns.
   * <p>
   * The modes are:
   * <ul>
   * <li>AUTO_RESIZE_OFF: Don't automatically adjust the column's widths at all. Use a horizontal
   * scrollbar to accomodate the columns when their sum exceeds the width of the
   * <code>Viewport</code>. If the <code>JTable</code> is not enclosed in a <code>JScrollPane</code>
   * this may leave parts of the table invisible.
   * <li>AUTO_RESIZE_NEXT_COLUMN: Use just the column after the resizing column. This results in the
   * "boundary" or divider between adjacent cells being independently adjustable.
   * <li>AUTO_RESIZE_SUBSEQUENT_COLUMNS: Use all columns after the one being adjusted to absorb the
   * changes. This is the default behavior.
   * <li>AUTO_RESIZE_LAST_COLUMN: Automatically adjust the size of the last column only. If the
   * bounds of the last column prevent the desired size from being allocated, set the width of the
   * last column to the appropriate limit and make no further adjustments.
   * <li>AUTO_RESIZE_ALL_COLUMNS: Spread the delta amongst all the columns in the
   * <code>JTable</code>, including the one that is being adjusted.
   * </ul>
   * <p>
   * <bold>Note:</bold> When a <code>JTable</code> makes adjustments to the widths of the columns it
   * respects their minimum and maximum values absolutely. It is therefore possible that, even after
   * this method is called, the total width of the columns is still not equal to the width of the
   * table. When this happens the <code>JTable</code> does not put itself in AUTO_RESIZE_OFF mode to
   * bring up a scroll bar, or break other commitments of its current auto-resize mode -- instead it
   * allows its bounds to be set larger (or smaller) than the total of the column minimum or
   * maximum, meaning, either that there will not be enough room to display all of the columns, or
   * that the columns will not fill the <code>JTable</code>'s bounds. These respectively, result in
   * the clipping of some columns or an area being painted in the <code>JTable</code>'s background
   * color during painting.
   * <p>
   * The mechanism for distributing the delta amongst the available columns is provided in a private
   * method in the <code>JTable</code> class:
   * <p>
   * <pre>
   *   adjustSizes(long targetSize, final Resizable3 r, boolean inverse)
   * </pre>
   * <p>
   * an explanation of which is provided in the following section. <code>Resizable3</code> is a
   * private interface that allows any data structure containing a collection of elements with a
   * size, preferred size, maximum size and minimum size to have its elements manipulated by the
   * algorithm.
   * <p>
   * <H3>Distributing the delta</H3>
   * <p>
   * <H4>Overview</H4>
   * <p>
   * Call "DELTA" the difference between the target size and the sum of the preferred sizes of the
   * elements in r. The individual sizes are calculated by taking the original preferred sizes and
   * adding a share of the DELTA - that share being based on how far each preferred size is from its
   * limiting bound (minimum or maximum).
   * <p>
   * <H4>Definition</H4>
   * <p>
   * Call the individual constraints min[i], max[i], and pref[i].
   * <p>
   * Call their respective sums: MIN, MAX, and PREF.
   * <p>
   * Each new size will be calculated using:
   * <p>
   * <p>
   * <pre>
   * size[i] = pref[i] + delta[i]
   * </pre>
   * <p>
   * where each individual delta[i] is calculated according to:
   * <p>
   * If (DELTA < 0) we are in shrink mode where:
   * <p>
   * <p>
   * <PRE>
   * DELTA
   * delta[i] = ------------ * (pref[i] - min[i])
   * (PREF - MIN)
   * </PRE>
   * <p>
   * If (DELTA > 0) we are in expand mode where:
   * <p>
   * <p>
   * <PRE>
   * DELTA
   * delta[i] = ------------ * (max[i] - pref[i])
   * (MAX - PREF)
   * </PRE>
   * <p>
   * The overall effect is that the total size moves that same percentage, k, towards the total
   * minimum or maximum and that percentage guarantees accomodation of the required space, DELTA.
   * <p>
   * <H4>Details</H4>
   * <p>
   * Naive evaluation of the formulae presented here would be subject to the aggregated rounding
   * errors caused by doing this operation in finite precision (using ints). To deal with this, the
   * multiplying factor above, is constantly recalculated and this takes account of the rounding
   * errors in the previous iterations. The result is an algorithm that produces a set of integers
   * whose values exactly sum to the supplied <code>targetSize</code>, and does so by spreading the
   * rounding errors evenly over the given elements.
   * <p>
   * <H4>When the MAX and MIN bounds are hit</H4>
   * <p>
   * When <code>targetSize</code> is outside the [MIN, MAX] range, the algorithm sets all sizes to
   * their appropriate limiting value (maximum or minimum).
   */
  public void doLayout() {
    GridSheetColumn resizingColumn = gridSheetPane.getColumnHeader().getResizingColumn();
    if (resizingColumn == null) {
      // setWidthsFromPreferredWidths(false);
    } else {
      // JTable behaves like a layout manger - but one in which the
      // user can come along and dictate how big one of the children
      // (columns) is supposed to be.

      // A column has been resized and JTable may need to distribute
      // any overall delta to other columns, according to the resize mode.
      // int columnIndex = gridSheetPane.viewIndexForColumn(resizingColumn);
      int delta = 0;
      GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(this);
      if (scrollPane.isFrozen()) {
        delta += scrollPane.getFrozenPoint().x;
      }
      // accommodateDelta(columnIndex, delta);
      // delta = getWidth() - getModel().getTotalColumnWidth();

      // If the delta cannot be completely accomodated, then the
      // resizing column will have to take any remainder. This means
      // that the column is not being allowed to take the requested
      // width. This happens under many circumstances: For example,
      // AUTO_RESIZE_NEXT_COLUMN specifies that any delta be distributed
      // to the column after the resizing column. If one were to attempt
      // to resize the last column of the table, there would be no
      // columns after it, and hence nowhere to distribute the delta.
      // It would then be given entirely back to the resizing column,
      // preventing it from changing size.
      if (delta != 0) {
        resizingColumn.setWidth(resizingColumn.getWidth() + delta);
      }

      // At this point the JTable has to work out what preferred sizes
      // would have resulted in the layout the user has chosen.
      // Thereafter, during window resizing etc. it has to work off
      // the preferred sizes as usual - the idea being that, whatever
      // the user does, everything stays in synch and things don't jump
      // around.
      // setWidthsFromPreferredWidths(true);
    }

    super.doLayout();
  }

  private interface Resizable2 {

    public int getElementCount();

    public int getLowerBoundAt(int i);

    public int getUpperBoundAt(int i);

    public void setSizeAt(int newSize, int i);
  }

  private interface Resizable3 extends Resizable2 {

    public int getMidPointAt(int i);
  }

  private void adjustSizes(long target, final Resizable3 r, boolean inverse) {
    int N = r.getElementCount();
    long totalPreferred = 0;
    for (int i = 0; i < N; i++) {
      totalPreferred += r.getMidPointAt(i);
    }
    Resizable2 s;
    if ((target < totalPreferred) == !inverse) {
      s = new Resizable2() {
        public int getElementCount() {
          return r.getElementCount();
        }

        public int getLowerBoundAt(int i) {
          return r.getLowerBoundAt(i);
        }

        public int getUpperBoundAt(int i) {
          return r.getMidPointAt(i);
        }

        public void setSizeAt(int newSize, int i) {
          r.setSizeAt(newSize, i);
        }

      };
    } else {
      s = new Resizable2() {
        public int getElementCount() {
          return r.getElementCount();
        }

        public int getLowerBoundAt(int i) {
          return r.getMidPointAt(i);
        }

        public int getUpperBoundAt(int i) {
          return r.getUpperBoundAt(i);
        }

        public void setSizeAt(int newSize, int i) {
          r.setSizeAt(newSize, i);
        }

      };
    }
    adjustSizes(target, s, !inverse);
  }

  private void adjustSizes(long target, Resizable2 r, boolean limitToRange) {
    long totalLowerBound = 0;
    long totalUpperBound = 0;
    for (int i = 0; i < r.getElementCount(); i++) {
      totalLowerBound += r.getLowerBoundAt(i);
      totalUpperBound += r.getUpperBoundAt(i);
    }

    if (limitToRange) {
      target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
    }

    for (int i = 0; i < r.getElementCount(); i++) {
      int lowerBound = r.getLowerBoundAt(i);
      int upperBound = r.getUpperBoundAt(i);
      // Check for zero. This happens when the distribution of the delta
      // finishes early due to a series of "fixed" entries at the end.
      // In this case, lowerBound == upperBound, for all subsequent terms.
      int newSize;
      if (totalLowerBound == totalUpperBound) {
        newSize = lowerBound;
      } else {
        double f = (double) (target - totalLowerBound) / (totalUpperBound - totalLowerBound);
        newSize = (int) Math.round(lowerBound + f * (upperBound - lowerBound));
        // We'd need to round manually in an all integer version.
        // size[i] = (int)(((totalUpperBound - target) * lowerBound +
        // (target - totalLowerBound) *
        // upperBound)/(totalUpperBound-totalLowerBound));
      }
      r.setSizeAt(newSize, i);
      target -= newSize;
      totalLowerBound -= lowerBound;
      totalUpperBound -= upperBound;
    }
  }

  /**
   * Overrides <code>JComponent</code>'s <code>getToolTipText</code> method in order to allow the
   * renderer's tips to be used if it has text set.
   * <p>
   * <bold>Note:</bold> For <code>JTable</code> to properly display tooltips of its renderers
   * <code>JTable</code> must be a registered component with the <code>ToolTipManager</code>. This
   * is done automatically in <code>initializeLocalVars</code>, but if at a later point
   * <code>JTable</code> is told <code>setToolTipText(null)</code> it will unregister the table
   * component, and no tips from renderers will display anymore.
   *
   * @see JComponent#getToolTipText
   */
  public String getToolTipText(MouseEvent event) {
    String tip = null;
    Point p = event.getPoint();

    // Locate the renderer under the event location
    int hitColumnIndex = gridSheetPane.columnAtPoint(p);
    int hitRowIndex = gridSheetPane.rowAtPoint(p);

    if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
      GridSheetCellRenderer renderer = getCellRenderer(hitRowIndex, hitColumnIndex);
      Component component = prepareRenderer(renderer, hitRowIndex, hitColumnIndex);

      // Now have to see if the component is a JComponent before
      // getting the tip
      if (component instanceof JComponent) {
        // Convert the event to the renderer's coordinate system
        Rectangle cellRect = getCellRect(hitRowIndex, hitColumnIndex, false);
        p.translate(-cellRect.x, -cellRect.y);
        MouseEvent newEvent = new MouseEvent(component, event.getID(), event.getWhen(),
            event.getModifiers(), p.x, p.y, event.getXOnScreen(), event.getYOnScreen(),
            event.getClickCount(), event.isPopupTrigger(), MouseEvent.NOBUTTON);

        tip = ((JComponent) component).getToolTipText(newEvent);
      }
    }

    // No tip from the renderer get our own tip
    if (tip == null) {
      tip = getToolTipText();
    }

    return tip;
  }

  //
  // Managing GridSheetTableUI
  //

  /**
   * Returns the L&F object that renders this component.
   *
   * @return the <code>GridSheetTableUI</code> object that renders this component
   */
  public GridSheetTableNoActionUI getUI() {
    return (GridSheetTableNoActionUI) ui;
  }

  /**
   * Sets the L&F object that renders this component and repaints.
   *
   * @param ui the GridSheetTableUI L&F object
   * @beaninfo bound: true hidden: true attribute: visualUpdate true description: The UI object that
   * implements the Component's LookAndFeel.
   * @see UIDefaults#getUI
   */
  public void setUI(GridSheetTableNoActionUI ui) {
    if (this.ui != ui) {
      super.setUI(ui);
      repaint();
    }
  }

  /**
   * Notification from the <code>UIManager</code> that the L&F has changed. Replaces the current UI
   * object with the latest version from the <code>UIManager</code>.
   *
   * @see JComponent#updateUI
   */
  public void updateUI() {

    // Update UI applied to parent ScrollPane
    configureEnclosingScrollPaneUI();

    setUI((GridSheetTableNoActionUI) UIManager.getUI(this));
  }

  /**
   * Returns the suffix used to construct the name of the L&F class used to render this component.
   *
   * @return the string "GridSheetTableUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID() {
    return uiClassID;
  }

  //
  // Implementing the CellEditorListener interface
  //

  /**
   * Invoked when editing is finished. The changes are saved and the editor is discarded.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by JTable.
   *
   * @param e the event received
   * @see CellEditorListener
   */
  public void editingStopped(ChangeEvent e) {
    // Take in the new value
    GridSheetCellEditor editor = getCellEditor();
    if (editor != null) {
      Object value = editor.getCellEditorValue();
      gridSheetPane.setValueAt(value, editingRow, editingColumn);
      removeEditor();
    }
  }

  /**
   * Invoked when editing is canceled. The editor object is discarded and the cell is rendered once
   * again.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by JTable.
   *
   * @param e the event received
   * @see CellEditorListener
   */
  public void editingCanceled(ChangeEvent e) {
    removeEditor();
  }

  //
  // Implementing the Scrollable interface
  //

  /**
   * Returns the preferred size of the viewport for this table.
   *
   * @return a <code>Dimension</code> object containing the <code>preferredSize</code> of the
   * <code>JViewport</code> which displays this table
   * @see Scrollable#getPreferredScrollableViewportSize
   */
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  /**
   * Returns the scroll increment (in pixels) that completely exposes one new row or column
   * (depending on the orientation).
   * <p>
   * This method is called each time the user requests a unit scroll.
   *
   * @param visibleRect the view area visible within the viewport
   * @param orientation either <code>SwingConstants.VERTICAL</code> or
   *                    <code>SwingConstants.HORIZONTAL</code>
   * @param direction   less than zero to scroll up/left, greater than zero for down/right
   * @return the "unit" increment for scrolling in the specified direction
   * @see Scrollable#getScrollableUnitIncrement
   */
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
    int leadingRow;
    int leadingCol;
    Rectangle leadingCellRect;

    int leadingVisibleEdge;
    int leadingCellEdge;
    int leadingCellSize;

    leadingRow = getLeadingRow(visibleRect);
    leadingCol = getLeadingCol(visibleRect);
    if (orientation == SwingConstants.VERTICAL && leadingRow < 0) {
      // Couldn't find leading row - return some default value
      return 10;
    } else if (orientation == SwingConstants.HORIZONTAL && leadingCol < 0) {
      // Couldn't find leading col - return some default value
      return 10;
    }

    // Note that it's possible for one of leadingCol or leadingRow to be
    // -1, depending on the orientation. This is okay, as getCellRect()
    // still provides enough information to calculate the unit increment.
    leadingCellRect = getCellRect(leadingRow, leadingCol, true);
    leadingVisibleEdge = leadingEdge(visibleRect, orientation);
    leadingCellEdge = leadingEdge(leadingCellRect, orientation);

    if (orientation == SwingConstants.VERTICAL) {
      leadingCellSize = leadingCellRect.height;

    } else {
      leadingCellSize = leadingCellRect.width;
    }

    // 4 cases:
    // #1: Leading cell fully visible, reveal next cell
    // #2: Leading cell fully visible, hide leading cell
    // #3: Leading cell partially visible, hide rest of leading cell
    // #4: Leading cell partially visible, reveal rest of leading cell
    if (leadingVisibleEdge == leadingCellEdge) { // Leading cell is fully
      // visible
      // Case #1: Reveal previous cell
      if (direction < 0) {
        int retVal = 0;

        if (orientation == SwingConstants.VERTICAL) {
          // Loop past any zero-height rows
          while (--leadingRow >= 0) {
            retVal = gridSheetPane.getRow(leadingRow).getHeight();
            if (retVal != 0) {
              break;
            }
          }
        } else { // HORIZONTAL
          // Loop past any zero-width cols
          while (--leadingCol >= 0) {
            retVal = getCellRect(leadingRow, leadingCol, true).width;
            if (retVal != 0) {
              break;
            }
          }
        }
        return retVal;
      } else { // Case #2: hide leading cell
        return leadingCellSize;
      }
    } else { // Leading cell is partially hidden
      // Compute visible, hidden portions
      int hiddenAmt = Math.abs(leadingVisibleEdge - leadingCellEdge);
      int visibleAmt = leadingCellSize - hiddenAmt;

      if (direction > 0) {
        // Case #3: hide showing portion of leading cell
        return visibleAmt;
      } else { // Case #4: reveal hidden portion of leading cell
        return hiddenAmt;
      }
    }
  }

  /**
   * Returns <code>visibleRect.height</code> or <code>visibleRect.width</code> , depending on this
   * table's orientation. Note that as of Swing 1.1.1 (Java 2 v 1.2.2) the value returned will
   * ensure that the viewport is cleanly aligned on a row boundary.
   *
   * @return <code>visibleRect.height</code> or <code>visibleRect.width</code> per the orientation
   * @see Scrollable#getScrollableBlockIncrement
   */
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

    if (gridSheetPane.getRowCount() == 0) {
      // Short-circuit empty table model
      if (SwingConstants.VERTICAL == orientation) {
        // int rh = gridSheetPane.getRowHeight();
        // return (rh > 0) ? Math.max(rh, (visibleRect.height / rh) * rh) : visibleRect.height;
        return visibleRect.height;
      } else {
        return visibleRect.width;
      }
    }
    // Shortcut for vertical scrolling of a table w/ uniform row height
    if (SwingConstants.VERTICAL == orientation) {
      int row = gridSheetPane.rowAtPoint(visibleRect.getLocation());
      assert row != -1;
      int col = gridSheetPane.columnAtPoint(visibleRect.getLocation());
      Rectangle cellRect = getCellRect(row, col, true);

      if (cellRect.y == visibleRect.y) {
        int rh = gridSheetPane.getModel().getDefaultRowHeight();
        assert rh > 0;
        return Math.max(rh, (visibleRect.height / rh) * rh);
      }
    }
    if (direction < 0) {
      return getPreviousBlockIncrement(visibleRect, orientation);
    } else {
      return getNextBlockIncrement(visibleRect, orientation);
    }
  }

  /**
   * Called to get the block increment for upward scrolling in cases of horizontal scrolling, or for
   * vertical scrolling of a table with variable row heights.
   */
  private int getPreviousBlockIncrement(Rectangle visibleRect, int orientation) {
    // Measure back from visible leading edge
    // If we hit the cell on its leading edge, it becomes the leading cell.
    // Else, use following cell

    int row;
    int col;

    int newEdge;
    Point newCellLoc;

    int visibleLeadingEdge = leadingEdge(visibleRect, orientation);
    boolean leftToRight = getComponentOrientation().isLeftToRight();
    int newLeadingEdge;

    // Roughly determine the new leading edge by measuring back from the
    // leading visible edge by the size of the visible rect, and find the
    // cell there.
    if (orientation == SwingConstants.VERTICAL) {
      newEdge = visibleLeadingEdge - visibleRect.height;
      int x = visibleRect.x + (leftToRight ? 0 : visibleRect.width);
      newCellLoc = new Point(x, newEdge);
    } else if (leftToRight) {
      newEdge = visibleLeadingEdge - visibleRect.width;
      newCellLoc = new Point(newEdge, visibleRect.y);
    } else { // Horizontal, right-to-left
      newEdge = visibleLeadingEdge + visibleRect.width;
      newCellLoc = new Point(newEdge - 1, visibleRect.y);
    }
    row = gridSheetPane.rowAtPoint(newCellLoc);
    col = gridSheetPane.columnAtPoint(newCellLoc);

    // If we're measuring past the beginning of the table, we get an invalid
    // cell. Just go to the beginning of the table in this case.
    if (orientation == SwingConstants.VERTICAL & row < 0) {
      newLeadingEdge = 0;
    } else if (orientation == SwingConstants.HORIZONTAL & col < 0) {
      if (leftToRight) {
        newLeadingEdge = 0;
      } else {
        newLeadingEdge = getWidth();
      }
    } else {
      // Refine our measurement
      Rectangle newCellRect = getCellRect(row, col, true);
      int newCellLeadingEdge = leadingEdge(newCellRect, orientation);
      int newCellTrailingEdge = trailingEdge(newCellRect, orientation);

      // Usually, we hit in the middle of newCell, and want to scroll to
      // the beginning of the cell after newCell. But there are a
      // couple corner cases where we want to scroll to the beginning of
      // newCell itself. These cases are:
      // 1) newCell is so large that it ends at or extends into the
      // visibleRect (newCell is the leading cell, or is adjacent to
      // the leading cell)
      // 2) newEdge happens to fall right on the beginning of a cell
      // Case 1
      if ((orientation == SwingConstants.VERTICAL || leftToRight)
          && (newCellTrailingEdge >= visibleLeadingEdge)) {
        newLeadingEdge = newCellLeadingEdge;
      } else if (orientation == SwingConstants.HORIZONTAL && !leftToRight
          && newCellTrailingEdge <= visibleLeadingEdge) {
        newLeadingEdge = newCellLeadingEdge;
      } // Case 2:
      else if (newEdge == newCellLeadingEdge) {
        newLeadingEdge = newCellLeadingEdge;
      } // Common case: scroll to cell after newCell
      else {
        newLeadingEdge = newCellTrailingEdge;
      }
    }
    return Math.abs(visibleLeadingEdge - newLeadingEdge);
  }

  /**
   * Called to get the block increment for downward scrolling in cases of horizontal scrolling, or
   * for vertical scrolling of a table with variable row heights.
   */
  private int getNextBlockIncrement(Rectangle visibleRect, int orientation) {
    // Find the cell at the trailing edge. Return the distance to put
    // that cell at the leading edge.
    int trailingRow = getTrailingRow(visibleRect);
    int trailingCol = getTrailingCol(visibleRect);

    Rectangle cellRect;
    boolean cellFillsVis;

    int cellLeadingEdge;
    int cellTrailingEdge;
    int newLeadingEdge;
    int visibleLeadingEdge = leadingEdge(visibleRect, orientation);

    // If we couldn't find trailing cell, just return the size of the
    // visibleRect. Note that, for instance, we don't need the
    // trailingCol to proceed if we're scrolling vertically, because
    // cellRect will still fill in the required dimensions. This would
    // happen if we're scrolling vertically, and the table is not wide
    // enough to fill the visibleRect.
    if (orientation == SwingConstants.VERTICAL && trailingRow < 0) {
      return visibleRect.height;
    } else if (orientation == SwingConstants.HORIZONTAL && trailingCol < 0) {
      return visibleRect.width;
    }
    cellRect = getCellRect(trailingRow, trailingCol, true);
    cellLeadingEdge = leadingEdge(cellRect, orientation);
    cellTrailingEdge = trailingEdge(cellRect, orientation);

    if (orientation == SwingConstants.VERTICAL || getComponentOrientation().isLeftToRight()) {
      cellFillsVis = cellLeadingEdge <= visibleLeadingEdge;
    } else { // Horizontal, right-to-left
      cellFillsVis = cellLeadingEdge >= visibleLeadingEdge;
    }

    if (cellFillsVis) {
      // The visibleRect contains a single large cell. Scroll to the end
      // of this cell, so the following cell is the first cell.
      newLeadingEdge = cellTrailingEdge;
    } else if (cellTrailingEdge == trailingEdge(visibleRect, orientation)) {
      // The trailing cell happens to end right at the end of the
      // visibleRect. Again, scroll to the beginning of the next cell.
      newLeadingEdge = cellTrailingEdge;
    } else {
      // Common case: the trailing cell is partially visible, and isn't
      // big enough to take up the entire visibleRect. Scroll so it
      // becomes the leading cell.
      newLeadingEdge = cellLeadingEdge;
    }
    return Math.abs(newLeadingEdge - visibleLeadingEdge);
  }

  /*
   * Return the row at the top of the visibleRect
   *
   * May return -1
   */
  private int getLeadingRow(Rectangle visibleRect) {
    Point leadingPoint;

    if (getComponentOrientation().isLeftToRight()) {
      leadingPoint = new Point(visibleRect.x, visibleRect.y);
    } else {
      leadingPoint = new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y);
    }
    return gridSheetPane.rowAtPoint(leadingPoint);
  }

  /*
   * Return the column at the leading edge of the visibleRect.
   *
   * May return -1
   */
  private int getLeadingCol(Rectangle visibleRect) {
    Point leadingPoint;

    if (getComponentOrientation().isLeftToRight()) {
      leadingPoint = new Point(visibleRect.x, visibleRect.y);
    } else {
      leadingPoint = new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y);
    }
    return gridSheetPane.columnAtPoint(leadingPoint);
  }

  /*
   * Return the row at the bottom of the visibleRect.
   *
   * May return -1
   */
  private int getTrailingRow(Rectangle visibleRect) {
    Point trailingPoint;

    if (getComponentOrientation().isLeftToRight()) {
      trailingPoint = new Point(visibleRect.x, visibleRect.y + visibleRect.height - 1);
    } else {
      trailingPoint =
          new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y + visibleRect.height - 1);
    }
    return gridSheetPane.rowAtPoint(trailingPoint);
  }

  /*
   * Return the column at the trailing edge of the visibleRect.
   *
   * May return -1
   */
  private int getTrailingCol(Rectangle visibleRect) {
    Point trailingPoint;

    if (getComponentOrientation().isLeftToRight()) {
      trailingPoint = new Point(visibleRect.x + visibleRect.width - 1, visibleRect.y);
    } else {
      trailingPoint = new Point(visibleRect.x, visibleRect.y);
    }
    return gridSheetPane.columnAtPoint(trailingPoint);
  }

  /*
   * Returns the leading edge ("beginning") of the given Rectangle. For VERTICAL, this is the top,
   * for left-to-right, the left side, and for right-to-left, the right side.
   */
  private int leadingEdge(Rectangle rect, int orientation) {
    if (orientation == SwingConstants.VERTICAL) {
      return rect.y;
    } else if (getComponentOrientation().isLeftToRight()) {
      return rect.x;
    } else { // Horizontal, right-to-left
      return rect.x + rect.width;
    }
  }

  /*
   * Returns the trailing edge ("end") of the given Rectangle. For VERTICAL, this is the bottom, for
   * left-to-right, the right side, and for right-to-left, the left side.
   */
  private int trailingEdge(Rectangle rect, int orientation) {
    if (orientation == SwingConstants.VERTICAL) {
      return rect.y + rect.height;
    } else if (getComponentOrientation().isLeftToRight()) {
      return rect.x + rect.width;
    } else { // Horizontal, right-to-left
      return rect.x;
    }
  }

  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  /**
   * Returns {@code false} to indicate that the height of the viewport does not determine the height
   * of the table, unless {@code getFillsViewportHeight} is {@code true} and the preferred height of
   * the table is smaller than the viewport's height.
   *
   * @return {@code false} unless {@code getFillsViewportHeight} is {@code true} and the table needs
   * to be stretched to fill the viewport
   * @see Scrollable#getScrollableTracksViewportHeight
   * @see #setFillsViewportHeight
   * @see #getFillsViewportHeight
   */
  public boolean getScrollableTracksViewportHeight() {
    Container parent = SwingUtilities.getUnwrappedParent(this);
    return getFillsViewportHeight() && parent instanceof JViewport
        && parent.getHeight() > getPreferredSize().height;
  }

  /**
   * Sets whether or not this table is always made large enough to fill the height of an enclosing
   * viewport. If the preferred height of the table is smaller than the viewport, then the table
   * will be stretched to fill the viewport. In other words, this ensures the table is never smaller
   * than the viewport. The default for this property is {@code false}.
   *
   * @param fillsViewportHeight whether or not this table is always made large enough to fill the
   *                            height of an enclosing viewport
   * @beaninfo bound: true description: Whether or not this table is always made large enough to
   * fill the height of an enclosing viewport
   * @see #getFillsViewportHeight
   * @see #getScrollableTracksViewportHeight
   * @since 1.6
   */
  public void setFillsViewportHeight(boolean fillsViewportHeight) {
    boolean old = this.fillsViewportHeight;
    this.fillsViewportHeight = fillsViewportHeight;
    resizeAndRepaint();
    firePropertyChange("fillsViewportHeight", old, fillsViewportHeight);
  }

  /**
   * Returns whether or not this table is always made large enough to fill the height of an
   * enclosing viewport.
   *
   * @return whether or not this table is always made large enough to fill the height of an
   * enclosing viewport
   * @see #setFillsViewportHeight
   * @since 1.6
   */
  public boolean getFillsViewportHeight() {
    return fillsViewportHeight;
  }

  //
  // Protected Methods
  //
  // @Override
  // protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
  //
  // boolean retValue = false;
  //
  // InputMap im = getInputMap(condition);
  // ActionMap am = getActionMap();
  //
  // if (im != null && am != null && isEnabled()) {
  // Object binding = im.get(ks);
  // Action action = (binding == null) ? null : am.get(binding);
  // if (action != null) {
  // retValue = SwingUtilities.notifyAction(action, ks, e, this, e.getModifiers());
  // if (retValue) {
  // // if (KeyMacro.isRecording()) {
  // // if ("startEditing".equals(binding)) {
  // // KeyMacro.append("editor = document.dataTable.startEditing()");
  // // } else {
  // // KeyMacro.put(DataTable.class, binding);
  // // }
  // // }
  // }
  // }
  // }
  // // Start editing when a key is typed.
  // if (!retValue && condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT && isFocusOwner()) {
  // // We do not have a binding for the event.
  // Component editorComponent = getEditorComponent();
  // if (editorComponent == null) {
  // if (e != null && e.getID() == KeyEvent.KEY_TYPED) {
  //
  // char ch = ks.getKeyChar();
  // int kc = e.getKeyCode();
  // if (kc != KeyEvent.VK_ENTER && kc != KeyEvent.VK_TAB && kc != KeyEvent.VK_DELETE
  // && isCompositionEnabled() && !isEditing() && 127 != ch && '\n' != ch && '\t' != ch
  // && KeyEvent.CHAR_UNDEFINED != ch) {
  // } else {
  // return false;
  // }
  // }
  //
  // // Only attempt to install the editor on a KEY_PRESSED,
  // if (e == null || e.getID() != KeyEvent.KEY_PRESSED) {
  // return false;
  // }
  // if (e.isMetaDown() || e.isAltDown() || e.isControlDown()) {
  // return false;
  // }
  // // Don't start when just a modifier is pressed
  // int code = e.getKeyCode();
  // if (code == KeyEvent.VK_SHIFT || code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_CONTROL
  // || code == KeyEvent.VK_ALT || code == KeyEvent.VK_META) {
  // return false;
  // }
  // // Try to install the editor
  // int anchorRow = gridSheetPane.getSelectionModel().getRowAnchorIndex();
  // int anchorColumn = gridSheetPane.getSelectionModel().getColumnAnchorIndex();
  // if (anchorRow != -1 && anchorColumn != -1 && !isEditing()) {
  // if (!editCellAt(anchorRow, anchorColumn, e)) {
  // return false;
  // }
  // }
  // editorComponent = getEditorComponent();
  // if (editorComponent == null) {
  // return false;
  // }
  // }
  // // If the editorComponent is a GridTableTextField, pass the event to
  // // it.
  // if (editorComponent instanceof GridTableTextField) {
  // retValue =
  // ((GridTableTextField) editorComponent).processKeyBinding(ks, e, WHEN_FOCUSED, pressed);
  // }
  // }
  // return retValue;
  // }

  private boolean isCompositionEnabled() {
    try {
      return getInputContext().isCompositionEnabled();
    } catch (RuntimeException e) {
      return true;
    }
  }

  /**
   * Creates default cell renderers for objects, numbers, doubles, dates, booleans, and icons.
   *
   * @see javax.swing.table.DefaultGridCellRenderer
   */
  protected GridSheetCellRenderer createDefaultRenderers() {
    return new DefaultGridSheetCellRenderer();
  }

  /**
   * Creates default cell editors for objects, numbers, and boolean values.
   *
   * @see DefaultCellEditor
   */
  protected void createDefaultEditors() {
    defaultEditorsByColumnClass = new HashMap<Class<?>, GridSheetCellEditor>();

    // Objects
    defaultEditorsByColumnClass.put(Object.class, new GridSheetCellStringEditor(this));
  }

  // /**
  // * Default Editors
  // */
  // static class GenericEditor extends DefaultCellEditor {
  //
  // Class[] argTypes = new Class[] { String.class };
  // java.lang.reflect.Constructor constructor;
  // Object value;
  //
  // public GenericEditor() {
  // super(new JTextField());
  // getComponent().setName("Table.editor");
  // }
  //
  // public boolean stopCellEditing() {
  // String s = (String) super.getCellEditorValue();
  // // Here we are dealing with the case where a user
  // // has deleted the string value in a cell, possibly
  // // after a failed validation. Return null, so that
  // // they have the option to replace the value with
  // // null or use escape to restore the original.
  // // For Strings, return "" for backward compatibility.
  // if ("".equals(s)) {
  // if (constructor.getDeclaringClass() == String.class) {
  // value = s;
  // }
  // super.stopCellEditing();
  // }
  //
  // try {
  // value = constructor.newInstance(new Object[] { s });
  // } catch (Exception e) {
  // ((JComponent) getComponent()).setBorder(new LineBorder(
  // Color.red));
  // return false;
  // }
  // return super.stopCellEditing();
  // }
  //
  // public Component getGridCellEditorComponent(GridSheetTable table,
  // Object value, boolean isSelected, int row, int column) {
  // this.value = null;
  // ((JComponent) getComponent())
  // .setBorder(new LineBorder(Color.black));
  // try {
  // Class<?> type = table.getColumnClass(column);
  // // Since our obligation is to produce a value which is
  // // assignable for the required type it is OK to use the
  // // String constructor for columns which are declared
  // // to contain Objects. A String is an Object.
  // if (type == Object.class) {
  // type = String.class;
  // }
  // constructor = type.getConstructor(argTypes);
  // } catch (Exception e) {
  // return null;
  // }
  // return super.getGridCellEditorComponent(table, value, isSelected,
  // row, column);
  // }
  //
  // public Object getCellEditorValue() {
  // return value;
  // }
  // }
  //
  // static class NumberEditor extends GenericEditor {
  //
  // public NumberEditor() {
  // ((JTextField) getComponent())
  // .setHorizontalAlignment(JTextField.RIGHT);
  // }
  // }
  //
  // static class BooleanEditor extends DefaultCellEditor {
  // public BooleanEditor() {
  // super(new JCheckBox());
  // JCheckBox checkBox = (JCheckBox) getComponent();
  // checkBox.setHorizontalAlignment(JCheckBox.CENTER);
  // }
  // }

  /**
   * Initializes table properties to their default values.
   */
  protected void initializeLocalVars() {
    setOpaque(true);
    createDefaultEditors();

    setEditingCell(-1, -1);
    // setPreferredScrollableViewportSize(new Dimension(250, 200));

    // I'm registered to do tool tips so we can draw tips for the renderers
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(this);

    setAutoscrolls(true);
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
    GridSheetScrollPane scrollPane = GridSheetUtils.getAncestorScrollPane(this);
    if (scrollPane != null && scrollPane.isFrozen()) {

      // When this method will repaint frozen area, tentatively, repaint
      // all bounds.
      Point divisionPoint = scrollPane.getDivisionPoint();
      Point frozenPoint = scrollPane.getFrozenPoint();
      if (x < divisionPoint.x) {
        width = getWidth();
      }
      if (y < divisionPoint.y) {
        height = getHeight();
      }
      x -= frozenPoint.x;
      width += frozenPoint.x;
      y -= frozenPoint.y;
      height += frozenPoint.y;
    }
    super.repaint(tm, x, y, width, height);
  }

  /**
   * Equivalent to <code>revalidate</code> followed by <code>repaint</code>.
   */
  protected void resizeAndRepaint() {
    revalidate();
    repaint();
  }

  /**
   * Returns the active cell editor, which is {@code null} if the table is not currently editing.
   *
   * @return the {@code GridSheetCellEditor} that does the editing, or {@code null} if the table is
   * not currently editing.
   * @see #cellEditor
   * @see #prepareCellEditor(int, int)
   */
  public GridSheetCellEditor getCellEditor() {
    return cellEditor;
  }

  /**
   * Sets the active cell editor.
   *
   * @param anEditor the active cell editor
   * @beaninfo bound: true description: The table's active cell editor.
   * @see #cellEditor
   */
  public void setCellEditor(GridSheetCellEditor anEditor) {
    GridSheetCellEditor oldEditor = cellEditor;
    cellEditor = anEditor;
    firePropertyChange("gridCellEditor", oldEditor, anEditor);
  }

  /**
   * Sets the <code>editingRow</code> and <code>editingColumn</code> variable.
   *
   * @param aRow    the row of the cell to be edited
   * @param aColumn the column of the cell to be edited
   * @see #editingRow
   * @see #editingColumn
   */
  public void setEditingCell(int aRow, int aColumn) {
    editingRow = aRow;
    editingColumn = aColumn;
  }

  /**
   * Returns an appropriate renderer for the cell specified by this row and column. If the
   * <code>GridSheetColumn</code> for this column has a non-null renderer, returns that. If not,
   * finds the class of the data in this column (using <code>getColumnClass</code>) and returns the
   * default renderer for this type of data.
   * <p>
   * <b>Note:</b> Throughout the table package, the internal implementations always use this method
   * to provide renderers so that this default behavior can be safely overridden by a subclass.
   *
   * @param row    the row of the cell to render, where 0 is the first row
   * @param column the column of the cell to render, where 0 is the first column
   * @return the assigned renderer; if <code>null</code> returns the default renderer for this type
   * of object
   * @see javax.swing.table.DefaultGridCellRenderer
   * @see javax.swing.table.GridColumn#setCellRenderer
   * @see #setDefaultRenderer
   */
  public GridSheetCellRenderer getCellRenderer(int row, int column) {
    return getDefaultRenderer();
  }

  /**
   * Prepares the renderer by querying the data model for the value and selection state of the cell
   * at <code>row</code>, <code>column</code>. Returns the component (may be a
   * <code>Component</code> or a <code>JComponent</code>) under the event location.
   * <p>
   * During a printing operation, this method will configure the renderer without indicating
   * selection or focus, to prevent them from appearing in the printed output. To do other
   * customizations based on whether or not the table is being printed, you can check the value of
   * {@link javax.swing.JComponent#isPaintingForPrint()}, either here or within custom renderers.
   * <p>
   * <b>Note:</b> Throughout the table package, the internal implementations always use this method
   * to prepare renderers so that this default behavior can be safely overridden by a subclass.
   *
   * @param renderer the <code>GridSheetCellRenderer</code> to prepare
   * @param row      the row of the cell to render, where 0 is the first row
   * @param column   the column of the cell to render, where 0 is the first column
   * @return the <code>Component</code> under the event location
   */
  public Component prepareRenderer(GridSheetCellRenderer renderer, int row, int column) {
    Object value = getCellValueAt(row, column);

    boolean isSelected = false;
    boolean hasFocus = false;

    // Only indicate the selection and focused cell if not printing
    if (!isPaintingForPrint()) {
      isSelected = gridSheetPane.isCellSelected(row, column);
      hasFocus = gridSheetPane.getSelectionModel().isAnchor(row, column);
    }

    return renderer.getGridCellRendererComponent(this, value, isSelected, hasFocus, row, column);
  }

  protected Object getCellValueAt(int row, int column) {
    return gridSheetPane.getValueAt(row, column);
  }

  //
  // Editing Support
  //
  public boolean startEdit() {
    GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
    return editCellAt(sm.getRowAnchorIndex(), sm.getColumnAnchorIndex());
  }

  /**
   * Programmatically starts editing the cell at <code>row</code> and <code>column</code>, if those
   * indices are in the valid range, and the cell at those indices is editable. Note that this is a
   * convenience method for <code>editCellAt(int, int, null)</code>.
   *
   * @param row    the row to be edited
   * @param column the column to be edited
   * @return false if for any reason the cell cannot be edited, or if the indices are invalid
   */
  public boolean editCellAt(int row, int column) {
    return editCellAt(row, column, null);
  }

  /**
   * Programmatically starts editing the cell at <code>row</code> and <code>column</code>, if those
   * indices are in the valid range, and the cell at those indices is editable. To prevent the
   * <code>JTable</code> from editing a particular table, column or cell value, return false from
   * the <code>isCellEditable</code> method in the <code>GridSheetModel</code> interface.
   *
   * @param row    the row to be edited
   * @param column the column to be edited
   * @param e      event to pass into <code>shouldSelectCell</code>; note that as of Java 2 platform
   *               v1.2, the call to <code>shouldSelectCell</code> is no longer made
   * @return false if for any reason the cell cannot be edited, or if the indices are invalid
   */
  public boolean editCellAt(int row, int column, EventObject e) {
    if (cellEditor != null && !cellEditor.stopCellEditing()) {
      return false;
    }

    if (row < 0 || row >= gridSheetPane.getRowCount() || column < 0
        || column >= gridSheetPane.getColumnCount()) {
      return false;
    }

    if (!isCellEditable(row, column)) {
      return false;
    }

    if (editorRemover == null) {
      KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      editorRemover = new CellEditorRemover(fm);
      fm.addPropertyChangeListener("permanentFocusOwner", editorRemover);
    }

    GridSheetCellEditor editor = prepareCellEditor(row, column);
    if (editor != null && editor.isCellEditable(e)) {
      Object value = gridSheetPane.getValueAt(row, column);
      boolean isSelected = gridSheetPane.isCellSelected(row, column);
      boolean b = editor.prepare(this, value, isSelected, row, column);
      if (b) {
        JComponent outerEditorComp = editor.getOuterEditorComponent();
        outerEditorComp.setBounds(getCellRect(row, column, false));
        add(outerEditorComp);
        outerEditorComp.validate();
        outerEditorComp.repaint();

        setCellEditor(editor);
        setEditingCell(row, column);
        editor.addCellEditorListener(this);

        return true;
      }
    }
    return false;
  }

  public void stopCellEditing() {
    if (isEditing()) {
      cellEditor.stopCellEditing();
    }
  }

  /**
   * Returns true if the cell at <code>row</code> and <code>column</code> is editable. Otherwise,
   * invoking <code>setValueAt</code> on the cell will have no effect.
   * <p>
   * <b>Note</b>: The column is specified in the table view's display order, and not in the
   * <code>GridSheetModel</code>'s column order. This is an important distinction because as the
   * user rearranges the columns in the table, the column at a given index in the view will change.
   * Meanwhile the user's actions never affect the model's column ordering.
   *
   * @param row    the row whose value is to be queried
   * @param column the column whose value is to be queried
   * @return true if the cell is editable
   * @see #setValueAt
   */
  public boolean isCellEditable(int row, int column) {
    return true;
  }

  /**
   * Returns true if a cell is being edited.
   *
   * @return true if the table is editing a cell
   * @see #editingColumn
   * @see #editingRow
   */
  public boolean isEditing() {
    return cellEditor != null;
  }

  /**
   * Returns the index of the column that contains the cell currently being edited. If nothing is
   * being edited, returns -1.
   *
   * @return the index of the column that contains the cell currently being edited; returns -1 if
   * nothing being edited
   * @see #editingRow
   */
  public int getEditingColumn() {
    return editingColumn;
  }

  /**
   * Returns the index of the row that contains the cell currently being edited. If nothing is being
   * edited, returns -1.
   *
   * @return the index of the row that contains the cell currently being edited; returns -1 if
   * nothing being edited
   * @see #editingColumn
   */
  public int getEditingRow() {
    return editingRow;
  }

  /**
   * Returns an appropriate editor for the cell specified by <code>row</code> and
   * <code>column</code>. If the <code>GridSheetColumn</code> for this column has a non-null editor,
   * returns that. If not, finds the class of the data in this column (using
   * <code>getColumnClass</code>) and returns the default editor for this type of data.
   * <p>
   * <b>Note:</b> Throughout the table package, the internal implementations always use this method
   * to provide editors so that this default behavior can be safely overridden by a subclass.
   *
   * @param row    the row of the cell to edit, where 0 is the first row
   * @param column the column of the cell to edit, where 0 is the first column
   * @return the editor for this cell; if <code>null</code> return the default editor for this type
   * of cell
   * @see DefaultCellEditor
   */
  protected GridSheetCellEditor prepareCellEditor(int row, int column) {
    return getDefaultEditor(String.class);
  }

  /**
   * Discards the editor object and frees the real estate it used for cell rendering.
   */
  private void removeEditor() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .removePropertyChangeListener("permanentFocusOwner", editorRemover);
    editorRemover = null;

    if (cellEditor != null) {
      cellEditor.removeCellEditorListener(this);
      Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      // boolean isFocusOwnerInTheTable =
      // focusOwner != null ? SwingUtilities.isDescendingFrom(focusOwner, this) : false;
      remove(cellEditor.getOuterEditorComponent());
      // if (isFocusOwnerInTheTable) {
      requestFocusInWindow();
      // }

      Rectangle cellRect = getCellRect(editingRow, editingColumn, false);

      setCellEditor(null);
      setEditingCell(-1, -1);

      repaint(cellRect);
    }
  }

  //
  // Implementing GridSheetSelectionListener interface
  //

  /**
   * Invoked when the row selection changes -- repaints to show the new selection.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by JTable.
   *
   * @param e the event received
   * @see ListSelectionListener
   */
  @Override
  public void selectionChanged(GridSheetSelectionEvent e) {
    boolean isAdjusting = e.getValueIsAdjusting();
    if (selectionAdjusting && !isAdjusting) {
      // The assumption is that when the model is no longer adjusting
      // we will have already gotten all the changes, and therefore
      // don't need to do an additional paint.
      selectionAdjusting = false;
      return;
    }
    selectionAdjusting = isAdjusting;
    // The getCellRect() calls will fail unless there is at least one
    // column.
    if (gridSheetPane.getRowCount() <= 0 || gridSheetPane.getColumnCount() <= 0) {
      return;
    }
    if (isEditing()) {
      stopCellEditing();
    }

    int firstRowIndex = limit(e.getFirstRow(), 0, gridSheetPane.getRowCount() - 1);
    int lastRowIndex = limit(e.getLastRow(), 0, gridSheetPane.getRowCount() - 1);
    int firstColumnIndex = limit(e.getFirstColumn(), 0, gridSheetPane.getColumnCount() - 1);
    int lastColumnIndex = limit(e.getLastColumn(), 0, gridSheetPane.getColumnCount() - 1);

    Rectangle firstRowRect = getOuterCellRect(firstRowIndex, firstColumnIndex);
    Rectangle lastRowRect = getOuterCellRect(lastRowIndex, lastColumnIndex);
    Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
    repaint(dirtyRegion);
  }

  private static int limit(int i, int a, int b) {
    return Math.min(b, Math.max(i, a));
  }

  // This class tracks changes in the keyboard focus state. It is used
  // when the JTable is editing to determine when to cancel the edit.
  // If focus switches to a component outside of the jtable, but in the
  // same window, this will cancel editing.
  class CellEditorRemover implements PropertyChangeListener {

    KeyboardFocusManager focusManager;

    public CellEditorRemover(KeyboardFocusManager fm) {
      this.focusManager = fm;
    }

    public void propertyChange(PropertyChangeEvent ev) {
      if (!isEditing() || getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE) {
        return;
      }

      Component c = focusManager.getPermanentFocusOwner();
      while (c != null) {
        if (c == GridSheetTable.this) {
          // focus remains inside the table
          return;
        } else if ((c instanceof Window) || (c instanceof Applet && c.getParent() == null)) {
          if (c == SwingUtilities.getRoot(GridSheetTable.this)) {
            if (!getCellEditor().stopCellEditing()) {
              getCellEditor().cancelCellEditing();
            }
          }
          break;
        }
        c = c.getParent();
      }
    }
  }

  // ///////////////
  // Printing Support
  // ///////////////

  /**
   * A convenience method that displays a printing dialog, and then prints this <code>JTable</code>
   * in mode <code>PrintMode.FIT_WIDTH</code>, with no header or footer text. A modal progress
   * dialog, with an abort option, will be shown for the duration of printing.
   * <p>
   * Note: In headless mode, no dialogs are shown and printing occurs on the default printer.
   *
   * @return true, unless printing is cancelled by the user
   * @throws SecurityException if this thread is not allowed to initiate a print job request
   * @throws PrinterException  if an error in the print system causes the job to be aborted
   * @see #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean,
   * PrintRequestAttributeSet, boolean, PrintService)
   * @see #getPrintable
   * @since 1.5
   */
  public boolean print() throws PrinterException {

    return print(PrintMode.FIT_WIDTH);
  }

  /**
   * A convenience method that displays a printing dialog, and then prints this <code>JTable</code>
   * in the given printing mode, with no header or footer text. A modal progress dialog, with an
   * abort option, will be shown for the duration of printing.
   * <p>
   * Note: In headless mode, no dialogs are shown and printing occurs on the default printer.
   *
   * @param printMode the printing mode that the printable should use
   * @return true, unless printing is cancelled by the user
   * @throws SecurityException if this thread is not allowed to initiate a print job request
   * @throws PrinterException  if an error in the print system causes the job to be aborted
   * @see #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean,
   * PrintRequestAttributeSet, boolean, PrintService)
   * @see #getPrintable
   * @since 1.5
   */
  public boolean print(PrintMode printMode) throws PrinterException {

    return print(printMode, null, null);
  }

  /**
   * A convenience method that displays a printing dialog, and then prints this <code>JTable</code>
   * in the given printing mode, with the specified header and footer text. A modal progress dialog,
   * with an abort option, will be shown for the duration of printing.
   * <p>
   * Note: In headless mode, no dialogs are shown and printing occurs on the default printer.
   *
   * @param printMode    the printing mode that the printable should use
   * @param headerFormat a <code>MessageFormat</code> specifying the text to be used in printing a
   *                     header, or null for none
   * @param footerFormat a <code>MessageFormat</code> specifying the text to be used in printing a
   *                     footer, or null for none
   * @return true, unless printing is cancelled by the user
   * @throws SecurityException if this thread is not allowed to initiate a print job request
   * @throws PrinterException  if an error in the print system causes the job to be aborted
   * @see #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean,
   * PrintRequestAttributeSet, boolean, PrintService)
   * @see #getPrintable
   * @since 1.5
   */
  public boolean print(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat)
      throws PrinterException {

    boolean showDialogs = !GraphicsEnvironment.isHeadless();
    return print(printMode, headerFormat, footerFormat, showDialogs, null, showDialogs);
  }

  /**
   * Prints this table, as specified by the fully featured null null null null null null
   * {@link #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean, PrintRequestAttributeSet, boolean, PrintService)
   * print} method, with the default printer specified as the print service.
   *
   * @param printMode       the printing mode that the printable should use
   * @param headerFormat    a <code>MessageFormat</code> specifying the text to be used in printing a
   *                        header, or <code>null</code> for none
   * @param footerFormat    a <code>MessageFormat</code> specifying the text to be used in printing a
   *                        footer, or <code>null</code> for none
   * @param showPrintDialog whether or not to display a print dialog
   * @param attr            a <code>PrintRequestAttributeSet</code> specifying any printing attributes, or
   *                        <code>null</code> for none
   * @param interactive     whether or not to print in an interactive mode
   * @return true, unless printing is cancelled by the user
   * @throws HeadlessException if the method is asked to show a printing dialog or run
   *                           interactively, and <code>GraphicsEnvironment.isHeadless</code> returns
   *                           <code>true</code>
   * @throws SecurityException if this thread is not allowed to initiate a print job request
   * @throws PrinterException  if an error in the print system causes the job to be aborted
   * @see #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean,
   * PrintRequestAttributeSet, boolean, PrintService)
   * @see #getPrintable
   * @since 1.5
   */
  public boolean print(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat,
                       boolean showPrintDialog, PrintRequestAttributeSet attr, boolean interactive)
      throws PrinterException, HeadlessException {

    return print(printMode, headerFormat, footerFormat, showPrintDialog, attr, interactive, null);
  }

  /**
   * Prints this <code>JTable</code>. Takes steps that the majority of developers would take in
   * order to print a <code>JTable</code>. In short, it prepares the table, calls
   * <code>getPrintable</code> to fetch an appropriate <code>Printable</code>, and then sends it to
   * the printer.
   * <p>
   * A <code>boolean</code> parameter allows you to specify whether or not a printing dialog is
   * displayed to the user. When it is, the user may use the dialog to change the destination
   * printer or printing attributes, or even to cancel the print. Another two parameters allow for a
   * <code>PrintService</code> and printing attributes to be specified. These parameters can be used
   * either to provide initial values for the print dialog, or to specify values when the dialog is
   * not shown.
   * <p>
   * A second <code>boolean</code> parameter allows you to specify whether or not to perform
   * printing in an interactive mode. If <code>true</code>, a modal progress dialog, with an abort
   * option, is displayed for the duration of printing . This dialog also prevents any user action
   * which may affect the table. However, it can not prevent the table from being modified by code
   * (for example, another thread that posts updates using <code>SwingUtilities.invokeLater</code>).
   * It is therefore the responsibility of the developer to ensure that no other code modifies the
   * table in any way during printing (invalid modifications include changes in: size, renderers, or
   * underlying data). Printing behavior is undefined when the table is changed during printing.
   * <p>
   * If <code>false</code> is specified for this parameter, no dialog will be displayed and printing
   * will begin immediately on the event-dispatch thread. This blocks any other events, including
   * repaints, from being processed until printing is complete. Although this effectively prevents
   * the table from being changed, it doesn't provide a good user experience. For this reason,
   * specifying <code>false</code> is only recommended when printing from an application with no
   * visible GUI.
   * <p>
   * Note: Attempting to show the printing dialog or run interactively, while in headless mode, will
   * result in a <code>HeadlessException</code>.
   * <p>
   * Before fetching the printable, this method will gracefully terminate editing, if necessary, to
   * prevent an editor from showing in the printed result. Additionally, <code>JTable</code> will
   * prepare its renderers during printing such that selection and focus are not indicated. As far
   * as customizing further how the table looks in the printout, developers can provide custom
   * renderers or paint code that conditionalize on the value of
   * {@link javax.swing.JComponent#isPaintingForPrint()}.
   * <p>
   * See {@link #getPrintable} for more description on how the table is printed.
   *
   * @param printMode       the printing mode that the printable should use
   * @param headerFormat    a <code>MessageFormat</code> specifying the text to be used in printing a
   *                        header, or <code>null</code> for none
   * @param footerFormat    a <code>MessageFormat</code> specifying the text to be used in printing a
   *                        footer, or <code>null</code> for none
   * @param showPrintDialog whether or not to display a print dialog
   * @param attr            a <code>PrintRequestAttributeSet</code> specifying any printing attributes, or
   *                        <code>null</code> for none
   * @param interactive     whether or not to print in an interactive mode
   * @param service         the destination <code>PrintService</code>, or <code>null</code> to use the
   *                        default printer
   * @return true, unless printing is cancelled by the user
   * @throws HeadlessException if the method is asked to show a printing dialog or run
   *                           interactively, and <code>GraphicsEnvironment.isHeadless</code> returns
   *                           <code>true</code>
   * @throws SecurityException if a security manager exists and its
   *                           {@link java.lang.SecurityManager#checkPrintJobAccess} method disallows this thread from
   *                           creating a print job request
   * @throws PrinterException  if an error in the print system causes the job to be aborted
   * @see #getPrintable
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @since 1.6
   */
  public boolean print(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat,
                       boolean showPrintDialog, PrintRequestAttributeSet attr, boolean interactive,
                       PrintService service) throws PrinterException, HeadlessException {

    // complain early if an invalid parameter is specified for headless mode
    boolean isHeadless = GraphicsEnvironment.isHeadless();
    if (isHeadless) {
      if (showPrintDialog) {
        throw new HeadlessException("Can't show print dialog.");
      }

      if (interactive) {
        throw new HeadlessException("Can't run interactively.");
      }
    }

    // Get a PrinterJob.
    // Do this before anything with side-effects since it may throw a
    // security exception - in which case we don't want to do anything else.
    final PrinterJob job = PrinterJob.getPrinterJob();

    if (isEditing()) {
      // try to stop cell editing, and failing that, cancel it
      if (!getCellEditor().stopCellEditing()) {
        getCellEditor().cancelCellEditing();
      }
    }

    if (attr == null) {
      attr = new HashPrintRequestAttributeSet();
    }

    final PrintingStatus printingStatus;

    // fetch the Printable
    Printable printable = getPrintable(printMode, headerFormat, footerFormat);

    if (interactive) {
      // wrap the Printable so that we can print on another thread
      printable = new ThreadSafePrintable(printable);
      printingStatus = PrintingStatus.createPrintingStatus(this, job);
      printable = printingStatus.createNotificationPrintable(printable);
    } else {
      // to please compiler
      printingStatus = null;
    }

    // set the printable on the PrinterJob
    job.setPrintable(printable);

    // if specified, set the PrintService on the PrinterJob
    if (service != null) {
      job.setPrintService(service);
    }

    // if requested, show the print dialog
    if (showPrintDialog && !job.printDialog(attr)) {
      // the user cancelled the print dialog
      return false;
    }

    // if not interactive, just print on this thread (no dialog)
    if (!interactive) {
      // do the printing
      job.print(attr);

      // we're done
      return true;
    }

    // make sure this is clear since we'll check it after
    printError = null;

    // to synchronize on
    final Object lock = new Object();

    // copied so we can access from the inner class
    final PrintRequestAttributeSet copyAttr = attr;

    // this runnable will be used to do the printing
    // (and save any throwables) on another thread
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          // do the printing
          job.print(copyAttr);
        } catch (Throwable t) {
          // save any Throwable to be rethrown
          synchronized (lock) {
            printError = t;
          }
        } finally {
          // we're finished - hide the dialog
          printingStatus.dispose();
        }
      }
    };

    // start printing on another thread
    Thread th = new Thread(runnable);
    th.start();

    printingStatus.showModal(true);

    // look for any error that the printing may have generated
    Throwable pe;
    synchronized (lock) {
      pe = printError;
      printError = null;
    }

    // check the type of error and handle it
    if (pe != null) {
      // a subclass of PrinterException meaning the job was aborted,
      // in this case, by the user
      if (pe instanceof PrinterAbortException) {
        return false;
      } else if (pe instanceof PrinterException) {
        throw (PrinterException) pe;
      } else if (pe instanceof RuntimeException) {
        throw (RuntimeException) pe;
      } else if (pe instanceof Error) {
        throw (Error) pe;
      }

      // can not happen
      throw new AssertionError(pe);
    }

    return true;
  }

  /**
   * Return a <code>Printable</code> for use in printing this JTable.
   * <p>
   * This method is meant for those wishing to customize the default <code>Printable</code>
   * implementation used by <code>JTable</code>'s <code>print</code> methods. Developers wanting
   * simply to print the table should use one of those methods directly.
   * <p>
   * The <code>Printable</code> can be requested in one of two printing modes. In both modes, it
   * spreads table rows naturally in sequence across multiple pages, fitting as many rows as
   * possible per page. <code>PrintMode.NORMAL</code> specifies that the table be printed at its
   * current size. In this mode, there may be a need to spread columns across pages in a similar
   * manner to that of the rows. When the need arises, columns are distributed in an order
   * consistent with the table's <code>ComponentOrientation</code>. <code>PrintMode.FIT_WIDTH</code>
   * specifies that the output be scaled smaller, if necessary, to fit the table's entire width (and
   * thereby all columns) on each page. Width and height are scaled equally, maintaining the aspect
   * ratio of the output.
   * <p>
   * The <code>Printable</code> heads the portion of table on each page with the appropriate section
   * from the table's <code>JTableHeader</code>, if it has one.
   * <p>
   * Header and footer text can be added to the output by providing <code>MessageFormat</code>
   * arguments. The printing code requests Strings from the formats, providing a single item which
   * may be included in the formatted string: an <code>Integer</code> representing the current page
   * number.
   * <p>
   * You are encouraged to read the documentation for <code>MessageFormat</code> as some characters,
   * such as single-quote, are special and need to be escaped.
   * <p>
   * Here's an example of creating a <code>MessageFormat</code> that can be used to print
   * "Duke's Table: Page - " and the current page number:
   * <p>
   * <p>
   * <pre>
   * // notice the escaping of the single quote
   * // notice how the page number is included with &quot;{0}&quot;
   * MessageFormat format = new MessageFormat(&quot;Duke''s Table: Page - {0}&quot;);
   * </pre>
   * <p>
   * The <code>Printable</code> constrains what it draws to the printable area of each page that it
   * prints. Under certain circumstances, it may find it impossible to fit all of a page's content
   * into that area. In these cases the output may be clipped, but the implementation makes an
   * effort to do something reasonable. Here are a few situations where this is known to occur, and
   * how they may be handled by this particular implementation:
   * <ul>
   * <li>In any mode, when the header or footer text is too wide to fit completely in the printable
   * area -- print as much of the text as possible starting from the beginning, as determined by the
   * table's <code>ComponentOrientation</code>.
   * <li>In any mode, when a row is too tall to fit in the printable area -- print the upper-most
   * portion of the row and paint no lower border on the table.
   * <li>In <code>PrintMode.NORMAL</code> when a column is too wide to fit in the printable area --
   * print the center portion of the column and leave the left and right borders off the table.
   * </ul>
   * <p>
   * It is entirely valid for this <code>Printable</code> to be wrapped inside another in order to
   * create complex reports and documents. You may even request that different pages be rendered
   * into different sized printable areas. The implementation must be prepared to handle this
   * (possibly by doing its layout calculations on the fly). However, providing different heights to
   * each page will likely not work well with <code>PrintMode.NORMAL</code> when it has to spread
   * columns across pages.
   * <p>
   * As far as customizing how the table looks in the printed result, <code>JTable</code> itself
   * will take care of hiding the selection and focus during printing. For additional
   * customizations, your renderers or painting code can customize the look based on the value of
   * {@link javax.swing.JComponent#isPaintingForPrint()}
   * <p>
   * Also, <i>before</i> calling this method you may wish to <i>first</i> modify the state of the
   * table, such as to cancel cell editing or have the user size the table appropriately. However,
   * you must not modify the state of the table <i>after</i> this <code>Printable</code> has been
   * fetched (invalid modifications include changes in size or underlying data). The behavior of the
   * returned <code>Printable</code> is undefined once the table has been changed.
   *
   * @param printMode    the printing mode that the printable should use
   * @param headerFormat a <code>MessageFormat</code> specifying the text to be used in printing a
   *                     header, or null for none
   * @param footerFormat a <code>MessageFormat</code> specifying the text to be used in printing a
   *                     footer, or null for none
   * @return a <code>Printable</code> for printing this JTable
   * @see #print(GridTable.PrintMode, MessageFormat, MessageFormat, boolean,
   * PrintRequestAttributeSet, boolean)
   * @see Printable
   * @see PrinterJob
   * @since 1.5
   */
  public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat,
                                MessageFormat footerFormat) {

    return new GridSheetPrintable(gridSheetPane, printMode, headerFormat, footerFormat);
  }

  /**
   * A <code>Printable</code> implementation that wraps another <code>Printable</code>, making it
   * safe for printing on another thread.
   */
  private class ThreadSafePrintable implements Printable {

    /**
     * The delegate <code>Printable</code>.
     */
    private Printable printDelegate;

    /**
     * To communicate any return value when delegating.
     */
    private int retVal;

    /**
     * To communicate any <code>Throwable</code> when delegating.
     */
    private Throwable retThrowable;

    /**
     * Construct a <code>ThreadSafePrintable</code> around the given delegate.
     *
     * @param printDelegate the <code>Printable</code> to delegate to
     */
    public ThreadSafePrintable(Printable printDelegate) {
      this.printDelegate = printDelegate;
    }

    /**
     * Prints the specified page into the given {@link Graphics} context, in the specified format.
     * <p>
     * Regardless of what thread this method is called on, all calls into the delegate will be done
     * on the event-dispatch thread.
     *
     * @param graphics   the context into which the page is drawn
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex  the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully, or NO_SUCH_PAGE if a non-existent
     * page index is specified
     * @throws PrinterException if an error causes printing to be aborted
     */
    public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex)
        throws PrinterException {

      // We'll use this Runnable
      Runnable runnable = new Runnable() {
        public synchronized void run() {
          try {
            // call into the delegate and save the return value
            retVal = printDelegate.print(graphics, pageFormat, pageIndex);
          } catch (Throwable throwable) {
            // save any Throwable to be rethrown
            retThrowable = throwable;
          } finally {
            // notify the caller that we're done
            notifyAll();
          }
        }
      };

      synchronized (runnable) {
        // make sure these are initialized
        retVal = -1;
        retThrowable = null;

        // call into the EDT
        SwingUtilities.invokeLater(runnable);

        // wait for the runnable to finish
        while (retVal == -1 && retThrowable == null) {
          try {
            runnable.wait();
          } catch (InterruptedException ie) {
            // short process, safe to ignore interrupts
          }
        }

        // if the delegate threw a throwable, rethrow it here
        if (retThrowable != null) {
          if (retThrowable instanceof PrinterException) {
            throw (PrinterException) retThrowable;
          } else if (retThrowable instanceof RuntimeException) {
            throw (RuntimeException) retThrowable;
          } else if (retThrowable instanceof Error) {
            throw (Error) retThrowable;
          }

          // can not happen
          throw new AssertionError(retThrowable);
        }

        return retVal;
      }
    }
  }

  public GridSheetRow getRow(int rowIndex) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getHeight() {
    return gridSheetPane.getTotalRowHeight();
  }

  @Override
  public int getWidth() {
    return gridSheetPane.getTotalColumnWidth();
  }

  /**
   * @return the autofillRange
   */
  public GridSheetCellRange getAutofillRange() {
    return autofillRange;
  }

  /**
   * @param autofillRange
   */
  public void setAutofillRange(GridSheetCellRange autofillRange) {
    this.autofillRange = autofillRange;
    repaint();
  }

  public void autofill(GridSheetCellRange base, Direction direction, int num) {
    // implement in subclass
  }

  @Override
  public boolean requestFocusInWindow() {
    if (isEditing()) {
      if (getCellEditor().getEditorComponent().requestFocusInWindow()) {
        return true;
      }
    }
    return super.requestFocusInWindow();
  }
}
