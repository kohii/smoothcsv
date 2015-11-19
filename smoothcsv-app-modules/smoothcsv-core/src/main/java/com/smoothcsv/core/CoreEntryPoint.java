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
package com.smoothcsv.core;

import static com.smoothcsv.framework.SCApplication.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.smoothcsv.commons.utils.JsonUtils;
import com.smoothcsv.core.condition.AppConditions;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel;
import com.smoothcsv.core.csvsheet.CsvGridSheetColumnHeaderUI;
import com.smoothcsv.core.csvsheet.CsvGridSheetTableUI;
import com.smoothcsv.core.csvsheet.CsvSheetStatusLabel;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.preference.EditorPrefPanel;
import com.smoothcsv.core.preference.GeneralPrefPanel;
import com.smoothcsv.core.preference.KeyBindingsPrefPanel;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.SCApplication.AfterCreateGuiEvent;
import com.smoothcsv.framework.SCApplication.BeforeOpenWindowEvent;
import com.smoothcsv.framework.SCApplication.ShutdownEvent;
import com.smoothcsv.framework.SCApplication.StartupEvent;
import com.smoothcsv.framework.SCApplication.WindowOpendEvent;
import com.smoothcsv.framework.command.CommandRepository;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCFrame;
import com.smoothcsv.framework.component.SCFrame.FileDroppedEvent;
import com.smoothcsv.framework.component.SCTabbedPane.ViewChangeEvent;
import com.smoothcsv.framework.component.SCTabbedPaneUI;
import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Conditions;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.framework.modular.AbstractModuleEntryPoint;
import com.smoothcsv.framework.preference.PrefPage;
import com.smoothcsv.framework.preference.PreferenceManager;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.framework.util.InvocationUtils;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.utils.SwingUtils;
import command.app.CloseAllCommand;
import command.app.OpenFileCommand;
import command.grid.GridSheetSelectCommand;
import command.grid.GridSheetSelectNextCellCommand;


/**
 *
 * @author kohii
 */
public class CoreEntryPoint extends AbstractModuleEntryPoint {

  @Override
  public void activate() {

    SCApplication app = SCApplication.getApplication();
    app.listeners().on(ShutdownEvent.class, new SCListener<ShutdownEvent>() {
      @Override
      public void call(ShutdownEvent event) {
        new CloseAllCommand().execute();

        Rectangle rectangle = components().getFrame().getBounds();
        Settings session = SettingManager.getSettings(AppSettingKeys.Session.$);
        Map<String, Object> map = new HashMap<>();
        map.put(AppSettingKeys.Session.WINDOW_WIDTH, rectangle.width);
        map.put(AppSettingKeys.Session.WINDOW_HEIGHT, rectangle.height);
        map.put(AppSettingKeys.Session.WINDOW_X, rectangle.x);
        map.put(AppSettingKeys.Session.WINDOW_Y, rectangle.y);
        session.saveAll(map);
      }
    });

    app.listeners().on(BeforeOpenWindowEvent.class, new SCListener<BeforeOpenWindowEvent>() {
      @Override
      public void call(BeforeOpenWindowEvent event) {
        SCFrame frame = components().getFrame();
        Settings session = SettingManager.getSettings(AppSettingKeys.Session.$);

        Integer w = session.getInteger(AppSettingKeys.Session.WINDOW_WIDTH, 600);
        Integer h = session.getInteger(AppSettingKeys.Session.WINDOW_HEIGHT, 500);
        Integer x = session.getInteger(AppSettingKeys.Session.WINDOW_X);
        Integer y = session.getInteger(AppSettingKeys.Session.WINDOW_Y);

        frame.setSize(w, h);
        if (x == null || y == null || x < 0 || y < 0) {
          frame.setLocationRelativeTo(null);
        } else {
          frame.setLocation(x, y);
        }
        SwingUtils.trimByWindowSize(frame);
      }
    });

    app.listeners().on(StartupEvent.class, new SCListener<StartupEvent>() {
      @Override
      public void call(StartupEvent event) {
        SCFocusManager.init();
        GridSheetUtils.initializeUI();
        UIDefaults uiDefaults = UIManager.getDefaults();
        uiDefaults.put("CsvGridSheetTableUI", CsvGridSheetTableUI.class.getName());
        uiDefaults.put("CsvGridSheetColumnHeaderUI", CsvGridSheetColumnHeaderUI.class.getName());
        uiDefaults.put("SCTabbedPaneUI", SCTabbedPaneUI.class.getName());
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setDismissDelay(100000);
        toolTipManager.setInitialDelay(200);
        toolTipManager.setReshowDelay(100);
      }
    });

    app.listeners().on(WindowOpendEvent.class, new SCListener<WindowOpendEvent>() {
      @Override
      public void call(WindowOpendEvent event) {
        Settings settings = SettingManager.getSettings(AppSettingKeys.Editor.$);
        settings.addPropertyChangeListener(AppSettingKeys.Editor.SIZE_OF_UNDOING,
            new PropertyChangeListener() {
              @Override
              public void propertyChange(PropertyChangeEvent evt) {
                for (BaseTabView<?> v : SCApplication.components().getTabbedPane().getAllViews()) {
                  ((CsvSheetView) v).getGridSheetPane().getUndoManager()
                      .setCapacity(settings.getInteger(AppSettingKeys.Editor.SIZE_OF_UNDOING));
                }
              }
            });
        InvocationUtils.runAsync(new Runnable() {
          @Override
          public void run() {
            PreferenceManager.getInstance().addPrefPage(
                new PrefPage("General", GeneralPrefPanel.class));
            PreferenceManager.getInstance().addPrefPage(
                new PrefPage("Editor", EditorPrefPanel.class));
            PreferenceManager.getInstance().addPrefPage(
                new PrefPage("Key Bindings", KeyBindingsPrefPanel.class));

            // Execute JsonUtils.stringify() so that it can perform faster next time
            try {
              JsonUtils.stringify(new HashMap<Object, Object>());
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
      }
    });

    app.listeners().on(AfterCreateGuiEvent.class, new SCListener<AfterCreateGuiEvent>() {
      @Override
      public void call(AfterCreateGuiEvent event) {

        // set components visible
        Settings coreSettings = SettingManager.getSettings(AppSettingKeys.Core.$);
        components().getStatusBar().setVisible(
            coreSettings.getBoolean(AppSettingKeys.Core.STATUSBAR_VISIBLE));
        components().getToolBar().setVisible(
            coreSettings.getBoolean(AppSettingKeys.Core.TOOLBAR_VISIBLE));
        CsvGridSheetCellValuePanel.getInstance().setValuePanelVisible(
            coreSettings.getBoolean(AppSettingKeys.Core.VALUEPANEL_VISIBLE));

        // Disable focus traversal
        components().getFrame().setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
          private static final long serialVersionUID = 1L;

          @Override
          protected boolean accept(Component aComponent) {
            return false;
          }
        });

        // Register Drag & Drop handler
        components().getFrame().listeners()
            .on(FileDroppedEvent.class, new SCListener<FileDroppedEvent>() {
              @Override
              public void call(FileDroppedEvent ev) {
                OpenFileCommand command =
                    (OpenFileCommand) CommandRepository.instance().getCommand("app:openFile");
                if (ev.getFile().isFile()) {
                  command.run(ev.getFile());
                } else if (ev.getFile().isDirectory()) {
                  command.chooseAndOpenFile(ev.getFile());
                }
              }
            });

        components().getTabbedPane().listeners()
            .on(ViewChangeEvent.class, new SCListener<ViewChangeEvent>() {
              @Override
              public void call(ViewChangeEvent ev) {
                if (ev.getOldView() != null && ev.getOldView() instanceof CsvSheetView) {
                  CsvSheetView oldView = (CsvSheetView) ev.getOldView();
                  oldView.getGridSheetPane().getTable().stopCellEditing();
                }
                CsvGridSheetCellValuePanel cvp = CsvGridSheetCellValuePanel.getInstance();
                if (ev.getNewView() != null) {
                  if (cvp.isValuePanelVisible() && !cvp.isFloating()) {
                    cvp.setValuePanelVisible(true);
                  }
                } else {
                  if (cvp.isFloating()) {
                    cvp.toggleFloating();
                  }
                }
              }
            });

        CsvSheetStatusLabel.instance().install();
      }
    });
  }

  @Override
  protected void loadCommands(CommandRepository repository) {

    {
      // File commands

      repository.register("app:newFile");
      repository.register("app:newFileAs");
      repository.register("app:openFile");
      repository.register("app:openFileAs");
      repository.register("app:save", AppConditions.WHEN_CSVSHEET_IS_SELECTED);
      repository.register("app:saveAs", AppConditions.WHEN_CSVSHEET_IS_SELECTED);
      repository.register("app:saveAll", AppConditions.WHEN_CSVSHEET_IS_SELECTED);
      repository.register("app:close", Conditions.WHEN_VIEW_EXISTS);
      repository.register("app:closeOthers", Conditions.WHEN_VIEW_EXISTS);
      repository.register("app:closeAll", Conditions.WHEN_VIEW_EXISTS);

      repository.register("csvsheet:showProperties", Conditions.WHEN_VIEW_EXISTS);

      repository.register("app:quit", Conditions.ALWAYS);
    }

    {
      // Tools commands

      repository.register("app:toggleCommandPalette");
      repository.register("app:showSqlTools");
      repository.register("app:showSettings");
    }

    {
      // Help commands

      repository.register("app:about");
    }

    {
      // GridSheet commands

      Condition whenGridIsActive = AppConditions.WHEN_GRID_IS_ACTIVE;
      // Condition whenEditing = AppConditions.WHEN_GRID_IS_EDITING;
      Condition whenNotEditing = AppConditions.WHEN_GRID_IS_NOT_EDITING;

      repository.register("grid:undo", AppConditions.WHEN_EDITOR_CAN_UNDO);
      repository.register("grid:redo", AppConditions.WHEN_EDITOR_CAN_REDO);

      repository.register("grid:copy", whenNotEditing);
      repository.register("grid:cut", whenNotEditing);
      repository.register("grid:paste", whenNotEditing);
      repository.register("grid:copyUsingFileFormat", whenNotEditing);
      repository.register("grid:cutUsingFileFormat", whenNotEditing);
      repository.register("grid:pasteUsingFileFormat", whenNotEditing);
      repository.register("grid:clear", whenNotEditing);
      repository.register("grid:deleteCell", whenGridIsActive);

      repository.register("grid:selectAll", whenGridIsActive);
      repository.register("grid:selectEntireRow", whenGridIsActive);
      repository.register("grid:selectEntireColumn", whenGridIsActive);

      repository.register("grid:startEdit", whenGridIsActive);

      repository.register("grid:fillDown", whenGridIsActive);
      repository.register("grid:fillUp", whenGridIsActive);
      repository.register("grid:fillLeft", whenGridIsActive);
      repository.register("grid:fillRight", whenGridIsActive);

      repository.register("grid:sort", whenGridIsActive);
      repository.register("grid:sortSelectedRows", whenGridIsActive);
      repository.register("grid:sortSelectedRange", whenGridIsActive);

      repository.register("grid:selectLeft", new GridSheetSelectCommand(
          GridSheetSelectCommand.DECREMENT, 0, false), whenGridIsActive);
      repository.register("grid:selectRight", new GridSheetSelectCommand(
          GridSheetSelectCommand.INCREMENT, 0, false), whenGridIsActive);
      repository.register("grid:selectUp", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.DECREMENT, false), whenGridIsActive);
      repository.register("grid:selectDown", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.INCREMENT, false), whenGridIsActive);

      repository.register("grid:extendLeft", new GridSheetSelectCommand(
          GridSheetSelectCommand.DECREMENT, 0, true), whenGridIsActive);
      repository.register("grid:extendRight", new GridSheetSelectCommand(
          GridSheetSelectCommand.INCREMENT, 0, true), whenGridIsActive);
      repository.register("grid:extendUp", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.DECREMENT, true), whenGridIsActive);
      repository.register("grid:extendDown", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.INCREMENT, true), whenGridIsActive);

      repository.register("grid:selectFirstColumn", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_FIRST, 0, false), whenGridIsActive);
      repository.register("grid:selectLastColumn", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_LAST, 0, false), whenGridIsActive);
      repository.register("grid:selectFirstRow", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.TO_FIRST, false), whenGridIsActive);
      repository.register("grid:selectLastRow", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.TO_LAST, false), whenGridIsActive);
      repository.register("grid:selectFirstCell", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_FIRST, GridSheetSelectCommand.TO_FIRST, false),
          whenGridIsActive);
      repository.register("grid:selectLastCell", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_LAST, GridSheetSelectCommand.TO_LAST, false), whenGridIsActive);

      repository.register("grid:extendFirstColumn", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_FIRST, 0, true), whenGridIsActive);
      repository.register("grid:extendLastColumn", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_LAST, 0, true), whenGridIsActive);
      repository.register("grid:extendFirstRow", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.TO_FIRST, true), whenGridIsActive);
      repository.register("grid:extendLastRow", new GridSheetSelectCommand(0,
          GridSheetSelectCommand.TO_LAST, true), whenGridIsActive);
      repository
          .register("grid:extendFirstCell", new GridSheetSelectCommand(
              GridSheetSelectCommand.TO_FIRST, GridSheetSelectCommand.TO_FIRST, true),
              whenGridIsActive);
      repository.register("grid:extendLastCell", new GridSheetSelectCommand(
          GridSheetSelectCommand.TO_LAST, GridSheetSelectCommand.TO_LAST, true), whenGridIsActive);

      repository.register("grid:selectNextCellHorizontally", new GridSheetSelectNextCellCommand(
          GridSheetSelectNextCellCommand.NEXT, 0), whenGridIsActive);
      repository.register("grid:selectPrevCellHorizontally", new GridSheetSelectNextCellCommand(
          GridSheetSelectNextCellCommand.PREVIOUS, 0), whenGridIsActive);
      repository.register("grid:selectNextCellVertically", new GridSheetSelectNextCellCommand(0,
          GridSheetSelectNextCellCommand.NEXT), whenGridIsActive);
      repository.register("grid:selectPrevCellVertically", new GridSheetSelectNextCellCommand(0,
          GridSheetSelectNextCellCommand.PREVIOUS), whenGridIsActive);

      repository.register("grid:insertRowsAbove", whenGridIsActive);
      repository.register("grid:insertRowsBelow", whenGridIsActive);
      repository.register("grid:deleteRows", whenGridIsActive);
      repository.register("grid:insertColumnsLeft", whenGridIsActive);
      repository.register("grid:insertColumnsRight", whenGridIsActive);
      repository.register("grid:deleteColumns", whenGridIsActive);

      repository.register("grid:goToLine", whenGridIsActive);
    }

    {
      // Cell Editor
      repository.register("grid:stopEdit", AppConditions.WHEN_CELLEDITOR_IS_ACTIVE);
      repository.register("grid:cancelEdit", AppConditions.WHEN_CELLEDITOR_IS_ACTIVE);
      repository.register("cell_editor:undo", AppConditions.WHEN_CELLEDITOR_IS_ACTIVE);
      repository.register("cell_editor:redo", AppConditions.WHEN_CELLEDITOR_IS_ACTIVE);
      repository.register("cell_editor:insertBreak", AppConditions.WHEN_CELLEDITOR_IS_ACTIVE);
    }

    {
      // Value Panel
      repository.register("value_panel:toggleFloating", AppConditions.WHEN_VALUEPANEL_IS_ACTIVE);
      repository.register("value_panel:expand", AppConditions.WHEN_VALUEPANEL_IS_ACTIVE);
      repository.register("value_panel:compress", AppConditions.WHEN_VALUEPANEL_IS_ACTIVE);
      repository.register("value_panel:undo", AppConditions.WHEN_VALUEPANEL_HAS_FOCUS);
      repository.register("value_panel:redo", AppConditions.WHEN_VALUEPANEL_HAS_FOCUS);
      repository.register("value_panel:insertBreak", AppConditions.WHEN_VALUEPANEL_HAS_FOCUS);
    }

    {
      // Conversion commands
      repository.register("convert:upperCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
      repository.register("convert:lowerCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
      repository.register("convert:swapCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
      repository.register("convert:titleCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
      repository.register("convert:snakeCaseToCamelCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
      repository.register("convert:camelCaseToSnakeCase", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    }

    {
      // View commands

      repository.register("view:autofitColumnWidth", Conditions.WHEN_VIEW_EXISTS);
      repository.register("view:autofitColumnWidthToSelectedData", Conditions.WHEN_VIEW_EXISTS);
      repository.register("view:previousView", Conditions.WHEN_VIEW_EXISTS);
      repository.register("view:nextView", Conditions.WHEN_VIEW_EXISTS);
      // repository.register("view:toggleToolBar");
      repository.register("view:toggleStatusBar");
      repository.register("view:toggleCellValuePanel");
    }

    {
      // find and replace command
      repository.register("find:show", AppConditions.WHEN_CSVSHEET_IS_SELECTED);
      repository.register("find:hide", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:next", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:prev", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:count", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:replaceNext", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:replacePrev", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
      repository.register("find:replaceAll", AppConditions.WHEN_FINDPANEL_IS_VISIBLE);
    }

    {
      // Macro
      repository.register("macro:toggleMacroTools");
      repository.register("macro:toggleConsole", AppConditions.WHEN_MACROTOOLS_IS_VISIBLE);
      repository.register("macroeditor:open", AppConditions.WHEN_MACROEDITOR_IS_VISIBLE);
      repository.register("macroeditor:save", AppConditions.WHEN_MACROEDITOR_IS_VISIBLE);
      repository.register("macroeditor:run", AppConditions.WHEN_MACROEDITOR_IS_VISIBLE);
      repository.register("macrolist:add", AppConditions.WHEN_MACROLIST_IS_VISIBLE);
      repository.register("macrolist:remove", AppConditions.WHEN_MACROLIST_IS_VISIBLE);
      repository.register("macrolist:run", AppConditions.WHEN_MACROLIST_IS_VISIBLE);
    }

    {
      // SQL
      repository.register("sql:run", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:open", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:save", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:nextSql", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:previousSql", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:showSqlHistory", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:addTable", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
      repository.register("sql:removeTable", AppConditions.WHEN_SQLTOOLS_IS_VISIBLE);
    }
  }
}
