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

/**
 * This class represents the SmoothCSV application itself.
 *
 * @author kohii
 */
public interface App {

  /**
   * Returns the name of this application.
   *
   * @return the name of this application
   */
  String getName();

  /**
   * Returns the version name of this application.
   *
   * @return the version name
   */
  String getVersion();

  /**
   * Creates a new csvsheet with the default properties.
   */
  void create();

  /**
   * Creates a new csvsheet with default properties and the specified number of rows and columns.
   *
   * @param rows    the number of rows for the csvsheet
   * @param columns the number of columns for the csvsheet
   */
  void create(int rows, int columns);

  /**
   * Creates a new csvsheet with the specified number of rows and columns and properties.
   *
   * @param rows       the number of rows for the csvsheet
   * @param columns    the number of columns for the csvsheet
   * @param properties the properties
   */
  void create(int rows, int columns, CsvProperties properties);

  /**
   * Opens the csvsheet that corresponds to the given file path with the default properties.
   *
   * @param pathname the file path to open
   */
  void open(String pathname);

  /**
   * Opens the csvsheet that corresponds to the given file path with the specified properties.
   *
   * @param pathname   the file path to open
   * @param properties the properties
   */
  void open(String pathname, CsvProperties properties);

  /**
   * Gets the active csvsheet. Returns null if there is no sheet.
   *
   * @return the active {@link CsvSheet} object
   */
  CsvSheet getActiveSheet();

  /**
   * Sets the active csvsheet.
   *
   * @param csvSheet the sheet to be activated
   */
  void setActiveSheet(CsvSheet csvSheet);

  /**
   * Returns the range of cells that is currently considered active. This generally means the range
   * that a user has selected in the active sheet.
   *
   * @return the active range
   */
  Range getActiveRange();

  /**
   * Returns the active {@link CellEditor} or null if there is no active CellEditor.
   *
   * @return the active {@link CellEditor}
   */
  CellEditor getActiveCellEditor();

  /**
   * Returns the active {@link CellEditor}.
   *
   * @param startEdit <code>true</code> to start editing if there is no active CellEditor.;
   *                  <code>false</code> to return null if there is no active CellEditor.
   * @return the active {@link CellEditor}
   */
  CellEditor getActiveCellEditor(boolean startEdit);

  /**
   * Gets all the sheets in this application.
   *
   * @return an array of all the sheets in the application
   */
  CsvSheet[] getSheets();
}
