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
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.RepaintManager;

import com.smoothcsv.swing.utils.SwingUtils;

public class GridSheetViewport extends JViewport {

  private static final long serialVersionUID = 2670604032210883162L;

  public GridSheetViewport() {}

  @Override
  public void scrollRectToVisible(Rectangle contentRect) {
    Component view = getView();

    if (view == null) {
      return;
    } else {
      if (!view.isValid()) {
        // If the view is not valid, validate. scrollRectToVisible
        // may fail if the view is not valid first, contentRect
        // could be bigger than invalid size.
        validateView();
      }
      int dx, dy;

      GridSheetScrollPane scrollPane = (GridSheetScrollPane) getParent();
      boolean isFrozenAreaSelectedX = false, isFrozenAreaSelectedY = false;
      int divisionPointX = 0, divisionPointY = 0;
      int frozenPointX = 0, frozenPointY = 0;
      if (scrollPane.isFrozen()) {
        Point scrolledDistance = getViewPosition();
        Point frozenPoint = scrollPane.getFrozenPoint();
        frozenPointX = frozenPoint.x;
        frozenPointY = frozenPoint.y;
        Point divisionPoint = scrollPane.getDivisionPoint();

        divisionPointX = divisionPoint.x;
        divisionPointY = divisionPoint.y;

        if (contentRect.x < divisionPointX - scrolledDistance.x) {
          isFrozenAreaSelectedX = true;
        }
        if (contentRect.y < divisionPointY - scrolledDistance.y) {
          isFrozenAreaSelectedY = true;
        }
      }

      dx = isFrozenAreaSelectedX ? 0
          : positionAdjustment(getWidth(), contentRect.width, contentRect.x, divisionPointX,
          frozenPointX);
      dy = isFrozenAreaSelectedY ? 0
          : positionAdjustment(getHeight(), contentRect.height, contentRect.y, divisionPointY,
          frozenPointY);

      if (dx != 0 || dy != 0) {
        Point viewPosition = getViewPosition();
        Dimension viewSize = view.getSize();
        int startX = viewPosition.x;
        int startY = viewPosition.y;
        Dimension extent = getExtentSize();

        viewPosition.x -= dx;
        viewPosition.y -= dy;
        // Only constrain the location if the view is valid. If the
        // the view isn't valid, it typically indicates the view
        // isn't visible yet and most likely has a bogus size as will
        // we, and therefore we shouldn't constrain the scrolling
        if (view.isValid()) {
          if (getParent().getComponentOrientation().isLeftToRight()) {
            if (viewPosition.x + extent.width > viewSize.width) {
              viewPosition.x = Math.max(0, viewSize.width - extent.width);
            } else if (viewPosition.x < 0) {
              viewPosition.x = 0;
            }
          } else {
            if (extent.width > viewSize.width) {
              viewPosition.x = viewSize.width - extent.width;
            } else {
              viewPosition.x = Math.max(0, Math.min(viewSize.width - extent.width, viewPosition.x));
            }
          }
          if (viewPosition.y + extent.height > viewSize.height) {
            viewPosition.y = Math.max(0, viewSize.height - extent.height);
          } else if (viewPosition.y < 0) {
            viewPosition.y = 0;
          }
        }
        if (viewPosition.x != startX || viewPosition.y != startY) {
          setViewPosition(viewPosition);
          // NOTE: How JViewport currently works with the
          // backing store is not foolproof. The sequence of
          // events when setViewPosition
          // (scrollRectToVisible) is called is to reset the
          // views bounds, which causes a repaint on the
          // visible region and sets an ivar indicating
          // scrolling (scrollUnderway). When
          // JViewport.paint is invoked if scrollUnderway is
          // true, the backing store is blitted. This fails
          // if between the time setViewPosition is invoked
          // and paint is received another repaint is queued
          // indicating part of the view is invalid. There
          // is no way for JViewport to notice another
          // repaint has occurred and it ends up blitting
          // what is now a dirty region and the repaint is
          // never delivered.
          // It just so happens JTable encounters this
          // behavior by way of scrollRectToVisible, for
          // this reason scrollUnderway is set to false
          // here, which effectively disables the backing
          // store.
          scrollUnderway = false;
        }
      }
    }
  }

  /**
   * Customized {@link JViewport#positionAdjustment} that considers frozen area.
   *
   * @param frozenPoint
   * @see JViewport#positionAdjustment
   */
  private int positionAdjustment(int parentWidth, int childWidth, int childAt, int divisionPoint,
                                 int frozenPoint) {
    parentWidth -= divisionPoint - frozenPoint;
    childAt -= divisionPoint;

    // +-----+
    // | --- | No Change
    // +-----+
    if (childAt >= 0 && childWidth + childAt <= parentWidth) {
      return 0;
    }

    // +-----+
    // --------- No Change
    // +-----+
    if (childAt <= 0 && childWidth + childAt >= parentWidth) {
      return 0;
    }

    // +-----+ +-----+
    // | ---- -> | ----|
    // +-----+ +-----+
    if (childAt > 0 && childWidth <= parentWidth) {
      return -childAt + parentWidth - childWidth;
    }

    // +-----+ +-----+
    // | -------- -> |--------
    // +-----+ +-----+
    if (childAt >= 0 && childWidth >= parentWidth) {
      return -childAt;
    }

    // +-----+ +-----+
    // ---- | -> |---- |
    // +-----+ +-----+
    if (childAt <= 0 && childWidth <= parentWidth) {
      return -childAt;
    }

    // +-----+ +-----+
    // -------- | -> --------|
    // +-----+ +-----+
    if (childAt < 0 && childWidth >= parentWidth) {
      return -childAt + parentWidth - childWidth;
    }

    return 0;
  }

  /**
   * @see JViewport#validateView
   */
  private void validateView() {
    Component validateRoot = SwingUtils.getValidateRoot(this, false);

    if (validateRoot == null) {
      return;
    }

    // Validate the root.
    validateRoot.validate();

    // And let the RepaintManager it does not have to validate from
    // validateRoot anymore.
    RepaintManager rm = RepaintManager.currentManager(this);

    if (rm != null) {
      rm.removeInvalidComponent((JComponent) validateRoot);
    }
  }
}
