package com.smoothcsv.swing.event;

import lombok.Value;

/**
 * @author kohii
 */
@Value
public class HoveredCellChangeEvent {
  int oldRow;
  int oldColumn;

  int newRow;
  int newColumn;

  public boolean isOutOfTableBounds() {
    return newRow < 0 || newColumn < 0;
  }

  public boolean isRowChanged() {
    return oldRow != newRow;
  }

  public boolean isColumnChanged() {
    return oldColumn != newColumn;
  }
}
