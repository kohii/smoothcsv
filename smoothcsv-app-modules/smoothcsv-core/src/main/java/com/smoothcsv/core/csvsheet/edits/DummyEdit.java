/**
 * 
 */
package com.smoothcsv.core.csvsheet.edits;

import com.smoothcsv.core.csvsheet.CsvGridSheetModel;

/**
 * @author kohii
 *
 */
public class DummyEdit implements GridSheetUndableEdit {

  @Override
  public void undo(CsvGridSheetModel model) {}

  @Override
  public void redo(CsvGridSheetModel model) {}

}
