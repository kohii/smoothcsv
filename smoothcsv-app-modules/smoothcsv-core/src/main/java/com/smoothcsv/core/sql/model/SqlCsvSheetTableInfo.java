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
package com.smoothcsv.core.sql.model;

import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.swing.gridsheet.model.GridSheetColumn;
import lombok.Getter;

/**
 * @author kohii
 */
public class SqlCsvSheetTableInfo implements SqlTableInfo {

  @Getter
  private CsvSheetView csvSheet;
  private String name;
  private List<SqlColumnInfo> columns;

  public SqlCsvSheetTableInfo(CsvSheetView csvSheet, String name) {
    this.csvSheet = csvSheet;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<SqlColumnInfo> getColumns() {
    if (columns != null) {
      return columns;
    }
    List<GridSheetColumn> columnList = csvSheet.getGridSheetPane().getModel().getColumns();
    columns = new ArrayList<SqlColumnInfo>(columnList.size());
    for (int i = 0; i < columnList.size(); i++) {
      String colName = columnList.get(i).getName();
      columns.add(new SqlColumnInfo(i + 1, colName));
    }
    return columns;
  }
}
