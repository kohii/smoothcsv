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
package command.grid;

import javax.swing.JComponent;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellStringEditor.CsvGridEditorComponent;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetCellStringEditor.GridTableTextField;

/**
 * @author kohii
 *
 */
public class StartEditCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheet) {
    run(gridSheet, true);
  }

  public void run(CsvGridSheetPane gridSheet, boolean editorCompGrabsFocus) {
    if (!gridSheet.isEditing()) {
      int anchorRow = gridSheet.getSelectionModel().getRowAnchorIndex();
      int anchorColumn = gridSheet.getSelectionModel().getColumnAnchorIndex();

      if (anchorRow != -1 && anchorColumn != -1 && !gridSheet.isEditing()) {
        if (!gridSheet.getTable().editCellAt(anchorRow, anchorColumn, null, editorCompGrabsFocus)) {
          return;
        }
      }
    }

    if (gridSheet.isEditing()) {
      JComponent editorComp = gridSheet.getTable().getCellEditor().getEditorComponent();
      if (editorComp instanceof GridTableTextField) {
        ((CsvGridEditorComponent) editorComp).setQuickEdit(false);
      }
      if (editorCompGrabsFocus) {
        editorComp.requestFocusInWindow();
      }
    }
  }
}
