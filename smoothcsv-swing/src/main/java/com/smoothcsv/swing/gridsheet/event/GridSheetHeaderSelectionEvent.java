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

@SuppressWarnings("serial")
public class GridSheetHeaderSelectionEvent extends java.util.EventObject {

  private int firstIndex;
  private int lastIndex;
  private boolean isHeaderSelected;
  private boolean isAdjusting;

  public GridSheetHeaderSelectionEvent(Object source, int firstIndex, int lastIndex,
                                       boolean isHeaderSelected, boolean isAdjusting) {
    super(source);
    this.firstIndex = firstIndex;
    this.lastIndex = lastIndex;
    this.isHeaderSelected = isHeaderSelected;
    this.isAdjusting = isAdjusting;
  }

  public int getFirstIndex() {
    return firstIndex;
  }

  public int getLastIndex() {
    return lastIndex;
  }

  public boolean isHeaderSelected() {
    return isHeaderSelected;
  }

  public boolean getValueIsAdjusting() {
    return isAdjusting;
  }
}
