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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

import lombok.Getter;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.macro.ConsoleInputHandler;
import com.smoothcsv.core.macro.component.SimpleTabbedPane.TabChangeEvent;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.command.CommandActionListener;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.swing.components.AwesomeIconButton;
import com.smoothcsv.swing.components.AwesomeIconToggleButton;
import com.smoothcsv.swing.components.ExSplitPane;
import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.icon.AwesomeIconConstants;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class MacroToolsPanel extends JPanel implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport =
      new SmoothComponentSupport(this, "macro-tools");

  private SimpleTabbedPane tabbedPane;

  @Getter
  private MacroEditor macroEditor;
  @Getter
  private MacroListPanel macroList;
  @Getter
  private MacroConsolePanel consolePanel;

  @Getter
  private boolean consoleAlwaysVisible = false;
  private boolean consoleVisible = false;

  private JSplitPane splitPane;

  private JToggleButton toggleConsoleAlwaysVisibleBtn;

  public MacroToolsPanel() {
    setLayout(new BorderLayout(0, 0));
    setBorder(null);

    tabbedPane = new SimpleTabbedPane();
    add(tabbedPane, BorderLayout.CENTER);

    JPanel headerPanel = new JPanel();
    headerPanel.setBorder(
        BorderFactory.createMatteBorder(1, 0, 1, 0, UIConstants.getDefaultBorderColor()));
    add(headerPanel, BorderLayout.NORTH);
    GridBagLayout gbl_headerPanel = new GridBagLayout();
    gbl_headerPanel.columnWidths = new int[] {0, 0, 0, 0};
    gbl_headerPanel.rowHeights = new int[] {0, 0};
    gbl_headerPanel.columnWeights = new double[] {1.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl_headerPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    headerPanel.setLayout(gbl_headerPanel);

    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    headerPanel.add(tabbedPane.getTabLabelsPanel(), gbc_lblNewLabel);

    toggleConsoleAlwaysVisibleBtn = new AwesomeIconToggleButton(AwesomeIconConstants.FA_TERMINAL);
    toggleConsoleAlwaysVisibleBtn.setToolTipText(CoreBundle.get("key.macro.toggleConsole"));
    toggleConsoleAlwaysVisibleBtn
        .addActionListener(new CommandActionListener("macro:toggleConsole"));
    GridBagConstraints gbc_cosoleBtn = new GridBagConstraints();
    gbc_cosoleBtn.insets = new Insets(0, 5, 0, 15);
    gbc_cosoleBtn.anchor = GridBagConstraints.EAST;
    gbc_cosoleBtn.gridx = 1;
    gbc_cosoleBtn.gridy = 0;
    headerPanel.add(toggleConsoleAlwaysVisibleBtn, gbc_cosoleBtn);

    JButton btnClose =
        new AwesomeIconButton(AwesomeIcon.create(AwesomeIconConstants.FA_TIMES_CIRCLE));
    btnClose.setFocusable(false);
    btnClose.setToolTipText(CoreBundle.get("key.close"));
    btnClose.setBorder(null);
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand("macro:toggleMacroTools");
      }
    });
    GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
    gbc_btnNewButton.gridx = 2;
    gbc_btnNewButton.gridy = 0;
    headerPanel.add(btnClose, gbc_btnNewButton);

    macroEditor = new MacroEditor();
    tabbedPane.add(CoreBundle.get("key.macroEditor"), macroEditor, false);

    macroList = new MacroListPanel();
    tabbedPane.add(CoreBundle.get("key.macroList"), macroList, false);

    consolePanel = new MacroConsolePanel();
    consolePanel.setInputHandler(new ConsoleInputHandler());
    tabbedPane.add(CoreBundle.get("key.macroConsole"), consolePanel, false);

    tabbedPane.listeners().on(SimpleTabbedPane.TabChangeEvent.class,
        new SCListener<SimpleTabbedPane.TabChangeEvent>() {
          @Override
          public void call(TabChangeEvent event) {
            if (consoleAlwaysVisible) {
              if (event.getNewTabComponent() == consolePanel) {
                setConsoleVisible(false);
              } else {
                if (event.getOldTabComponent() == consolePanel) {
                  setConsoleVisible(true);
                }
              }
            }
          }
        });
  }

  public void setConsoleAlwaysVisible(boolean b) {
    toggleConsoleAlwaysVisibleBtn.setSelected(b);
    if (b == consoleAlwaysVisible) {
      return;
    }
    this.consoleAlwaysVisible = b;

    if (tabbedPane.getSelectedTabComponent() == consolePanel) {
      // do nothing
    } else {
      setConsoleVisible(b);
    }
  }

  private void setConsoleVisible(boolean b) {
    if (b == consoleVisible) {
      return;
    }

    if (b) {
      remove(tabbedPane);
      splitPane = new ExSplitPane();
      splitPane.setBackground(UIConstants.getDefaultBorderColor());
      splitPane.setResizeWeight(0.5);
      splitPane.setLeftComponent(tabbedPane);
      splitPane.setRightComponent(consolePanel);
      add(splitPane, BorderLayout.CENTER);
    } else {
      splitPane.removeAll();
      remove(splitPane);
      splitPane = null;
      add(tabbedPane, BorderLayout.CENTER);
    }
    tabbedPane.getSelectedTabComponent().requestFocusInWindow();
    revalidate();
    repaint();
    this.consoleVisible = b;
  }

  @Override
  public boolean requestFocusInWindow() {
    return tabbedPane.requestFocusInWindow();
  }

  public JPanel getSelectedTabComponent() {
    return tabbedPane.getSelectedTabComponent();
  }

  public void setSelectedTabComponent(JPanel comp) {
    tabbedPane.showTab(comp);
  }
}
