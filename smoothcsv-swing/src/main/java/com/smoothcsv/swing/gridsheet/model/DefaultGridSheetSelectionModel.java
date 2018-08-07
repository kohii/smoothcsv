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
package com.smoothcsv.swing.gridsheet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.event.EventListenerList;

import com.smoothcsv.commons.functions.IntRangeConsumer;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.event.GridSheetColumnHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetCornerHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetHeaderSelectionEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetRowHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionListener;

public class DefaultGridSheetSelectionModel implements GridSheetSelectionModel {

  private static final int MIN = -1;
  private static final int MAX = Integer.MAX_VALUE;

  // public static final int DEFAULT = 0;
  // public static final int CHANGE_ONLY_HORIZONTAL = 1;
  // public static final int CHANGE_ONLY_VERTICAL = 2;

  private GridSheetPane gridSheetPane;
  protected EventListenerList listenerList = new EventListenerList();
  protected EventListenerList headerListenerList = new EventListenerList();

  // main
  private int minRowIndex = MAX;
  private int maxRowIndex = MIN;
  private int minColumnIndex = MAX;
  private int maxColumnIndex = MIN;

  // additional
  private List<GridSheetCellRange> additionalSelections;
  private int additionalMinRowIndex = MAX;
  private int additionalMaxRowIndex = MIN;
  private int additionalMinColumnIndex = MAX;
  private int additionalMaxColumnIndex = MIN;

  // anchor
  private int rowAnchor = -1;
  private int columnAnchor = -1;

  // headers
  private boolean rowHeaderSelection = false;
  private boolean columnHeaderSelection = false;

  private boolean isAdjusting = false;
  private int firstAdjustedRowIndex = MAX;
  private int lastAdjustedRowIndex = MIN;
  private int firstAdjustedColumnIndex = MAX;
  private int lastAdjustedColumnIndex = MIN;
  private int firstChangedRowIndex = MAX;
  private int lastChangedRowIndex = MIN;
  private int firstChangedColumnIndex = MAX;
  private int lastChangedColumnIndex = MIN;
  private boolean hasRowHeaderSelectionChanged = false;
  private boolean hasColumnHeaderSelectionChanged = false;
  private boolean hasCornerHeaderSelectionChanged = false;

  // constructor
  public DefaultGridSheetSelectionModel(GridSheetPane gridSheetPane) {
    this.gridSheetPane = gridSheetPane;
    this.additionalSelections = new ArrayList<>(3);
    init();
  }

  // methods
  private void init() {
    setSelectionInterval(0, 0, 0, 0);
  }

  // adjust
  public boolean getValueIsAdjusting() {
    return isAdjusting;
  }

  public void setValueIsAdjusting(boolean isAdjusting) {
    if (isAdjusting != this.isAdjusting) {
      this.isAdjusting = isAdjusting;
      fireValueChanged(isAdjusting);
    }
  }

  // Updates first and last change indices
  private void markAsDirty(int r, int c) {
    if (r == -1 || c == -1) {
      return;
    }
    firstAdjustedRowIndex = Math.min(firstAdjustedRowIndex, r);
    lastAdjustedRowIndex = Math.max(lastAdjustedRowIndex, r);
    firstAdjustedColumnIndex = Math.min(firstAdjustedColumnIndex, c);
    lastAdjustedColumnIndex = Math.max(lastAdjustedColumnIndex, c);
  }

  // additional
  public void saveMainSelection() {
    GridSheetCellRange additionalSelection =
        new GridSheetCellRange(minRowIndex, maxRowIndex, minColumnIndex, maxColumnIndex);
    additionalSelections.add(additionalSelection);

    additionalMinRowIndex = Math.min(additionalMinRowIndex, additionalSelection.getFirstRow());
    additionalMaxRowIndex = Math.max(additionalMaxRowIndex, additionalSelection.getLastRow());
    additionalMinColumnIndex =
        Math.min(additionalMinColumnIndex, additionalSelection.getFirstColumn());
    additionalMaxColumnIndex =
        Math.max(additionalMaxColumnIndex, additionalSelection.getLastColumn());
  }

  // /////////////////
  // headers
  // /////////////////
  public boolean isRowHeaderSelected() {
    return rowHeaderSelection;
  }

  public boolean isColumnHeaderSelected() {
    return columnHeaderSelection;
  }

  public void setRowHeaderSelected(boolean isSelected) {
    boolean old = rowHeaderSelection;
    rowHeaderSelection = isSelected;
    if (isSelected != old) {
      firstAdjustedRowIndex = minRowIndex;
      lastAdjustedRowIndex = maxRowIndex;
      hasRowHeaderSelectionChanged = true;
      hasCornerHeaderSelectionChanged = true;
    }
    fireValueChanged();
  }

  public void setColumnHeaderSelected(boolean isSelected) {
    boolean old = columnHeaderSelection;
    columnHeaderSelection = isSelected;
    if (isSelected != old) {
      firstAdjustedColumnIndex = minColumnIndex;
      lastAdjustedColumnIndex = maxColumnIndex;
      hasColumnHeaderSelectionChanged = true;
      hasCornerHeaderSelectionChanged = true;
    }
    fireValueChanged();
  }

  // public void setAllSelected(boolean isSelected, boolean resetAnchor) {
  //
  // setValueIsAdjusting(true);
  //
  // clearAdditionalSelection();
  // columnHeaderSelection = isSelected;
  // rowHeaderSelection = isSelected;
  //
  // if (isSelected) {
  // if (resetAnchor) {
  // setSelectionInterval(0, 0, table.getRowCount() - 1,
  // table.getColumnCount() - 1);
  // } else {
  // setSelectionIntervalNoChangeAnchor(0, 0,
  // table.getRowCount() - 1, table.getColumnCount() - 1);
  // }
  // }
  //
  // setValueIsAdjusting(false);
  // }
  // //////////////////////
  // selection setter
  // //////////////////////
  public void setSelectionInterval(int rowAnchor, int columnAnchor, int rowLead, int columnLead) {

    int minRowIndex = Math.min(rowAnchor, rowLead);
    int maxRowIndex = Math.max(rowAnchor, rowLead);
    int minColumnIndex = Math.min(columnAnchor, columnLead);
    int maxColumnIndex = Math.max(columnAnchor, columnLead);

    if (minRowIndex < 0 || minColumnIndex < 0 || gridSheetPane.getRowCount() <= maxRowIndex
        || gridSheetPane.getColumnCount() <= maxColumnIndex) {
      throw new IllegalArgumentException(
          String.format("rowAnchor=%d, columnAnchor=%d, rowLead=%d, columnLead=%d", rowAnchor,
              columnAnchor, rowLead, columnLead));
    }

    if (minRowIndex != this.minRowIndex || maxRowIndex != this.maxRowIndex) {
      hasRowHeaderSelectionChanged = true;
    }
    if (minColumnIndex != this.minColumnIndex || maxColumnIndex != this.maxColumnIndex) {
      hasColumnHeaderSelectionChanged = true;
    }

    if (minRowIndex != this.minRowIndex || minColumnIndex != this.minColumnIndex
        || maxRowIndex != this.maxRowIndex || maxColumnIndex != this.maxColumnIndex) {
      markAsDirty(minRowIndex, minColumnIndex);
      markAsDirty(this.minRowIndex, this.minColumnIndex);
      markAsDirty(maxRowIndex, maxColumnIndex);
      markAsDirty(this.maxRowIndex, this.maxColumnIndex);
      this.minRowIndex = minRowIndex;
      this.minColumnIndex = minColumnIndex;
      this.maxRowIndex = maxRowIndex;
      this.maxColumnIndex = maxColumnIndex;
    }

    updateAnchor(rowAnchor, columnAnchor);

    clearAdditionalSelection();

    fireValueChanged();
  }

  public void setSelectionIntervalNoChangeAnchor(int minRowIndex, int minColumnIndex,
                                                 int maxRowIndex, int maxColumnIndex) {

    if (minRowIndex < 0 || minColumnIndex < 0 || gridSheetPane.getRowCount() <= maxRowIndex
        || gridSheetPane.getColumnCount() <= maxColumnIndex || minRowIndex > maxRowIndex
        || minColumnIndex > maxColumnIndex) {
      throw new IllegalArgumentException(
          String.format("minRowIndex=%d, minColumnIndex=%d, maxRowIndex=%d, maxColumnIndex=%d",
              minRowIndex, minColumnIndex, maxRowIndex, maxColumnIndex));
    }

    if (!isAdditionallySelected() && minRowIndex == this.minRowIndex
        && minColumnIndex == this.minColumnIndex && maxRowIndex == this.maxRowIndex
        && maxColumnIndex == this.maxColumnIndex) {
      return;
    }

    if (minRowIndex != this.minRowIndex || maxRowIndex != this.maxRowIndex) {
      hasRowHeaderSelectionChanged = true;
    }
    if (minColumnIndex != this.minColumnIndex || maxColumnIndex != this.maxColumnIndex) {
      hasColumnHeaderSelectionChanged = true;
    }

    clearAdditionalSelection();

    if (minRowIndex != this.minRowIndex || minColumnIndex != this.minColumnIndex
        || maxRowIndex != this.maxRowIndex || maxColumnIndex != this.maxColumnIndex) {
      markAsDirty(minRowIndex, minColumnIndex);
      markAsDirty(this.minRowIndex, this.minColumnIndex);
      markAsDirty(maxRowIndex, maxColumnIndex);
      markAsDirty(this.maxRowIndex, this.maxColumnIndex);
      this.minRowIndex = minRowIndex;
      this.minColumnIndex = minColumnIndex;
      this.maxRowIndex = maxRowIndex;
      this.maxColumnIndex = maxColumnIndex;
    }

    clearAdditionalSelection();

    fireValueChanged();
  }

  public void selectEntireRow() {
    int lastColumnIndex = gridSheetPane.getColumnCount() - 1;
    this.minColumnIndex = 0;
    this.maxColumnIndex = lastColumnIndex;

    for (GridSheetCellRange selBounds : additionalSelections) {
      selBounds.firstColumn = 0;
      selBounds.lastColumn = lastColumnIndex;
    }
    markAsDirty(getMinRowSelectionIndex(), 0);
    markAsDirty(getMaxRowSelectionIndex(), lastColumnIndex);
    hasColumnHeaderSelectionChanged = true;
    fireValueChanged();
  }

  public void selectEntireColumn() {
    int lastRowIndex = gridSheetPane.getRowCount() - 1;
    this.minRowIndex = 0;
    this.maxRowIndex = lastRowIndex;

    for (GridSheetCellRange selBounds : additionalSelections) {
      selBounds.firstRow = 0;
      selBounds.lastRow = lastRowIndex;
    }
    markAsDirty(0, getMinColumnSelectionIndex());
    markAsDirty(lastRowIndex, getMaxColumnSelectionIndex());
    hasRowHeaderSelectionChanged = true;
    fireValueChanged();
  }

  public void addSelectionInterval(int rowAnchor, int columnAnchor, int rowLead, int columnLead) {

    if (!isAdditionallySelected()) {
      markAsDirty(minRowIndex, minColumnIndex);
      markAsDirty(maxRowIndex, maxColumnIndex);
    }

    saveMainSelection();

    int minRowIndex = Math.min(rowAnchor, rowLead);
    int maxRowIndex = Math.max(rowAnchor, rowLead);
    int minColumnIndex = Math.min(columnAnchor, columnLead);
    int maxColumnIndex = Math.max(columnAnchor, columnLead);

    if (minRowIndex < 0 || minColumnIndex < 0 || gridSheetPane.getRowCount() <= maxRowIndex
        || gridSheetPane.getColumnCount() <= maxColumnIndex) {
      throw new IllegalArgumentException(
          String.format("rowAnchor=%d, columnAnchor=%d, rowLead=%d, columnLead=%d", rowAnchor,
              columnAnchor, rowLead, columnLead));
    }

    if (minRowIndex != this.minRowIndex || maxRowIndex != this.maxRowIndex) {
      hasRowHeaderSelectionChanged = true;
    }
    if (minColumnIndex != this.minColumnIndex || maxColumnIndex != this.maxColumnIndex) {
      hasColumnHeaderSelectionChanged = true;
    }
    this.minRowIndex = minRowIndex;
    this.minColumnIndex = minColumnIndex;
    this.maxRowIndex = maxRowIndex;
    this.maxColumnIndex = maxColumnIndex;

    markAsDirty(rowAnchor, columnAnchor);
    markAsDirty(rowLead, columnLead);

    updateAnchor(rowAnchor, columnAnchor);

    fireValueChanged();
  }

  // public void extendSelection(int row, int column) {
  // if (!contains(rowAnchor, columnAnchor)) {
  // int minRowIndex = Math.min(row, rowAnchor);
  // int maxRowIndex = Math.max(row, rowAnchor);
  // int minColumnIndex = Math.min(column, columnAnchor);
  // int maxColumnIndex = Math.max(column, columnAnchor);
  // clearAdditionalSelection();
  // markAsDirty(minRowIndex, minColumnIndex);
  // markAsDirty(maxRowIndex, maxColumnIndex);
  // markAsDirty(this.minRowIndex, this.minColumnIndex);
  // markAsDirty(this.maxRowIndex, this.maxColumnIndex);
  // this.minRowIndex = minRowIndex;
  // this.minColumnIndex = minColumnIndex;
  // this.maxRowIndex = maxRowIndex;
  // this.maxColumnIndex = maxColumnIndex;
  // } else {
  // markAsDirty(this.minRowIndex, this.minColumnIndex);
  // markAsDirty(this.maxRowIndex, this.maxColumnIndex);
  // if (row < minRowIndex || maxRowIndex < row) {
  // hasRowHeaderSelectionChanged = true;
  // }
  // if (column < minColumnIndex || maxColumnIndex < column) {
  // hasColumnHeaderSelectionChanged = true;
  // }
  // clearAdditionalSelection();
  // if (rowAnchor == minRowIndex) {
  // maxRowIndex = row;
  // } else if (rowAnchor == maxRowIndex) {
  // minRowIndex = row;
  // }
  // if (columnAnchor == minColumnIndex) {
  // maxColumnIndex = column;
  // } else if (columnAnchor == maxColumnIndex) {
  // minColumnIndex = column;
  // }
  // markAsDirty(this.minRowIndex, this.minColumnIndex);
  // markAsDirty(this.maxRowIndex, this.maxColumnIndex);
  // }
  // fireValueChanged();
  // }
  //
  // public void stretchSelection(int row, int column, int option) {
  // if (option != CHANGE_ONLY_HORIZONTAL) {
  // int minRowIndex = Math.min(row, rowAnchor);
  // int maxRowIndex = Math.max(row, rowAnchor);
  // }
  //
  // if (option != CHANGE_ONLY_VERTICAL) {
  // int minColumnIndex = Math.min(column, columnAnchor);
  // int maxColumnIndex = Math.max(column, columnAnchor);
  // }
  // clearAdditionalSelection();
  // markAsDirty(minRowIndex, minColumnIndex);
  // markAsDirty(maxRowIndex, maxColumnIndex);
  // markAsDirty(this.minRowIndex, this.minColumnIndex);
  // markAsDirty(this.maxRowIndex, this.maxColumnIndex);
  // this.minRowIndex = minRowIndex;
  // this.minColumnIndex = minColumnIndex;
  // this.maxRowIndex = maxRowIndex;
  // this.maxColumnIndex = maxColumnIndex;
  // fireValueChanged();
  // }
  public void changeLeadSelection(int row, int column, int option) {

    if (row < 0 || column < 0 || gridSheetPane.getRowCount() <= row
        || gridSheetPane.getColumnCount() <= column) {
      throw new IllegalArgumentException(String.format("row=%d, column=%d", row, column));
    }

    int minRowIndex;
    int maxRowIndex;
    int minColumnIndex;
    int maxColumnIndex;
    if (option != CHANGE_ONLY_HORIZONTAL) {
      hasRowHeaderSelectionChanged = true;
      minRowIndex = Math.min(row, rowAnchor);
      maxRowIndex = Math.max(row, rowAnchor);
    } else {
      minRowIndex = this.minRowIndex;
      maxRowIndex = this.maxRowIndex;
    }
    if (option != CHANGE_ONLY_VERTICAL) {
      hasColumnHeaderSelectionChanged = true;
      minColumnIndex = Math.min(column, columnAnchor);
      maxColumnIndex = Math.max(column, columnAnchor);
    } else {
      minColumnIndex = this.minColumnIndex;
      maxColumnIndex = this.maxColumnIndex;
    }

    markAsDirty(minRowIndex, minColumnIndex);
    markAsDirty(maxRowIndex, maxColumnIndex);
    markAsDirty(this.minRowIndex, this.minColumnIndex);
    markAsDirty(this.maxRowIndex, this.maxColumnIndex);
    this.minRowIndex = minRowIndex;
    this.minColumnIndex = minColumnIndex;
    this.maxRowIndex = maxRowIndex;
    this.maxColumnIndex = maxColumnIndex;
    fireValueChanged();
  }

  public void updateAnchor(int rowAnchor, int columnAnchor) {
    if (!contains(rowAnchor, columnAnchor)) {
      throw new IllegalArgumentException(rowAnchor + ":" + columnAnchor);
    }
    if (rowAnchor != this.rowAnchor || columnAnchor != this.columnAnchor) {
      markAsDirty(rowAnchor, columnAnchor);
      markAsDirty(this.rowAnchor, this.columnAnchor);

      this.rowAnchor = rowAnchor;
      this.columnAnchor = columnAnchor;
      Object[] listeners = listenerList.getListenerList();
      GridSheetFocusEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == GridSheetFocusListener.class) {
          if (e == null) {
            e = new GridSheetFocusEvent(this, rowAnchor, columnAnchor, isAdjusting);
          }
          ((GridSheetFocusListener) listeners[i + 1]).valueChanged(e);
        }
      }
      fireValueChanged();
    }
  }

  // clear
  private void clearAdditionalSelection() {
    if (!additionalSelections.isEmpty()) {
      markAsDirty(additionalMinRowIndex, additionalMinColumnIndex);
      markAsDirty(additionalMaxRowIndex, additionalMaxColumnIndex);
      additionalSelections.clear();
      additionalMinRowIndex = MAX;
      additionalMaxRowIndex = MIN;
      additionalMinColumnIndex = MAX;
      additionalMaxColumnIndex = MIN;
      hasRowHeaderSelectionChanged = true;
      hasColumnHeaderSelectionChanged = true;
    }
  }

  public void clearHeaderSelection() {
    setRowHeaderSelected(false);
    setColumnHeaderSelected(false);
  }

  public void clearSelection() {
    clearAdditionalSelection();
    clearHeaderSelection();
    init();
    fireValueChanged();
  }

  // ////////////////////////
  // selection getter
  // ////////////////////////
  public boolean isSingleCellSelected() {
    int minR = Math.max(0, getMainMinRowSelectionIndex());
    int maxR = Math.min(gridSheetPane.getRowCount() - 1, getMainMaxRowSelectionIndex());
    if (minR != maxR) {
      return false;
    }
    int minC = Math.max(0, getMainMinColumnSelectionIndex());
    int maxC = Math.min(gridSheetPane.getColumnCount() - 1, getMainMaxColumnSelectionIndex());
    if (minC != maxC) {
      return false;
    }
    if (isAdditionallySelected()) {
      for (GridSheetCellRange bounds : additionalSelections) {
        if (bounds.getLastRow() != minR || bounds.getFirstRow() != minR
            || bounds.getLastColumn() != minC || bounds.getFirstColumn() != minC) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isCellSelected(int row, int column) {
    if ((minRowIndex <= row && row <= maxRowIndex || isColumnHeaderSelected())
        && (minColumnIndex <= column && column <= maxColumnIndex || isRowHeaderSelected())) {
      return true;
    }
    for (GridSheetCellRange as : additionalSelections) {
      if (as.contains(row, column)) {
        return true;
      }
    }
    return false;
  }

  public boolean isRowSelected(int row) {
    if (isColumnHeaderSelected()) {
      return true;
    }
    if (minRowIndex <= row && row <= maxRowIndex
        && minColumnIndex < gridSheetPane.getColumnCount()) {
      return true;
    }
    if (additionalSelections == null) {
      return false;
    }
    for (GridSheetCellRange as : additionalSelections) {
      if (as.containsRowAt(row) && as.getFirstColumn() < gridSheetPane.getColumnCount()) {
        return true;
      }
    }
    return false;
  }

  public boolean isColumnSelected(int column) {
    if (isRowHeaderSelected()) {
      return true;
    }
    if (minColumnIndex <= column && column <= maxColumnIndex
        && minRowIndex < gridSheetPane.getRowCount()) {
      return true;
    }
    if (additionalSelections == null) {
      return false;
    }
    for (GridSheetCellRange as : additionalSelections) {
      if (as.containsColumnAt(column) && as.getFirstRow() < gridSheetPane.getRowCount()) {
        return true;
      }
    }
    return false;
  }

  public boolean isAdditionallySelected() {
    return !additionalSelections.isEmpty();
  }

  public boolean isAnchor(int row, int column) {
    return row == this.rowAnchor && column == this.columnAnchor;
  }

  public int getMinRowSelectionIndex() {
    return Math.min(minRowIndex, additionalMinRowIndex);
  }

  public int getMaxRowSelectionIndex() {
    return Math.max(maxRowIndex, additionalMaxRowIndex);
  }

  public int getMinColumnSelectionIndex() {
    return Math.min(minColumnIndex, additionalMinColumnIndex);
  }

  public int getMaxColumnSelectionIndex() {
    return Math.max(maxColumnIndex, additionalMaxColumnIndex);
  }

  public int getMainMinRowSelectionIndex() {
    return minRowIndex;
  }

  public int getMainMaxRowSelectionIndex() {
    return maxRowIndex;
  }

  public int getMainMinColumnSelectionIndex() {
    return minColumnIndex;
  }

  public int getMainMaxColumnSelectionIndex() {
    return maxColumnIndex;
  }

  public int getRowAnchorIndex() {
    return clipToRowSize(rowAnchor);
  }

  public int getColumnAnchorIndex() {
    return clipToColumnSize(columnAnchor);
  }

  public int getSelectedRowCount() {
    int min = Math.max(0, getMinRowSelectionIndex());
    int max = Math.min(gridSheetPane.getRowCount() - 1, getMaxRowSelectionIndex());

    if (!additionalSelections.isEmpty()) {
      int ret = 0;
      for (int i = min; i <= max; i++) {
        if (isRowSelected(i)) {
          ret++;
        }
      }
      return ret;
    } else {
      if (max < min) {
        return 0;
      } else {
        return max - min + 1;
      }
    }
  }

  public int getSelectedColumnCount() {
    int min = Math.max(0, getMinColumnSelectionIndex());
    int max = Math.min(gridSheetPane.getColumnCount() - 1, getMaxColumnSelectionIndex());

    if (!additionalSelections.isEmpty()) {
      int ret = 0;
      for (int i = min; i <= max; i++) {
        if (isColumnSelected(i)) {
          ret++;
        }
      }
      return ret;
    } else {
      if (max < min) {
        return 0;
      } else {
        return max - min + 1;
      }
    }
  }

  public int[] getSelectedRows() {
    int iMin = clipToRowSize(minRowIndex);
    int iMax = clipToRowSize(maxRowIndex);

    if ((iMin == -1) || (iMax == -1)) {
      return new int[0];
    }

    if (additionalSelections != null) {
      for (GridSheetCellRange as : additionalSelections) {
        iMin = Math.min(iMin, as.getFirstRow());
        iMax = Math.max(iMax, as.getLastRow());
      }
    }

    iMax = Math.min(iMax, gridSheetPane.getRowCount() - 1);

    int[] rvTmp = new int[1 + (iMax - iMin)];
    int n = 0;
    for (int i = iMin; i <= iMax; i++) {
      if (isRowSelected(i)) {
        rvTmp[n++] = i;
      }
    }
    int[] rv = new int[n];
    System.arraycopy(rvTmp, 0, rv, 0, n);
    return rv;
  }

  public int[] getSelectedColumns() {
    int iMin = clipToColumnSize(minColumnIndex);
    int iMax = clipToColumnSize(maxColumnIndex);

    if ((iMin == -1) || (iMax == -1)) {
      return new int[0];
    }

    if (additionalSelections != null) {
      for (GridSheetCellRange as : additionalSelections) {
        iMin = Math.min(iMin, as.getFirstColumn());
        iMax = Math.max(iMax, as.getLastColumn());
      }
    }

    int[] rvTmp = new int[1 + (iMax - iMin)];
    int n = 0;
    for (int i = iMin; i <= iMax; i++) {
      if (isColumnSelected(i)) {
        rvTmp[n++] = i;
      }
    }
    int[] rv = new int[n];
    System.arraycopy(rvTmp, 0, rv, 0, n);
    return rv;
  }

  /**
   * @return the additionalSelections
   */
  public List<GridSheetCellRange> getAdditionalSelections() {
    return Collections.unmodifiableList(additionalSelections);
  }

  // export & import
  public GridSheetSelectionSnapshot exportSelection() {
    GridSheetCellRange[] selections = new GridSheetCellRange[additionalSelections.size() + 1];
    selections[0] =
        new GridSheetCellRange(minRowIndex, maxRowIndex, minColumnIndex, maxColumnIndex);
    for (int i = 1; i < selections.length; i++) {
      selections[i] = additionalSelections.get(i - 1);
    }
    return new GridSheetSelectionSnapshot(rowAnchor, columnAnchor, selections,
        isRowHeaderSelected(), isColumnHeaderSelected());
  }

  public void importSelection(GridSheetSelectionSnapshot selectionContainer) {
    // int oldMinR = getMinRowSelectionIndex();
    // int oldMaxR = getMaxColumnSelectionIndex();
    // int oldMinC = getMinColumnSelectionIndex();
    // int oldMaxC = getMaxColumnSelectionIndex();

    setValueIsAdjusting(true);

    GridSheetCellRange[] selections = selectionContainer.getSelections();
    GridSheetCellRange s = selections[selections.length - 1];
    setSelectionIntervalNoChangeAnchor(clipToRowSize(s.getFirstRow()),
        clipToColumnSize(s.getFirstColumn()), clipToRowSize(s.getLastRow()),
        clipToColumnSize(s.getLastColumn()));
    for (int i = selections.length - 2; 0 <= i; i--) {
      s = selections[i];
      addSelectionInterval(clipToRowSize(s.getFirstRow()), clipToColumnSize(s.getFirstColumn()),
          clipToRowSize(s.getLastRow()), clipToColumnSize(s.getLastColumn()));
    }

    updateAnchor(clipToRowSize(selectionContainer.getRowAnchor()),
        clipToColumnSize(selectionContainer.getColumnAnchor()));

    setValueIsAdjusting(false);
  }

  // events
  public void addGridFocusListener(GridSheetFocusListener l) {
    listenerList.add(GridSheetFocusListener.class, l);
  }

  public void removeGridFocusListener(GridSheetFocusListener l) {
    listenerList.remove(GridSheetFocusListener.class, l);
  }

  public void addGridSelectionListener(GridSheetSelectionListener l) {
    listenerList.add(GridSheetSelectionListener.class, l);
  }

  public void removeGridSelectionListener(GridSheetSelectionListener l) {
    listenerList.remove(GridSheetSelectionListener.class, l);
  }

  public void addColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l) {
    headerListenerList.add(GridSheetColumnHeaderSelectionListener.class, l);
  }

  public void removeColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l) {
    headerListenerList.remove(GridSheetColumnHeaderSelectionListener.class, l);
  }

  public void addRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l) {
    headerListenerList.add(GridSheetRowHeaderSelectionListener.class, l);
  }

  public void removeRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l) {
    headerListenerList.remove(GridSheetRowHeaderSelectionListener.class, l);
  }

  public void addCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l) {
    headerListenerList.add(GridSheetCornerHeaderSelectionListener.class, l);
  }

  public void removeCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l) {
    headerListenerList.remove(GridSheetCornerHeaderSelectionListener.class, l);

  }

  public void GridCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l) {
    headerListenerList.remove(GridSheetCornerHeaderSelectionListener.class, l);
  }

  /**
   * Notifies listeners that we have ended a series of adjustments.
   */
  protected void fireValueChanged(boolean isAdjusting) {
    if (lastChangedRowIndex == MIN && lastChangedColumnIndex == MIN && !hasRowHeaderSelectionChanged
        && !hasColumnHeaderSelectionChanged && !hasCornerHeaderSelectionChanged) {
      return;
    }

    /*
     * Change the values before sending the event to the listeners in case the event causes a
     * listener to make another change to the selection.
     */
    int oldFirstChangedRowIndex = firstChangedRowIndex;
    int oldFirstChangedColumnIndex = firstChangedColumnIndex;
    int oldLastChangedRowIndex = lastChangedRowIndex;
    int oldLastChangedColumnIndex = lastChangedColumnIndex;
    firstChangedRowIndex = MAX;
    firstChangedColumnIndex = MAX;
    lastChangedRowIndex = MIN;
    lastChangedColumnIndex = MIN;
    boolean oldRowHeaderSelectionChanged = hasRowHeaderSelectionChanged;
    boolean oldColumnHeaderSelectionChanged = hasColumnHeaderSelectionChanged;
    boolean oldCornerHeaderSelectionChanged = hasCornerHeaderSelectionChanged;
    hasRowHeaderSelectionChanged = false;
    hasColumnHeaderSelectionChanged = false;
    hasCornerHeaderSelectionChanged = false;

    fireValueChanged(oldFirstChangedRowIndex, oldFirstChangedColumnIndex, oldLastChangedRowIndex,
        oldLastChangedColumnIndex, oldRowHeaderSelectionChanged, oldColumnHeaderSelectionChanged,
        oldCornerHeaderSelectionChanged, isAdjusting);
  }

  protected void fireValueChanged(int firstRowIndex, int firstColumnIndex, int lastRowIndex,
                                  int lastColumnIndex, boolean hasRowHeaderSelectionChanged,
                                  boolean hasColumnHeaderSelectionChanged, boolean hasCornerHeaderSelectionChanged) {
    fireValueChanged(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex,
        hasRowHeaderSelectionChanged, hasColumnHeaderSelectionChanged,
        hasCornerHeaderSelectionChanged, getValueIsAdjusting());
  }

  protected void fireValueChanged(int firstRowIndex, int firstColumnIndex, int lastRowIndex,
                                  int lastColumnIndex, boolean hasRowHeaderSelectionChanged,
                                  boolean hasColumnHeaderSelectionChanged, boolean hasCornerHeaderSelectionChanged,
                                  boolean isAdjusting) {
    if (lastRowIndex < firstRowIndex && lastColumnIndex < firstColumnIndex) {
      return;
    }
    {
      Object[] listeners = listenerList.getListenerList();
      GridSheetSelectionEvent e = null;
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == GridSheetSelectionListener.class) {
          if (e == null) {
            e = new GridSheetSelectionEvent(this, firstRowIndex, firstColumnIndex, lastRowIndex,
                lastColumnIndex, isAdjusting);
          }
          ((GridSheetSelectionListener) listeners[i + 1]).selectionChanged(e);
        }
      }
    }

    {
      if (hasRowHeaderSelectionChanged) {
        Object[] listeners = headerListenerList.getListenerList();
        GridSheetHeaderSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == GridSheetRowHeaderSelectionListener.class) {
            if (e == null) {
              e = new GridSheetHeaderSelectionEvent(this, firstRowIndex, lastRowIndex,
                  isRowHeaderSelected(), isAdjusting);
            }
            ((GridSheetRowHeaderSelectionListener) listeners[i + 1]).headersSelectionChanged(e);
          }
        }
      }
    }

    {
      if (hasColumnHeaderSelectionChanged) {
        Object[] listeners = headerListenerList.getListenerList();
        GridSheetHeaderSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == GridSheetColumnHeaderSelectionListener.class) {
            if (e == null) {
              e = new GridSheetHeaderSelectionEvent(this, firstColumnIndex, lastColumnIndex,
                  isColumnHeaderSelected(), isAdjusting);
            }
            ((GridSheetColumnHeaderSelectionListener) listeners[i + 1]).headersSelectionChanged(e);
          }
        }
      }
    }

    {
      if (hasCornerHeaderSelectionChanged) {
        Object[] listeners = headerListenerList.getListenerList();
        GridSheetHeaderSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == GridSheetCornerHeaderSelectionListener.class) {
            if (e == null) {
              e = new GridSheetHeaderSelectionEvent(this, -1, -1,
                  isRowHeaderSelected() && isColumnHeaderSelected(), isAdjusting);
            }
            ((GridSheetCornerHeaderSelectionListener) listeners[i + 1]).headersSelectionChanged(e);
          }
        }
      }
    }

  }

  private void fireValueChanged() {
    if (lastAdjustedRowIndex == MIN && lastAdjustedColumnIndex == MIN) {
      return;
    }
    /*
     * If getValueAdjusting() is true, (eg. during a drag opereration) record the bounds of the
     * changes so that, when the drag finishes (and setValueAdjusting(false) is called) we can post
     * a single event with bounds covering all of these individual adjustments.
     */
    if (getValueIsAdjusting()) {
      firstChangedRowIndex = Math.min(firstChangedRowIndex, firstAdjustedRowIndex);
      firstChangedColumnIndex = Math.min(firstChangedColumnIndex, firstAdjustedColumnIndex);
      lastChangedRowIndex = Math.max(lastChangedRowIndex, lastAdjustedRowIndex);
      lastChangedColumnIndex = Math.max(lastChangedColumnIndex, lastAdjustedColumnIndex);
    }
    /*
     * Change the values before sending the event to the listeners in case the event causes a
     * listener to make another change to the selection.
     */
    int oldFirstAdjustedRowIndex = firstAdjustedRowIndex;
    int oldFirstAdjustedColumnIndex = firstAdjustedColumnIndex;
    int oldLastAdjustedRowIndex = lastAdjustedRowIndex;
    int oldLastAdjustedColumnIndex = lastAdjustedColumnIndex;
    firstAdjustedRowIndex = MAX;
    firstAdjustedColumnIndex = MAX;
    lastAdjustedRowIndex = MIN;
    lastAdjustedColumnIndex = MIN;

    boolean oldRowHeaderSelectionChanged = hasRowHeaderSelectionChanged;
    boolean oldColumnHeaderSelectionChanged = hasColumnHeaderSelectionChanged;
    boolean oldCornerHeaderSelectionChanged = hasCornerHeaderSelectionChanged;
    hasRowHeaderSelectionChanged = false;
    hasColumnHeaderSelectionChanged = false;
    hasCornerHeaderSelectionChanged = false;

    fireValueChanged(oldFirstAdjustedRowIndex, oldFirstAdjustedColumnIndex, oldLastAdjustedRowIndex,
        oldLastAdjustedColumnIndex, oldRowHeaderSelectionChanged, oldColumnHeaderSelectionChanged,
        oldCornerHeaderSelectionChanged);
  }

  // private
  // private boolean contains(int rowAnchor, int rowLead, int columnAnchor,
  // int columnLead) {
  // int minR = Math.min(rowAnchor, rowLead);
  // int maxR = Math.max(rowAnchor, rowLead);
  // int minC = Math.min(columnAnchor, columnLead);
  // int maxC = Math.max(columnAnchor, columnLead);
  // return minRowIndex <= minR && maxR <= maxRowIndex
  // && minColumnIndex <= minC && maxC <= maxColumnIndex;
  // }

  private boolean contains(int row, int column) {
    if (mainSelectionContains(row, column)) {
      return true;
    }
    for (int i = 0; i < additionalSelections.size(); i++) {
      GridSheetCellRange as = additionalSelections.get(i);
      if ((as.getFirstRow() <= row && row <= as.getLastRow() || isColumnHeaderSelected())
          && (as.getFirstColumn() <= column && column <= as.getLastColumn()
          || isRowHeaderSelected())) {
        return true;
      }
    }
    return false;
  }

  private boolean mainSelectionContains(int row, int column) {
    return (minRowIndex <= row && row <= maxRowIndex || isColumnHeaderSelected())
        && (minColumnIndex <= column && column <= maxColumnIndex || isRowHeaderSelected());
  }

  // Utilities

  public void forEachSelectedCell(CellConsumer callback) {
    int minR = getMinRowSelectionIndex();
    int maxR = getMaxRowSelectionIndex();
    int minC = getMinColumnSelectionIndex();
    int maxC = getMaxColumnSelectionIndex();

    if (isAdditionallySelected()) {
      for (int r = minR; r <= maxR; r++) {
        for (int c = minC; c <= maxC; c++) {
          if (isCellSelected(r, c)) {
            callback.accept(r, c);
          }
        }
      }
    } else {
      for (int r = minR; r <= maxR; r++) {
        for (int c = minC; c <= maxC; c++) {
          callback.accept(r, c);
        }
      }
    }
  }

  public void forEachSelectedRows(IntConsumer callback) {
    int minR = getMinRowSelectionIndex();
    int maxR = getMaxRowSelectionIndex();
    if (isAdditionallySelected()) {
      for (int i = minR; i <= maxR; i++) {
        if (isRowSelected(i)) {
          callback.accept(i);
        }
      }
    } else {
      for (int i = minR; i <= maxR; i++) {
        callback.accept(i);
      }
    }
  }

  public void forEachSelectedColumns(IntConsumer callback) {
    int minC = getMinColumnSelectionIndex();
    int maxC = getMaxColumnSelectionIndex();
    if (isAdditionallySelected()) {
      for (int i = minC; i <= maxC; i++) {
        if (isColumnSelected(i)) {
          callback.accept(i);
        }
      }
    } else {
      for (int i = minC; i <= maxC; i++) {
        callback.accept(i);
      }
    }
  }

  public void forEachSelectedRowsAsBlock(IntRangeConsumer callback) {
    forEachSelectedRowsAsBlock(callback, false);
  }

  public void forEachSelectedRowsAsBlock(IntRangeConsumer callback, boolean reverse) {
    int iMin = clipToRowSize(getMinRowSelectionIndex());
    int iMax = clipToRowSize(getMaxRowSelectionIndex());

    if ((iMin == -1) || (iMax == -1)) {
      return;
    }

    if (!reverse) {
      int fromIndex = -1;
      boolean isPrevSelected = false;
      for (int i = iMin; i <= iMax; i++) {
        if (isRowSelected(i)) {
          if (!isPrevSelected) {
            fromIndex = i;
          }
          isPrevSelected = true;
        } else {
          if (isPrevSelected) {
            callback.accept(fromIndex, i - 1);
            isPrevSelected = false;
          }
        }
      }
      if (isPrevSelected) {
        callback.accept(fromIndex, iMax);
      }
    } else {
      int toIndex = -1;
      boolean isPrevSelected = false;
      for (int i = iMax; i >= iMin; i--) {
        if (isRowSelected(i)) {
          if (!isPrevSelected) {
            toIndex = i;
          }
          isPrevSelected = true;
        } else {
          if (isPrevSelected) {
            callback.accept(i - 1, toIndex);
            isPrevSelected = false;
          }
        }
      }
      if (isPrevSelected) {
        callback.accept(iMin, toIndex);
      }
    }
  }


  public void forEachSelectedColumnsAsBlock(IntRangeConsumer callback) {
    forEachSelectedColumnsAsBlock(callback, false);
  }

  public void forEachSelectedColumnsAsBlock(IntRangeConsumer callback, boolean reverse) {
    int iMin = clipToColumnSize(getMinColumnSelectionIndex());
    int iMax = clipToColumnSize(getMaxColumnSelectionIndex());

    if ((iMin == -1) || (iMax == -1)) {
      return;
    }

    if (!reverse) {
      int fromIndex = -1;
      boolean isPrevSelected = false;
      for (int i = iMin; i <= iMax; i++) {
        if (isColumnSelected(i)) {
          if (!isPrevSelected) {
            fromIndex = i;
          }
          isPrevSelected = true;
        } else {
          if (isPrevSelected) {
            callback.accept(fromIndex, i - 1);
            isPrevSelected = false;
          }
        }
      }
      if (isPrevSelected) {
        callback.accept(fromIndex, iMax);
      }
    } else {
      int toIndex = -1;
      boolean isPrevSelected = false;
      for (int i = iMax; i >= iMin; i--) {
        if (isColumnSelected(i)) {
          if (!isPrevSelected) {
            toIndex = i;
          }
          isPrevSelected = true;
        } else {
          if (isPrevSelected) {
            callback.accept(i - 1, toIndex);
            isPrevSelected = false;
          }
        }
      }
      if (isPrevSelected) {
        callback.accept(iMin, toIndex);
      }
    }
  }

  public void forEachSelectedColumnsAsBlock(int rowIndex, IntRangeConsumer callback) {
    forEachSelectedColumnsAsBlock(rowIndex, callback, false);
  }

  public void forEachSelectedColumnsAsBlock(int rowIndex, IntRangeConsumer callback,
                                            boolean reverse) {

    if (additionalSelections != null) {
      int iMin = clipToColumnSize(getMinColumnSelectionIndex());
      int iMax = clipToColumnSize(getMaxColumnSelectionIndex());

      if ((iMin == -1) || (iMax == -1)) {
        return;
      }

      if (!reverse) {
        int fromIndex = -1;
        boolean isPrevSelected = false;
        for (int i = iMin; i <= iMax; i++) {
          if (isCellSelected(rowIndex, i)) {
            if (!isPrevSelected) {
              fromIndex = i;
            }
            isPrevSelected = true;
          } else {
            if (isPrevSelected) {
              callback.accept(fromIndex, i - 1);
              isPrevSelected = false;
            }
          }
        }
        if (isPrevSelected) {
          callback.accept(fromIndex, iMax);
        }
      } else {
        int toIndex = -1;
        boolean isPrevSelected = false;
        for (int i = iMax; i >= iMin; i--) {
          if (isCellSelected(rowIndex, i)) {
            if (!isPrevSelected) {
              toIndex = i;
            }
            isPrevSelected = true;
          } else {
            if (isPrevSelected) {
              callback.accept(i - 1, toIndex);
              isPrevSelected = false;
            }
          }
        }
        if (isPrevSelected) {
          callback.accept(iMin, toIndex);
        }
      }
    } else {
      int iMin = clipToColumnSize(minColumnIndex);
      int iMax = clipToColumnSize(maxColumnIndex);

      if ((iMin == -1) || (iMax == -1)) {
        return;
      }

      callback.accept(iMin, iMax);
    }
  }

  public void correctSelectionIfInvalid() {
    if (gridSheetPane.getRowCount() <= minRowIndex
        || gridSheetPane.getColumnCount() <= minColumnIndex || maxRowIndex < minRowIndex
        || maxColumnIndex < minColumnIndex) {
      clearAdditionalSelection();
      setSelectionIntervalNoChangeAnchor(clipToRowSize(minRowIndex),
          clipToColumnSize(minColumnIndex), clipToRowSize(maxRowIndex),
          clipToColumnSize(maxColumnIndex));
    }
    if (!contains(rowAnchor, columnAnchor)) {
      int rowAnchor = Math.min(maxRowIndex, Math.max(this.rowAnchor, minRowIndex));
      int columnAnchor = Math.min(maxColumnIndex, Math.max(this.columnAnchor, minColumnIndex));
      updateAnchor(rowAnchor, columnAnchor);
    }
  }

  private int clipToRowSize(int index) {
    if (index < 0) {
      return 0;
    }
    int max = gridSheetPane.getRowCount() - 1;
    if (max < index) {
      return max;
    }
    return index;
  }

  private int clipToColumnSize(int index) {
    int max = gridSheetPane.getColumnCount() - 1;
    if (max < index) {
      return max;
    }
    return index;
  }
}
