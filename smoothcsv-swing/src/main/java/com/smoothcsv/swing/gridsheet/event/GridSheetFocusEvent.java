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
package com.smoothcsv.swing.gridsheet.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public class GridSheetFocusEvent extends EventObject {
  private int row;
  private int column;
  private boolean isAdjusting;

  public GridSheetFocusEvent(Object source, int row, int column, boolean isAdjusting) {
    super(source);
    this.row = row;
    this.column = column;
    this.isAdjusting = isAdjusting;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public boolean getValueIsAdjusting() {
    return isAdjusting;
  }

  /**
   * Returns a {@code String} that displays and identifies this object's properties.
   *
   * @return a String representation of this object
   */
  public String toString() {
    String properties = " source=" + getSource() + " row= " + row + " column= " + column
        + " isAdjusting= " + isAdjusting + " ";
    return getClass().getName() + "[" + properties + "]";
  }

}
