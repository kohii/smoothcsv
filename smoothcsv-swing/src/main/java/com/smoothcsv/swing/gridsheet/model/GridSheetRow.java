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

public class GridSheetRow {

  private int height;

  private boolean visible = true;

  private GridSheetModel model;

  public GridSheetRow(int height) {
    this.height = height;
  }

  public GridSheetRow() {
    this(-1);
  }

  protected GridSheetRow(GridSheetModel model) {
    this();
    this.model = model;
  }

  /**
   * @param model the model to set
   */
  void setModel(GridSheetModel model) {
    assert model != null;
    this.model = model;
    if (this.height == -1) {
      this.height = model.getDefaultRowHeight();
    }
  }

  void removeModel() {
    this.model = null;
  }

  public int getHeight() {
    if (!visible) {
      return 0;
    }

    return height == -1 ? model.getDefaultRowHeight() : height;
  }

  public void setHeight(int height) {
    int oldVal = this.height;
    this.height = Math.min(Math.max(height, model.getMinRowHeight()), model.getMaxRowHeight());
    if (oldVal != height) {
      model.fireHeightUpdated();
    }
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean isVisible) {
    boolean oldVal = this.visible;
    this.visible = isVisible;
    if (oldVal != isVisible) {
      model.fireVisibleRowsUpdated();
    }
  }

  /**
   * @return the model
   */
  protected GridSheetModel getModel() {
    return model;
  }
}
