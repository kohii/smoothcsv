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
package com.smoothcsv.core.command;

import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.swing.gridsheet.model.CellConsumer;

/**
 * @author kohii
 */
public abstract class ConvertCommandBase extends GridCommand {

  @Override
  public final void run(CsvGridSheetPane gridSheetPane) {
    try (EditTransaction tran = gridSheetPane.transaction()) {
      gridSheetPane.getSelectionModel().forEachSelectedCell(new CellConsumer() {
        @Override
        public void accept(int row, int column) {
          Object val = gridSheetPane.getValueAt(row, column);
          String converted = convert((String) val);
          if (val != converted) {
            gridSheetPane.setValueAt(converted, row, column);
          }
        }
      });
    }
  }

  protected abstract String convert(String val);
}
