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
public class InsertCellEdit implements GridSheetModelUndoableEdit {


  private final int rowIndex;
  private final int columnIndex;
  private final String[] data;

  /**
   * @param rowIndex
   * @param columnIndex
   * @param data
   */
  public InsertCellEdit(int rowIndex, int columnIndex, String[] data) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.data = data;
  }

  @Override
  public void undo(CsvGridSheetModel model) {
    model.deleteCell(rowIndex, columnIndex, columnIndex + data.length - 1);
  }

  @Override
  public void redo(CsvGridSheetModel model) {
    model.insertCell(rowIndex, columnIndex, data);
  }
}
