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
public class ChangeValueEdit implements GridSheetModelUndoableEdit {

  private String oldValue;
  private String newValue;
  private int row;
  private int column;

  /**
   * @param oldValue
   * @param newValue
   * @param row
   * @param column
   */
  public ChangeValueEdit(String oldValue, String newValue, int row, int column) {
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.row = row;
    this.column = column;
  }

  @Override
  public void undo(CsvGridSheetModel model) {
    model.setValueAt(oldValue, row, column);
  }

  @Override
  public void redo(CsvGridSheetModel model) {
    model.setValueAt(newValue, row, column);
  }

}
