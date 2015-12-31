/*
 * Copyright 2015 kohii
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
package com.smoothcsv.core.macro.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;
import com.smoothcsv.swing.components.AwesomeIconButton;
import com.smoothcsv.swing.icon.AwesomeIcon;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class SimpleTabbedPane extends JPanel {

  private List<Tab> tabList = new ArrayList<>();
  @Getter
  private JPanel tabLabelsPanel;
  private int selected = -1;

  private EventListenerSupport listeners = new EventListenerSupportImpl();

  public SimpleTabbedPane() {
    setLayout(new BorderLayout(0, 0));

    tabLabelsPanel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) tabLabelsPanel.getLayout();
    flowLayout.setVgap(0);
    flowLayout.setAlignment(FlowLayout.LEFT);
    flowLayout.setHgap(0);
  }

  public void add(String tabTitle, JPanel comp, boolean closeable) {
    add(tabTitle, comp, null, closeable);
  }

  public void add(String tabTitle, Supplier<JPanel> compSupplier, boolean closeable) {
    add(tabTitle, null, compSupplier, closeable);
  }

  private void add(String tabTitle, JPanel comp, Supplier<JPanel> compSupplier, boolean closeable) {
    TabLabel label = new TabLabel(tabTitle, closeable);
    label.setOpaque(true);
    Border paddingBorder = BorderFactory.createEmptyBorder(2, 10, 2, 10);
    label.setBorder(paddingBorder);
    label.setBackground(null);
    Tab tab = new Tab(tabTitle, comp, compSupplier, label, closeable);
    tabList.add(tab);
    label.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        showTab(tab);
      }
    });
    tabLabelsPanel.add(label);
    if (closeable) {
      label.addCloseButtonMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
            removeTab(tabList.indexOf(tab));
          }
        }
      });
    }
    if (tabList.size() == 1) {
      showTab(0);
    }
  }

  public void removeTab(int index) {
    Tab targetTab = tabList.get(index);
    Tab selectedTab = tabList.get(selected);
    if (index == selected) {
      if (tabList.size() > 1) {
        int newTabIndex = index == tabList.size() - 1 ? index - 1 : index + 1;
        showTab(newTabIndex);
      } else {
        remove(selectedTab.getComp());
        listeners.invokeListeners(new TabChangeEvent(selectedTab.getComp(), null));

      }
    }
    if (index < selected && selected != -1) {
      selected--;
    }
    tabLabelsPanel.remove(targetTab.getLabel());
    tabList.remove(index);
    revalidate();
    repaint();
  }

  public void showTab(int index) {
    if (index == selected) {
      return;
    }
    Tab oldActiveTab = null;
    if (selected != -1) {
      oldActiveTab = tabList.get(selected);
      oldActiveTab.getLabel().setForeground(Color.BLACK);
      oldActiveTab.getLabel().setBackground(null);
      remove(oldActiveTab.getComp());
    }
    Tab newActiveTab = tabList.get(index);
    newActiveTab.getLabel().setForeground(Color.WHITE);
    newActiveTab.getLabel().setBackground(Color.GRAY);
    selected = index;
    JPanel comp = newActiveTab.getComp();
    add(comp, BorderLayout.CENTER);
    revalidate();
    repaint();
    comp.requestFocusInWindow();
    listeners.invokeListeners(
        new TabChangeEvent(oldActiveTab == null ? null : oldActiveTab.getComp(), comp));
  }

  public void showTab(JPanel comp) {
    for (int i = 0; i < tabList.size(); i++) {
      if (tabList.get(i).getComp() == comp) {
        showTab(i);
        return;
      }
    }
  }

  private void showTab(Tab tab) {
    showTab(tabList.indexOf(tab));
  }

  public EventListenerSupport listeners() {
    return listeners;
  }

  public JPanel getSelectedTabComponent() {
    return tabList.get(selected).getComp();
  }

  @Override
  public boolean requestFocusInWindow() {
    if (selected > -1) {
      return tabList.get(selected).getComp().requestFocusInWindow();
    }
    return super.requestFocusInWindow();
  }

  private static class Tab {
    private String title;
    private JPanel comp;
    private Supplier<JPanel> compSupplier;
    private JComponent label;
    private boolean closeable = false;

    public Tab(String title, JPanel comp, Supplier<JPanel> compSupplier, JComponent label,
        boolean closeable) {
      this.title = title;
      this.comp = comp;
      this.compSupplier = compSupplier;
      this.label = label;
      this.closeable = closeable;
    }

    public String getTitle() {
      return title;
    }

    public JPanel getComp() {
      if (comp == null) {
        comp = compSupplier.get();
      }
      return comp;
    }

    public JComponent getLabel() {
      return label;
    }

    public boolean isCloseable() {
      return closeable;
    }
  }

  @Getter
  @AllArgsConstructor
  public static class TabChangeEvent implements SCEvent {
    private JPanel oldTabComponent;
    private JPanel newTabComponent;
  }

  private static class TabLabel extends JPanel {

    private static final AwesomeIcon CLOSE_ICON = AwesomeIcon.create(AwesomeIcon.FA_CLOSE);

    private AwesomeIconButton button;
    private JLabel label;

    public TabLabel(String text, boolean showCloseButton) {
      FlowLayout flowLayout = (FlowLayout) getLayout();
      flowLayout.setVgap(0);
      flowLayout.setHgap(2);
      label = new JLabel(text);
      add(label);
      if (showCloseButton) {
        button = new AwesomeIconButton(CLOSE_ICON);
        add(button);
      }
    }

    public void addCloseButtonMouseListener(MouseListener ml) {
      button.addMouseListener(ml);
    }

    @Override
    public void setForeground(Color fg) {
      super.setForeground(fg);
      if (button != null) {
        button.setIcon(fg.equals(CLOSE_ICON.getColor()) ? CLOSE_ICON : CLOSE_ICON.create(fg));
        button.setBorder(null);
      }
      if (label != null) {
        label.setForeground(fg);
      }
    }
  }
}
