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
package com.smoothcsv.core;

import java.io.File;
import java.util.List;
import java.util.Locale;

import com.smoothcsv.core.component.SmoothCsvComponentManager;
import com.smoothcsv.core.handler.SmoothCsvErrorHandler;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.support.CommandMapFactory;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.framework.modular.ModuleManifest.Language;

import command.app.NewFileCommand;
import command.app.OpenFileCommand;

/**
 *
 * @author kohii
 */
public class SmoothCsvApp extends SCApplication {

  public SmoothCsvApp(int os, boolean debug) {
    super("SmoothCSV", os, debug);
    ErrorHandlerFactory.setErrorHandler(new SmoothCsvErrorHandler());
  }

  @Override
  protected SmoothCsvComponentManager createComponentManager() {
    return new SmoothCsvComponentManager();
  }

  @Override
  protected CommandMapFactory createCommandMapFactory() {
    return new SmoothCsvCommandMapFactory();
  }

  @Override
  protected void handleArgs(String[] args) {
    listeners().on(BeforeOpenWindowEvent.class, new SCListener<BeforeOpenWindowEvent>() {
      @Override
      public void call(BeforeOpenWindowEvent event) {
        if (args == null || args.length == 0) {
          new NewFileCommand().execute();
        } else {
          OpenFileCommand command = new OpenFileCommand();
          for (String filepath : args) {
            File f = new File(filepath);
            if (f.isFile()) {
              command.run(f);
            } else if (f.isDirectory()) {
              command.chooseAndOpenFile(f);
            }
          }
        }
      }
    });
  }

  @Override
  protected void setupLanguageSetting() {
    List<Language> langs = getModuleManager().getAvailableLanguages();
    String langSetting = CoreSettings.getInstance().get(CoreSettings.LANGUAGE);
    String languageToUse = null;
    for (Language lang : langs) {
      if (lang.getId().equals(langSetting)) {
        languageToUse = langSetting;
      }
    }
    if (languageToUse == null) {
      languageToUse = Language.EN.getId();
      CoreSettings.getInstance().save(CoreSettings.LANGUAGE, languageToUse);
    }
    Locale.setDefault(new Locale(languageToUse));
  }
}
