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
package command.grid;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.framework.component.dialog.NumberInputDialog;
import com.smoothcsv.framework.util.MessageBundles;

/**
 * @author kohii
 *
 */
public class GoToLineCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheet) {
    Integer lineNo = NumberInputDialog.showDialog(MessageBundles.getString("ISCA0007"));
    if (lineNo == null) {
      return;
    }
    lineNo = Math.max(1, Math.min(lineNo, gridSheet.getRowCount()));
    int row = lineNo - 1;
    int column = 0;
    gridSheet.getSelectionModel().setSelectionInterval(row, column, row, column);
    gridSheet.getTable().scrollRectToVisible(row, column);
  }
}
