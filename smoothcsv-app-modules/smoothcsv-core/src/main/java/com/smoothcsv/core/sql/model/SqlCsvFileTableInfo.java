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

import java.io.File;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.csv.CsvMeta;

/**
 * @author kohii
 *
 */
public class SqlCsvFileTableInfo implements SqlTableInfo {

  @Getter
  private final File file;
  @Getter
  @Setter
  private CsvMeta csvMeta;

  public SqlCsvFileTableInfo(File file) {
    this.file = file;
  }

  @Override
  public String getName() {
    return FileUtils.getCanonicalPath(file);
  }

  @Override
  public List<SqlColumnInfo> getColumns() {
    return null;
  }
}
