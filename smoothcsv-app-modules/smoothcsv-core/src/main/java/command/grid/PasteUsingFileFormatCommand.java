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

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.swing.utils.ClipboardUtils;

/**
 * @author kohii
 */
public class PasteUsingFileFormatCommand extends GridCommand {

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {
    String text = ClipboardUtils.readText();
    if (StringUtils.isEmpty(text)) {
      return;
    }
    PasteCommand.paste(gridSheetPane, text, gridSheetPane.getCsvSheetView().getViewInfo()
        .getCsvMeta(), false);
  }
}
