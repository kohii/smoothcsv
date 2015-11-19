/*
 * Copyright 2014 kohii.
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

import lombok.Getter;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.GridSheetColumnHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CsvGridSheetColumnHeader extends GridSheetColumnHeader implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport;

  /**
   * @param gridSheetPane
   * @param renderer
   */
  public CsvGridSheetColumnHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {
    super(gridSheetPane, renderer);
    componentSupport = new SmoothComponentSupport(this, "grid-columnheader");
    componentSupport.setStyleClasses(new String[] {"grid-header"});
  }

  @Override
  public String getUIClassID() {
    return "CsvGridSheetColumnHeaderUI";
  }

  @Override
  public boolean beforeShowPopupMenu(MouseEvent e) {
    if (getGridSheetPane().isEditing()) {
      getGridSheetPane().getTable().stopCellEditing();
    }
    Point p = e.getPoint();
    int column = getGridSheetPane().columnAtPoint(p);
    if (column < 0) {
      return false;
    }
    GridSheetSelectionModel sm = getGridSheetPane().getSelectionModel();
    if (!sm.isColumnHeaderSelected() || !sm.isColumnSelected(column)) {
      sm.setSelectionInterval(0, column, getGridSheetPane().getRowCount() - 1, column);
      sm.setColumnHeaderSelected(true);
      sm.setRowHeaderSelected(false);
    }
    return SmoothComponent.super.beforeShowPopupMenu(e);
  }
}
