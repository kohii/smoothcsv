/*
 * Copyright 2014 kohii.
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
package com.smoothcsv.framework.component;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 *
 */
public class SCTabbedPaneUI extends BasicTabbedPaneUI {

  private static final Insets TAB_INSETS = new Insets(1, 0, 0, 0);

  /**
   * The color to use to fill in the background
   */
  private Color selectedColor;

  /**
   * The color to use to fill in the background
   */
  private Color unselectedColor;

  private Color tabBorderColor;

  // ------------------------------------------------------------------------------------------------------------------
  // Custom installation methods
  // ------------------------------------------------------------------------------------------------------------------

  public static ComponentUI createUI(JComponent c) {
    return new SCTabbedPaneUI();
  }

  protected void installDefaults() {
    super.installDefaults();

    tabBorderColor = SwingUtils.alpha(Color.BLACK, tabPane.getBackground(), 77);

    tabInsets = new Insets(7, 1, 7, 0);

    // tabAreaInsets.left = (calculateTabHeight(0, 0, tabPane.getFont().getSize()) / 4) + 1;
    selectedTabPadInsets = new Insets(0, 0, 0, 0);

    selectedColor = tabPane.getBackground();
    unselectedColor = SwingUtils.alpha(Color.BLACK, tabPane.getBackground(), 20);
    tabAreaInsets = new Insets(0, 2, 4, 0);
  }

  // ------------------------------------------------------------------------------------------------------------------
  // Custom sizing methods
  // ------------------------------------------------------------------------------------------------------------------

  public int getTabRunCount(JTabbedPane pane) {
    return 1;
  }

  protected Insets getContentBorderInsets(int tabPlacement) {
    return TAB_INSETS;
  }

  protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
    int vHeight = fontHeight + 6;
    if (vHeight % 2 == 0) {
      vHeight += 1;
    }
    return vHeight;
  }

  protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
    return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + metrics.getHeight();
  }

  protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y,
      int w, int h, boolean isSelected) {
    Graphics2D g2D = (Graphics2D) g;
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int[] xp =
        new int[] {x - 4, x - 1, x + 3, x + 4, x + 6, x + w - 6, x + w - 4, x + w - 3, x + w + 1,
            x + w + 4};
    int[] yp = new int[] {y + h, y + h - 2, y + 3, y + 1, y, y, y + 1, y + 3, y + h - 2, y + h};

    Polygon shape = new Polygon(xp, yp, xp.length);

    if (isSelected) {
      g2D.setColor(selectedColor);
    } else {
      g2D.setColor(unselectedColor);
    }
    g2D.fill(shape);

    g2D.setColor(tabBorderColor);
    g2D.draw(shape);

    if (isSelected) {
      g2D.setColor(selectedColor);
      g2D.drawLine(x - 2, y + h, x + w + 2, y + h);
    }

    // g2D.fill(shape);
    //
    // if (runCount > 1) {
    // g2D.fill(shape);
    // }
  }

  protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w,
      int h, boolean isSelected) {
    // Do nothing
  }

  protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x,
      int y, int w, int h) {
    g.setColor(tabBorderColor);
    g.drawLine(x, y, x + w, y);

    // Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, calcRect);
    // g.setColor(tabBorderColor);
    // if (selRect != null) {
    // g.drawLine(x, y, x + w, y);
    // g.setColor(Color.BLUE);
    // g.drawLine(selRect.x, y, selRect.x + selRect.width, y);
    // } else {
    // g.drawLine(x, y, x + w, y);
    // }
    // g.drawLine(x, y, selRect.x - 1, y);
    // g.drawLine(x, y, x + w, y);
    // g.setColor(Color.BLUE);
    // g.drawLine(selRect.x, y, selRect.x + selRect.width, y);

    // g.drawLine(selectedRect.x + selectedRect.width + (selectedRect.height / 4), y, x + w, y);
    // g.setColor(selectedColor);
    // g.drawLine(selectedRect.x - (selectedRect.height / 4) + 1, y, selectedRect.x
    // + selectedRect.width + (selectedRect.height / 4) - 1, y);

  }

  protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex,
      int x, int y, int w, int h) {
    // Do nothing
  }

  protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x,
      int y, int w, int h) {
    // Do nothing
  }

  protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex,
      int x, int y, int w, int h) {
    // Do nothing
  }

  protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
      Rectangle iconRect, Rectangle textRect, boolean isSelected) {
    // Do nothing
  }

  protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {
    return 0;
  }

  @Override
  protected void installKeyboardActions() {}

  @Override
  protected void uninstallKeyboardActions() {}
}
