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
package com.smoothcsv.core.csvsheet;

import com.smoothcsv.core.celleditor.SCTextArea;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.swing.gridsheet.GridSheetCellStringEditor;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

/**
 * @author kohii
 */
public class CsvGridSheetCellStringEditor extends GridSheetCellStringEditor {

  private static final long serialVersionUID = -5621709429755504992L;

  /**
   * @param gridTable
   */
  public CsvGridSheetCellStringEditor(GridSheetTable gridTable) {
    super(gridTable);
  }

  @Override
  public boolean prepare(GridSheetTable table, Object value, boolean isSelected, int row,
                         int column) {
    CsvGridSheetCellValuePanel.getInstance().getUndoManager().discardAllEdits();
    return true;
  }

  @Override
  protected CsvGridEditorComponent createTextComponent() {
    return new CsvGridEditorComponent(this);
  }

  @SuppressWarnings("serial")
  public static class CsvGridEditorComponent extends SCTextArea {

    private boolean quickEdit = false;

    @Getter
    @Setter
    private boolean keyRecording;

    protected CsvGridEditorComponent(CsvGridSheetCellStringEditor editor) {
      super("cell-editor");
      setDocument(CsvGridSheetCellValuePanel.getInstance().getTextArea().getDocument());
      setFont(SCAppearanceManager.getInlineCelleditorFont());
    }

    /**
     * @return the quickEdit
     */
    public boolean isQuickEdit() {
      return quickEdit;
    }

    /**
     * @param quickEdit the quickEdit to set
     */
    public void setQuickEdit(boolean quickEdit) {
      this.quickEdit = quickEdit;
      if (quickEdit) {
        addPseudoClass("quick-edit");
      } else {
        removePseudoClass("quick-edit");
      }
    }

    @Override
    public void replaceSelection(String content) {
      super.replaceSelection(content);
      if (keyRecording) {
        MacroRecorder.getInstance().recordKeyTyping(content);
      }
    }

    @Override
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
      return super.processKeyBinding(ks, e, condition, pressed);
    }

    @Override
    protected void processInputMethodEvent(InputMethodEvent e) {
      super.processInputMethodEvent(e);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#requestFocus()
     */
    @Override
    public void requestFocus() {
      // TODO Auto-generated method stub
      super.requestFocus();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#requestFocus(boolean)
     */
    @Override
    public boolean requestFocus(boolean temporary) {
      // TODO Auto-generated method stub
      return super.requestFocus(temporary);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#requestFocusInWindow(boolean)
     */
    @Override
    protected boolean requestFocusInWindow(boolean temporary) {
      // TODO Auto-generated method stub
      return super.requestFocusInWindow(temporary);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#requestFocusInWindow()
     */
    @Override
    public boolean requestFocusInWindow() {
      // TODO Auto-generated method stub
      return super.requestFocusInWindow();
    }
  }
}
