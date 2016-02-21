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
package com.smoothcsv.framework.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.component.view.ViewInfo;

import lombok.Getter;

public abstract class BaseTabView<T extends ViewInfo> extends JPanel implements SmoothComponent {

  private static final long serialVersionUID = 1077678164829315948L;

  private static int seq = 1;

  private final SCTabComponent tabComponent;

  private final T viewInfo;

  private final SmoothComponentSupport componentSupport;

  @Getter
  private final int viewId;

  public BaseTabView(T viewInfo) {

    viewId = seq++;

    componentSupport = createComponentSupport();

    setFocusable(false);
    setLayout(new BorderLayout());
    setBorder(null);

    this.viewInfo = viewInfo;

    tabComponent = createTabComponent();

    viewInfo.getPropertyChangeSupport().addPropertyChangeListener("shortTitle",
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            tabComponent.setTabTitle((String) evt.getNewValue());
          }
        });
  }

  protected SmoothComponentSupport createComponentSupport() {
    return new SmoothComponentSupport(this, "view");
  }

  @Override
  public SmoothComponentSupport getComponentSupport() {
    return componentSupport;
  }

  public T getViewInfo() {
    return viewInfo;
  }

  public SCTabComponent createTabComponent() {
    return new SCTabComponent();
  }

  public SCTabComponent getTabComponent() {
    return tabComponent;
  }

  protected void onCloseIconClicked() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    tabbedPane.removeTabAt(tabbedPane.indexOfComponent(this));
  }

  @SuppressWarnings("serial")
  protected class SCTabComponent extends JPanel {

    private final JButton closeButton;
    private final JLabel tabTitle;

    public SCTabComponent() {
      super(new BorderLayout());
      closeButton = new JButton(CloseTabIcon.INSTANCE);
      closeButton.setRolloverIcon(HoveredCloseTabIcon.INSTANCE);
      tabTitle = new JLabel();
      tabTitle.setMinimumSize(new Dimension(30, 10));
      // tabTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

      setFocusable(false);
      setOpaque(false);
      // setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

      // button.setBorderPainted(false);
      // button.setFocusPainted(false);
      // button.setContentAreaFilled(false);
      closeButton.setFocusable(false);
      closeButton.setBorder(BorderFactory.createEmptyBorder());
      closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          onCloseIconClicked();
        }
      });
      add(tabTitle, BorderLayout.CENTER);
      add(closeButton, BorderLayout.EAST);
    }

    public JButton getCloseButton() {
      return closeButton;
    }

    public void setTabTitle(String title) {
      tabTitle.setText(title);
    }
  }

  protected static class CloseTabIcon implements Icon {

    public static final CloseTabIcon INSTANCE = new CloseTabIcon();

    private final int width;
    private final int height;

    public CloseTabIcon() {
      width = 16;
      height = 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2D = (Graphics2D) g;
      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      g.translate(x, y);
      g.setColor(Color.LIGHT_GRAY);
      g.drawLine(4, 4, 11, 11);
      g.drawLine(4, 5, 10, 11);
      g.drawLine(5, 4, 11, 10);
      g.drawLine(11, 4, 4, 11);
      g.drawLine(11, 5, 5, 11);
      g.drawLine(10, 4, 4, 10);
      g.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
      return width;
    }

    @Override
    public int getIconHeight() {
      return height;
    }
  }

  protected static class HoveredCloseTabIcon extends CloseTabIcon {

    protected static final HoveredCloseTabIcon INSTANCE = new HoveredCloseTabIcon();

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2D = (Graphics2D) g;
      g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

      g.translate(x, y);
      g.setColor(Color.GRAY);
      g.drawLine(4, 4, 11, 11);
      g.drawLine(4, 5, 10, 11);
      g.drawLine(5, 4, 11, 10);
      g.drawLine(11, 4, 4, 11);
      g.drawLine(11, 5, 5, 11);
      g.drawLine(10, 4, 4, 10);
      g.translate(-x, -y);
    }
  }

  protected void onTabActivated() {}

  protected void onTabDeactivated() {}
}
