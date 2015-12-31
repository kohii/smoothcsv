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
package com.smoothcsv.framework.preference;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.DialogBase;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends DialogBase {

  private JList<PrefPage> sideBar;
  private JPanel mainPanel;
  private JLabel titleLabel;
  private JPanel currentPrefPanel;

  public PreferencesDialog(String title) {
    super(SCApplication.components().getFrame(), title);
    getContentPanel().setBorder(BorderFactory.createEmptyBorder());

    List<PrefPage> pages = PreferenceManager.getInstance().getAllPages();
    DefaultListModel<PrefPage> model = new DefaultListModel<>();
    for (PrefPage prefPage : pages) {
      model.addElement(prefPage);
    }
    sideBar = new JList<>(model);
    sideBar.setFocusable(false);
    sideBar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sideBar.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 10));
        return this;
      }
    });

    JScrollPane sideScrollPane = new JScrollPane(sideBar);
    sideScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
    sideScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
    sideScrollPane.setMinimumSize(new Dimension(140, 140));

    mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 4));
    JScrollPane mainScrollPane = new JScrollPane(mainPanel);
    mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
    mainScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
    mainPanel.setLayout(new BorderLayout(0, 0));

    JPanel titlePanel = new JPanel();
    titlePanel.setLayout(new BorderLayout());
    mainPanel.add(titlePanel, BorderLayout.NORTH);

    titleLabel = new JLabel();
    Font font = titleLabel.getFont();
    titleLabel.setFont(font.deriveFont(font.getSize() * 1.4f));
    titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    titlePanel.add(titleLabel);

    JLabel emptyLabel = new JLabel();
    emptyLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
    titlePanel.add(emptyLabel, BorderLayout.SOUTH);

    JSplitPane splitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideScrollPane, mainScrollPane);
    getContentPanel().add(splitPane);

    sideBar.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        int idx = sideBar.getSelectedIndex();
        if (idx < 0) {
          sideBar.setSelectedIndex(0);
        } else {
          showPage(sideBar.getModel().getElementAt(idx));
        }
      }
    });

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        invokeFocusLost();
      }
    });

    sideBar.setSelectedIndex(0);
  }

  private void showPage(PrefPage prefPage) {
    try {
      titleLabel.setText(prefPage.getTitle());
      if (currentPrefPanel != null) {
        // invoke focus lost before removing
        invokeFocusLost();

        mainPanel.remove(currentPrefPanel);
      }
      JPanel panel = prefPage.getPrefCompClass().newInstance();
      mainPanel.add(panel, BorderLayout.CENTER);
      currentPrefPanel = panel;
      revalidate();
      repaint();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new UnexpectedException(e);
    }
  }

  private void invokeFocusLost() {
    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    if (focusOwner == null) {
      return;
    }
    PrefUtils.invokeFocusLost(focusOwner);
  }

  @Override
  protected JPanel createButtonPanel(DialogOperationAction[] actions) {
    return null;
  }
}
