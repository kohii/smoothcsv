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
package command.find;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.find.FindAndReplacePanel;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.swing.gridsheet.GridSheetCellEditor;

/**
 * @author kohii
 *
 */
public class ShowCommand extends Command {

  @Override
  public void run() {

    String initText = null;
    CsvSheetView view = (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    if (view != null) {
      if (view.getGridSheetPane().isEditing()) {
        GridSheetCellEditor cellEditor = view.getGridSheetPane().getTable().getCellEditor();
        JComponent editorComp = cellEditor.getEditorComponent();
        if (editorComp instanceof JTextComponent) {
          initText = ((JTextComponent) editorComp).getSelectedText();
        }
        view.getGridSheetPane().getTable().stopCellEditing();
      }
    }

    FindAndReplacePanel findAndReplacePanel = FindAndReplacePanel.getInstance();
    findAndReplacePanel.open();
    if (StringUtils.isNotEmpty(initText)) {
      findAndReplacePanel.setFindWhatText(initText);
    }
    findAndReplacePanel.initFocus();

    ApplicationStatus.getInstance().setFindAndReplacePanelVisible(true);
  }
}
