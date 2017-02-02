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
package com.smoothcsv.framework.component.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.smoothcsv.framework.SCApplication;

public class BasicFileChooser extends JFileChooser {

  private static final long serialVersionUID = -5242698313464258128L;

  private Object[] actions;

  private List<Function<File, Boolean>> approveSelectionListeners;

  public BasicFileChooser() {
    super();
    init();
  }

  public BasicFileChooser(File currentDirectory) {
    super(currentDirectory);
    init();
  }

  public BasicFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
    init();
  }

  private void init() {
    approveSelectionListeners = new ArrayList<>(3);
    try {
      customizeAction();
      searchAndClick(this, UIManager.getIcon("FileChooser.detailsViewIcon"));
    } finally {
      restoreAction();
    }
  }

  @Override
  public void approveSelection() {
    if (getDialogType() == SAVE_DIALOG) {
      setSelectedFile(getFileToSave());
      File file = getSelectedFile();

      if (file.exists()) {
        java.awt.Toolkit.getDefaultToolkit().beep();
        if (!MessageDialogs.confirm("WSCC0004", file.getName())) {
          return;
        } else if (!file.canWrite()) {
          MessageDialogs.alert("WSCC0005");
          return;
        }
      }
    }

    for (Function<File, Boolean> f : approveSelectionListeners) {
      if (!f.apply(getSelectedFile())) {
        return;
      }
    }

    super.approveSelection();
  }

  @Override
  public int showSaveDialog(Component parent) throws HeadlessException {
    try {
      customizeAction();
      File f = getCurrentDirectory();
      int ret = super.showSaveDialog(parent);
      if (ret != APPROVE_OPTION) {
        setCurrentDirectory(f);
      }
      return ret;
    } catch (Exception e) {
      return ERROR_OPTION;
    } finally {
      restoreAction();
    }
  }

  public int showSaveDialog() throws HeadlessException {
    return showSaveDialog(SCApplication.components().getFrame());
  }

  @Override
  public int showOpenDialog(Component parent) throws HeadlessException {
    try {
      customizeAction();
      File f = getCurrentDirectory();
      int ret = super.showOpenDialog(parent);
      if (ret != APPROVE_OPTION) {
        setCurrentDirectory(f);
      }
      return ret;
    } catch (Exception e) {
      return ERROR_OPTION;
    } finally {
      restoreAction();
    }
  }

  private void customizeAction() {
    ActionMap actionmap = SwingUtilities.getUIActionMap(new JTable());

    actions = new Object[4];
    actions[0] = new Object[]{"selectNextRowCell", actionmap.get("selectNextRowCell")};
    actions[1] = new Object[]{"selectPreviousRowCell", actionmap.get("selectPreviousRowCell")};
    actions[2] = new Object[]{"selectNextColumnCell", actionmap.get("selectNextColumnCell")};
    actions[3] = new Object[]{"selectPreviousColumnCell", actionmap.get("selectPreviousColumnCell")};

    actionmap.remove("selectNextRowCell");
    actionmap.remove("selectPreviousRowCell");
    actionmap.remove("selectNextColumnCell");
    actionmap.remove("selectPreviousColumnCell");
  }

  public int showOpenDialog() throws HeadlessException {
    return showOpenDialog(SCApplication.components().getFrame());
  }

  private void restoreAction() {
    ActionMap actionmap = SwingUtilities.getUIActionMap(new JTable());
    for (Object obj : actions) {
      Object[] oa = (Object[]) obj;
      if (oa[1] != null) {
        actionmap.put(oa[0], (Action) oa[1]);
      }
    }
    actions = null;
  }

  private File getFileToSave() {
    File dir = getCurrentDirectory();
    File file = getSelectedFile();

    FileFilter filter = getFileFilter();
    if (filter != null && !filter.equals(getAcceptAllFileFilter())
        && filter instanceof FileNameExtensionFilter) {
      if (file != null) {
        String name = file.getName();
        String ext = ((FileNameExtensionFilter) filter).getExtensions()[0];
        if (name.indexOf('.') < 0) {
          return new File(dir, name + '.' + ext);
        } else if (name.endsWith(".")) {
          return new File(dir, name + ext);
        }
      }
    }
    return file;
  }

  public void addOnApproveSelection(Function<File, Boolean> callback) {
    this.approveSelectionListeners.add(callback);
  }

  private static boolean searchAndClick(Container parent, Icon icon) {
    for (Component c : parent.getComponents()) {
      if (c instanceof JToggleButton && ((JToggleButton) c).getIcon() == icon) {
        ActionListener[] actions = ((AbstractButton) c).getActionListeners();
        for (ActionListener actionListener : actions) {
          if (actionListener instanceof Action) {
            Action action = (Action) actionListener;
            KeyStroke ks = KeyStroke.getKeyStroke(10, 0, false);
            KeyEvent event =
                new KeyEvent((AbstractButton) c, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                    0, 10, KeyEvent.CHAR_UNDEFINED);
            SwingUtilities.notifyAction(action, ks, event, (AbstractButton) c, 0);
          }
        }
        // ((AbstractButton)c).doClick();
        return true;
      } else {
        if (searchAndClick((Container) c, icon)) {
          return true;
        }
      }
    }
    return false;
  }

}
