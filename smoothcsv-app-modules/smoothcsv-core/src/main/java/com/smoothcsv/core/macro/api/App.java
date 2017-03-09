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
package com.smoothcsv.core.macro.api;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.macro.MacroUtils;
import com.smoothcsv.core.macro.apiimpl.APIBase;
import com.smoothcsv.core.macro.apiimpl.CellEditorImpl;
import com.smoothcsv.core.macro.apiimpl.CsvSheetImpl;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCTabbedPane;
import command.app.NewFileCommand;
import command.app.OpenFileCommand;

import java.io.File;
import java.util.ResourceBundle;

/**
 * This class represents the SmoothCSV application itself.
 *
 * @author kohii
 */
public class App extends APIBase {

  private App() {}

  /**
   * Returns the name of this application.
   *
   * @return the name of this application
   */
  public static String getName() {
    return SCApplication.getApplication().getName();
  }

  /**
   * Returns the version name of this application.
   *
   * @return the version name
   */
  public static String getVersion() {
    return ResourceBundle.getBundle("application").getString("version.name");
  }

  /**
   * Creates a new csvsheet with the default properties.
   */
  public static void newSheet() {
    new NewFileCommand().run();
  }

  /**
   * Creates a new csvsheet with default properties and the specified number of rows and columns.
   *
   * @param rows    the number of rows for the csvsheet
   * @param columns the number of columns for the csvsheet
   */
  public static void newSheet(int rows, int columns) {
    newSheet(rows, columns, CsvProperties.defaultProperties());
  }

  /**
   * Creates a new csvsheet with the specified number of rows and columns and properties.
   *
   * @param rows       the number of rows for the csvsheet
   * @param columns    the number of columns for the csvsheet
   * @param properties the properties
   */
  public static void newSheet(int rows, int columns, CsvProperties properties) {
    NewFileCommand.run(rows, columns, MacroUtils.toCsvMeta(properties), SCTabbedPane.LAST);
  }

  /**
   * Opens the csvsheet that corresponds to the given file path with the default properties.
   *
   * @param pathname the file path to open
   */
  public static void open(String pathname) {
    open(pathname, CsvProperties.defaultProperties());
  }

  /**
   * Opens the csvsheet that corresponds to the given file path with the specified properties.
   *
   * @param pathname   the file path to open
   * @param properties the properties
   */
  public static void open(String pathname, CsvProperties properties) {
    OpenFileCommand.run(new File(pathname), MacroUtils.toCsvMeta(properties), null, SCTabbedPane.LAST);
  }

  /**
   * Gets the active csvsheet. Returns null if there is no sheet.
   *
   * @return the active {@link CsvSheet} object
   */
  public static CsvSheet getActiveSheet() {
    CsvSheetView csvSheetView =
        (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    return csvSheetView == null ? null : new CsvSheetImpl(csvSheetView.getViewId());
  }

  /**
   * Sets the active csvsheet.
   *
   * @param csvSheet the sheet to be activated
   */
  public static void setActiveSheet(CsvSheet csvSheet) {
    csvSheet.activate();
  }

  /**
   * Returns the range of cells that is currently considered active. This generally means the range
   * that a user has selected in the active sheet.
   *
   * @return the active range
   */
  public static Range getActiveRange() {
    CsvSheet activeSheet = getActiveSheet();
    return activeSheet == null ? null : activeSheet.getActiveRange();
  }

  /**
   * Returns the active {@link CellEditor} or null if there is no active CellEditor.
   *
   * @return the active {@link CellEditor}
   */
  public static CellEditor getActiveCellEditor() {
    return getActiveCellEditor(false);
  }

  /**
   * Returns the active {@link CellEditor}.
   *
   * @param startEdit <code>true</code> to start editing if there is no active CellEditor.;
   *                  <code>false</code> to return null if there is no active CellEditor.
   * @return the active {@link CellEditor}
   */
  public static CellEditor getActiveCellEditor(boolean startEdit) {
    CsvSheetView csvSheetView =
        (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    if (csvSheetView == null) {
      return null;
    }
    if (!csvSheetView.getGridSheetPane().isEditing()) {
      if (startEdit) {
        csvSheetView.getGridSheetPane().getTable().startEdit();
      } else {
        return null;
      }
    }
    return new CellEditorImpl(new CsvSheetImpl(csvSheetView.getViewId()));
  }

  /**
   * Gets all the sheets in this application.
   *
   * @return an array of all the sheets in the application
   */
  public static CsvSheet[] getSheets() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    int compCount = tabbedPane.getTabCount();
    CsvSheet[] sheets = new CsvSheet[compCount];
    for (int i = 0; i < compCount; i++) {
      sheets[i] = new CsvSheetImpl(tabbedPane.getComponentAt(i).getViewId());
    }
    return sheets;
  }
}
