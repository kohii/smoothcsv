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
package command.debug;

import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;


/**
 * @author kohii
 */
public class PrintGridDataCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    System.out.println();
    CsvGridSheetModel model = gridSheetPane.getModel();
    for (int i = 0; i < model.getRowCount(); i++) {
      System.out.print(model.getValueAt(i, 0));
      for (int j = 1; j < model.getColumnCount(); j++) {
        System.out.print("\t" + model.getValueAt(i, j));
      }
      System.out.println();
    }
    System.out.println();
  }
}
