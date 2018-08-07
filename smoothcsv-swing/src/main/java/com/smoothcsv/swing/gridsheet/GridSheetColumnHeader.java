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
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.Transient;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.smoothcsv.swing.gridsheet.event.GridSheetColumnHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetColumnModelEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetHeaderSelectionEvent;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import com.smoothcsv.swing.gridsheet.ui.GridSheetColumnHeaderUI;

@SuppressWarnings("serial")
public class GridSheetColumnHeader extends AbstractGridSheetHeaderComponent implements
    GridSheetColumnHeaderSelectionListener {

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "GridSheetColumnHeaderUI";

  //
  // Instance Variables
  //
  /**
   * The default renderer
   */
  private GridSheetHeaderRenderer defaultRenderer;

  /**
   * The index of the column being resized. <code>null</code> if not resizing.
   */
  transient protected GridSheetColumn resizingColumn;

  //
  // Constructors
  //
  public GridSheetColumnHeader(GridSheetPane gridSheetPane) {
    this(gridSheetPane, null);
  }

  public GridSheetColumnHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {

    super(gridSheetPane);

    setDefaultRenderer(renderer != null ? renderer : createDefaultRenderer());

    // Initialize local ivars
    initializeLocalVars();

    // Get UI going
    updateUI();
  }

  //
  // Local behavior attributes
  //

  /**
   * Sets the default renderer
   *
   * @param defaultRenderer the default renderer
   */
  public void setDefaultRenderer(GridSheetHeaderRenderer defaultRenderer) {
    this.defaultRenderer = defaultRenderer;
  }

  /**
   * Returns the default renderer used when no <code>headerRenderer</code> is defined by a
   * <code>GridRow</code>.
   *
   * @return the default renderer
   */
  @Transient
  public GridSheetHeaderRenderer getDefaultRenderer() {
    return defaultRenderer;
  }

  /**
   * Returns the resizing column. If no column is being resized this method returns
   * <code>null</code>.
   *
   * @return the resizing column, if a resize is in process, otherwise returns <code>null</code>
   */
  public GridSheetColumn getResizingColumn() {
    return resizingColumn;
  }

  /**
   * Returns the index of the column that <code>point</code> lies in, or -1 if it lies out of
   * bounds.
   *
   * @return the index of the column that <code>point</code> lies in, or -1 if it lies out of bounds
   */
  public int columnAtPoint(Point point) {
    return gridSheetPane.columnAtPoint(point);
  }

  public int columnAtPoint(Point point, int min, int max) {
    return gridSheetPane.columnAtPoint(point, min, max);
  }

  /**
   * Returns the rectangle containing the header tile at <code>column</code>. When the
   * <code>column</code> parameter is out of bounds this method uses the same conventions as the
   * <code>GTable</code> method <code>getCellRect</code>.
   *
   * @return the rectangle containing the header tile at <code>column</code>
   * @see GridTable#getCellRect
   */
  public Rectangle getHeaderRect(int column) {
    Rectangle r = new Rectangle();

    r.height = getHeight();

    if (column < 0) {
      // x = width = 0;
    } else if (column >= gridSheetPane.getColumnCount()) {
      if (getComponentOrientation().isLeftToRight()) {
        r.x = getWidth();
      }
    } else {
      for (int i = 0; i < column; i++) {
        r.x += gridSheetPane.getColumn(i).getWidth();
      }
      r.width = gridSheetPane.getColumn(column).getWidth();
    }
    return r;
  }

  public String getHeaderValue(int index) {
    return gridSheetPane.getColumnName(index);
  }

  //
  // Managing GridSheetColumnHeaderUI
  //

  /**
   * Returns the look and feel (L&F) object that renders this component.
   *
   * @return the <code>GridSheetColumnHeaderUI</code> object that renders this component
   */
  public GridSheetColumnHeaderUI getUI() {
    return (GridSheetColumnHeaderUI) ui;
  }

  /**
   * Sets the look and feel (L&F) object that renders this component.
   *
   * @param ui the <code>GridSheetColumnHeaderUI</code> L&F object
   * @see UIDefaults#getUI
   */
  public void setUI(GridSheetColumnHeaderUI ui) {
    if (this.ui != ui) {
      super.setUI(ui);
      repaint();
    }
  }

  /**
   * Notification from the <code>UIManager</code> that the look and feel (L&F) has changed. Replaces
   * the current UI object with the latest version from the <code>UIManager</code>.
   *
   * @see JComponent#updateUI
   */
  public void updateUI() {
    setUI((GridSheetColumnHeaderUI) UIManager.getUI(this));

    GridSheetHeaderRenderer renderer = getDefaultRenderer();
    if (renderer instanceof Component) {
      SwingUtilities.updateComponentTreeUI((Component) renderer);
    }
  }

  /**
   * Returns the suffix used to construct the name of the look and feel (L&F) class used to render
   * this component.
   *
   * @return "GridSheetColumnHeaderUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID() {
    return uiClassID;
  }

  //
  // Managing models
  //
  //
  // Implementing GridColumnModelListener interface
  //

  /**
   * Invoked when a column is added to the table column model.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by
   * <code>GridSheetTable</code>.
   *
   * @param e the event received
   * @see GridColumnModelListener
   */
  public void columnAdded(GridSheetColumnModelEvent e) {
    resizeAndRepaint();
  }

  /**
   * Invoked when a column is removed from the table column model.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by
   * <code>GridSheetTable</code>.
   *
   * @param e the event received
   * @see GridColumnModelListener
   */
  public void columnRemoved(GridSheetColumnModelEvent e) {
    resizeAndRepaint();
  }

  //
  // Package Methods
  //

  /**
   * Returns a default renderer to be used when no header renderer is defined by a
   * <code>GridSheetColumn</code>.
   *
   * @return the default table column renderer
   * @since 1.3
   */
  protected GridSheetHeaderRenderer createDefaultRenderer() {
    return new DefaultGridSheetHeaderCellRenderer();
  }

  /**
   * Initializes the local variables and properties with default values. Used by the constructor
   * methods.
   */
  protected void initializeLocalVars() {
    setOpaque(true);
    resizingAllowed = true;
    resizingColumn = null;
  }

  /**
   * Sets the header's <code>resizingColumn</code> to <code>aColumn</code>.
   * <p>
   * Application code will not use this method explicitly, it is used internally by the column
   * sizing mechanism.
   *
   * @param aColumn the column being resized, or <code>null</code> if no column is being resized
   */
  public void setResizingColumn(GridSheetColumn aColumn) {
    resizingColumn = aColumn;
  }

  @Override
  public void headersSelectionChanged(GridSheetHeaderSelectionEvent e) {
    repaint();
  }

  public int getColumnCount() {
    return gridSheetPane.getColumnCount();
  }

  public Component prepareRenderer(GridSheetHeaderRenderer renderer, int column) {
    Object value = getHeaderValue(column);

    boolean isSelected = false;
    boolean hasFocus = false;

    // Only indicate the selection and focused cell if not printing
    if (!isPaintingForPrint()) {
      GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      isSelected = sm.isColumnSelected(column);
      hasFocus = sm.isColumnHeaderSelected();
    }

    return renderer.getGridCellRendererComponent(this, value, isSelected, hasFocus, column);
  }

  public void scrollRectToVisible(int columnIndex) {
    Rectangle cellRect = getHeaderRect(columnIndex);
    if (cellRect != null) {
      scrollRectToVisible(cellRect);
    }
  }

  public int getMinWidth() {
    return getColumnCount() * getGridSheetPane().getModel().getMinColumnWidth();
  }

  public int getMaxWidth() {
    return getColumnCount() * getGridSheetPane().getModel().getMaxColumnWidth();
  }

  @Override
  public int getWidth() {
    return gridSheetPane.getTotalColumnWidth();
  }

} // End of Class GridGridColumnHeader
