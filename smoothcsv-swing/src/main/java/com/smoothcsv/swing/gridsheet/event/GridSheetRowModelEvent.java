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

import com.smoothcsv.swing.gridsheet.model.GridSheetModel;

@SuppressWarnings("serial")
public class GridSheetRowModelEvent extends java.util.EventObject {
  //
  // Instance Variables
  //

  /**
   * The index of the column from where it was moved or removed
   */
  protected int fromIndex;

  /**
   * The index of the column to where it was moved or added
   */
  protected int toIndex;

  //
  // Constructors
  //

  /**
   * Constructs a {@code TableColumnModelEvent} object.
   *
   * @param source the {@code TableColumnModel} that originated the event
   * @param from   an int specifying the index from where the column was moved or removed
   * @param to     an int specifying the index to where the column was moved or added
   * @see #getFromIndex
   * @see #getToIndex
   */
  public GridSheetRowModelEvent(GridSheetModel source, int from, int to) {
    super(source);
    fromIndex = from;
    toIndex = to;
  }

  //
  // Querying Methods
  //

  /**
   * Returns the fromIndex. Valid for removed or moved events
   */
  public int getFromIndex() {
    return fromIndex;
  }

  ;

  /**
   * Returns the toIndex. Valid for add and moved events
   */
  public int getToIndex() {
    return toIndex;
  }

  ;
}
