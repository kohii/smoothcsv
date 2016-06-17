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
package command.cell_editor;

import com.smoothcsv.core.command.VisibleComponentCommandBase;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellStringEditor.CsvGridEditorComponent;

import javax.swing.text.BadLocationException;

/**
 * @author kohii
 */
public class DeleteToBeginningOfLineCommand
    extends VisibleComponentCommandBase<CsvGridEditorComponent> {

  public DeleteToBeginningOfLineCommand() {
    super("cell-editor");
  }

  @Override
  public void run(CsvGridEditorComponent component) {
    int start = component.getSelectionStart();
    int end = component.getSelectionEnd();
    try {
      start = component.getLineStartOffset(component.getLineOfOffset(start));
    } catch (BadLocationException ignore) {
    }
    if (start == end) {
      if (start > 0) {
        component.replaceRange("", start - 1, end);
      }
    } else {
      component.replaceRange("", start, end);
    }
  }
}
