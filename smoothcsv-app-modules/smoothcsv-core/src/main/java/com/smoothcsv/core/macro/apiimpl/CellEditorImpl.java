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
package com.smoothcsv.core.macro.apiimpl;

import javax.swing.text.JTextComponent;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.macro.api.CellEditor;
import com.smoothcsv.core.macro.api.CsvSheet;
import com.smoothcsv.swing.gridsheet.GridSheetCellEditor;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 *
 * @author kohii
 *
 */
public class CellEditorImpl implements CellEditor {

  private CsvSheet parent;

  CellEditorImpl(CsvSheet csvSheet) {
    this.parent = csvSheet;
  }

  @Override
  public boolean type(String text) {
    CsvSheetView csvSheetView = ((CsvSheetImpl) parent).getCsvSheetView();
    if (!csvSheetView.getGridSheetPane().isEditing()) {
      SwingUtils.beep();
      return false;
    }
    GridSheetCellEditor editor = csvSheetView.getGridSheetPane().getTable().getCellEditor();
    ((JTextComponent) editor.getEditorComponent()).replaceSelection(text);
    return true;
  }
}
