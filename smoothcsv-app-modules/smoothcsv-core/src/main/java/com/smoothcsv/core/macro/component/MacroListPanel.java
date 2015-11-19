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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import lombok.Getter;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.macro.MacroInfo;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.exception.AbortionException;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.ArrayCsvWriter;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.framework.util.DirectoryResolver;
import com.smoothcsv.swing.icon.AwesomeIconConstants;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class MacroListPanel extends JPanel implements SmoothComponent {

  @Getter
  private SmoothComponentSupport componentSupport = new SmoothComponentSupport(this, "macro-list");

  private File confFile =
      new File(DirectoryResolver.instance().getSettingDirectory(), "macros.tsv");
  private List<MacroInfo> macroInfoList = new ArrayList<>();

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
    toolBar.add("macrolist:add", AwesomeIconConstants.FA_PLUS, CoreBundle.get("key.macroList.add")
        + "...");
    toolBar.add("macrolist:remove", AwesomeIconConstants.FA_MINUS,
        CoreBundle.get("key.macroList.remove"));
    toolBar.add("macrolist:run", AwesomeIconConstants.FA_PLAY, CoreBundle.get("key.macroList.run"));

    // toolBar.add("macrolist:addMacro", AwesomeIconConstants.FA_PLUS, "Add a new macro");
    // toolBar.add("macrolist:removeMacro", AwesomeIconConstants.FA_MINUS,
    // "Remove the selected macro");
    // toolBar.add("macrolist:selectNextMacro", AwesomeIconConstants.FA_ANGLE_DOWN,
    // "Select the next macro");
    // toolBar.add("macrolist:selectPrevMacro", AwesomeIconConstants.FA_PLUS,
    // "Select the previous macro");
    load();
  }

  private void addKeyAction(KeyStroke keyStroke, Action action) {
    Object key = action.getValue(Action.NAME);
    getActionMap().put(key, action);
    getInputMap().put(keyStroke, key);
  }

  private void sortList() {
    Collections.sort(macroInfoList);
  }

  private void render() {
    listBodyPanel.removeAll();
    for (MacroInfo macroInfo : macroInfoList) {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridwidth = GridBagConstraints.REMAINDER;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      MacroListItemPanel itemPanel = new MacroListItemPanel(macroInfo);
      itemPanel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          MacroListItemPanel item = (MacroListItemPanel) e.getComponent();
          int index = macroInfoList.indexOf(item.getMacroInfo());
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

    if (macroInfoList.size() > 0) {
      select(Math.max(selected, 0));
    }
    repaint();
  }

  public void selectNext(int direction) {
    if ((direction < 0 && selected <= 0) || (direction > 0 && macroInfoList.size() - 1 <= selected)) {
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
      ((MacroListItemPanel) comp).setSelected(true);;
    }
    selected = index;

    listBodyPanel.scrollRectToVisible(comp.getBounds());
  }

  // private void evaluateCommandsEnabled() {
  // ((ManualCondition) CommandRepository.instance().getDef("macrolist:remove").getEnableWhen())
  // .setValue2(selected >= 0);
  // ((ManualCondition) CommandRepository.instance().getDef("macrolist:run").getEnableWhen())
  // .setValue2(selected >= 0);
  // }

  public File getSelectedMacroFile() {
    if (selected < 0) {
      return null;
    }
    return macroInfoList.get(selected).getFile();
  }

  @Override
  public void scrollRectToVisible(Rectangle aRect) {
    super.scrollRectToVisible(aRect);
  }

  public void addMacroFiles(File... files) {
    for (File file : files) {
      if (!file.exists() || !file.isFile() || !file.canRead()) {
        throw new AppException("WSCC0001", file);
      }
      boolean alreadyExists = false;
      for (MacroInfo mi : macroInfoList) {
        if (mi.getFile().equals(file)) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        macroInfoList.add(new MacroInfo(file));
      }
    }
    sortList();
    render();
    save();
  }

  public void removeSelectedMacro() {
    if (selected < 0) {
      throw new AbortionException();
    }
    macroInfoList.remove(selected);
    if (macroInfoList.isEmpty()) {
      selected = -1;
    } else {
      selected = Math.min(selected, macroInfoList.size() - 1);
    }
    render();
    save();
  }

  public void save() {
    FileUtils.ensureWritable(confFile);
    try (OutputStream os = new FileOutputStream(confFile);
        ArrayCsvWriter writer =
            new ArrayCsvWriter(new OutputStreamWriter(os, "UTF-8"), CsvSupport.TSV_PROPERTIES)) {
      for (MacroInfo macroInfo : macroInfoList) {
        writer.writeRow(new String[] {macroInfo.getFilePath()});
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public void load() {
    macroInfoList.clear();
    if (confFile.exists()) {
      try (InputStream in = new FileInputStream(confFile);
          ArrayCsvReader reader =
              new ArrayCsvReader(new InputStreamReader(in, "UTF-8"), CsvSupport.TSV_PROPERTIES,
                  CsvSupport.SKIP_EMPTYROW_OPTION, 2)) {
        String[] rowData;
        while ((rowData = reader.readRow()) != null) {
          macroInfoList.add(new MacroInfo(rowData[0]));
        }
        sortList();
      } catch (IOException e) {
        throw new UnexpectedException(e);
      }
    }
    render();
  }
}
