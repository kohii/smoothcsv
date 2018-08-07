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
package com.smoothcsv.swing.gridsheet.model;

/**
 * @author kohii
 */
public interface IGridSheetStructure {


  //
  // Get structure data
  //

  GridSheetColumn getColumn(int index);

  GridSheetRow getRow(int index);

  int getColumnCount();

  int getRowCount();

  //
  // Change structure
  //

  void addColumn(GridSheetColumn column);

  void addColumn(GridSheetColumn[] column);

  void addColumn(int numColumns);

  void insertColumn(int index, GridSheetColumn column);

  void insertColumn(int index, GridSheetColumn[] column);

  void insertColumn(int index, int numColumns);

  GridSheetColumn deleteColumn(int index);

  void addRow(GridSheetRow row);

  void addRow(GridSheetRow[] row);

  void addRow(int numRows);

  void insertRow(int index, GridSheetRow row);

  void insertRow(int index, GridSheetRow[] row);

  void insertRow(int index, int numRows);

  GridSheetRow deleteRow(int index);

  // Name ------------------------

  String getColumnName(int column);

  String getRowName(int row);

  // get or set structure info ------------------

  int getTotalColumnWidth();

  int getTotalRowHeight();

  int getDefaultRowHeight();

  void setDefaultRowHeight(int defaultRowHeight);

  int getDefaultColumnWidth();

  void setDefaultColumnWidth(int defaultColumnWidth);

  int getMinRowHeight();

  int getMaxRowHeight();

  int getMaxColumnWidth();

  int getMinColumnWidth();
}
