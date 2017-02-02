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
package command.grid;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.CsvSheetTextPaneConfig;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.KeyStrokeUtils;
import com.smoothcsv.swing.components.text.EditorPanel;
import com.smoothcsv.swing.components.text.ExTextPane;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
public class EditSelectedRangeAsTextCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    String text = CopyCommand.copy(gridSheetPane, gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta());
    EditorDialog dialog = new EditorDialog(text);
    dialog.setSize(600, 500);
    if (dialog.showDialog() != DialogOperation.OK) {
      return;
    }
    PasteCommand.paste(gridSheetPane,
        dialog.getText(),
        gridSheetPane.getCsvSheetView().getViewInfo().getCsvMeta(),
        false);
  }

  private static class EditorDialog extends DialogBase {

    private final ExTextPane textArea;

    public EditorDialog(String text) {
      super(SCApplication.components().getFrame(), "SmoothCSV");

      textArea = new ExTextPane(CsvSheetTextPaneConfig.getInstance());
      textArea.setText(text);

      if (Env.getOS() == Env.OS_MAC) {
        customizeActionsForMac(textArea);
      }
      SwingUtils.installUndoManager(textArea);

      EditorPanel editorPanel = new EditorPanel(textArea);

      editorPanel.getScrollPane().setHorizontalScrollBarPolicy(
          CsvSheetTextPaneConfig.getInstance().isWordWrap()
              ? JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
              : JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
      );

      getContentPanel().setBorder(null);
      getContentPanel().add(editorPanel);
    }

    private void customizeActionsForMac(ExTextPane textArea) {
      textArea.getInputMap().put(KeyStrokeUtils.parse("cmd+backspace"), "DeleteToBeginningOfLine");
      textArea.getActionMap().put("DeleteToBeginningOfLine", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          int start = textArea.getSelectionStart();
          int end = textArea.getSelectionEnd();
          try {
            start = textArea.getLineStartOffset(textArea.getLineOfOffset(start));
          } catch (BadLocationException ignore) {
          }
          if (start == end) {
            if (start > 0) {
              textArea.replaceRange("", start - 1, end);
            }
          } else {
            textArea.replaceRange("", start, end);
          }
        }
      });
      textArea.getInputMap().put(KeyStrokeUtils.parse("cmd+delete"), "DeleteToEndOfLine");
      textArea.getActionMap().put("DeleteToEndOfLine", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          int start = textArea.getSelectionStart();
          int end = textArea.getSelectionEnd();
          try {
            int line = textArea.getLineOfOffset(end);
            end = textArea.getLineEndOffset(line);
            if (line != textArea.getLineCount() - 1) {
              end -= 1;
            }
          } catch (BadLocationException ignore) {
          }
          if (start == end) {
            if (start < textArea.getText().length()) {
              textArea.replaceRange("", start, end + 1);
            }
          } else {
            textArea.replaceRange("", start, end);
          }
        }
      });
    }

    public String getText() {
      return textArea.getText();
    }
  }
}
