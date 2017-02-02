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
package com.smoothcsv.core.macro.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.macro.MacroInfo;
import com.smoothcsv.core.macro.UserDefinedMacroList;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.exception.AbortionException;
import com.smoothcsv.swing.icon.AwesomeIconConstants;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class MacroListPanel extends JPanel implements SmoothComponent {

  @Getter
  private SmoothComponentSupport componentSupport = new SmoothComponentSupport(this, "macro-list");

  private int selected = -1;
  private JPanel listBodyPanel;
  private AbstractAction selectNextAction;
  private AbstractAction selectPrevAction;

  public MacroListPanel() {
    setLayout(new BorderLayout(0, 0));
    setBorder(null);
    setFocusable(true);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);

    listBodyPanel = new JPanel();
    listBodyPanel.setBackground(Color.WHITE);
    listBodyPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
        UIConstants.getDefaultBorderColor()));
    listBodyPanel.setLayout(new GridBagLayout());
    scrollPane.setViewportView(listBodyPanel);

    SCToolBar toolBar = new SCToolBar();
    add(toolBar, BorderLayout.NORTH);

    // toolBar.add("macromanager:addMacro", AwesomeIconConstants.FA_PLUS, "Add A New Item");
    // toolBar.add("macromanager:removeMacro", AwesomeIconConstants.FA_MINUS,
    // "Remove The Selected Item");
    // toolBar.add("macromanager:removeMacro", AwesomeIconConstants.FA_ANGLE_UP,
    // "Move The Selected Item To Above");
    // toolBar.add("macromanager:removeMacro", AwesomeIconConstants.FA_ANGLE_DOWN,
    // "Move The Selected Item To Below");

    // Actions ////////////////////////////////

    selectNextAction = new AbstractAction("select-next") {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectNext(+1);
      }
    };
    selectPrevAction = new AbstractAction("select-prev") {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectNext(-1);
      }
    };
    addKeyAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), selectNextAction);
    addKeyAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), selectPrevAction);
    toolBar.add("macrolist:Add", AwesomeIconConstants.FA_PLUS, CoreBundle.get("key.macroList.add")
        + "...");
    toolBar.add("macrolist:Remove", AwesomeIconConstants.FA_MINUS,
        CoreBundle.get("key.macroList.remove"));
    toolBar.add("macrolist:Run", AwesomeIconConstants.FA_PLAY, CoreBundle.get("key.macroList.run"));

    // toolBar.add("macrolist:AddMacro", AwesomeIconConstants.FA_PLUS, "Add a new macro");
    // toolBar.add("macrolist:RemoveMacro", AwesomeIconConstants.FA_MINUS,
    // "Remove the selected macro");
    // toolBar.add("macrolist:SelectNextMacro", AwesomeIconConstants.FA_ANGLE_DOWN,
    // "Select the next macro");
    // toolBar.add("macrolist:SelectPrevMacro", AwesomeIconConstants.FA_PLUS,
    // "Select the previous macro");
    render();
  }

  private void addKeyAction(KeyStroke keyStroke, Action action) {
    Object key = action.getValue(Action.NAME);
    getActionMap().put(key, action);
    getInputMap().put(keyStroke, key);
  }

  private void render() {
    List<MacroInfo> macros = UserDefinedMacroList.getInstance().getMacroInfoList();
    listBodyPanel.removeAll();
    for (MacroInfo macroInfo : macros) {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      MacroListItemPanel itemPanel = new MacroListItemPanel(macroInfo);
      itemPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          MacroListItemPanel item = (MacroListItemPanel) e.getComponent();
          int index = macros.indexOf(item.getMacroInfo());
          if (selected == index) {
            return;
          }
          select(index);
        }
      });
      listBodyPanel.add(itemPanel, gbc);
    }

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.weightx = 1;
    gbc.weighty = 1;
    listBodyPanel.add(new JLabel(), gbc);

    if (macros.size() > 0) {
      select(Math.max(selected, 0));
    }
    repaint();
  }

  public void selectNext(int direction) {
    if ((direction < 0 && selected <= 0) || (direction > 0
        && UserDefinedMacroList.getInstance().getMacroInfoList().size() - 1 <= selected)) {
      throw new AbortionException();
    }
    select(selected + direction);
  }

  private void select(int index) {
    requestFocusInWindow();
    if (selected >= 0) {
      Component prevSelectedItem = listBodyPanel.getComponent(selected);
      ((MacroListItemPanel) prevSelectedItem).setSelected(false);
    }
    Component comp = listBodyPanel.getComponent(index);
    if (comp instanceof MacroListItemPanel) {
      ((MacroListItemPanel) comp).setSelected(true);
      ;
    }
    selected = index;

    listBodyPanel.scrollRectToVisible(comp.getBounds());
  }

  // private void evaluateCommandsEnabled() {
  // ((ManualCondition) CommandRepository.instance().getDef("macrolist:Remove").getEnableWhen())
  // .setValue2(selected >= 0);
  // ((ManualCondition) CommandRepository.instance().getDef("macrolist:Run").getEnableWhen())
  // .setValue2(selected >= 0);
  // }

  public File getSelectedMacroFile() {
    if (selected < 0) {
      return null;
    }
    return UserDefinedMacroList.getInstance().getMacroInfoList().get(selected).getFile();
  }

  @Override
  public void scrollRectToVisible(Rectangle aRect) {
    super.scrollRectToVisible(aRect);
  }

  public void addMacroFiles(File... files) {
    UserDefinedMacroList.getInstance().add(files);
    render();
  }

  public void removeSelectedMacro() {
    if (selected < 0) {
      throw new AbortionException();
    }
    UserDefinedMacroList userDefinedMacroList = UserDefinedMacroList.getInstance();
    userDefinedMacroList.remove(selected);
    if (userDefinedMacroList.getMacroInfoList().isEmpty()) {
      selected = -1;
    } else {
      selected = Math.min(selected, userDefinedMacroList.getMacroInfoList().size() - 1);
    }
    render();
  }
}
