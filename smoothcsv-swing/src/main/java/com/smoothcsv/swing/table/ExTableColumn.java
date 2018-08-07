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
package com.smoothcsv.swing.table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
public class ExTableColumn {


  @Setter
  @Getter
  private String text;

  @Setter
  @Getter
  private String fieldName;

  @Setter
  @Getter
  private boolean editable = true;

  @Getter
  @Setter
  @SuppressWarnings("rawtypes")
  private ExTableCellValueExtracter cellValueExtracter;

  public ExTableColumn() {}


  public ExTableColumn(String text, String fieldName) {
    this(text, fieldName, true);
  }

  public ExTableColumn(String text, String fieldName, boolean editable) {
    this(text, new DefaultTableCellValueExtracter(fieldName), editable);
  }


  @SuppressWarnings("rawtypes")
  public ExTableColumn(String text, ReadOnlyExTableCellValueExtracter cellValueExtracter) {
    this(text, new ExTableCellValueExtracter() {

      @SuppressWarnings("unchecked")
      @Override
      public Object getValue(Object rowData, ExTableColumn column, int rowIndex, int columnIndex) {
        return cellValueExtracter.getValue(rowData, column, rowIndex, columnIndex);
      }

      @Override
      public void setValue(Object value, Object rowData, ExTableColumn column, int rowIndex,
                           int columnIndex) {
        throw new UnsupportedOperationException();
      }
    }, false);
  }


  @SuppressWarnings("rawtypes")
  public ExTableColumn(String text, ExTableCellValueExtracter cellValueExtracter) {
    this(text, cellValueExtracter, true);
  }

  @SuppressWarnings("rawtypes")
  public ExTableColumn(String text, ExTableCellValueExtracter cellValueExtracter, boolean editable) {
    this.text = text;
    this.cellValueExtracter = cellValueExtracter;
    this.editable = editable;
  }

  @SuppressWarnings("unchecked")
  protected Object getValue(Object rowData, int rowIndex, int columnIndex) {
    return getCellValueExtracter().getValue(rowData, this, rowIndex, columnIndex);
  }

  @SuppressWarnings("unchecked")
  protected void setValue(Object value, Object rowData, int rowIndex, int columnIndex) {
    getCellValueExtracter().setValue(value, rowData, this, rowIndex, columnIndex);
  }

}
