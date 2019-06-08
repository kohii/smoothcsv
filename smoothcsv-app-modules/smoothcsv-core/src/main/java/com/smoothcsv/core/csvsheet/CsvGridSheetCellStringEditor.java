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

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.smoothcsv.core.celleditor.SCTextArea;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.swing.gridsheet.GridSheetCellStringEditor;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.mutable.MutableBoolean;

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
    ((CsvGridEditorComponent) getEditorComponent()).setIgnoreNextKeyEvent(false);
    return super.prepare(table, value, isSelected, row, column);
  }

  @Override
  protected CsvGridEditorComponent createTextComponent() {
    return new CsvGridEditorComponent(this);
  }

  @SuppressWarnings("serial")
  public static class CsvGridEditorComponent extends SCTextArea {

    private boolean quickEdit = false;

    /**
     * HACK: We want to ignore key events after InputMethodEvent handled by GridSheetTable
     * in order to avoid inserting unnecessary characters when using Live Conversion.
     */
    private boolean ignoreNextKeyEvent = false;

    @Getter
    @Setter
    private boolean keyRecording;

    protected CsvGridEditorComponent(CsvGridSheetCellStringEditor editor) {
      super("cell-editor");
      final MutableBoolean syncing = new MutableBoolean(false);
      final CsvGridSheetCellValuePanel.ValuePanelTextArea textArea = CsvGridSheetCellValuePanel.getInstance().getTextArea();
      getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          if (!syncing.booleanValue() && editor.getTable().isEditing()) {
            syncing.setValue(true);
            textArea.setText(getText());
            syncing.setValue(false);
          }
        }
      });
      textArea.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          if (!syncing.booleanValue() && editor.getTable().isEditing()) {
            syncing.setValue(true);
            setText(textArea.getText());
            syncing.setValue(false);
          }
        }
      });
      setFont(SCAppearanceManager.getInlineCelleditorFont());
    }

    void ignoreNextKeyEvent() {
      setIgnoreNextKeyEvent(true);
    }

    void setIgnoreNextKeyEvent(boolean ignoreNextKeyEvent) {
      this.ignoreNextKeyEvent = ignoreNextKeyEvent;
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
    protected void processKeyEvent(KeyEvent e) {
      if (ignoreNextKeyEvent) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            setIgnoreNextKeyEvent(false);
          }
        });
      } else {
        super.processKeyEvent(e);
      }
    }

    @Override
    protected void processInputMethodEvent(InputMethodEvent e) {
      super.processInputMethodEvent(e);
    }
  }
}
