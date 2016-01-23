/*
 * Copyright 2015 kohii
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
package com.smoothcsv.mac;

import java.io.File;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.swing.utils.SwingUtils;

import command.app.OpenFileCommand;

/**
 *
 * @author kohii
 */
public class SmoothCSV {

  public static void main(String[] args) {
    customizeForMac();
    SCApplication application = new SmoothCsvApp(Env.OS_MAC, false);
    application.launch(args);
  }

  static void customizeForMac() {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.macos.smallTabs", "true");

    Application app = Application.getApplication();

    app.setAboutHandler(new AboutHandler() {
      @Override
      public void handleAbout(AboutEvent e) {
        CommandRegistry.instance().runCommand("app:about");
      }
    });

    app.setDockIconImage(SwingUtils.getImage("/img/appicon.png"));
    app.setOpenFileHandler(new OpenFilesHandler() {
      @Override
      public void openFiles(OpenFilesEvent e) {
        OpenFileCommand command = new OpenFileCommand();
        for (File f : e.getFiles()) {
          if (f.isFile()) {
            command.run(f);
          } else if (f.isDirectory()) {
            command.chooseAndOpenFile(f);
          }
        }
      }
    });

    // TODO
    // app.setOpenURIHandler(new OpenURIHandler() {
    // @Override
    // public void openURI(OpenURIEvent e) {
    //
    // }
    // });

    app.setPreferencesHandler(new PreferencesHandler() {
      @Override
      public void handlePreferences(PreferencesEvent e) {
        CommandRegistry.instance().runCommand("app:show-settings");
      }
    });

    app.setQuitHandler(new QuitHandler() {
      @Override
      public void handleQuitRequestWith(QuitEvent ev, QuitResponse res) {
        try {
          if (SCApplication.getApplication().exit()) {
            res.performQuit();
          } else {
            res.cancelQuit();
          }
        } catch (CancellationException e) {
          res.cancelQuit();
        } catch (RuntimeException e) {
          res.performQuit();
        }
      }
    });

    // app.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
  }
}
