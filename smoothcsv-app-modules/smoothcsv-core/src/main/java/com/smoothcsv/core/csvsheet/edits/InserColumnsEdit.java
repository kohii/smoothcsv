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
 *
 */
public class InserColumnsEdit implements GridSheetUndableEdit {

  private int index;
  private int numColumns;

  /**
   * @param index
   * @param numColumns
   */
  public InserColumnsEdit(int index, int numColumns) {
    this.index = index;
    this.numColumns = numColumns;
  }

  @Override
  public void undo(CsvGridSheetModel model) {
    model.deleteColumn(index, numColumns);
  }

  @Override
  public void redo(CsvGridSheetModel model) {
    model.insertColumn(index, numColumns);
  }
}
