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
package com.smoothcsv.core.macro.apiimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.core.macro.api.CellVisitor;
import com.smoothcsv.core.macro.api.CsvSheet;
import com.smoothcsv.core.macro.api.Range;
import com.smoothcsv.core.sort.BlanksOption;
import com.smoothcsv.core.sort.Order;
import com.smoothcsv.core.sort.SortCriteria;
import com.smoothcsv.core.sort.ValueType;
import com.smoothcsv.swing.gridsheet.model.CellConsumer;
import com.smoothcsv.swing.gridsheet.model.CellRect;
import command.grid.PasteCommand;
import command.grid.PasteCommand.PasteRange;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;

/**
 * @author kohii
 */
public class RangeImpl extends APIBase implements Range {

  private CsvSheetImpl parent;
  private int row;
  private int column;
  private int numRows;
  private int numColumns;

  RangeImpl(CsvSheetImpl csvSheet, int row, int column, int numRows, int numColumns) {
    this.parent = csvSheet;
    this.row = row;
    this.column = column;
    this.numRows = numRows;
    this.numColumns = numColumns;
  }

  @Override
  public Range activate() {
    parent.setActiveRange(this);
    return this;
  }

  @Override
  public Range clear() {
    CsvGridSheetPane gridSheetPane = getGridSheet();
    try (EditTransaction tran = gridSheetPane.transaction()) {
      forEachCell(new ICellConsumer() {
        @Override
        public boolean accept(int row, int column) {
          gridSheetPane.setValueAt("", row - 1, column - 1);
          return true;
        }
      });
    }
    return this;
  }

  @Override
  public Range getCell(int row, int column) {
    return new RangeImpl(parent, this.row + row - 1, this.column + column - 1, 1, 1);
  }

  @Override
  public int getRow() {
    return row;
  }

  @Override
  public int getColumn() {
    return column;
  }

  @Override
  public int getLastRow() {
    return row + numRows - 1;
  }

  @Override
  public int getLastColumn() {
    return column + numColumns - 1;
  }

  @Override
  public int getNumRows() {
    return numRows;
  }

  @Override
  public int getNumColumns() {
    return numColumns;
  }

  @Override
  public CsvSheet getSheet() {
    return parent;
  }

  @Override
  public String getValue() {
    return getGridSheet().getValueAt(row - 1, column - 1);
  }

  @Override
  public String[][] getValues() {
    CsvGridSheetPane gridSheet = getGridSheet();
    String[][] values = new String[numRows][];
    for (int r = row - 1, lastRow = getLastRow(); r < lastRow; r++) {
      String[] rowValues = new String[numColumns];
      for (int c = column - 1, lastColumn = getLastColumn(); c < lastColumn; c++) {
        rowValues[c] = gridSheet.getValueAt(r, c);
      }
      values[r] = rowValues;
    }
    return values;
  }

  @Override
  public boolean isBlank() {
    CsvGridSheetPane gridSheet = getGridSheet();
    MutableBoolean result = new MutableBoolean(true);
    forEachCell(new ICellConsumer() {
      @Override
      public boolean accept(int r, int c) {
        String val = gridSheet.getValueAt(r, c);
        if (val != null && !val.toString().isEmpty()) {
          result.setFalse();
          return false;
        }
        return true;
      }
    });
    return result.booleanValue();
  }

  @Override
  public void copyTo(Range destination) {
    copyTo(destination, false);
  }

  private void copyTo(Range destination, boolean move) {
    String[][] values = getValues();
    if (move) {
      clear();
    }
    List<List<String>> valueList = new ArrayList<>(values.length);
    for (int i = 0; i < values.length; i++) {
      valueList.add(Arrays.asList(values[i]));
    }
    RangeImpl destRangeImpl = (RangeImpl) destination;
    PasteRange pasteRange = new PasteRange() {
      @Override
      public int getRow() {
        return destination.getRow();
      }

      @Override
      public int getColumn() {
        return destination.getColumn();
      }

      @Override
      public void forEach(CellConsumer callback) {
        destRangeImpl.forEachCell(new ICellConsumer() {
          @Override
          public boolean accept(int row, int column) {
            callback.accept(row, column);
            return true;
          }
        });
      }
    };
    PasteCommand.paste(destRangeImpl.getGridSheet(), pasteRange, valueList, true);
  }

  @Override
  public void moveTo(Range destination) {
    copyTo(destination, true);
  }

  @Override
  public Range offset(int rowOffset, int columnOffset) {
    return new RangeImpl(parent, row + rowOffset, column + columnOffset, numRows, numColumns);
  }

  @Override
  public Range offset(int rowOffset, int columnOffset, int numRows, int numColumns) {
    return new RangeImpl(parent, row + rowOffset, column + columnOffset, numRows, numColumns);
  }

  @Override
  public Range setValue(String value) {
    getGridSheet().setValueAt(value, row - 1, column - 1);
    return this;
  }

  @Override
  public Range setValues(String[][] values) {
    CsvGridSheetPane gridSheet = getGridSheet();
    try (EditTransaction tran = gridSheet.transaction()) {
      forEachCell(new ICellConsumer() {
        @Override
        public boolean accept(int r, int c) {
          gridSheet.setValueAt(values[r - row][c - column], r - 1, c - 1);
          return true;
        }
      });
    }
    return this;
  }

  @Override
  public Range sort(Object sortSpecObj) {
    List<SortCriteria> sortCriterias = parseSortSpec(sortSpecObj);
    getGridSheet().getModel().sort(sortCriterias, toCellRect());
    return this;
  }

  @Override
  public void forEach(CellVisitor callback) {
    RangeImpl cell = new RangeImpl(parent, 1, 1, 1, 1);
    forEachCell(new ICellConsumer() {
      @Override
      public boolean accept(int row, int column) {
        cell.row = row;
        cell.column = column;
        Object retVal = callback.call(cell, ObjectUtils.toString(cell.getValue()), row, column);
        return retVal == null || retVal != Boolean.FALSE;
      }
    });
  }

  private List<SortCriteria> parseSortSpec(Object sortSpecObj) {
    if (sortSpecObj instanceof NativeObject) {
      NativeObject o = (NativeObject) sortSpecObj;
      SortCriteria c = new SortCriteria();
      Object column = o.get("column");
      if (column == null) {
        throw new IllegalArgumentException("'column' is required");
      } else {
        int intColumn;
        if (column instanceof Number) {
          intColumn = ((Number) column).intValue();
        } else {
          intColumn = Integer.parseInt(column.toString());
        }
        if (intColumn < 1 || getNumColumns() < intColumn) {
          throw new IllegalArgumentException("'column' is out of bounds");
        }
        c.setColumn(intColumn - 1);
      }
      Object ascending = o.get("ascending");
      if (ascending != null) {
        if (ascending instanceof Boolean) {
          c.setOrder((Boolean) ascending ? Order.ASCENDING : Order.DESCENDING);
        } else {
          throw new IllegalArgumentException("'ascending' must be a boolean");
        }
      }
      Object type = o.get("type");
      if (type != null) {
        if ("string".equalsIgnoreCase(type.toString())) {
          c.setType(ValueType.STRING);
        } else if ("number".equalsIgnoreCase(type.toString())) {
          c.setType(ValueType.NUMBER);
        } else {
          throw new IllegalArgumentException("'type' must be either 'string' or 'number'");
        }
      }
      Object blanksOption = o.get("blanks");
      if (blanksOption != null) {
        if ("first".equalsIgnoreCase(blanksOption.toString())) {
          c.setBlanksOption(BlanksOption.BLANKS_FIRST);
        } else if ("last".equalsIgnoreCase(blanksOption.toString())) {
          c.setBlanksOption(BlanksOption.BLANKS_LAST);
        } else if ("default".equalsIgnoreCase(blanksOption.toString())) {
          c.setBlanksOption(c.getOrder() == Order.ASCENDING ? BlanksOption.BLANKS_LAST
              : BlanksOption.BLANKS_FIRST);
        } else {
          throw new IllegalArgumentException("'blanks' must be either 'first' or 'last'");
        }
      }
      return Arrays.asList(c);
    } else if (sortSpecObj instanceof Number) {
      SortCriteria c = new SortCriteria();
      int intColumn = ((Number) sortSpecObj).intValue();
      if (intColumn < 1 || getNumColumns() < intColumn) {
        throw new IllegalArgumentException("'column' is out of bounds");
      }
      c.setColumn(intColumn - 1);
      return Arrays.asList(c);
    } else if (sortSpecObj instanceof NativeArray) {
      NativeArray array = (NativeArray) sortSpecObj;
      List<SortCriteria> ret = new ArrayList<>(1);
      for (int i = 0, len = array.size(); i < len; i++) {
        Object o = array.get(i);
        ret.addAll(parseSortSpec(o));
      }
      return ret;
    } else {
      throw new IllegalArgumentException(ScriptRuntime.toString(sortSpecObj));
    }
  }

  private CsvGridSheetPane getGridSheet() {
    return parent.getCsvSheetView().getGridSheetPane();
  }

  private void forEachCell(ICellConsumer cellConsumer) {
    for (int r = row, lastRow = getLastRow(); r <= lastRow; r++) {
      for (int c = column, lastColumn = getLastColumn(); c <= lastColumn; c++) {
        if (!cellConsumer.accept(r, c)) {
          return;
        }
      }
    }
  }

  // private boolean containsRowAt(int row) {
  // return getRow() <= row && row <= getLastRow();
  // }
  //
  // private boolean containsColumnAt(int column) {
  // return getColumn() <= column && column <= getLastColumn();
  // }
  //
  // private boolean containsCellAt(int row, int column) {
  // return containsRowAt(row) && containsColumnAt(column);
  // }

  private CellRect toCellRect() {
    return new CellRect(getRow() - 1, getColumn() - 1, getLastRow() - 1, getLastColumn() - 1);
  }

  private static interface ICellConsumer {
    boolean accept(int row, int column);
  }
}
