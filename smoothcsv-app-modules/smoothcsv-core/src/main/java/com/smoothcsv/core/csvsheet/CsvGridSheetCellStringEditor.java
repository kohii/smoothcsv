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
package com.smoothcsv.core.csvsheet;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import lombok.Getter;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.GridSheetCellStringEditor;
import com.smoothcsv.swing.gridsheet.GridSheetTable;

/**
 * @author kohii
 *
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
  public boolean prepare(GridSheetTable table, Object value, boolean isSelected, int row, int column) {
    boolean b = super.prepare(table, value, isSelected, row, column);
    CsvGridSheetCellValuePanel.getInstance().getUndoManager().discardAllEdits();
    return b;
  }

  @Override
  protected GridTableTextField createTextComponent() {
    return new CsvGridEditorComponent(this);
  }

  @SuppressWarnings("serial")
  public static class CsvGridEditorComponent extends GridTableTextField implements SmoothComponent {

    private boolean quickEdit = false;

    @Getter
    private final SmoothComponentSupport componentSupport = new SmoothComponentSupport(this,
        "cell-editor");

    /**
     * @param table
     */
    protected CsvGridEditorComponent(GridSheetCellStringEditor editor) {
      super(editor);

      setDocument(CsvGridSheetCellValuePanel.getInstance().getTextArea().getDocument());
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
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
      return super.processKeyBinding(ks, e, condition, pressed);
    }

    @Override
    protected void processInputMethodEvent(InputMethodEvent e) {
      super.processInputMethodEvent(e);
    }
  }
}
