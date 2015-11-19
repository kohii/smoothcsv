/*
 * Copyright 2014 kohii.
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
package com.smoothcsv.core.macro.api.impl;

import java.io.File;
import java.util.ResourceBundle;

import lombok.Getter;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.macro.api.CsvSheet;
import com.smoothcsv.core.macro.api.Range;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCTabbedPane;

import command.app.NewFileCommand;
import command.app.OpenFileCommand;

/**
 * @author kohii
 *
 */
public class App extends APIBase {

  @Getter
  private static final App instance = new App();

  private App() {}

  /**
   * Returns the name of this application.
   * 
   * @return the name of this application
   */
  public String getName() {
    return SCApplication.getApplication().getName();
  }

  /**
   * Returns the version name of this application.
   * 
   * @return the version name
   */
  public String getVersion() {
    return ResourceBundle.getBundle("application").getString("version.name");
  }

  /**
   * Creates a new csvsheet with the default properties.
   */
  public void create() {
    new NewFileCommand().run();
  }

  /**
   * Creates a new csvsheet with the specified number of rows and columns and properties.
   * 
   * @param rows the number of rows for the csvsheet
   * @param columns the number of columns for the csvsheet
   * @param properties the properties
   */
  public void create(int rows, int columns, CsvProperties properties) {
    new NewFileCommand().run(rows, columns, properties.toCsvMeta());
  }

  /**
   * Opens the csvsheet that corresponds to the given file path with the default properties.
   * 
   * @param pathname the file path to open
   */
  public void open(String pathname) {
    open(pathname, CsvProperties.defaultProperties());
  }

  /**
   * Opens the csvsheet that corresponds to the given file path with the specified properties.
   * 
   * @param pathname the file path to open
   * @param properties the properties
   */
  public void open(String pathname, CsvProperties properties) {
    new OpenFileCommand().run(new File(pathname), properties.toCsvMeta(), null);
  }

  /**
   * Gets the active csvsheet. Returns null if there is no sheet.
   * 
   * @return the active {@link CsvSheet} object
   */
  public CsvSheet getActiveSheet() {
    CsvSheetView csvSheetView =
        (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    return csvSheetView == null ? null : new CsvSheetImpl(csvSheetView.getViewId());
  }

  /**
   * Sets the active csvsheet.
   */
  public void setActiveSheet(CsvSheet csvSheet) {
    csvSheet.activate();
  }

  /**
   * Returns the range of cells that is currently considered active. This generally means the range
   * that a user has selected in the active sheet.
   * 
   * @return the active range
   */
  public Range getActiveRange() {
    CsvSheet activeSheet = getActiveSheet();
    return activeSheet == null ? null : activeSheet.getActiveRange();
  }

  /**
   * Gets all the sheets in this application.
   * 
   * @return an array of all the sheets in the application
   */
  public CsvSheet[] getSheets() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    int compCount = tabbedPane.getTabCount();
    CsvSheet[] sheets = new CsvSheet[compCount];
    for (int i = 0; i < compCount; i++) {
      sheets[i] = new CsvSheetImpl(tabbedPane.getComponentAt(i).getViewId());
    }
    return sheets;
  }
}
