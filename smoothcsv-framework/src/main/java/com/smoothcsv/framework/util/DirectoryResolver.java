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
package com.smoothcsv.framework.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.SCApplication;

/**
 *
 * @author kohii
 */
public class DirectoryResolver {

  private static final Logger LOG = LoggerFactory.getLogger(DirectoryResolver.class);

  private static final char FILE_SEPARATOR = File.separatorChar;
  private static final String SETTING_DIRECTORY_NAME = "setting";
  private static final String HISTORY_DIRECTORY_NAME = "history";
  private static final String TEMPORARY_DIRECTORY_NAME = "tmp";
  private static final String MODULE_DIRECTORY_NAME = "modules";
  private static final String MACRO_DIRECTORY_NAME = "macro";

  private static DirectoryResolver instance;

  public static DirectoryResolver instance() {
    if (instance == null) {
      if (Env.isDebug()) {
        instance = new DirectoryResolver() {
          @Override
          protected String createAppDataDirectory() {
            LOG.debug("user.home={}" + System.getProperty("user.home"));
            LOG.debug("user.dir={}" + System.getProperty("user.dir"));
            return System.getProperty("user.home") + FILE_SEPARATOR + "SmoothCSV_DEV"
                + FILE_SEPARATOR;
          }
        };
        LOG.debug("setting directory: {}", instance.getSettingDirectory());
        LOG.debug("history directory: {}", instance.getHistoryDirectory());
        LOG.debug("temporary directory: {}", instance.getTemporaryDirectory());
      } else {
        instance = new DirectoryResolver();
      }
    }
    return instance;
  }

  private File settingDirectory;

  private File historyDirectory;

  private File appDataDirectory;

  private File userDirectory;

  private File temporaryDirectory;

  private File moduleJarDirectory;

  private File macroFileDirectory;

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

  public File getHistoryDirectory() {
    if (historyDirectory == null) {
      historyDirectory = new File(getAppDataDirectory(), HISTORY_DIRECTORY_NAME);
      FileUtils.ensureDirectoryExists(historyDirectory);
    }
    return historyDirectory;
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

  protected File getUserDataDirectory() {
    if (userDirectory == null) {
      String appName = SCApplication.getApplication().getName();
      String base = System.getProperty("user.home");
      if (base == null) {
        base = System.getProperty("user.dir");
        if (base == null) {
          base = "." + FILE_SEPARATOR;
        }
      }
      userDirectory = new File(base + FILE_SEPARATOR + '.' + appName + FILE_SEPARATOR);
    }
    return userDirectory;
  }

  protected String createAppDataDirectory() {
    String appName =
        SCApplication.getApplication() == null ? "SmoothCSV" : SCApplication.getApplication()
            .getName();

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
      return new StringBuilder().append(userDir == null ? "" : userDir).append(FILE_SEPARATOR)
          .append('.').append(appName).append(FILE_SEPARATOR).toString();
    }
  }
}
