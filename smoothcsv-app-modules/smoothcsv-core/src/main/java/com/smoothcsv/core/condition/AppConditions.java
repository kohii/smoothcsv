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
package com.smoothcsv.core.condition;

import java.beans.PropertyChangeListener;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCTabbedPane;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Conditions;
import com.smoothcsv.framework.event.SCListener;

/**
 * @author kohii
 */
public class AppConditions {

  public static void createConditions() {

    Conditions.register("csvsheet_can_undo", new Condition() {
      private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener =
          e -> revalidate();

      @Override
      protected void activate() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        tabbedPane.listeners().on(SCTabbedPane.ViewChangeEvent.class, e -> {
          if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
            ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                .off(stateChangeListener);
          }
          if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
            CsvSheetView view = (CsvSheetView) e.getNewView();
            view.getGridSheetPane().getUndoManager().listeners()
                .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
          }
          revalidate();
        });
      }

      @Override
      protected boolean computeValue() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        BaseTabView<?> view = tabbedPane.getSelectedView();
        if (view != null && view instanceof CsvSheetView) {
          return ((CsvSheetView) view).getGridSheetPane().getUndoManager().canUndo();
        }
        return false;
      }
    });

    Conditions.register("csvsheet_can_redo", new Condition() {
      private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener =
          e -> revalidate();

      @Override
      protected void activate() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        tabbedPane.listeners().on(SCTabbedPane.ViewChangeEvent.class, e -> {
          if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
            ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                .off(stateChangeListener);
          }
          if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
            CsvSheetView view = (CsvSheetView) e.getNewView();
            view.getGridSheetPane().getUndoManager().listeners()
                .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
          }
          revalidate();
        });
      }

      @Override
      protected boolean computeValue() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        BaseTabView<?> view = tabbedPane.getSelectedView();
        if (view != null && view instanceof CsvSheetView) {
          return ((CsvSheetView) view).getGridSheetPane().getUndoManager().canRedo();
        }
        return false;
      }
    });

    Conditions.register("csvsheet_is_at_savepoint", new Condition() {
      private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener =
          e -> revalidate();

      @Override
      protected void activate() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        tabbedPane.listeners().on(SCTabbedPane.ViewChangeEvent.class, e -> {
          if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
            ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                .off(stateChangeListener);
          }
          if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
            CsvSheetView view = (CsvSheetView) e.getNewView();
            view.getGridSheetPane().getUndoManager().listeners()
                .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
          }
          revalidate();
        });
      }

      @Override
      protected boolean computeValue() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        BaseTabView<?> view = tabbedPane.getSelectedView();
        if (view != null && view instanceof CsvSheetView) {
          return ((CsvSheetView) view).getGridSheetPane().getUndoManager().isSavepoint();
        }
        return false;
      }
    });

    Conditions.register("csvsheet_has_file", new Condition() {
      private PropertyChangeListener filePropertyChangeListener = e -> revalidate();

      @Override
      protected void activate() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        tabbedPane.listeners().on(SCTabbedPane.ViewChangeEvent.class, e -> {
          if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
            ((CsvSheetView) e.getNewView()).getViewInfo().getPropertyChangeSupport()
                .removePropertyChangeListener("file", filePropertyChangeListener);
          }
          BaseTabView<?> view = e.getNewView();
          if (view != null && view instanceof CsvSheetView) {
            CsvSheetView csvSheetView = (CsvSheetView) view;
            csvSheetView.getViewInfo().getPropertyChangeSupport().addPropertyChangeListener("file",
                filePropertyChangeListener);
            setValue(csvSheetView.getViewInfo().getFile() != null);
          } else {
            setValue(false);
          }
        });
        BaseTabView<?> view = tabbedPane.getSelectedView();
        setValue(view != null && (view instanceof CsvSheetView)
            && ((CsvSheetView) view).getViewInfo().getFile() != null);
      }

      @Override
      protected boolean computeValue() {
        SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
        BaseTabView<?> view = tabbedPane.getSelectedView();
        return view != null && (view instanceof CsvSheetView)
            && ((CsvSheetView) view).getViewInfo().getFile() != null;
      }
    });

    Condition whenEditingCell = Conditions.getCondition("available(cell-editor)");
    Conditions.register("editing_cell", whenEditingCell);

    Condition whenNotEditingCell = Conditions.getCondition("available(csv-sheet)&&!editing_cell");
    Conditions.register("not_editing_cell", whenNotEditingCell);

    Conditions.register("recording_macro", MacroRecorder.RECORDING);
  }
}
