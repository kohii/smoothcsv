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

import java.sql.JDBCType;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
public class SqlColumnInfo {

  @Getter
  private final long columnId;

  private String name;

  @Getter
  @Setter
  private int columnIndex;

  private final CsvSheetView csvSheet;

  @Getter
  @Setter
  private JDBCType type = JDBCType.VARCHAR;

  public SqlColumnInfo(long columnId, int columnIndex, CsvSheetView csvSheet) {
    this.columnId = columnId;
    this.columnIndex = columnIndex;
    this.csvSheet = csvSheet;
  }

  /**
   * @return the name
   */
  public String getName() {
    if (StringUtils.isNotEmpty(name)) {
      return name;
    }
    if (csvSheet.getGridSheetPane().getModel().usesFirstRowAsHeader()) {
     return csvSheet.getGridSheetPane().getModel().getColumnName(columnIndex);
    }
    return "c" + (columnIndex + 1);
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name == null || name.isEmpty() ? null : name.trim();
  }
}
