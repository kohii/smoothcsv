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
package com.smoothcsv.core.csvsheet.edits;

import lombok.Getter;

import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionSnapshot;

/**
 * @author kohii
 *
 */
public class SingleGridSheetEditContainer implements GridSheetEditContainer {

  @Getter
  private GridSheetSelectionSnapshot selection;
  private GridSheetUndableEdit edit;

  public SingleGridSheetEditContainer(GridSheetSelectionSnapshot selection,
      GridSheetUndableEdit edit) {
    this.selection = selection;
    this.edit = edit;
  }

  @Override
  public void undo(CsvGridSheetModel model) {
    edit.undo(model);
  }

  @Override
  public void redo(CsvGridSheetModel model) {
    edit.redo(model);
  }
}
