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
package com.smoothcsv.swing.gridsheet;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

import com.smoothcsv.swing.components.LineBreakableTextField;
import lombok.Getter;

@SuppressWarnings("serial")
public class GridSheetCellStringEditor extends AbstractCellEditor implements GridSheetCellEditor {

  @Getter
  protected GridSheetTable table;
  private JTextComponent editorComponent;
  private JScrollPane scrollPane;

  protected int clickCountToStart = 2;

  public GridSheetCellStringEditor(GridSheetTable gridTable) {
    this.table = gridTable;
    editorComponent = createTextComponent();
    scrollPane =
        new JScrollPane(editorComponent, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    editorComponent.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
  }

  @Override
  public boolean prepare(GridSheetTable table, Object value, boolean isSelected, int row, int column) {
    editorComponent.setText(value == null ? "" : value.toString());
    return true;
  }

  @Override
  public String getCellEditorValue() {
    return editorComponent.getText();
  }

  @Override
  public JComponent getEditorComponent() {
    return editorComponent;
  }

  @Override
  public JComponent getOuterEditorComponent() {
    return scrollPane;
  }

  @Override
  public boolean isCellEditable(EventObject e) {
    if (e instanceof MouseEvent) {
      return ((MouseEvent) e).getClickCount() >= clickCountToStart;
    }
    return true;
  }

  protected JTextComponent createTextComponent() {
    return new GridTableTextField();
  }

  public static class GridTableTextField extends LineBreakableTextField {

    public GridTableTextField() {
      super(false);
    }
  }
}
