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

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class GridSheetScrollPane extends JScrollPane {

  private static final long serialVersionUID = -3376848464761620150L;

  private boolean frozen = false;

  private int frozenRow = -1;
  private int frozenColumn = -1;
  private Point frozenPoint;
  private int divisionRow = -1;
  private int divisionColumn = -1;

  private int scrollModeToSave;

  private final GridSheetPane gridSheetPane;

  /**
   * Creates a <code>GridScrollPane</code> that displays the contents of the specified component
   */
  public GridSheetScrollPane(GridSheetPane gridSheetPane) {
    super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
    this.gridSheetPane = gridSheetPane;
    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
    setFocusable(false);
  }

  @Override
  protected JViewport createViewport() {
    return new GridSheetViewport();
  }

  public boolean isFrozen() {
    return frozen;
  }

  public Point getFrozenPoint() {
    return ((GridSheetTable) getViewport().getView()).getCellRect(frozenRow, frozenColumn, false)
        .getLocation();
  }

  public Point getDivisionPoint() {
    return ((GridSheetTable) getViewport().getView())
        .getCellRect(divisionRow, divisionColumn, false).getLocation();
  }

  public void setFrozen(boolean frozen) {
    if (frozen == this.frozen) {
      return;
    }
    this.frozen = frozen;
    if (frozen) {
      GridSheetPane gridSheetPane = (GridSheetPane) getParent();
      frozenPoint = getViewport().getViewPosition();
      frozenRow = gridSheetPane.rowAtPoint(frozenPoint);
      frozenColumn = gridSheetPane.columnAtPoint(frozenPoint);
      divisionRow = gridSheetPane.getSelectionModel().getMinRowSelectionIndex();
      divisionColumn = gridSheetPane.getSelectionModel().getMinColumnSelectionIndex();
      frozenRow = Math.min(divisionRow, frozenRow);
      frozenColumn = Math.min(divisionColumn, frozenColumn);
      scrollModeToSave = getViewport().getScrollMode();
      getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
      getRowHeader().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
      getColumnHeader().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
      getViewport().setViewPosition(new Point());
      revalidate();
      repaint();
      getViewport().revalidate();
      getViewport().repaint();
    } else {
      frozenPoint = null;
      frozenRow = 0;
      frozenColumn = 0;
      divisionRow = -1;
      divisionColumn = -1;
      getViewport().setScrollMode(scrollModeToSave);
      getRowHeader().setScrollMode(scrollModeToSave);
      getColumnHeader().setScrollMode(scrollModeToSave);
      revalidate();
      repaint();
      getViewport().revalidate();
      getViewport().repaint();
    }
  }

  public void translateToOriginalViewPoint(Point p) {
    translateToOriginalViewPoint(p, true, true);
  }

  public void translateToOriginalViewPoint(Point p, boolean x, boolean y) {
    if (!frozen) {
      return;
    }
    Point frozenPoint = getFrozenPoint();
    Point divisionPoint = getDivisionPoint();
    Point scrollDistance = getViewport().getViewPosition();
    if (x) {
      if (p.x - scrollDistance.x < divisionPoint.x - frozenPoint.x) {
        p.x -= scrollDistance.x - frozenPoint.x;
      } else {
        p.x += frozenPoint.x;
      }
    }
    if (y) {
      if (p.y - scrollDistance.y < divisionPoint.y - frozenPoint.y) {
        p.y -= scrollDistance.y - frozenPoint.y;
      } else {
        p.y += frozenPoint.y;
      }
    }
  }
}
