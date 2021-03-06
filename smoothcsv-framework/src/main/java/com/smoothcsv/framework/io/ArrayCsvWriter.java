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

import java.io.Writer;

import com.smoothcsv.csv.prop.CsvProperties;
import com.smoothcsv.csv.prop.LineSeparator;
import com.smoothcsv.csv.writer.AbstractCsvWriter;
import com.smoothcsv.csv.writer.CsvWriteOption;

/**
 * @author kohii
 */
public class ArrayCsvWriter extends AbstractCsvWriter<String[]> {

  public ArrayCsvWriter(Writer out) {
    super(out);
  }

  public ArrayCsvWriter(Writer out, CsvProperties properties) {
    super(out, properties, CsvWriteOption.DEFAULT);
  }

  @Override
  protected Object extractLineSeparator(String[] row, int rowIndex) {
    return LineSeparator.DEFAULT.stringValue();
  }

  @Override
  protected String extractValue(String[] row, int rowIndex, int columnIndex) {
    return row[columnIndex];
  }

  @Override
  protected int extractColumnSize(String[] row, int rowIndex) {
    return row.length;
  }
}
