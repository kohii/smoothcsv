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

import java.awt.Component;
import java.awt.Font;

import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.swing.gridsheet.AbstractGridSheetHeaderComponent;
import com.smoothcsv.swing.gridsheet.renderer.DefaultGridSheetHeaderCellRenderer;

/**
 * @author kohii
 */
public class CsvGridSheetHeaderCellRenderer extends DefaultGridSheetHeaderCellRenderer {

  private static final long serialVersionUID = 1L;

  public CsvGridSheetHeaderCellRenderer() {}

  @Override
  public String getUIClassID() {
    return "CsvGridSheetHeaderCellUI";
  }

  @Override
  public Component getGridCellRendererComponent(AbstractGridSheetHeaderComponent header,
      Object value, boolean isSelected, boolean hasFocus, int index) {
    setValue(value);
    if (isSelected) {
      if (hasFocus) {
        setForeground(SCAppearanceManager.getGridHeaderFocusedForeground());
        setBackground(SCAppearanceManager.getGridHeaderFocusedBackground());
      } else {
        setForeground(SCAppearanceManager.getGridHeaderSelectedForeground());
        setBackground(SCAppearanceManager.getGridHeaderSelectedBackground());
      }
    } else {
      setForeground(SCAppearanceManager.getGridHeaderForeground());
      setBackground(SCAppearanceManager.getGridHeaderBackground());
    }
    return this;
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.JComponent#setFont(java.awt.Font)
   */
  @Override
  public void setFont(Font font) {
    super.setFont(font);
  }
}
