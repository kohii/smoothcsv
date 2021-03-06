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
package command.value_panel;

import javax.swing.text.BadLocationException;

import com.smoothcsv.core.command.ValuePanelCommandBase;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel;

/**
 * @author kohii
 */
public class DeleteToBeginningOfLineCommand extends ValuePanelCommandBase {

  @Override
  public void run(CsvGridSheetCellValuePanel valuePanel) {
    CsvGridSheetCellValuePanel.ValuePanelTextArea component = valuePanel.getTextArea();
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
