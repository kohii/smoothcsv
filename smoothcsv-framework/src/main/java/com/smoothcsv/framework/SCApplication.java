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
package com.smoothcsv.framework;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.framework.component.ComponentManager;
import com.smoothcsv.framework.component.SCFrame;
import com.smoothcsv.framework.component.support.CommandMapFactory;
import com.smoothcsv.framework.component.support.DefaultCommandMapFactory;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.condition.AvailableCondition;
import com.smoothcsv.framework.condition.Conditions;
import com.smoothcsv.framework.condition.FocusCondition;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;
import com.smoothcsv.framework.menu.MainMenuItems;
import com.smoothcsv.framework.menu.ToolBarItems;
import com.smoothcsv.framework.modular.ModuleManager;
import com.smoothcsv.framework.setting.Session;
import lombok.Getter;


/**
 * @author kohii
 */
public abstract class SCApplication {

  private static SCApplication instance;

  public static SCApplication getApplication() {
    return instance;
  }

  private static ComponentManager componentManager;

  public static ComponentManager components() {
    return componentManager;
  }

  private ModuleManager moduleManager;

  private CommandMapFactory commandMapFactory;

  private final EventListenerSupport eventListenerSupport = new EventListenerSupportImpl();

  private final String name;

  private final long startTime;

  private String[] filesToBeOpendOnReady;

  @Getter
  private boolean ready = false;

  public SCApplication(String applicationName, int os, boolean debug) {
    Env.init(os, debug);
    startTime = System.currentTimeMillis();
    if (instance != null) {
      throw new IllegalStateException();
    }
    instance = this;

    name = applicationName;
  }

  public EventListenerSupport listeners() {
    return eventListenerSupport;
  }

  public final void launch(String[] args) {
    start();

    if (args.length > 0) {
      filesToBeOpendOnReady = args;
    }
  }

  public void start() {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        ErrorHandlerFactory.getErrorHandler().handle(e);
      }
    });

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SmoothComponentManager.startAdjustingComponents();

        // Create CommandMapFactory
        commandMapFactory = createCommandMapFactory();

        // Create conditions
        Conditions.register("available", cssSelector -> new AvailableCondition(cssSelector));
        Conditions.register("focus", cssSelector -> new FocusCondition(cssSelector));

        // Load modules and language setting
        moduleManager = createModuleManager();
        prepareModuleManifests();
        setupLanguageSetting();
        moduleManager.loadModules();

        listeners().invokeListeners(new StartupEvent());

        // Create GUI
        initLookAndFeel();
        componentManager = createComponentManager();
        componentManager.initComponents();
        componentManager.getFrame().setTitle(name);

        listeners().invokeListeners(new AfterCreateGuiEvent());

        // Load menus
        MainMenuItems.instance().loadToMenuBar(componentManager.getMenuBar());

        // Load toolbar
        ToolBarItems.instance().loadToToolBar(componentManager.getToolBar());


        // Initialize conditions
        Conditions.initializeConditions();

        listeners().invokeListeners(new BeforeOpenWindowEvent());

        // Show GUI
        openWindow();

        listeners().invokeListeners(new AfterOpenWindowEvent());
      }
    });
  }

  protected void prepareModuleManifests() {
    moduleManager.readModuleManifestsFromClasspath();
  }

  protected void setupLanguageSetting() {}

  public boolean exit() {
    try {
      listeners().invokeListeners(new ShutdownEvent());
      Session.getSession().flush();
      System.exit(0);
      return true;
    } catch (Throwable t) {
      if (t instanceof CancellationException) {
        return false;
      } else {
        ErrorHandlerFactory.getErrorHandler().handle(t);
        System.exit(0);
        return true;
      }
    }
  }

  /**
   * @return the moduleManager
   */
  public ModuleManager getModuleManager() {
    return moduleManager;
  }

  public CommandMapFactory getCommandMapFactory() {
    return commandMapFactory;
  }

  /**
   * @return the startTime
   */
  public long getStartTime() {
    return startTime;
  }

  public String getName() {
    return name;
  }

  protected void openWindow() {
    SCFrame frame = componentManager.getFrame();

    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        exit();
      }

      @Override
      public void windowOpened(WindowEvent e) {
        SmoothComponentManager.stopAdjustingComponents();

        ready = true;
        if (filesToBeOpendOnReady != null && filesToBeOpendOnReady.length > 0) {
          File[] files = new File[filesToBeOpendOnReady.length];
          for (int i = 0; i < files.length; i++) {
            files[i] = new File(filesToBeOpendOnReady[i]);
          }
          filesToBeOpendOnReady = null;
          requestOpenFiles(files);
        }

        listeners().invokeListeners(new WindowOpendEvent());
      }
    });
    frame.setVisible(true);
  }

  protected void initLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException ex) {
      throw new UnexpectedException(ex);
    }
  }

  protected ComponentManager createComponentManager() {
    return new ComponentManager();
  }

  protected CommandMapFactory createCommandMapFactory() {
    return new DefaultCommandMapFactory();
  }

  protected ModuleManager createModuleManager() {
    return new ModuleManager();
  }

  public abstract void requestOpenFiles(File[] files);

  // events ----------------------
  public static class AfterCreateGuiEvent implements SCEvent {
  }

  public static class StartupEvent implements SCEvent {
  }

  public static class ShutdownEvent implements SCEvent {
  }

  public static class WindowOpendEvent implements SCEvent {
  }

  public static class BeforeOpenWindowEvent implements SCEvent {
  }

  public static class AfterOpenWindowEvent implements SCEvent {
  }
}
