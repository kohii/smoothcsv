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

import java.util.List;

import com.smoothcsv.swing.gridsheet.event.GridSheetColumnHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetCornerHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetRowHeaderSelectionListener;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionListener;

/**
 * @author kohii
 */
public interface GridSheetSelectionModel {

  public static final int DEFAULT = 0;
  public static final int CHANGE_ONLY_HORIZONTAL = 1;
  public static final int CHANGE_ONLY_VERTICAL = 2;

  boolean isRowHeaderSelected();

  boolean isColumnHeaderSelected();

  void setRowHeaderSelected(boolean selected);

  void setColumnHeaderSelected(boolean selected);

  boolean isCellSelected(int row, int column);

  boolean isRowSelected(int row);

  boolean isColumnSelected(int column);

  int getMinRowSelectionIndex();

  int getMinColumnSelectionIndex();

  int getMaxRowSelectionIndex();

  int getMaxColumnSelectionIndex();

  int getMainMinRowSelectionIndex();

  int getMainMinColumnSelectionIndex();

  int getMainMaxRowSelectionIndex();

  int getMainMaxColumnSelectionIndex();

  boolean getValueIsAdjusting();

  void setValueIsAdjusting(boolean adjusting);

  void addSelectionInterval(int rowAnchor, int columnAnchor, int rowLead, int columnLead);

  void setSelectionInterval(int rowAnchor, int columnAnchor, int rowLead, int columnLead);

  void setSelectionIntervalNoChangeAnchor(int minRowIndex, int minColumnIndex, int maxRowIndex,
                                          int maxColumnIndex);

  void changeLeadSelection(int row, int column, int option);

  void clearHeaderSelection();

  void clearSelection();

  boolean isSingleCellSelected();

  int getRowAnchorIndex();

  int getColumnAnchorIndex();

  boolean isAnchor(int row, int column);

  boolean isAdditionallySelected();

  void updateAnchor(int row, int column);

  List<GridSheetCellRange> getAdditionalSelections();

  void selectEntireRow();

  void selectEntireColumn();

  void addGridFocusListener(GridSheetFocusListener l);

  void removeGridFocusListener(GridSheetFocusListener l);

  void addGridSelectionListener(GridSheetSelectionListener l);

  void removeGridSelectionListener(GridSheetSelectionListener l);

  void addColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l);

  void removeColumnHeaderSelectionListener(GridSheetColumnHeaderSelectionListener l);

  void addRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l);

  void removeRowHeaderSelectionListener(GridSheetRowHeaderSelectionListener l);

  void addCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l);

  void removeCornerHeaderSelectionListener(GridSheetCornerHeaderSelectionListener l);
}
