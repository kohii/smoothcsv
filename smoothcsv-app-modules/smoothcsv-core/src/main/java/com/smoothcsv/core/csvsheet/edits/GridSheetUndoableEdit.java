package com.smoothcsv.core.csvsheet.edits;

import com.smoothcsv.core.csvsheet.CsvGridSheetPane;

/**
 * @author kohii
 */
public interface GridSheetUndoableEdit {

  void undo(CsvGridSheetPane gridSheetPane);

  void redo(CsvGridSheetPane gridSheetPane);
}
