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
package com.smoothcsv.core;

import static com.smoothcsv.framework.SCApplication.components;

import java.awt.Component;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.smoothcsv.core.condition.AppConditions;
import com.smoothcsv.core.constants.CoreSessionKeys;
import com.smoothcsv.core.csv.FileBackupService;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellRendererUI;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel;
import com.smoothcsv.core.csvsheet.CsvGridSheetColumnHeaderUI;
import com.smoothcsv.core.csvsheet.CsvGridSheetHeaderCellRendererUI;
import com.smoothcsv.core.csvsheet.CsvGridSheetTableUI;
import com.smoothcsv.core.csvsheet.CsvSheetStatusLabel;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.menu.RecentlyOpenedFilesMenu;
import com.smoothcsv.core.menu.UserDefinedMacrosMenu;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.SCApplication.AfterCreateGuiEvent;
import com.smoothcsv.framework.SCApplication.AfterOpenWindowEvent;
import com.smoothcsv.framework.SCApplication.BeforeOpenWindowEvent;
import com.smoothcsv.framework.SCApplication.ShutdownEvent;
import com.smoothcsv.framework.SCApplication.StartupEvent;
import com.smoothcsv.framework.SCApplication.WindowOpendEvent;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCFrame;
import com.smoothcsv.framework.component.SCFrame.FileDroppedEvent;
import com.smoothcsv.framework.component.SCTabbedPane.ViewChangeEvent;
import com.smoothcsv.framework.component.SCTabbedPaneUI;
import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.framework.menu.MainMenuItems;
import com.smoothcsv.framework.modular.ModuleEntryPointBase;
import com.smoothcsv.framework.modular.ModuleManifest;
import com.smoothcsv.framework.setting.Session;
import com.smoothcsv.framework.setting.Settings;
import com.smoothcsv.framework.util.InvocationUtils;
import com.smoothcsv.swing.components.History;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.utils.SwingUtils;
import command.app.CloseAllCommand;
import command.app.OpenFileCommand;


/**
 * @author kohii
 */
public class CoreEntryPoint extends ModuleEntryPointBase {

  @Override
  public ModuleManifest getManifest() {
    return ModuleManifest.builder()
        .name("smoothcsv-core")
        .author("kohii")
        .supportedLanguages(new ModuleManifest.Language[]{
            new ModuleManifest.Language("en", "English"),
            new ModuleManifest.Language("ja", "日本語")})
        .build();
  }

  @Override
  public void activate() {

    SCApplication app = SCApplication.getApplication();

    app.listeners().on(ShutdownEvent.class, new SCListener<ShutdownEvent>() {
      @Override
      public void call(ShutdownEvent event) {
        new CloseAllCommand().execute();

        for (History history : History.getAllHistories()) {
          if (!history.flushesAutomatically()) {
            history.flush();
          }
        }

        if (CoreSettings.getInstance().getBoolean(CoreSettings.DELETE_BACKUP_ON_EXIT, false)) {
          FileBackupService.getInstance().deleteAll();
        } else {
          FileBackupService.getInstance().deleteOldBackups();
        }

        Rectangle rectangle = components().getFrame().getBounds();
        Session session = Session.getSession();
        session.save(CoreSessionKeys.WINDOW_WIDTH, rectangle.width);
        session.save(CoreSessionKeys.WINDOW_HEIGHT, rectangle.height);
        session.save(CoreSessionKeys.WINDOW_X, rectangle.x);
        session.save(CoreSessionKeys.WINDOW_Y, rectangle.y);
      }
    });

    app.listeners().on(BeforeOpenWindowEvent.class, new SCListener<BeforeOpenWindowEvent>() {
      @Override
      public void call(BeforeOpenWindowEvent event) {
        SCFrame frame = components().getFrame();

        Session session = Session.getSession();
        Integer w = session.getInteger(CoreSessionKeys.WINDOW_WIDTH, 600);
        Integer h = session.getInteger(CoreSessionKeys.WINDOW_HEIGHT, 500);
        Integer x = session.getInteger(CoreSessionKeys.WINDOW_X, null);
        Integer y = session.getInteger(CoreSessionKeys.WINDOW_Y, null);

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
        uiDefaults.put("CsvGridSheetCellUI", CsvGridSheetCellRendererUI.class.getName());
        uiDefaults.put("CsvGridSheetHeaderCellUI",
            CsvGridSheetHeaderCellRendererUI.class.getName());
        uiDefaults.put("CsvGridSheetColumnHeaderUI", CsvGridSheetColumnHeaderUI.class.getName());
        uiDefaults.put("SCTabbedPaneUI", SCTabbedPaneUI.class.getName());
        SCAppearanceManager.init();
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.setDismissDelay(100000);
        toolTipManager.setInitialDelay(20);
        toolTipManager.setReshowDelay(20);

        MainMenuItems.instance().registerCustomMenu("[open_recent]", caption -> new RecentlyOpenedFilesMenu(caption));
        MainMenuItems.instance().registerCustomMenu("[user_defined_macros]", caption -> new UserDefinedMacrosMenu(caption));
      }
    });

    app.listeners().on(WindowOpendEvent.class, new SCListener<WindowOpendEvent>() {
      @Override
      public void call(WindowOpendEvent event) {
        Settings settings = CoreSettings.getInstance();
        settings.addPropertyChangeListener(CoreSettings.SIZE_OF_UNDOING,
            new PropertyChangeListener() {
              @Override
              public void propertyChange(PropertyChangeEvent evt) {
                for (BaseTabView<?> v : SCApplication.components().getTabbedPane().getAllViews()) {
                  ((CsvSheetView) v).getGridSheetPane().getUndoManager()
                      .setCapacity(settings.getInteger(CoreSettings.SIZE_OF_UNDOING));
                }
              }
            });
        InvocationUtils.runAsync(new AfterStartupTask());
      }
    });

    app.listeners().on(AfterCreateGuiEvent.class, new SCListener<AfterCreateGuiEvent>() {
      @Override
      public void call(AfterCreateGuiEvent event) {

        // set components visible
        Settings coreSettings = CoreSettings.getInstance();
        components().getStatusBar()
            .setVisible(coreSettings.getBoolean(CoreSettings.STATUSBAR_VISIBLE));
        components().getToolBar().setVisible(coreSettings.getBoolean(CoreSettings.TOOLBAR_VISIBLE));
        CsvGridSheetCellValuePanel.getInstance()
            .setValuePanelVisible(coreSettings.getBoolean(CoreSettings.VALUE_PANEL_VISIBLE));

        // Disable focus traversal
        components().getFrame().setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
          private static final long serialVersionUID = 1L;

          @Override
          protected boolean accept(Component aComponent) {
            return false;
          }
        });

        // Register Drag & Drop handler
        components().getFrame().listeners().on(FileDroppedEvent.class,
            new SCListener<FileDroppedEvent>() {
              @Override
              public void call(FileDroppedEvent ev) {
                SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    OpenFileCommand command = new OpenFileCommand();
                    if (ev.getFile().isFile()) {
                      command.run(ev.getFile());
                    } else if (ev.getFile().isDirectory()) {
                      command.chooseAndOpenFile(ev.getFile());
                    }
                  }
                });
              }
            });

        components().getTabbedPane().listeners().on(ViewChangeEvent.class,
            new SCListener<ViewChangeEvent>() {
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

    app.listeners().on(AfterOpenWindowEvent.class, new SCListener<AfterOpenWindowEvent>() {
      @Override
      public void call(AfterOpenWindowEvent event) {
        BaseTabView<?> view = components().getTabbedPane().getSelectedView();
        if (view != null) {
          view.requestFocusInWindow();
        }
      }
    });
  }

  @Override
  protected void loadConditions() {
    AppConditions.createConditions();
  }
}
