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
package com.smoothcsv.framework.util;

import java.io.File;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.Env;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
public class DirectoryResolver {

  private static final char FILE_SEPARATOR = File.separatorChar;
  private static final String SETTING_DIRECTORY_NAME = "setting";
  private static final String SESSION_DIRECTORY_NAME = "session";
  private static final String TEMPORARY_DIRECTORY_NAME = "tmp";
  private static final String MODULE_DIRECTORY_NAME = "modules";
  private static final String MACRO_DIRECTORY_NAME = "macro";
  private static final String BACKUP_DIRECTORY_NAME = "backup";
  private static final String LOG_DIRECTORY_NAME = "logs";

  private static DirectoryResolver instance;

  public static DirectoryResolver instance() {
    if (instance == null) {
      if (Env.isDebug()) {
        instance = new DirectoryResolver() {
          @Override
          protected String createAppDataDirectory() {
            System.out.println("user.home=" + System.getProperty("user.home"));
            System.out.println("user.dir=" + System.getProperty("user.dir"));
            return System.getProperty("user.home")
                + FILE_SEPARATOR
                + "SmoothCSV 2 DEV"
                + FILE_SEPARATOR;
          }
        };
        System.out.println("setting directory: " + instance.getSettingDirectory());
        System.out.println("session directory: " + instance.getSessionDirectory());
        System.out.println("temporary directory: " + instance.getTemporaryDirectory());
      } else {
        instance = new DirectoryResolver();
      }
    }
    return instance;
  }

  private File settingDirectory;

  private File sessionDirectory;

  private File appDataDirectory;

  // private File userDirectory;

  private File temporaryDirectory;

  private File moduleJarDirectory;

  private File macroFileDirectory;

  private File backupDirectory;

  private File logDirectory;

  public DirectoryResolver() {}

  public File getAppDataDirectory() {
    if (appDataDirectory == null) {
      appDataDirectory = new File(createAppDataDirectory());
    }
    return appDataDirectory;
  }

  public File getSettingDirectory() {
    if (settingDirectory == null) {
      settingDirectory = new File(getAppDataDirectory(), SETTING_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(settingDirectory);
    }
    return settingDirectory;
  }

  public File getTemporaryDirectory() {
    if (temporaryDirectory == null) {
      temporaryDirectory = new File(getAppDataDirectory(), TEMPORARY_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(temporaryDirectory);
    }
    return temporaryDirectory;
  }

  public File getSessionDirectory() {
    if (sessionDirectory == null) {
      sessionDirectory = new File(getAppDataDirectory(), SESSION_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(sessionDirectory);
    }
    return sessionDirectory;
  }

  public File getModuleJarDirectory() {
    if (moduleJarDirectory == null) {
      moduleJarDirectory = new File(getAppDataDirectory(), MODULE_DIRECTORY_NAME);
    }
    return moduleJarDirectory;
  }

  public File getMacroFileDirectory() {
    if (macroFileDirectory == null) {
      macroFileDirectory = new File(getAppDataDirectory(), MACRO_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(macroFileDirectory);
    }
    return macroFileDirectory;
  }

  public File getBackupDirectory() {
    if (backupDirectory == null) {
      backupDirectory = new File(getAppDataDirectory(), BACKUP_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(backupDirectory);
    }
    return backupDirectory;
  }

  public File getLogDirectory() {
    if (logDirectory == null) {
      logDirectory = new File(getAppDataDirectory(), LOG_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(logDirectory);
    }
    return logDirectory;
  }

  // protected File getUserDataDirectory() {
  // if (userDirectory == null) {
  // String appName = SCApplication.getApplication().getName();
  // String base = System.getProperty("user.home");
  // if (base == null) {
  // base = System.getProperty("user.dir");
  // if (base == null) {
  // base = "." + FILE_SEPARATOR;
  // }
  // }
  // userDirectory = new File(base + FILE_SEPARATOR + '.' + appName + FILE_SEPARATOR);
  // }
  // return userDirectory;
  // }

  protected String createAppDataDirectory() {
    String appName = "SmoothCSV 2";

    if (PlatformUtils.isWindows()) {
      String appdata = System.getenv("APPDATA");
      if (appdata != null) {
        return appdata + FILE_SEPARATOR + appName + FILE_SEPARATOR;
      } else {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
          String userDir = System.getProperty("user.dir");
          if (userDir != null) {
            return userDir + FILE_SEPARATOR + '.' + appName + FILE_SEPARATOR;
          } else {
            return "." + FILE_SEPARATOR;
          }
        } else {
          return userHome + FILE_SEPARATOR + '.' + appName + FILE_SEPARATOR;
        }
      }
    } else {
      String userDir = System.getProperty("user.home");
      if (StringUtils.isNotEmpty(userDir)) {
        return userDir
            + (userDir.charAt(userDir.length() - 1) == FILE_SEPARATOR ? "" : FILE_SEPARATOR)
            + "Library" + FILE_SEPARATOR + "Application Support" + FILE_SEPARATOR + "SmoothCSV 2"
            + FILE_SEPARATOR;
      } else {
        return "~" + FILE_SEPARATOR + ".SmoothCSV 2" + FILE_SEPARATOR;
      }
    }
  }
}
