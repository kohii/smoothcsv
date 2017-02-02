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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * @author kohii
 */
public class SqlCsvFileTables {

  @Getter
  private static SqlCsvFileTables instance = new SqlCsvFileTables();

  private List<SqlCsvFileTableInfo> tables = new ArrayList<>();

  private SqlCsvFileTables() {}

  public List<SqlCsvFileTableInfo> getTables() {
    return tables;
  }

  public boolean contains(File f) {
    for (SqlCsvFileTableInfo sqlCsvFileTableInfo : tables) {
      if (f.equals(sqlCsvFileTableInfo.getFile())) {
        return true;
      }
    }
    return false;
  }

  public void addTable(File f) {
    SqlCsvFileTableInfo tableInfo = new SqlCsvFileTableInfo(f);
    tables.add(tableInfo);
  }
}
