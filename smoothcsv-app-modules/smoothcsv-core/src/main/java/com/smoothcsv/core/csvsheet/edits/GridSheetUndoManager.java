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
package com.smoothcsv.core.csvsheet.edits;

import java.util.ArrayList;
import java.util.LinkedList;

import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionSnapshot;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
public class GridSheetUndoManager {

  private static final GridSheetEditContainer START_POINT = new GridSheetEditContainer() {
    @Override
    public GridSheetSelectionSnapshot getSelection() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void undo(CsvGridSheetPane gridSheetPane) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void redo(CsvGridSheetPane gridSheetPane) {
      throw new UnsupportedOperationException();
    }
  };

  private EventListenerSupport eventListenerSupport = new EventListenerSupportImpl();

  private int capacity;

  private final CsvGridSheetPane gridSheetPane;

  private final LinkedList<GridSheetEditContainer> undoStack;
  private final LinkedList<GridSheetEditContainer> redoStack;

  private GridSheetEditContainer savepoint;

  @Getter
  @Setter
  private boolean collecting = true;

  @Getter
  private boolean transactionStarted;

  private ArrayList<GridSheetUndoableEdit> edits;

  public GridSheetUndoManager(CsvGridSheetPane gridSheetPane, int capacity) {
    this.gridSheetPane = gridSheetPane;
    this.capacity = capacity;
    this.undoStack = new LinkedList<GridSheetEditContainer>();
    this.redoStack = new LinkedList<GridSheetEditContainer>();

    savepoint = START_POINT;
    undoStack.add(savepoint);
  }

  public void put(GridSheetUndoableEdit edit) {

    if (!collecting) {
      return;
    }

    if (transactionStarted) {
      edits.add(edit);
      return;
    }

    GridSheetSelectionSnapshot selection = gridSheetPane.getSelectionModel().exportSelection();
    GridSheetEditContainer editContainer = new SingleGridSheetEditContainer(selection, edit);

    undoStack.addFirst(editContainer);
    redoStack.clear();
    limitStackSize();
    fireStateChange();
  }

  public void clear() {
    undoStack.clear();
    redoStack.clear();
    fireStateChange();
  }

  public void undo() {
    if (!canUndo()) {
      throw new IllegalStateException("Cannot undo.");
    }
    if (gridSheetPane.isEditing()) {
      throw new IllegalStateException("Cannot undo.");
    }

    setCollecting(false);
    GridSheetEditContainer edit = undoStack.pollFirst();
    assert edit != null;
    edit.undo(gridSheetPane);
    redoStack.addFirst(edit);
    setCollecting(true);

    gridSheetPane.getSelectionModel().importSelection(edit.getSelection());

    fireStateChange();

    gridSheetPane.cellValueChanged(null);
  }

  public void redo() {
    if (!canRedo()) {
      throw new IllegalStateException();
    }
    if (gridSheetPane.isEditing()) {
      return;
    }

    setCollecting(false);
    GridSheetEditContainer edit = redoStack.pollFirst();
    assert edit != null;
    edit.redo(gridSheetPane);
    undoStack.addFirst(edit);
    setCollecting(true);

    gridSheetPane.getSelectionModel().importSelection(edit.getSelection());

    fireStateChange();

    gridSheetPane.cellValueChanged(null);
  }

  public boolean canUndo() {
    return collecting && !transactionStarted && !undoStack.isEmpty()
        && undoStack.getFirst() != START_POINT;
  }

  public boolean canRedo() {
    return collecting && !transactionStarted && !redoStack.isEmpty();
  }

  void startTransaction() {
    if (transactionStarted) {
      throw new IllegalStateException("Already started transaction.");
    }
    edits = new ArrayList<>();
    transactionStarted = true;
  }

  void stopTransaction() {
    if (!transactionStarted) {
      throw new IllegalStateException("Has not started transaction.");
    }
    transactionStarted = false;

    GridSheetSelectionSnapshot selection = gridSheetPane.getSelectionModel().exportSelection();

    GridSheetEditContainer editContainer;
    if (edits.size() == 1) {
      editContainer = new SingleGridSheetEditContainer(selection, edits.get(0));
    } else {
      editContainer = new MultiGridSheetEditContainer(selection, edits.toArray(new GridSheetUndoableEdit[0]));
    }
    undoStack.addFirst(editContainer);

    redoStack.clear();

    limitStackSize();
    fireStateChange();

    edits = null;

    gridSheetPane.cellValueChanged(null);
  }

  void stopTransactionWithoutCollecting() {
    if (!transactionStarted) {
      throw new IllegalStateException("Has not started transaction.");
    }
    transactionStarted = false;
    edits = null;
  }

  public void save() {
    savepoint = undoStack.getFirst();
    fireStateChange();
  }

  public boolean isSavepoint() {
    return !undoStack.isEmpty() && savepoint == undoStack.getFirst();
  }

  private void limitStackSize() {
    while (undoStack.size() > capacity)
      undoStack.removeLast();
    while (redoStack.size() > capacity)
      redoStack.removeLast();
  }

  /**
   * @return the capacity
   */
  public int getCapacity() {
    return capacity;
  }

  /**
   * @param capacity the capacity to set
   */
  public void setCapacity(int capacity) {
    this.capacity = capacity;
    limitStackSize();
    fireStateChange();
  }

  public boolean canUndoOrRedo() {
    return canUndo() || canRedo();
  }

  public EventListenerSupport listeners() {
    return this.eventListenerSupport;
  }

  private void fireStateChange() {
    eventListenerSupport.invokeListeners(new StateChangeEvent(isSavepoint(), canUndo(), canRedo()));
  }

  public static class StateChangeEvent implements SCEvent {
    private boolean savePoint;
    private boolean canUndo;
    private boolean canRedo;

    public StateChangeEvent(boolean savePoint, boolean canUndo, boolean canRedo) {
      this.savePoint = savePoint;
      this.canUndo = canUndo;
      this.canRedo = canRedo;
    }

    public boolean isSavePoint() {
      return savePoint;
    }

    public boolean canRedo() {
      return canRedo;
    }

    public boolean canUndo() {
      return canUndo;
    }
  }
}
