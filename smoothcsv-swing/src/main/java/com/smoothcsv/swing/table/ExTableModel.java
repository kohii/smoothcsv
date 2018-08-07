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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author kohii
 */
public class ExTableModel<T> implements TableModel {

  private List<TableModelListener> tableModelListeners = new ArrayList<>();

  private List<ExTableColumn> columns;

  private List<T> data;

  // for filtering
  private int[] indicesToOriginalData;

  public ExTableModel(List<T> data, List<ExTableColumn> columns) {
    this.data = new ArrayList<>(data);
    this.columns = columns;
  }

  public ExTableModel() {
    this(new ArrayList<>(), new ArrayList<>());
  }

  public void setData(List<T> data) {
    this.data = data;
    TableModelEvent e =
        new TableModelEvent(this, 0, getRowCount(), TableModelEvent.ALL_COLUMNS,
            TableModelEvent.UPDATE);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  public void addRow(T rowData) {
    insertRow(getRowCount(), rowData);
  }

  public void insertRow(int index, T rowData) {
    data.add(toOriginalIndex(index), rowData);
    TableModelEvent e =
        new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  public void removeRow(T rowData) {
    int index = data.indexOf(rowData);
    if (index < 0) {
      return;
    }
    removeRow(index);
  }

  public void removeRow(int index) {
    data.remove(toOriginalIndex(index));
    TableModelEvent e =
        new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  public void addColumn(ExTableColumn column) {
    insertColumn(getColumnCount(), column);
  }

  public void insertColumn(int index, ExTableColumn column) {
    columns.add(column);
    TableModelEvent e =
        new TableModelEvent(this, 0, Integer.MAX_VALUE, index, TableModelEvent.INSERT);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  public void remove(ExTableColumn column) {
    int index = columns.indexOf(column);
    if (index < 0) {
      return;
    }
    removeColumn(index);
  }

  public void removeColumn(int index) {
    columns.remove(index);
    TableModelEvent e =
        new TableModelEvent(this, 0, Integer.MAX_VALUE, index, TableModelEvent.DELETE);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  @Override
  public int getRowCount() {
    if (indicesToOriginalData != null) {
      return indicesToOriginalData.length;
    }
    return data.size();
  }

  @Override
  public int getColumnCount() {
    return columns.size();
  }

  @Override
  public String getColumnName(int column) {
    return columns.get(column).getText();
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return columns.get(column).isEditable();
  }

  @Override
  public Object getValueAt(int row, int column) {
    row = toOriginalIndex(row);
    return columns.get(column).getValue(data.get(row), row, column);
  }

  @Override
  public void setValueAt(Object aValue, int row, int column) {
    row = toOriginalIndex(row);
    columns.get(column).setValue(aValue, data.get(row), row, column);
    TableModelEvent e = new TableModelEvent(this, row, row, column);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return Object.class;
  }

  @Override
  public void addTableModelListener(TableModelListener l) {
    tableModelListeners.add(l);
  }

  @Override
  public void removeTableModelListener(TableModelListener l) {
    tableModelListeners.remove(l);
  }

  public List<T> getData() {
    return new ArrayList<>(data);
  }

  public T getRowDataAt(int row) {
    return data.get(toOriginalIndex(row));
  }

  public void doFilter(ExTableRowFilter<T> filter) {
    if (filter == null) {
      this.indicesToOriginalData = null;
    } else {
      int[] indices = new int[data.size()];
      int next = 0;
      for (int i = 0; i < data.size(); i++) {
        T rowData = data.get(i);
        if (filter.include(rowData, i)) {
          indices[next++] = i;
        }
      }
      this.indicesToOriginalData = Arrays.copyOf(indices, next);
    }
    TableModelEvent e =
        new TableModelEvent(this, 0, Integer.MAX_VALUE, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.UPDATE);
    for (TableModelListener l : tableModelListeners) {
      l.tableChanged(e);
    }
  }

  private int toOriginalIndex(int r) {
    if (indicesToOriginalData == null) {
      return r;
    }
    return indicesToOriginalData[r];
  }
}
