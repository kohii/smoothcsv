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
package com.smoothcsv.core.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.csv.prop.CsvProperties;
import com.smoothcsv.csv.prop.LineSeparator;
import com.smoothcsv.csv.reader.CsvReadOption;
import com.smoothcsv.csv.reader.DefaultCsvReader;
import lombok.Getter;

/**
 * @author kohii
 */
public class SmoothCsvReader extends DefaultCsvReader {

  @Getter
  private LineSeparator firstLineSeparator;
  private LineSeparator lastLineSeparator;

  /**
   * @param in
   * @param properties
   * @param options
   */
  public SmoothCsvReader(Reader in, CsvProperties properties, CsvReadOption options) {
    super(in, properties, options);
  }

  /**
   * @param in
   * @param properties
   */
  public SmoothCsvReader(Reader in, CsvProperties properties) {
    super(in, properties);
  }

  /**
   * @param in
   */
  public SmoothCsvReader(Reader in) {
    super(in);
  }

  @Override
  protected void handleLineSeparator(List<String> row, int rowIndex,
                                     LineSeparator lineSeparator) {
    if (firstLineSeparator == null) {
      firstLineSeparator = lineSeparator;
    }
    lastLineSeparator = lineSeparator;
  }

  @Override
  public List<String> readRow() throws IOException {
    List<String> list = super.readRow();
    if (list == null) {
      if (lastLineSeparator != null) {
        lastLineSeparator = null;
        return new ArrayList<>();
      }
    }
    return list;
  }
}
