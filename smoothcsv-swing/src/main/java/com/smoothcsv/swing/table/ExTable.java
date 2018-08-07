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

import java.awt.Rectangle;

import javax.swing.JTable;

/**
 * @author kohii
 */
public class ExTable<T> extends JTable {
  private static final long serialVersionUID = 2223569234825767520L;

  public ExTable(ExTableModel<T> model) {
    super(model);
  }

  public ExTable() {
    this(new ExTableModel<>());
  }

  @SuppressWarnings("unchecked")
  @Override
  public ExTableModel<T> getModel() {
    return (ExTableModel<T>) super.getModel();
  }

  public void ensureCellIsVisible(int rowIndex, int columnIndex) {
    if (getAutoscrolls()) {
      Rectangle cellRect = getCellRect(rowIndex, columnIndex, false);
      if (cellRect != null) {
        scrollRectToVisible(cellRect);
      }
    }
  }

  public void selecteRowAt(int row) {
    getSelectionModel().setSelectionInterval(row, row);
    if (getAutoscrolls()) {
      Rectangle cellRect = getCellRect(row, 0, false);
      if (cellRect != null) {
        scrollRectToVisible(cellRect);
      }
    }
  }
}
