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

import java.util.Objects;

import lombok.Getter;

public class GridSheetColumn {

  private static long seq = 0;

  @Getter
  private final long id;

  private int width;

  private boolean visible = true;

  private GridSheetModel model;

  @Getter
  private String name;

  protected GridSheetColumn(int width, GridSheetModel model, String name) {
    this(seq++, width, model, name);
  }

  protected GridSheetColumn(long id, int width, GridSheetModel model, String name) {
    this.id = id;
    this.width = width;
    this.model = model;
    this.name = name;
  }

  /**
   * @param model the model to set
   */
  void setModel(GridSheetModel model) {
    assert model != null;
    this.model = model;
    if (this.width == -1) {
      this.width = model.getDefaultColumnWidth();
    }
  }

  void removeModel() {
    this.model = null;
  }

  public int getWidth() {
    return visible ? width : -1;
  }

  public void setWidth(int width) {
    int oldVal = this.width;
    this.width = Math.min(Math.max(width, model.getMinColumnWidth()), model.getMaxColumnWidth());
    if (oldVal != width) {
      model.fireWidthUpdated();
    }
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean isVisible) {
    if (this.visible != isVisible) {
      this.visible = isVisible;
      model.fireVisibleColumnsUpdated();
    }
  }

  public void setName(String name) {
    if (!Objects.equals(name, this.name)) {
      this.name = name;
      model.fireVisibleColumnsUpdated();
    }
  }
}
