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

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;

/**
 * @author kohii
 */
public class RedoCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    redo(gridSheetPane);
  }

  public static boolean redo(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.stopCellEditingIfEditing();
    GridSheetUndoManager undoManager = gridSheetPane.getUndoManager();
    if (undoManager.canRedo()) {
      undoManager.redo();
      return true;
    } else {
      return false;
    }
  }
}
