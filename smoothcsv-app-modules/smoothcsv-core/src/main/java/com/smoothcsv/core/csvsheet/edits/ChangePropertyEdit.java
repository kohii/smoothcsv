package com.smoothcsv.core.csvsheet.edits;

import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import lombok.RequiredArgsConstructor;

/**
 * @author kohii
 */
@RequiredArgsConstructor
public class ChangePropertyEdit implements GridSheetUndoableEdit {

  private final CsvMeta oldCsvMeta;
  private final CsvMeta newCsvMeta;

  @Override
  public void undo(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getCsvSheetView().getViewInfo().setCsvMeta(oldCsvMeta);
  }

  @Override
  public void redo(CsvGridSheetPane gridSheetPane) {
    gridSheetPane.getCsvSheetView().getViewInfo().setCsvMeta(newCsvMeta);
  }
}
