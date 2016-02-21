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
package com.smoothcsv.core.csvsheet;

import java.awt.Point;
import java.awt.event.MouseEvent;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetRowHeader;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;

import lombok.Getter;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CsvGridSheetRowHeader extends GridSheetRowHeader implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport;

  /**
   * @param gridSheetPane
   * @param renderer
   */
  public CsvGridSheetRowHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {
    super(gridSheetPane, renderer);
    componentSupport = new SmoothComponentSupport(this, "grid-rowheader");
    componentSupport.setStyleClasses(new String[] {"grid-header"});
  }

  @Override
  public boolean beforeShowPopupMenu(MouseEvent e) {
    if (getGridSheetPane().isEditing()) {
      getGridSheetPane().getTable().stopCellEditing();
    }
    Point p = e.getPoint();
    int row = getGridSheetPane().rowAtPoint(p);
    if (row < 0) {
      return false;
    }
    GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
    if (!sm.isRowHeaderSelected() || !sm.isRowSelected(row)) {
      sm.setSelectionInterval(row, 0, row, getGridSheetPane().getColumnCount() - 1);
      sm.setRowHeaderSelected(true);
      sm.setColumnHeaderSelected(false);
    }
    return SmoothComponent.super.beforeShowPopupMenu(e);
  }
}
