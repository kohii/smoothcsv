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
package com.smoothcsv.core.csvsheet.edits;

import com.smoothcsv.core.csvsheet.CsvGridSheetModel;

/**
 * @author kohii
 */
public class DeleteRowsEdit implements GridSheetModelUndoableEdit {

  private int index;
  private String[][] data;

  /**
   * @param index
   * @param data
   */
  public DeleteRowsEdit(int index, String[][] data) {
    this.index = index;
    this.data = data;
  }

  @Override
  public void undo(CsvGridSheetModel model) {
    model.insertRow(index, data);
  }

  @Override
  public void redo(CsvGridSheetModel model) {
    model.deleteRow(index, data.length);
  }
}
