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

import lombok.Setter;

import com.smoothcsv.csv.NewlineCharacter;
import com.smoothcsv.csv.writer.AbstractCsvWriter;
import com.smoothcsv.csv.writer.CsvWriterOptions;

/**
 * @author kohii
 *
 */
public class SmoothCsvWriter extends AbstractCsvWriter<List<Object>> {

  @Setter
  private boolean writeLineSeparater = true;
  private NewlineCharacter newlineCharacter;

  /**
   * @param out
   * @param properties
   * @param options
   */
  public SmoothCsvWriter(Writer out, CsvMeta properties, CsvWriterOptions options) {
    super(out, properties, options);
    this.newlineCharacter = properties.getNewlineCharacter();
  }

  /**
   * @param out
   * @param properties
   */
  public SmoothCsvWriter(Writer out, CsvMeta properties) {
    super(out, properties);
    this.newlineCharacter = properties.getNewlineCharacter();
  }

  @Override
  protected Object extractLineSeparator(List<Object> row, int rowIndex) {
    return writeLineSeparater ? newlineCharacter.stringValue() : "";
  }

  @Override
  protected String extractValue(List<Object> row, int rowIndex, int columnIndex) {
    Object value = row.get(columnIndex);
    return value == null ? "" : value.toString();
  }

  @Override
  protected int extractColumnSize(List<Object> row, int rowIndex) {
    return row.size();
  }

}
