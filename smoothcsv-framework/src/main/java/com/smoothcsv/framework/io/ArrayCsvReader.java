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
package com.smoothcsv.framework.io;

import java.io.Reader;

import com.smoothcsv.csv.prop.CsvProperties;
import com.smoothcsv.csv.reader.AbstractCsvReader;
import com.smoothcsv.csv.reader.CsvReadOption;

/**
 * @author kohii
 */
public class ArrayCsvReader extends AbstractCsvReader<String[]> {

  private final int length;

  public ArrayCsvReader(Reader in, int length) {
    this(in, CsvProperties.DEFAULT, length);
  }

  public ArrayCsvReader(Reader in, CsvProperties properties, int length) {
    super(in, properties, CsvReadOption.DEFAULT);
    this.length = length;
  }

  public ArrayCsvReader(Reader in, CsvProperties properties, CsvReadOption options, int length) {
    super(in, properties, options);
    this.length = length;
  }

  @Override
  protected String[] createNewRow(int rowIndex) {
    return new String[length];
  }

  @Override
  protected void handleValue(String[] row, int rowIndex, int columnIndex, String value) {
    if (columnIndex < length) {
      row[columnIndex] = value;
    }
  }

}
