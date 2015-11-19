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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import lombok.Getter;

import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CsvGridSheetCellRenderer extends JLabel implements GridSheetCellRenderer {

  @Getter
  private Object value;

  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

  public CsvGridSheetCellRenderer() {
    setOpaque(true);
    setBorder(noFocusBorder);
    setName("Grid.cellRenderer");
    setBackground(Color.WHITE);

    setUI(new CsvGridSheetCellRendererUI());
  }

  @Override
  public Component getGridCellRendererComponent(GridSheetTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {

    setValue(value);

    return this;
  }

  protected void setValue(Object value) {
    this.value = value;
    setText((value == null) ? "" : GridSheetUtils.escapeCellValue(value.toString()));
  }
}
