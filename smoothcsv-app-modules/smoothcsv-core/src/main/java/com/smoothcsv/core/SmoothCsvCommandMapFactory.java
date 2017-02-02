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
package com.smoothcsv.core;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import com.smoothcsv.core.csvsheet.CsvGridSheetCellStringEditor.CsvGridEditorComponent;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel.ValuePanelTextArea;
import com.smoothcsv.core.macro.Macro;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.core.macro.SCAppMacroRuntime;
import com.smoothcsv.framework.component.support.CommandActionMap;
import com.smoothcsv.framework.component.support.DefaultCommandMapFactory;
import com.smoothcsv.framework.component.support.SmoothComponent;

/**
 * @author kohii
 */
public class SmoothCsvCommandMapFactory extends DefaultCommandMapFactory {

  @Override
  public CommandActionMap createActionMap(SmoothComponent component) {
    return new SmoothCsvCommandActionMap();
  }

  @SuppressWarnings("serial")
  static class SmoothCsvCommandActionMap extends CommandActionMap {
    private Action keyTypeAction = new KeyTypedAction();

    @Override
    public Action get(Object key) {
      if (key instanceof String) {
        String keyString = (String) key;
        if (keyString.startsWith("macro:Run ")) {
          return new MacroAction(keyString.substring("macro:Run ".length()).trim());
        }
      }
      if (DefaultEditorKit.defaultKeyTypedAction.equals(key)) {
        return keyTypeAction;
      }
      return super.get(key);
    }

    @Override
    protected Action createActionFromCommand(String[] commandIds) {
      return new CommandWrapperAction(commandIds) {
        @Override
        protected void executeCommand(String commandId) {
          super.executeCommand(commandId);
          if (MacroRecorder.isRecording()) {
            MacroRecorder.getInstance().recordCommand(commandId);
          }
        }
      };
    }
  }

  private static class MacroAction implements Action {

    final String path;

    public MacroAction(String path) {
      this.path = path;
    }

    @Override
    public Object getValue(String key) {
      return null;
    }

    @Override
    public void putValue(String key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setEnabled(boolean b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      File macroFile = new File(path);
      SCAppMacroRuntime.getMacroRuntime().execute(new Macro(macroFile));
      if (MacroRecorder.isRecording()) {
        MacroRecorder.getInstance().recordMacroExecution(path);
      }
    }
  }

  @SuppressWarnings("serial")
  private static class KeyTypedAction
      extends javax.swing.text.DefaultEditorKit.DefaultKeyTypedAction {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (MacroRecorder.isRecording()) {
        JTextComponent comp = getTextComponent(e);
        if (comp instanceof CsvGridEditorComponent) {
          CsvGridEditorComponent textComp = (CsvGridEditorComponent) comp;
          try {
            textComp.setKeyRecording(true);
            super.actionPerformed(e);
          } finally {
            textComp.setKeyRecording(false);
          }
          return;
        } else if (comp instanceof ValuePanelTextArea) {
          ValuePanelTextArea textComp = (ValuePanelTextArea) comp;
          try {
            textComp.setKeyRecording(true);
            super.actionPerformed(e);
          } finally {
            textComp.setKeyRecording(false);
          }
          return;
        }
      }
      super.actionPerformed(e);
    }
  }
}
