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

import com.smoothcsv.swing.gridsheet.event.GridSheetHeaderSelectionEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetRowHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetRowModelEvent;
import com.smoothcsv.swing.gridsheet.model.GridSheetRow;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import com.smoothcsv.swing.gridsheet.ui.GridSheetRowHeaderUI;

@SuppressWarnings("serial")
public class GridSheetRowHeader extends AbstractGridSheetHeaderComponent
    implements GridSheetRowHeaderSelectionListener {

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "GridSheetRowHeaderUI";

  //
  // Instance Variables
  //
  /**
   * The default renderer
   */
  private GridSheetHeaderRenderer defaultRenderer;

  //
  // Constructors
  //
  public GridSheetRowHeader(GridSheetPane gridSheetPane) {
    this(gridSheetPane, null);
  }

  public GridSheetRowHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {

    super(gridSheetPane);

    setResizingAllowed(false);

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
   * <code>GridSheetRow</code>.
   *
   * @return the default renderer
   */
  @Transient
  public GridSheetHeaderRenderer getDefaultRenderer() {
    return defaultRenderer;
  }

  // /**
  // * Returns the resizing row. If no row is being resized this method
  // * returns <code>null</code>.
  // *
  // * @return the resizing row, if a resize is in process, otherwise returns
  // * <code>null</code>
  // */
  // public GridSheetRow getResizingRow() {
  // return resizingRow;
  // }

  /**
   * Returns the index of the row that <code>point</code> lies in, or -1 if it lies out of bounds.
   *
   * @return the index of the row that <code>point</code> lies in, or -1 if it lies out of bounds
   */
  public int rowAtPoint(Point point) {
    return gridSheetPane.rowAtPoint(point);
  }

  public int rowAtPoint(Point point, int min, int max) {
    return gridSheetPane.rowAtPoint(point, min, max);
  }

  /**
   * Returns the rectangle containing the header tile at <code>row</code>. When the <code>row</code>
   * parameter is out of bounds this method uses the same conventions as the <code>GTable</code>
   * method <code>getCellRect</code>.
   *
   * @return the rectangle containing the header tile at <code>row</code>
   * @see GridTable#getCellRect
   */
  public Rectangle getHeaderRect(int row) {
    Rectangle r = new Rectangle();

    r.width = getWidth();

    if (row < 0) {
      // y = height = 0;
    } else if (row >= gridSheetPane.getRowCount()) {
      r.y = getHeight();
    } else {
      for (int i = 0; i < row; i++) {
        r.y += gridSheetPane.getRow(i).getHeight();
      }
      r.height = gridSheetPane.getRow(row).getHeight();
    }
    return r;
  }

  //
  // Managing GridSheetRowHeaderUI
  //

  /**
   * Returns the look and feel (L&F) object that renders this component.
   *
   * @return the <code>GridSheetRowHeaderUI</code> object that renders this component
   */
  public GridSheetRowHeaderUI getUI() {
    return (GridSheetRowHeaderUI) ui;
  }

  /**
   * Sets the look and feel (L&F) object that renders this component.
   *
   * @param ui the <code>GridSheetRowHeaderUI</code> L&F object
   * @see UIDefaults#getUI
   */
  public void setUI(GridSheetRowHeaderUI ui) {
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
    setUI((GridSheetRowHeaderUI) UIManager.getUI(this));

    GridSheetHeaderRenderer renderer = getDefaultRenderer();
    if (renderer instanceof Component) {
      SwingUtilities.updateComponentTreeUI((Component) renderer);
    }
  }

  /**
   * Returns the suffix used to construct the name of the look and feel (L&F) class used to render
   * this component.
   *
   * @return "GridSheetRowHeaderUI"
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
  // Implementing GridRowModelListener interface
  //

  /**
   * Invoked when a row is added to the table row model.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by
   * <code>GridSheetTable</code>.
   *
   * @param e the event received
   * @see GridRowModelListener
   */
  public void rowAdded(GridSheetRowModelEvent e) {
    resizeAndRepaint();
  }

  /**
   * Invoked when a row is removed from the table row model.
   * <p>
   * Application code will not use these methods explicitly, they are used internally by
   * <code>GridSheetTable</code>.
   *
   * @param e the event received
   * @see GridRowModelListener
   */
  public void rowRemoved(GridSheetRowModelEvent e) {
    resizeAndRepaint();
  }

  //
  // Package Methods
  //

  /**
   * Returns a default renderer to be used when no header renderer is defined by a
   * <code>GridSheetRow</code>.
   *
   * @return the default table row renderer
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
    resizingAllowed = false;
    // resizingRow = null;
  }

  public void setResizingRow(Object object) {
    throw new UnsupportedOperationException();
  }

  public GridSheetRow getResizingRow() {
    throw new UnsupportedOperationException();
  }

  public String getHeaderValue(int rowIndex) {
    return String.valueOf(rowIndex + 1);
  }

  public int getRowCount() {
    return gridSheetPane.getRowCount();
  }

  public int getMinHeight() {
    return gridSheetPane.getModel().getMinRowHeight() * getRowCount();
  }

  public int getMaxHeight() {
    return gridSheetPane.getModel().getMaxRowHeight() * getRowCount();
  }

  @Override
  public int getHeight() {
    return gridSheetPane.getTotalRowHeight();
  }

  @Override
  public void headersSelectionChanged(GridSheetHeaderSelectionEvent e) {
    repaint();
  }

  public Component prepareRenderer(GridSheetHeaderRenderer renderer, int row) {
    Object value = getHeaderValue(row);

    boolean isSelected = false;
    boolean hasFocus = false;

    // Only indicate the selection and focused cell if not printing
    if (!isPaintingForPrint()) {
      GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      isSelected = sm.isRowSelected(row);
      hasFocus = sm.isRowHeaderSelected();
    }

    return renderer.getGridCellRendererComponent(this, value, isSelected, hasFocus, row);
  }

  public void scrollRectToVisible(int rowIndex) {
    Rectangle cellRect = getHeaderRect(rowIndex);
    if (cellRect != null) {
      scrollRectToVisible(cellRect);
    }
  }

} // End of Class GridGridRowHeader
