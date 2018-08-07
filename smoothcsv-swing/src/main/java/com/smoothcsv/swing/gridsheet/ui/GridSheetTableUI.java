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
package com.smoothcsv.swing.gridsheet.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.plaf.ComponentUI;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.swing.gridsheet.GridSheetCellEditor;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.utils.SwingUtils;
import sun.swing.UIAction;

public class GridSheetTableUI extends GridSheetTableNoActionUI {


  // Listeners that are attached to the GridSheetTable
  protected KeyListener keyListener;
  private ExHandler handler;

  //
  // Helper class for keyboard actions
  //
  private static class Actions extends UIAction {

    private static final String CANCEL_EDITING = "cancelEditing";
    private static final String SELECT_ALL = "selectAll";
    private static final String SELECT_ALL_ROW = "selectAllRow";
    private static final String SELECT_ALL_COLUMN = "selectAllColumn";
    // private static final String CLEAR_SELECTION = "clearSelection";
    private static final String START_EDITING = "startEditing";

    private static final String NEXT_ROW = "selectNextRow";
    private static final String NEXT_ROW_CELL = "selectNextRowCell";
    private static final String NEXT_ROW_EXTEND_SELECTION = "selectNextRowExtendSelection";
    private static final String NEXT_ROW_EDGE = "selectNextRowEdge";
    private static final String NEXT_ROW_EDGE_EXTEND_SELECTION = "selectNextRowEdgeExtendSelection";
    private static final String PREVIOUS_ROW = "selectPrevRow";
    private static final String PREVIOUS_ROW_CELL = "selectPrevRowCell";
    private static final String PREVIOUS_ROW_EXTEND_SELECTION = "selectPrevRowExtendSelection";
    private static final String PREVIOUS_ROW_EDGE = "selectPrevRowEdge";
    private static final String PREVIOUS_ROW_EDGE_EXTEND_SELECTION =
        "selectPrevRowEdgeExtendSelection";

    private static final String NEXT_COLUMN = "selectNextColumn";
    private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
    private static final String NEXT_COLUMN_EXTEND_SELECTION = "selectNextColumnExtendSelection";
    private static final String NEXT_COLUMN_EDGE = "selectNextColumnEdge";
    private static final String NEXT_COLUMN_EDGE_EXTEND_SELECTION =
        "selectNextColumnEdgeExtendSelection";
    private static final String PREVIOUS_COLUMN = "selectPrevColumn";
    private static final String PREVIOUS_COLUMN_CELL = "selectPrevColumnCell";
    private static final String PREVIOUS_COLUMN_EXTEND_SELECTION =
        "selectPrevColumnExtendSelection";
    private static final String PREVIOUS_COLUMN_EDGE = "selectPrevColumnEdge";
    private static final String PREVIOUS_COLUMN_EDGE_EXTEND_SELECTION =
        "selectPrevColumnEdgeExtendSelection";

    private static final String SCROLL_LEFT_CHANGE_SELECTION = "scrollLeftChangeSelection";
    private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
    private static final String SCROLL_RIGHT_CHANGE_SELECTION = "scrollRightChangeSelection";
    private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";

    private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
    private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
    private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
    private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";

    private static final String FIRST_COLUMN = "selectFirstColumn";
    private static final String FIRST_COLUMN_EXTEND_SELECTION = "selectFirstColumnExtendSelection";
    private static final String LAST_COLUMN = "selectLastColumn";
    private static final String LAST_COLUMN_EXTEND_SELECTION = "selectLastColumnExtendSelection";

    private static final String FIRST_ROW = "selectFirstRow";
    private static final String FIRST_ROW_EXTEND_SELECTION = "selectFirstRowExtendSelection";
    private static final String LAST_ROW = "selectLastRow";
    private static final String LAST_ROW_EXTEND_SELECTION = "selectLastRowExtendSelection";

    // // add the lead item to the selection without changing lead or anchor
    // private static final String ADD_TO_SELECTION = "addToSelection";
    // extend the selection to the lead item
    // private static final String EXTEND_TO = "extendTo";
    // move the anchor to the lead and ensure only that item is selected
    // private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    // give focus to the GridTableHeader, if one exists
    // private static final String FOCUS_HEADER = "focusHeader";
    protected int dx;
    protected int dy;
    protected boolean extend;
    protected boolean inSelection;

    // horizontally, forwards always means right,
    // regardless of component orientation
    protected boolean forwards;
    protected boolean vertically;
    protected boolean toLimit;

    // protected int leadRow;
    // protected int leadColumn;
    protected int anchorRow;
    protected int anchorColumn;

    Actions(String name) {
      super(name);
    }

    Actions(String name, int dx, int dy, boolean extend, boolean inSelection) {
      super(name);

      assert (-1 <= dx && dx <= 1 && -1 <= dy && dy <= 1);

      // Actions spcifying true for "inSelection" are
      // fairly sensitive to bad parameter values. They require
      // that one of dx and dy be 0 and the other be -1 or 1.
      // Bogus parameter values could cause an infinite loop.
      // To prevent any problems we massage the params here
      // and complain if we get something we can't deal with.
      if (inSelection) {
        this.inSelection = true;

        // look at the sign of dx and dy only
        dx = sign(dx);
        dy = sign(dy);

        // make sure one is zero, but not both
        assert (dx == 0 || dy == 0) && !(dx == 0 && dy == 0);
      }

      this.dx = dx;
      this.dy = dy;
      this.extend = extend;
    }

    Actions(String name, boolean extend, boolean forwards, boolean vertically, boolean toLimit) {
      this(name, 0, 0, extend, false);
      this.forwards = forwards;
      this.vertically = vertically;
      this.toLimit = toLimit;
    }

    Actions(String name, boolean extend, boolean forwards, boolean vertically) {
      this(name, 0, 0, extend, false);
      this.forwards = forwards;
      this.vertically = vertically;
    }

    private static int clipToRange(int i, int a, int b) {
      return Math.min(Math.max(i, a), b - 1);
    }

    private void moveWithinTableRange(GridSheetTable table, int dx, int dy,
                                      GridSheetSelectionModel sm) {
      GridSheetPane gridSheetPane = table.getGridSheetPane();
      anchorRow = clipToRange(anchorRow + dy, 0, gridSheetPane.getRowCount());
      anchorColumn = clipToRange(anchorColumn + dx, 0, gridSheetPane.getColumnCount());
      sm.clearHeaderSelection();
      sm.setSelectionInterval(anchorRow, anchorColumn, anchorRow, anchorColumn);
      table.scrollRectToVisible(anchorRow, anchorColumn);
    }

    private static int sign(int num) {
      return (num < 0) ? -1 : ((num == 0) ? 0 : 1);
    }

    /**
     * Called to move within the selected range of the given GridSheetTable. This method uses the
     * table's notion of selection, which is important to allow the user to navigate between items
     * visually selected on screen. This notion may or may not be the same as what could be
     * determined by directly querying the selection models. It depends on certain table properties
     * (such as whether or not row or column selection is allowed). When performing modifications,
     * it is recommended that caution be taken in order to preserve the intent of this method,
     * especially when deciding whether to query the selection models or interact with
     * GridSheetTable directly.
     */
    private boolean moveWithinSelectedRange(GridSheetTable table, int dx, int dy,
                                            GridSheetSelectionModel sm) {
      boolean stayInSelection = !sm.isSingleCellSelected();
      GridSheetUtils.moveAnchor(table.getGridSheetPane(),
          dx != 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL, dx < 0 || dy < 0,
          stayInSelection, true);
      return stayInSelection;
    }

    // /**
    // * Find the next lead row and column based on the given dx/dy and
    // * max/min values.
    // */
    // private void calcNextPos(int dx, int minX, int maxX, int dy, int
    // minY,
    // int maxY) {
    //
    // }
    public void actionPerformed(ActionEvent e) {
      String key = getName();
      GridSheetTable table = (GridSheetTable) e.getSource();

      GridSheetPane gridSheetPane = table.getGridSheetPane();

      anchorRow = gridSheetPane.getSelectionModel().getRowAnchorIndex();
      anchorColumn = gridSheetPane.getSelectionModel().getColumnAnchorIndex();

      if (key == SCROLL_LEFT_CHANGE_SELECTION || // Paging Actions
          key == SCROLL_LEFT_EXTEND_SELECTION || key == SCROLL_RIGHT_CHANGE_SELECTION
          || key == SCROLL_RIGHT_EXTEND_SELECTION || key == SCROLL_UP_CHANGE_SELECTION
          || key == SCROLL_UP_EXTEND_SELECTION || key == SCROLL_DOWN_CHANGE_SELECTION
          || key == SCROLL_DOWN_EXTEND_SELECTION || key == FIRST_COLUMN
          || key == FIRST_COLUMN_EXTEND_SELECTION || key == FIRST_ROW
          || key == FIRST_ROW_EXTEND_SELECTION || key == LAST_COLUMN
          || key == LAST_COLUMN_EXTEND_SELECTION || key == LAST_ROW
          || key == LAST_ROW_EXTEND_SELECTION) {
        if (toLimit) {
          if (vertically) {
            int rowCount = gridSheetPane.getRowCount();
            this.dx = 0;
            this.dy = forwards ? rowCount : -rowCount;
          } else {
            int colCount = gridSheetPane.getColumnCount();
            this.dx = forwards ? colCount : -colCount;
            this.dy = 0;
          }
        } else {
          Dimension delta = table.getParent().getSize();

          if (vertically) {
            Rectangle r = table.getCellRect(anchorRow, 0, true);
            if (forwards) {
              // scroll by at least one cell
              r.y += Math.max(delta.height, r.height);
            } else {
              r.y -= delta.height;
            }

            this.dx = 0;
            int newRow = gridSheetPane.rowAtPoint(r.getLocation());
            if (newRow == -1 && forwards) {
              newRow = gridSheetPane.getRowCount();
            }
            this.dy = newRow - anchorRow;
          } else {
            Rectangle r = table.getCellRect(0, anchorColumn, true);

            if (forwards) {
              // scroll by at least one cell
              r.x += Math.max(delta.width, r.width);
            } else {
              r.x -= delta.width;
            }

            int newColumn = gridSheetPane.columnAtPoint(r.getLocation());
            if (newColumn == -1) {
              boolean ltr = table.getComponentOrientation().isLeftToRight();

              newColumn = forwards ? (ltr ? gridSheetPane.getColumnCount() : 0)
                  : (ltr ? 0 : gridSheetPane.getColumnCount());

            }
            this.dx = newColumn - anchorColumn;
            this.dy = 0;
          }
        }
      }
      if (key == NEXT_ROW || // Navigate Actions
          key == NEXT_ROW_CELL || key == NEXT_ROW_EXTEND_SELECTION || key == NEXT_COLUMN
          || key == NEXT_COLUMN_CELL || key == NEXT_COLUMN_EXTEND_SELECTION || key == PREVIOUS_ROW
          || key == PREVIOUS_ROW_CELL || key == PREVIOUS_ROW_EXTEND_SELECTION
          || key == PREVIOUS_COLUMN || key == PREVIOUS_COLUMN_CELL
          || key == PREVIOUS_COLUMN_EXTEND_SELECTION || // Paging Actions.
          key == SCROLL_LEFT_CHANGE_SELECTION || key == SCROLL_LEFT_EXTEND_SELECTION
          || key == SCROLL_RIGHT_CHANGE_SELECTION || key == SCROLL_RIGHT_EXTEND_SELECTION
          || key == SCROLL_UP_CHANGE_SELECTION || key == SCROLL_UP_EXTEND_SELECTION
          || key == SCROLL_DOWN_CHANGE_SELECTION || key == SCROLL_DOWN_EXTEND_SELECTION) {

        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
          return;
        }

        // Unfortunately, this strategy introduces bugs because
        // of the asynchronous nature of requestFocus() call below.
        // Introducing a delay with invokeLater() makes this work
        // in the typical case though race conditions then allow
        // focus to disappear altogether. The right solution appears
        // to be to fix requestFocus() so that it queues a request
        // for the focus regardless of who owns the focus at the
        // time the call to requestFocus() is made. The optimisation
        // to ignore the call to requestFocus() when the component
        // already has focus may ligitimately be made as the
        // request focus event is dequeued, not before.
        // boolean wasEditingWithFocus = table.isEditing() &&
        // table.getEditorComponent().isFocusOwner();
        if (gridSheetPane.getRowCount() <= 0 || gridSheetPane.getColumnCount() <= 0) {
          // bail - don't try to move selection on an empty table
          return;
        }

        GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
        if (!inSelection) {
          if (extend) {
            int leadRowDirection = dy;
            int leadColumnDirection = dx;
            int minR = sm.getMinRowSelectionIndex();
            int minC = sm.getMinColumnSelectionIndex();
            int maxR = sm.getMaxRowSelectionIndex();
            int maxC = sm.getMaxColumnSelectionIndex();
            if (minR == anchorRow) {
              maxR += dy;
              if (maxR < anchorRow) {
                minR -= anchorRow - maxR;
                maxR = anchorRow;
              }
              if (dy < 0) {
                leadRowDirection *= -1;
              }
            } else if (maxR == anchorRow) {
              minR += dy;
              if (anchorRow < minR) {
                maxR -= anchorRow - minR;
                minR = anchorRow;
              }
              if (0 < dy) {
                leadRowDirection *= -1;
              }
            } else {
              if (dy > 0) {
                maxR += dy;
              } else if (dy < 0) {
                minR += dy;
              }
            }
            if (minC == anchorColumn) {
              maxC += dx;
              if (maxC < anchorColumn) {
                minC -= anchorColumn - maxC;
                maxC = anchorColumn;
              }
              if (dx < 0) {
                leadColumnDirection *= -1;
              }
            } else if (maxC == anchorColumn) {
              minC += dx;
              if (anchorColumn < minC) {
                maxC -= anchorColumn - minC;
                minC = anchorColumn;
              }
              if (0 < dx) {
                leadColumnDirection *= -1;
              }
            } else {
              if (dx > 0) {
                maxC += dx;
              } else if (dx < 0) {
                minC += dx;
              }
            }
            minR = clipToRange(minR, 0, gridSheetPane.getRowCount());
            minC = clipToRange(minC, 0, gridSheetPane.getColumnCount());
            maxR = clipToRange(maxR, 0, gridSheetPane.getRowCount());
            maxC = clipToRange(maxC, 0, gridSheetPane.getColumnCount());
            if (sm.isColumnHeaderSelected()
                && (minR != 0 || maxR != gridSheetPane.getRowCount() - 1)) {
              sm.setValueIsAdjusting(false);
              sm.setColumnHeaderSelected(false);
            }
            if (sm.isRowHeaderSelected()
                && (minC != 0 || maxC != gridSheetPane.getColumnCount() - 1)) {
              sm.setValueIsAdjusting(false);
              sm.setRowHeaderSelected(false);
            }
            sm.setSelectionIntervalNoChangeAnchor(minR, minC, maxR, maxC);
            if (sm.getValueIsAdjusting()) {
              sm.setValueIsAdjusting(false);
            }
            table.scrollRectToVisible(leadRowDirection < 0 ? minR : maxR,
                leadColumnDirection < 0 ? minC : maxC);
          } else {
            moveWithinTableRange(table, dx, dy, sm);
          }
        } else {
          moveWithinSelectedRange(table, dx, dy, sm);
        }

        /*
         * if (wasEditingWithFocus) { table.editCellAt(leadRow, leadColumn); final Component
         * editorComp = table.getEditorComponent(); if (editorComp != null) {
         * SwingUtilities.invokeLater(new Runnable() { public void run() {
         * editorComp.requestFocus(); } }); } }
         */
      } else if (key == NEXT_ROW_EDGE || key == PREVIOUS_ROW_EDGE
          || key == NEXT_ROW_EDGE_EXTEND_SELECTION || key == PREVIOUS_ROW_EDGE_EXTEND_SELECTION) {
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
          return;
        }

        GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();

        int rowCount = gridSheetPane.getRowCount();
        int limitFrom;
        int limitTo;
        if (forwards) {
          limitFrom = 0;
          limitTo = rowCount - 1;
        } else {
          limitFrom = rowCount - 1;
          limitTo = 0;
        }

        int focusedRow = sm.getRowAnchorIndex();
        int focusedColumn = sm.getColumnAnchorIndex();
        Object current = gridSheetPane.getValueAt(focusedRow, focusedColumn);
        boolean isEmpty = ObjectUtils.isEmpty(current);
        int d = forwards ? 1 : -1;
        if (!isEmpty && focusedRow != limitTo
            && ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow + d, focusedColumn))) {
          isEmpty = true;
          focusedRow += d;
        }
        int row = limitTo;
        int limitMin = Math.min(limitFrom, limitTo);
        int limitMax = Math.max(limitFrom, limitTo);
        for (int i = focusedRow; limitMin <= i && i <= limitMax; i += d) {
          boolean b = ObjectUtils.isEmpty(gridSheetPane.getValueAt(i, focusedColumn));
          if (isEmpty) {
            if (!b) {
              row = i;
              break;
            }
          } else {
            if (b) {
              row = i - d;
              break;
            }
          }
        }

        sm.setValueIsAdjusting(true);
        sm.clearHeaderSelection();
        if (extend) {
          sm.changeLeadSelection(row, focusedColumn, GridSheetSelectionModel.CHANGE_ONLY_VERTICAL);
        } else {
          sm.setSelectionInterval(row, focusedColumn, row, focusedColumn);
        }
        table.scrollRectToVisible(row, focusedColumn);

        sm.setValueIsAdjusting(false);

      } else if (key == NEXT_COLUMN_EDGE || key == PREVIOUS_COLUMN_EDGE
          || key == NEXT_COLUMN_EDGE_EXTEND_SELECTION
          || key == PREVIOUS_COLUMN_EDGE_EXTEND_SELECTION) {
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
          return;
        }

        GridSheetSelectionModel sm = gridSheetPane.getSelectionModel();

        int columnCount = gridSheetPane.getColumnCount();
        int limitFrom;
        int limitTo;
        if (forwards) {
          limitFrom = 0;
          limitTo = columnCount - 1;
        } else {
          limitFrom = columnCount - 1;
          limitTo = 0;
        }

        int focusedRow = sm.getRowAnchorIndex();
        int focusedColumn = sm.getColumnAnchorIndex();
        Object current = gridSheetPane.getValueAt(focusedRow, focusedColumn);
        boolean isEmpty = ObjectUtils.isEmpty(current);
        int d = forwards ? 1 : -1;
        if (!isEmpty && focusedColumn != limitTo
            && ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow, focusedColumn + d))) {
          isEmpty = true;
          focusedColumn += d;
        }
        int column = limitTo;
        int limitMin = Math.min(limitFrom, limitTo);
        int limitMax = Math.max(limitFrom, limitTo);
        for (int i = focusedColumn; limitMin <= i && i <= limitMax; i += d) {
          boolean b = ObjectUtils.isEmpty(gridSheetPane.getValueAt(focusedRow, i));
          if (isEmpty) {
            if (!b) {
              column = i;
              break;
            }
          } else {
            if (b) {
              column = i - d;
              break;
            }
          }
        }
        sm.setValueIsAdjusting(true);
        sm.clearHeaderSelection();
        if (extend) {
          sm.changeLeadSelection(focusedRow, column,
              GridSheetSelectionModel.CHANGE_ONLY_HORIZONTAL);
        } else {
          sm.setSelectionInterval(focusedRow, column, focusedRow, column);
        }
        table.scrollRectToVisible(focusedRow, column);
        sm.setValueIsAdjusting(false);

      } else if (key == CANCEL_EDITING) {
        table.getCellEditor().cancelCellEditing();
      } else if (key == SELECT_ALL) {
        gridSheetPane.selectAll(false);
      } else if (key == SELECT_ALL_ROW) {
        gridSheetPane.selectEntireRow();
      } else if (key == SELECT_ALL_COLUMN) {
        gridSheetPane.selectEntireColumn();
      } else if (key == START_EDITING) {
        if (!table.hasFocus()) {
          if (table.isEditing()) {
            table.getCellEditor().getEditorComponent().requestFocus();
            return;
          }
          table.requestFocus();
          return;
        }
        table.editCellAt(anchorRow, anchorColumn, e);
        GridSheetCellEditor editor = table.getCellEditor();
        if (editor != null) {
          editor.getEditorComponent().requestFocus();
        }
        // } else if (key == ADD_TO_SELECTION) {
        // if (!table.isCellSelected(anchorRow, anchorColumn)) {
        // int oldAnchorRow = rsm.getAnchorSelectionIndex();
        // int oldAnchorColumn = csm.getAnchorSelectionIndex();
        // rsm.setValueIsAdjusting(true);
        // csm.setValueIsAdjusting(true);
        // table.changeSelection(leadRow, leadColumn, true, false);
        // rsm.setAnchorSelectionIndex(oldAnchorRow);
        // csm.setAnchorSelectionIndex(oldAnchorColumn);
        // rsm.setValueIsAdjusting(false);
        // csm.setValueIsAdjusting(false);
        // }
        // } else if (key == MOVE_SELECTION_TO) {
        // table.changeSelection(leadRow, leadColumn, false, false);
        // } else if (key == FOCUS_HEADER) {
        // GridTableHeader th = table.getTableHeader();
        // if (th != null) {
        // // Set the header's selected column to match the table.
        // int col = table.getSelectedColumn();
        // if (col >= 0) {
        // TableHeaderUI thUI = th.getUI();
        // if (thUI instanceof BasicTableHeaderUI) {
        // ((BasicTableHeaderUI) thUI).selectColumn(col);
        // }
        // }
        //
        // // Then give the header the focus.
        // th.requestFocusInWindow();
        // }
      }
    }

    public boolean isEnabled(Object sender) {
      // String key = getName();
      //
      // if (key == CANCEL_EDITING && sender instanceof GridSheetTable) {
      // return ((GridSheetTable) sender).isEditing();
      // } else if (key == NEXT_ROW_CHANGE_LEAD
      // || key == PREVIOUS_ROW_CHANGE_LEAD) {
      // // discontinuous selection actions are only enabled for
      // // DefaultListSelectionModel
      // return sender != null
      // && ((GridSheetTable) sender).getSelectionModel() instanceof
      // DefaultListSelectionModel;
      // } else if (key == NEXT_COLUMN_CHANGE_LEAD
      // || key == PREVIOUS_COLUMN_CHANGE_LEAD) {
      // // discontinuous selection actions are only enabled for
      // // DefaultListSelectionModel
      // return sender != null
      // && ((GridSheetTable) sender).getColumnModel()
      // .getSelectionModel() instanceof DefaultListSelectionModel;
      // } else if (key == ADD_TO_SELECTION && sender instanceof
      // GridSheetTable) {
      // // This action is typically bound to SPACE.
      // // If the table is already in an editing mode, SPACE should
      // // simply enter a space character into the table, and not
      // // select a cell. Likewise, if the lead cell is already selected
      // // then hitting SPACE should just enter a space character
      // // into the cell and begin editing. In both of these cases
      // // this action will be disabled.
      // GridSheetTable table = (GridSheetTable) sender;
      // int leadRow = getAdjustedLead(table, true);
      // int leadCol = getAdjustedLead(table, false);
      // return !(table.isEditing() || table.isCellSelected(leadRow,
      // leadCol));
      // } else if (key == FOCUS_HEADER && sender instanceof
      // GridSheetTable) {
      // GridSheetTable table = (GridSheetTable) sender;
      // return table.getTableHeader() != null;
      // }

      return true;
    }
  }

  /**
   * Creates the key listener for handling keyboard navigation in the GridSheetTable.
   */
  protected KeyListener createKeyListener() {
    return getHandler();
  }

  //
  // The installation/uninstall procedures and support
  //
  public static ComponentUI createUI(JComponent c) {
    return new GridSheetTableUI();
  }

  // Installation
  public void installUI(JComponent c) {
    super.installUI(c);
    installKeyboardActions();
  }

  /**
   * Attaches listeners to the GridSheetTable.
   */
  protected void installListeners() {
    super.installListeners();
    keyListener = createKeyListener();
    getTable().addKeyListener(keyListener);
  }

  /**
   * Register all keyboard actions on the GridSheetTable.
   */
  protected void installKeyboardActions() {
    // LazyActionMap.installLazyActionMap(table, BasicTableUI.class,
    // "Table.actionMap");

    InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    loadInputMap(inputMap);
    ActionMap actioinMap = getTable().getActionMap();
    loadActionMap(actioinMap);
  }

  InputMap getInputMap(int condition) {
    if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
      // InputMap keyMap = (InputMap) DefaultLookup.get(table, this,
      // "Grid.ancestorInputMap");
      // InputMap rtlKeyMap;
      //
      // if (table.getComponentOrientation().isLeftToRight()
      // || ((rtlKeyMap = (InputMap) DefaultLookup.get(table, this,
      // "Grid.ancestorInputMap.RightToLeft")) == null)) {
      // return keyMap;
      // } else {
      // rtlKeyMap.setParent(keyMap);
      // return rtlKeyMap;
      // }
      return getTable().getInputMap(condition);
    }
    return null;
  }

  protected void loadInputMap(InputMap map) {

    final int shortcutKeyMask = SwingUtils.getMenuShortcutKeyMask();
    final int ctrlShift = shortcutKeyMask | KeyEvent.SHIFT_DOWN_MASK;

    // arrow key
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), Actions.NEXT_COLUMN);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, 0), Actions.NEXT_COLUMN);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), Actions.PREVIOUS_COLUMN);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, 0), Actions.PREVIOUS_COLUMN);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), Actions.NEXT_ROW);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), Actions.NEXT_ROW);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), Actions.PREVIOUS_ROW);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), Actions.PREVIOUS_ROW);

    // shift + arrow key
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.SHIFT_DOWN_MASK),
        Actions.NEXT_COLUMN_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, KeyEvent.SHIFT_DOWN_MASK),
        Actions.NEXT_COLUMN_EXTEND_SELECTION);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_COLUMN_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_COLUMN_EXTEND_SELECTION);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK),
        Actions.NEXT_ROW_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, KeyEvent.SHIFT_DOWN_MASK),
        Actions.NEXT_ROW_EXTEND_SELECTION);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_ROW_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_ROW_EXTEND_SELECTION);

    // home & end
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), Actions.FIRST_COLUMN);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), Actions.LAST_COLUMN);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, shortcutKeyMask), Actions.FIRST_ROW);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, shortcutKeyMask), Actions.LAST_ROW);

    // shift + home & end
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.SHIFT_DOWN_MASK),
        Actions.FIRST_COLUMN_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.SHIFT_DOWN_MASK),
        Actions.LAST_COLUMN_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, ctrlShift),
        Actions.FIRST_ROW_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, ctrlShift), Actions.LAST_ROW_EXTEND_SELECTION);

    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), Actions.NEXT_COLUMN_CELL);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_COLUMN_CELL);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Actions.NEXT_ROW_CELL);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK),
        Actions.PREVIOUS_ROW_CELL);

    // ctrl + arrow key
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, shortcutKeyMask), Actions.NEXT_COLUMN_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, shortcutKeyMask),
        Actions.NEXT_COLUMN_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, shortcutKeyMask),
        Actions.PREVIOUS_COLUMN_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, shortcutKeyMask),
        Actions.PREVIOUS_COLUMN_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, shortcutKeyMask), Actions.NEXT_ROW_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, shortcutKeyMask), Actions.NEXT_ROW_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, shortcutKeyMask), Actions.PREVIOUS_ROW_EDGE);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, shortcutKeyMask), Actions.PREVIOUS_ROW_EDGE);

    // ctrl + shift + arrow key
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ctrlShift),
        Actions.NEXT_COLUMN_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_RIGHT, ctrlShift),
        Actions.NEXT_COLUMN_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ctrlShift),
        Actions.PREVIOUS_COLUMN_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_LEFT, ctrlShift),
        Actions.PREVIOUS_COLUMN_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ctrlShift),
        Actions.NEXT_ROW_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, ctrlShift),
        Actions.NEXT_ROW_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ctrlShift),
        Actions.PREVIOUS_ROW_EDGE_EXTEND_SELECTION);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, ctrlShift),
        Actions.PREVIOUS_ROW_EDGE_EXTEND_SELECTION);

    // select all
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, shortcutKeyMask), Actions.SELECT_ALL);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, ctrlShift), Actions.SELECT_ALL);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_DOWN_MASK),
        Actions.SELECT_ALL_COLUMN);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, shortcutKeyMask), Actions.SELECT_ALL_ROW);

    // others
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), Actions.START_EDITING);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Actions.CANCEL_EDITING);
  }

  protected void loadActionMap(ActionMap map) {

    putActionToMap(map, new Actions(Actions.NEXT_COLUMN, 1, 0, false, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_COLUMN, -1, 0, false, false));
    putActionToMap(map, new Actions(Actions.NEXT_ROW, 0, 1, false, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_ROW, 0, -1, false, false));

    putActionToMap(map, new Actions(Actions.NEXT_COLUMN_EXTEND_SELECTION, 1, 0, true, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_COLUMN_EXTEND_SELECTION, -1, 0, true, false));
    putActionToMap(map, new Actions(Actions.NEXT_ROW_EXTEND_SELECTION, 0, 1, true, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_ROW_EXTEND_SELECTION, 0, -1, true, false));

    putActionToMap(map, new Actions(Actions.FIRST_COLUMN, false, false, false, true));
    putActionToMap(map, new Actions(Actions.LAST_COLUMN, false, true, false, true));
    putActionToMap(map, new Actions(Actions.FIRST_ROW, false, false, true, true));
    putActionToMap(map, new Actions(Actions.LAST_ROW, false, true, true, true));

    putActionToMap(map,
        new Actions(Actions.FIRST_COLUMN_EXTEND_SELECTION, true, false, false, true));
    putActionToMap(map, new Actions(Actions.LAST_COLUMN_EXTEND_SELECTION, true, true, false, true));
    putActionToMap(map, new Actions(Actions.FIRST_ROW_EXTEND_SELECTION, true, false, true, true));
    putActionToMap(map, new Actions(Actions.LAST_ROW_EXTEND_SELECTION, true, true, true, true));

    putActionToMap(map, new Actions(Actions.SCROLL_UP_CHANGE_SELECTION, false, false, true, false));
    putActionToMap(map,
        new Actions(Actions.SCROLL_DOWN_CHANGE_SELECTION, false, true, true, false));
    putActionToMap(map, new Actions(Actions.SCROLL_UP_EXTEND_SELECTION, true, false, true, false));
    putActionToMap(map, new Actions(Actions.SCROLL_DOWN_EXTEND_SELECTION, true, true, true, false));

    putActionToMap(map, new Actions(Actions.NEXT_COLUMN_CELL, 1, 0, false, true));
    putActionToMap(map, new Actions(Actions.PREVIOUS_COLUMN_CELL, -1, 0, false, true));
    putActionToMap(map, new Actions(Actions.NEXT_ROW_CELL, 0, 1, false, true));
    putActionToMap(map, new Actions(Actions.PREVIOUS_ROW_CELL, 0, -1, false, true));

    putActionToMap(map, new Actions(Actions.SELECT_ALL));
    putActionToMap(map, new Actions(Actions.SELECT_ALL_ROW));
    putActionToMap(map, new Actions(Actions.SELECT_ALL_COLUMN));

    putActionToMap(map, new Actions(Actions.CANCEL_EDITING));
    putActionToMap(map, new Actions(Actions.START_EDITING));

    map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
    map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
    map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
        TransferHandler.getPasteAction());

    putActionToMap(map,
        new Actions(Actions.SCROLL_LEFT_CHANGE_SELECTION, false, false, false, false));
    putActionToMap(map,
        new Actions(Actions.SCROLL_RIGHT_CHANGE_SELECTION, false, true, false, false));
    putActionToMap(map,
        new Actions(Actions.SCROLL_LEFT_EXTEND_SELECTION, true, false, false, false));
    putActionToMap(map,
        new Actions(Actions.SCROLL_RIGHT_EXTEND_SELECTION, true, true, false, false));

    putActionToMap(map, new Actions(Actions.PREVIOUS_COLUMN_EDGE, false, false, false));
    putActionToMap(map, new Actions(Actions.NEXT_COLUMN_EDGE, false, true, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_ROW_EDGE, false, false, true));
    putActionToMap(map, new Actions(Actions.NEXT_ROW_EDGE, false, true, true));

    putActionToMap(map,
        new Actions(Actions.PREVIOUS_COLUMN_EDGE_EXTEND_SELECTION, true, false, false));
    putActionToMap(map, new Actions(Actions.NEXT_COLUMN_EDGE_EXTEND_SELECTION, true, true, false));
    putActionToMap(map, new Actions(Actions.PREVIOUS_ROW_EDGE_EXTEND_SELECTION, true, false, true));
    putActionToMap(map, new Actions(Actions.NEXT_ROW_EDGE_EXTEND_SELECTION, true, true, true));

    // putActionToMap(map, new Actions(Actions.ADD_TO_SELECTION));
    // putActionToMap(map, new Actions(Actions.EXTEND_TO));
    // putActionToMap(map, new Actions(Actions.MOVE_SELECTION_TO));
    // putActionToMap(map, new Actions(Actions.FOCUS_HEADER));
  }

  protected void putActionToMap(ActionMap map, Action action) {
    map.put(action.getValue(Action.NAME), action);
  }

  public void uninstallUI(JComponent c) {
    super.uninstallUI(c);
    uninstallKeyboardActions();
  }

  protected void uninstallListeners() {
    getTable().removeKeyListener(keyListener);
    keyListener = null;
  }

  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(getTable(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
        null);
    SwingUtilities.replaceUIActionMap(getTable(), null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.swing.components.gridsheet.ui.GridSheetTableNoActionUI#getHandler()
   */
  @Override
  protected ExHandler getHandler() {
    if (handler == null) {
      handler = new ExHandler();
    }
    return handler;
  }

  class ExHandler extends Handler implements KeyListener {
    public void propertyChange(PropertyChangeEvent event) {
      super.propertyChange(event);
      String changeName = event.getPropertyName();
      if ("componentOrientation" == changeName) {
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(getTable(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            inputMap);
      }
    }


    public void keyTyped(KeyEvent e) {
      KeyStroke keyStroke = KeyStroke.getKeyStroke(e.getKeyChar(), e.getModifiers());

      // We register all actions using ANCESTOR_OF_FOCUSED_COMPONENT
      // which means that we might perform the appropriate action
      // in the table and then forward it to the editor if the editor
      // had focus. Make sure this doesn't happen by checking our
      // InputMaps.
      InputMap map = getTable().getInputMap(JComponent.WHEN_FOCUSED);
      if (map != null && map.get(keyStroke) != null) {
        return;
      }
      map = getTable().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      if (map != null && map.get(keyStroke) != null) {
        return;
      }

      keyStroke = KeyStroke.getKeyStrokeForEvent(e);

      // The AWT seems to generate an unconsumed \r event when
      // ENTER (\n) is pressed.
      if (e.getKeyChar() == '\r') {
        return;
      }

      GridSheetPane gridSheetPane = getGridSheetPane();
      int anchorRow = gridSheetPane.getSelectionModel().getRowAnchorIndex();
      int anchorColumn = gridSheetPane.getSelectionModel().getColumnAnchorIndex();

      if (anchorRow != -1 && anchorColumn != -1 && !getTable().isEditing()) {
        if (!getTable().editCellAt(anchorRow, anchorColumn)) {
          return;
        }
      }

      // Forwarding events this way seems to put the component
      // in a state where it believes it has focus. In reality
      // the table retains focus - though it is difficult for
      // a user to tell, since the caret is visible and flashing.
      // Calling table.requestFocus() here, to get the focus back to
      // the table, seems to have no effect.
      GridSheetCellEditor cellEditor = getTable().getCellEditor();
      if (getTable().isEditing() && cellEditor != null) {
        JComponent editorComp = cellEditor.getEditorComponent();
        if (editorComp instanceof JComponent) {
          JComponent component = (JComponent) editorComp;
          map = component.getInputMap(JComponent.WHEN_FOCUSED);
          Object binding = (map != null) ? map.get(keyStroke) : null;
          if (binding == null) {
            map = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            binding = (map != null) ? map.get(keyStroke) : null;
          }
          if (binding != null) {
            ActionMap am = component.getActionMap();
            Action action = (am != null) ? am.get(binding) : null;
            if (action != null
                && SwingUtilities.notifyAction(action, keyStroke, e, component, e.getModifiers())) {
              e.consume();
            }
          }
        }
      }
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e) {
      // do nothing
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e) {
      // do nothing
    }
  }
}
