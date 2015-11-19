/*
 * Copyright 2014 kohii.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCTabbedPane;
import com.smoothcsv.framework.condition.AndCondition;
import com.smoothcsv.framework.condition.ComponentHasFocusCondition;
import com.smoothcsv.framework.condition.ComponentVisibleCondition;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.NotCondition;
import com.smoothcsv.framework.event.SCListener;

/**
 *
 * @author kohii
 */
public class AppConditions {

  public static final Condition WHEN_CSVSHEET_IS_SELECTED = new Condition("editorIsSelected") {
    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          BaseTabView<?> tab = tabbedPane.getSelectedView();
          setValue(tab != null && tab instanceof CsvSheetView);
        }
      });
      BaseTabView<?> tab = tabbedPane.getSelectedView();
      setValue(tab != null && tab instanceof CsvSheetView);
    }
  };

  public static final Condition WHEN_GRID_IS_ACTIVE = new ComponentVisibleCondition("gridIsActive",
      "grid");

  public static final Condition WHEN_VALUEPANEL_IS_ACTIVE = new ComponentVisibleCondition(
      "valuepanelIsActive", "value-panel");

  public static final Condition WHEN_CELLEDITOR_IS_ACTIVE = new ComponentVisibleCondition(
      "cellEditorIsActive", "cell-editor");

  public static final Condition WHEN_GRID_IS_NOT_EDITING = new AndCondition(
      "gridIsActiveNotEditing", WHEN_GRID_IS_ACTIVE, new NotCondition(WHEN_CELLEDITOR_IS_ACTIVE));

  public static final Condition WHEN_FINDPANEL_IS_VISIBLE = new ComponentVisibleCondition(
      "findpanelIsVisible", "find-panel");

  public static final Condition WHEN_MACROTOOLS_IS_VISIBLE = new ComponentVisibleCondition(
      "macrotoolsIsVisible", "macro-tools");

  public static final Condition WHEN_MACROEDITOR_IS_VISIBLE = new ComponentVisibleCondition(
      "macroeditorIsVisible", "macro-editor");

  public static final Condition WHEN_MACROLIST_IS_VISIBLE = new ComponentVisibleCondition(
      "macrolistIsVisible", "macro-list");

  public static final Condition WHEN_SQLTOOLS_IS_VISIBLE = new ComponentVisibleCondition(
      "sqltoolsIsVisible", "sql-tools");

  public static final Condition WHEN_SELECTED_EDITOR_HAS_FILE = new Condition(
      "selectedEditorHasFile") {

    private PropertyChangeListener filePropertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent e) {
        setValue(e.getNewValue() != null);
      }
    };

    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.listeners().on(
          SCTabbedPane.ViewChangeEvent.class,
          e -> {
            if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
              ((CsvSheetView) e.getNewView()).getViewInfo().getPropertyChangeSupport()
                  .removePropertyChangeListener("file", filePropertyChangeListener);
            }
            BaseTabView<?> view = e.getNewView();
            if (view != null && view instanceof CsvSheetView) {
              CsvSheetView csvSheetView = (CsvSheetView) view;
              csvSheetView.getViewInfo().getPropertyChangeSupport()
                  .addPropertyChangeListener("file", filePropertyChangeListener);
              setValue(csvSheetView.getViewInfo().getFile() != null);
            } else {
              setValue(false);
            }
          });
      BaseTabView<?> view = tabbedPane.getSelectedView();
      setValue(view != null && (view instanceof CsvSheetView)
          && ((CsvSheetView) view).getViewInfo().getFile() != null);
    }
  };

  public static final Condition WHEN_EDITOR_IS_AT_SAVEPOINT = new Condition("atSavePoint") {

    private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener = e -> {
      setValue(e.isSavePoint());
    };

    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.listeners().on(
          SCTabbedPane.ViewChangeEvent.class,
          e -> {
            if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
              ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                  .off(stateChangeListener);
            }
            if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
              CsvSheetView view = (CsvSheetView) e.getNewView();
              view.getGridSheetPane().getUndoManager().listeners()
                  .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
              setValue(view.getGridSheetPane().getUndoManager().isSavepoint());
            } else {
              setValue(false);
            }
          });
    }
  };
  public static final Condition WHEN_EDITOR_CAN_UNDO = new Condition("canUndo") {

    private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener = e -> {
      setValue(e.canUndo());
    };

    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.listeners().on(
          SCTabbedPane.ViewChangeEvent.class,
          e -> {
            if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
              ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                  .off(stateChangeListener);
            }
            if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
              CsvSheetView view = (CsvSheetView) e.getNewView();
              view.getGridSheetPane().getUndoManager().listeners()
                  .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
              setValue(view.getGridSheetPane().getUndoManager().canUndo());
            } else {
              setValue(false);
            }
          });
    }
  };

  public static final Condition WHEN_EDITOR_CAN_REDO = new Condition("canRedo") {

    private SCListener<GridSheetUndoManager.StateChangeEvent> stateChangeListener = e -> {
      setValue(e.canRedo());
    };

    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.listeners().on(
          SCTabbedPane.ViewChangeEvent.class,
          e -> {
            if (e.getOldView() != null && e.getOldView() instanceof CsvSheetView) {
              ((CsvSheetView) e.getOldView()).getGridSheetPane().getUndoManager().listeners()
                  .off(stateChangeListener);
            }
            if (e.getNewView() != null && e.getNewView() instanceof CsvSheetView) {
              CsvSheetView view = (CsvSheetView) e.getNewView();
              view.getGridSheetPane().getUndoManager().listeners()
                  .on(GridSheetUndoManager.StateChangeEvent.class, stateChangeListener);
              setValue(view.getGridSheetPane().getUndoManager().canRedo());
            } else {
              setValue(false);
            }
          });
    }
  };

  public static final Condition WHEN_VALUEPANEL_HAS_FOCUS = new ComponentHasFocusCondition(
      "valuepanelHasFocus", "value-panel");

}
