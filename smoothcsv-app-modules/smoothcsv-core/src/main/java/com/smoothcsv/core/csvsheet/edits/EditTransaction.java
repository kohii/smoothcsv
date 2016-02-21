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

import java.io.Closeable;

/**
 * @author kohii
 *
 */
public class EditTransaction implements Closeable {

  private GridSheetUndoManager undoManager;
  private boolean done = false;

  public EditTransaction(GridSheetUndoManager undoManager) {
    this.undoManager = undoManager;
    undoManager.startTransaction();
  }

  @Override
  public void close() {
    if (!done) {
      commit();
    }
    undoManager = null;
  }

  public void commit() {
    undoManager.stopTransaction();
    done = true;
  }

  public void rollback() {
    undoManager.stopTransactionWithoutCollecting();
    done = true;
  }
}
