/*
 * Copyright 2015 kohii
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

import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 *
 */
public class SqlColumnInfo {

  private String name;

  @Getter
  @Setter
  private int columnIndex;

  @Getter
  @Setter
  private JDBCType type = JDBCType.VARCHAR;

  public SqlColumnInfo(int columnIndex) {
    this(columnIndex, null);
  }

  public SqlColumnInfo(int columnIndex, String name) {
    this.name = name;
    this.columnIndex = columnIndex;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name == null ? "c" + columnIndex : name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name == null || name.isEmpty() ? null : name.trim();
  }
}
