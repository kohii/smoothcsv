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
package com.smoothcsv.swing.components;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * @author kohii
 */
public class ExSplitPane extends JSplitPane {
  private static final long serialVersionUID = -2243392782426503970L;
  private static final int dividerDragSize = 7;
  private static final int dividerDragOffset = dividerDragSize / 2;

  public ExSplitPane() {
    setDividerSize(1);
    setFocusable(false);
    setContinuousLayout(true);
    setBorder(null);
  }

  public ExSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
    super(newOrientation, newLeftComponent, newRightComponent);
    setDividerSize(1);
    setFocusable(false);
    setContinuousLayout(true);
    setBorder(null);
  }

  @Override
  public void layout() {
    super.layout();

    // increase divider width or height
    BasicSplitPaneDivider divider = ((BasicSplitPaneUI) getUI()).getDivider();
    Rectangle bounds = divider.getBounds();
    if (orientation == HORIZONTAL_SPLIT) {
      bounds.x -= dividerDragOffset;
      bounds.width = dividerDragSize;
    } else {
      bounds.y -= dividerDragOffset;
      bounds.height = dividerDragSize;
    }
    divider.setBounds(bounds);
  }

  @Override
  public void updateUI() {
    setUI(new SplitPaneWithZeroSizeDividerUI());
    revalidate();
  }

  private static class SplitPaneWithZeroSizeDividerUI extends BasicSplitPaneUI {
    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
      return new ZeroSizeDivider(this);
    }

    @Override
    public int getDividerLocation(JSplitPane jc) {
      return super.getDividerLocation(jc) + dividerDragOffset;
    }
  }

  private static class ZeroSizeDivider extends BasicSplitPaneDivider {
    public ZeroSizeDivider(BasicSplitPaneUI ui) {
      super(ui);
      super.setBorder(null);
      setBackground(UIManager.getColor("controlShadow"));
    }

    @Override
    public void setBorder(Border border) {
      // ignore
    }

    @Override
    public void paint(Graphics g) {
      g.setColor(getBackground());
      if (orientation == HORIZONTAL_SPLIT)
        g.drawLine(dividerDragOffset, 0, dividerDragOffset, getHeight() - 1);
      else
        g.drawLine(0, dividerDragOffset, getWidth() - 1, dividerDragOffset);
    }

    @Override
    protected void dragDividerTo(int location) {
      super.dragDividerTo(location + dividerDragOffset);
    }

    @Override
    protected void finishDraggingTo(int location) {
      super.finishDraggingTo(location + dividerDragOffset);
    }
  }
}
