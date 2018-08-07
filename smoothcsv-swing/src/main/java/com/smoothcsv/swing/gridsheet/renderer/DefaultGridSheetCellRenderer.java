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
package com.smoothcsv.swing.gridsheet.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;

@SuppressWarnings("serial")
public class DefaultGridSheetCellRenderer extends JLabel implements GridSheetCellRenderer {

  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  public DefaultGridSheetCellRenderer() {
    setOpaque(true);
    setBorder(noFocusBorder);
    setName("Grid.cellRenderer");
    setBackground(Color.WHITE);
  }

  @Override
  public Component getGridCellRendererComponent(GridSheetTable table, Object value,
                                                boolean isSelected, boolean hasFocus, int row, int column) {

    if (value == null) {
      setValue("");
    } else {
      setValue(GridSheetUtils.escapeCellValue(value));
    }

    // setBackground(Color.WHITE);
    // if (hasFocus) {
    // setBorder(colorProvider.getCellBorder(value, colorKey | GridSheetColorProvider.FOCUSED));
    // } else {
    // setBorder(colorProvider.getCellBorder(value, colorKey));
    // }

    // setForeground(colorProvider.getCellForegroundColor(value, column));

    return this;
  }

  protected void setValue(Object value) {
    setText((value == null) ? "" : value.toString());
  }

}
