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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import com.smoothcsv.swing.gridsheet.event.GridSheetCornerHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetHeaderSelectionEvent;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;

@SuppressWarnings("serial")
public class GridSheetCornerHeader extends AbstractGridSheetHeaderComponent
    implements GridSheetCornerHeaderSelectionListener, MouseListener {

  //
  // Instance Variables
  //

  private boolean selectionAdjusting;

  private Dimension preferredSize;

  private GridSheetHeaderRenderer renderer;

  //
  // Constructors
  //
  public GridSheetCornerHeader(GridSheetPane gridSheetPane) {
    this(gridSheetPane, null);
  }

  public GridSheetCornerHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {
    super(gridSheetPane);
    // gridSheetPane.getSelectionModel()
    // .addCornerHeaderSelectionListener(this);
    setOpaque(true);
    setRenderer(renderer != null ? renderer : createDefaultRenderer());
    addMouseListener(this);
    // setLayout(new BorderLayout());
  }

  //
  // Methods
  //
  @Override
  protected void paintComponent(Graphics g) {
    JComponent c = (JComponent) prepareRenderer(renderer);

    if (c.getParent() != this) {
      // this.add(c);
    }
    //
    // Rectangle r = getBounds();
    // c.validate();
    // c.paintComponents(g);


    // c.setBounds(-r.width, -r.height, 0, 0);

    boolean wasDoubleBuffered = false;
    if ((c instanceof JComponent) && ((JComponent) c).isDoubleBuffered()) {
      wasDoubleBuffered = true;
      ((JComponent) c).setDoubleBuffered(false);
    }

    Rectangle r = getBounds();
    c.setBounds(0, 0, r.width, r.height);
    c.validate();

    Graphics cg = g.create(0, 0, r.width, r.height);
    try {
      c.paint(cg);

      cg.setColor(gridSheetPane.getColorProvider().getRuleLineColor());
      int w = getWidth() - 1;
      int h = getHeight() - 1;
      cg.drawLine(0, h, w, h);
      cg.drawLine(w, 0, w, h);
    } finally {
      cg.dispose();
    }

    if (wasDoubleBuffered && (c instanceof JComponent)) {
      ((JComponent) c).setDoubleBuffered(true);
    }

    // GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    // if (sm.isColumnHeaderSelected() && sm.isRowHeaderSelected()) {
    // g.setColor(Color.GRAY);
    // } else {
    // g.setColor(Color.LIGHT_GRAY);
    // }
    // g.fillRect(0, 0, getWidth(), getHeight());

    // this.remove(c);

  }

  // selection
  @Override
  public void headersSelectionChanged(GridSheetHeaderSelectionEvent e) {
    // boolean isAdjusting = e.getValueIsAdjusting();
    // if (selectionAdjusting && !isAdjusting) {
    // // The assumption is that when the model is no longer adjusting
    // // we will have already gotten all the changes, and therefore
    // // don't need to do an additional paint.
    // selectionAdjusting = false;
    // return;
    // }
    // selectionAdjusting = isAdjusting;
    repaint();
  }

  // mouse
  @Override
  public void mouseClicked(MouseEvent e) {
    // none
  }

  @Override
  public void mousePressed(MouseEvent e) {
    GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    gridSheetPane.selectAll(false);
    getGridSheetPane().getTable().requestFocusInWindow();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // none
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // none
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // none
  }

  /**
   * @param renderer the renderer to set
   */
  public void setRenderer(GridSheetHeaderRenderer renderer) {
    this.renderer = renderer;
  }


  public Component prepareRenderer(GridSheetHeaderRenderer renderer) {

    boolean isSelected = false;

    // Only indicate the selection and focused cell if not printing
    if (!isPaintingForPrint()) {
      GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
      isSelected = sm.isRowHeaderSelected() && sm.isColumnHeaderSelected();
    }

    return renderer.getGridCellRendererComponent(this, "", isSelected, isSelected, 0);
  }

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


  // /**
  // * Overridden to avoid propagating a invalidate up the tree when the cell renderer child is
  // * configured.
  // */
  // public void invalidate() {}
  //
  //
  // /**
  // * Shouldn't be called.
  // */
  // public void paint(Graphics g) {}
  //
  //
  // /**
  // * Shouldn't be called.
  // */
  // public void update(Graphics g) {}
}
