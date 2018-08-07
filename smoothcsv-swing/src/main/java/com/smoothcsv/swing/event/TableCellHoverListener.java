package com.smoothcsv.swing.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

import lombok.Getter;

/**
 * @author kohii
 */
public abstract class TableCellHoverListener extends MouseAdapter {

  @Getter
  private int hoveredRowIndex = -1;

  @Getter
  private int hoveredColumnIndex = -1;

  @Override
  public void mouseExited(MouseEvent e) {
    changeHoveredCell(-1, -1);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    JTable table = (JTable) e.getSource();
    int row = table.rowAtPoint(e.getPoint());
    int column = table.columnAtPoint(e.getPoint());

    changeHoveredCell(row, column);
  }

  private void changeHoveredCell(int row, int column) {
    if (hoveredRowIndex == row && hoveredColumnIndex == column) {
      return;
    }

    int oldRow = hoveredRowIndex;
    hoveredRowIndex = row;

    int oldColumn = hoveredColumnIndex;
    hoveredColumnIndex = column;

    hoveredCellChanged(new HoveredCellChangeEvent(
        oldRow,
        oldColumn,
        hoveredRowIndex,
        hoveredColumnIndex
    ));
  }

  public abstract void hoveredCellChanged(HoveredCellChangeEvent event);

  public void installTo(JTable table) {
    table.addMouseListener(this);
    table.addMouseMotionListener(this);
  }
}
