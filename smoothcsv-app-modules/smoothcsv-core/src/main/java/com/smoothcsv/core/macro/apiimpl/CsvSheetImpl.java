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
package com.smoothcsv.core.macro.apiimpl;

import java.io.File;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.core.macro.MacroUtils;
import com.smoothcsv.core.macro.api.CsvProperties;
import com.smoothcsv.core.macro.api.CsvSheet;
import com.smoothcsv.core.macro.api.Range;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import command.app.CloseCommand;
import command.app.SaveAsCommand;
import command.app.SaveCommand;
import command.grid.AutofitColumnWidthCommand;
import command.grid.RedoCommand;
import command.grid.UndoCommand;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
public class CsvSheetImpl extends APIBase implements CsvSheet {

  private final int id;

  CsvSheetView getCsvSheetView() {
    return (CsvSheetView) SCApplication.components().getTabbedPane().getViewById(id);
  }

  CsvSheetImpl(int csvSheetViewId) {
    id = csvSheetViewId;
  }

  @Override
  public CsvSheet activate() {
    SCApplication.components().getTabbedPane().setSelectedComponent(getCsvSheetView());
    return this;
  }

  @Override
  public int getIndex() {
    int index = SCApplication.components().getTabbedPane().indexOfComponent(getCsvSheetView());
    return index == -1 ? -1 : index + 1;
  }

  @Override
  public String getPathname() {
    File f = getCsvSheetView().getViewInfo().getFile();
    return f == null ? null : FileUtils.getCanonicalPath(f);
  }

  @Override
  public void setPathname(String pathname) {
    File file = new File(pathname);
    getCsvSheetView().getViewInfo().setFile(file);
  }

  @Override
  public CsvProperties getProperties() {
    return MacroUtils.toCsvProperties(getCsvSheetView().getViewInfo().getCsvMeta());
  }

  @Override
  public CsvSheet setProperties(CsvProperties properties) {
    getCsvSheetView().getViewInfo().setCsvMeta(MacroUtils.toCsvMeta(properties));
    return this;
  }

  @Override
  public boolean isModified() {
    return !getGridSheet().getUndoManager().isSavepoint();
  }

  @Override
  public void save() {
    new SaveCommand().run(getCsvSheetView());
  }

  @Override
  public void saveAs(String filePath) {
    new SaveAsCommand().run(getCsvSheetView());
  }

  @Override
  public void close() {
    close(false, null);
  }

  @Override
  public void close(boolean saveChanges, String pathname) {
    if (!saveChanges || getGridSheet().getUndoManager().isSavepoint()) {
      CloseCommand.close(getCsvSheetView(), false);
    } else {
      File f;
      if (StringUtils.isEmpty(pathname)) {
        CsvSheetView csvSheet = getCsvSheetView();
        f = csvSheet.getViewInfo().getFile();
        if (f == null) {
          f = SaveAsCommand.chooseFile(csvSheet.getViewInfo());
          if (f == null) {
            return;
          }
        }
      } else {
        f = new File(pathname);
      }
      SaveCommand.save(getCsvSheetView(), f);
      CloseCommand.close(getCsvSheetView(), false);
    }
  }

  @Override
  public boolean undo() {
    return UndoCommand.undo(getGridSheet());
  }

  @Override
  public boolean redo() {
    return RedoCommand.redo(getGridSheet());
  }

  @Override
  public CsvSheet autoResizeColumn(int columnPosition) {
    AutofitColumnWidthCommand.run(getGridSheet(), columnPosition - 1, columnPosition - 1);
    return this;
  }

  @Override
  public int getRowHeight(int rowPosition) {
    return getGridSheet().getModel().getRow(rowPosition - 1).getHeight();
  }

  // @Override
  // public CsvSheet setRowHeight(int rowPosition, int height) {
  // getGridSheet().getModel().getRow(rowPosition - 1).setHeight(height);
  // return this;
  // }

  @Override
  public int getColumnWidth(int columnPosition) {
    return getGridSheet().getModel().getColumn(columnPosition - 1).getWidth();
  }

  @Override
  public CsvSheet setColumnWidth(int columnPosition, int width) {
    getGridSheet().getModel().getColumn(columnPosition - 1).setWidth(width);
    return null;
  }

  @Override
  public CsvSheet insertRowAfter(int afterPosition) {
    return insertRowsAfter(afterPosition, 1);
  }

  @Override
  public CsvSheet insertRowsAfter(int afterPosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    if (gridSheetPane.isEditing()) {
      gridSheetPane.getTable().stopCellEditing();
    }
    GridSheetModel model = gridSheetPane.getModel();
    model.insertRow(afterPosition, howMany);
    gridSheetPane.getSelectionModel().clearHeaderSelection();
    return this;
  }

  @Override
  public CsvSheet insertRowBefore(int beforePosition) {
    return insertRowsBefore(beforePosition, 1);
  }

  @Override
  public CsvSheet insertRowsBefore(int beforePosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    gridSheetPane.stopCellEditingIfEditing();
    ;
    GridSheetModel model = gridSheetPane.getModel();
    model.insertRow(beforePosition - 1, howMany);
    gridSheetPane.getSelectionModel().clearHeaderSelection();
    return this;
  }

  @Override
  public CsvSheet deleteRow(int rowPosition) {
    return deleteRows(rowPosition, 1);
  }

  @Override
  public CsvSheet deleteRows(int rowPosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    try (EditTransaction tran = gridSheetPane.transaction()) {
      GridSheetModel model = gridSheetPane.getModel();
      model.deleteRow(rowPosition - 1, howMany);
      gridSheetPane.getSelectionModel().clearHeaderSelection();
    }
    return this;
  }


  @Override
  public CsvSheet insertColumnAfter(int afterPosition) {
    return insertColumnsAfter(afterPosition, 1);
  }

  @Override
  public CsvSheet insertColumnsAfter(int afterPosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    if (gridSheetPane.isEditing()) {
      gridSheetPane.getTable().stopCellEditing();
    }
    GridSheetModel model = gridSheetPane.getModel();
    model.insertColumn(afterPosition, howMany);
    gridSheetPane.getSelectionModel().clearHeaderSelection();
    return this;
  }

  @Override
  public CsvSheet insertColumnBefore(int beforePosition) {
    return insertColumnsBefore(beforePosition, 1);
  }

  @Override
  public CsvSheet insertColumnsBefore(int beforePosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    gridSheetPane.stopCellEditingIfEditing();
    ;
    GridSheetModel model = gridSheetPane.getModel();
    model.insertColumn(beforePosition - 1, howMany);
    gridSheetPane.getSelectionModel().clearHeaderSelection();
    return this;
  }

  @Override
  public CsvSheet deleteColumn(int columnPosition) {
    return deleteColumns(columnPosition, 1);
  }

  @Override
  public CsvSheet deleteColumns(int columnPosition, int howMany) {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    try (EditTransaction tran = gridSheetPane.transaction()) {
      GridSheetModel model = gridSheetPane.getModel();
      model.deleteColumn(columnPosition - 1, howMany);
      gridSheetPane.getSelectionModel().clearHeaderSelection();
    }
    return this;
  }

  @Override
  public int getNumRows() {
    return getGridSheet().getRowCount();
  }

  @Override
  public int getNumColumns() {
    return getGridSheet().getColumnCount();
  }

  @Override
  public Range getRange(int row, int column) {
    return getRange(row, column, 1, 1);
  }

  @Override
  public Range getRange(int row, int column, int numRows, int numColumns) {
    return new RangeImpl(this, row, column, numRows, numColumns);
  }

  @Override
  public Range getRange() {
    return getRange(1, 1, getNumRows(), getNumColumns());
  }

  @Override
  public Range getActiveRange() {
    GridSheetSelectionModel sm = getGridSheet().getSelectionModel();
    int row = sm.getMainMinRowSelectionIndex();
    int column = sm.getMainMinColumnSelectionIndex();
    int numRows = sm.getMainMaxRowSelectionIndex() - row + 1;
    int numColumns = sm.getMainMaxColumnSelectionIndex() - column + 1;
    return getRange(row + 1, column + 1, numRows, numColumns);
  }

  @Override
  public Range setActiveRange(Range range) {
    GridSheetSelectionModel sm = getGridSheet().getSelectionModel();
    sm.setSelectionInterval(range.getRow() - 1, range.getColumn() - 1, range.getLastRow() - 1,
        range.getLastColumn() - 1);
    return getActiveRange();
  }

  @Override
  public Range getActiveCell() {
    GridSheetSelectionModel sm = getGridSheet().getSelectionModel();
    return getRange(sm.getRowAnchorIndex() + 1, sm.getColumnAnchorIndex() + 1);
  }

  @Override
  public CsvSheet sort(Object sortSpecObj) {
    getRange().sort(sortSpecObj);
    return this;
  }

  private CsvGridSheetPane getGridSheet() {
    return getCsvSheetView().getGridSheetPane();
  }
}
