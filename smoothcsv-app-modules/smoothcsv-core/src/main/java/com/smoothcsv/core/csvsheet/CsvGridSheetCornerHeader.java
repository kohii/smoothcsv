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

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.GridSheetCornerHeader;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetHeaderRenderer;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvGridSheetCornerHeader extends GridSheetCornerHeader implements SmoothComponent {

  @Getter
  private SmoothComponentSupport componentSupport;

  /**
   * @param gridSheetPane
   * @param renderer
   */
  public CsvGridSheetCornerHeader(GridSheetPane gridSheetPane, GridSheetHeaderRenderer renderer) {
    super(gridSheetPane, renderer);
    componentSupport = new SmoothComponentSupport(this, "grid-cornerheader");
    componentSupport.setStyleClasses(new String[]{"grid-header"});
  }
}
