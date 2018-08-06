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

import java.io.Writer;
import java.util.List;

import com.smoothcsv.csv.prop.LineSeparator;
import com.smoothcsv.csv.writer.AbstractCsvWriter;
import com.smoothcsv.csv.writer.CsvWriteOption;
import lombok.Setter;

/**
 * @author kohii
 */
public class SmoothCsvWriter extends AbstractCsvWriter<List<String>> {

  @Setter
  private boolean writeLineSeparator = true;
  private LineSeparator lineSeparator;

  /**
   * @param out
   * @param csvMeta
   * @param options
   */
  public SmoothCsvWriter(Writer out, CsvMeta csvMeta, CsvWriteOption options) {
    super(out, csvMeta.toCsvProperties(), options);
    this.lineSeparator = csvMeta.getLineSeparator();
  }

  /**
   * @param out
   * @param csvMeta
   */
  public SmoothCsvWriter(Writer out, CsvMeta csvMeta) {
    super(out, csvMeta.toCsvProperties());
    this.lineSeparator = csvMeta.getLineSeparator();
  }

  @Override
  protected Object extractLineSeparator(List<String> row, int rowIndex) {
    return writeLineSeparator ? lineSeparator.stringValue() : "";
  }

  @Override
  protected String extractValue(List<String> row, int rowIndex, int columnIndex) {
    Object value = row.get(columnIndex);
    return value == null ? "" : value.toString();
  }

  @Override
  protected int extractColumnSize(List<String> row, int rowIndex) {
    return row.size();
  }

}
