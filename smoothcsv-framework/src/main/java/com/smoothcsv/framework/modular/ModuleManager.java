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
package com.smoothcsv.framework.modular;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.smoothcsv.commons.exception.IORuntimeException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.modular.ModuleManifest.Language;

/**
 *
 * @author kohii
 */
public class ModuleManager {

  private final Map<String, Module> modules = new HashMap<>();
  @Getter
  private List<Language> availableLanguages;

  public ModuleManager() {}

  public void readModuleManifestsFromClasspath() {
    try {
      Enumeration<URL> moduleManifestFiles =
          getClass().getClassLoader().getResources("module.sc.properties");
      while (moduleManifestFiles.hasMoreElements()) {
        URL url = (URL) moduleManifestFiles.nextElement();
        URI classpathRoot = url.toURI().resolve(".");
        Properties properties = new Properties();
        try (InputStream in = url.openStream()) {
          properties.load(in);
          String name = properties.getProperty("name");
          String entryPoint = properties.getProperty("entryPoint");
          String dependenciesStr = properties.getProperty("dependencies");
          String supportedLanguagesStr = properties.getProperty("supportedLanguages");
          String[] dependencies = null;
          if (StringUtils.isNoneBlank(dependenciesStr)) {
            dependencies = StringUtils.splitPreserveAllTokens(dependenciesStr, ",");
          }
          Language[] supportedLanguages = null;
          if (StringUtils.isNotBlank(supportedLanguagesStr)) {
            String[] supportedLanguageStrArray =
                StringUtils.splitPreserveAllTokens(supportedLanguagesStr, ",");
            supportedLanguages = new Language[supportedLanguageStrArray.length];
            for (int i = 0; i < supportedLanguageStrArray.length; i++) {
              String langStr = supportedLanguageStrArray[i];
              int leftParenthesesIdx = langStr.indexOf('(');
              String langId = langStr.substring(leftParenthesesIdx + 1, langStr.length() - 1);
              String langName = langStr.substring(0, leftParenthesesIdx);
              supportedLanguages[i] = new Language(langId, langName);
            }
          }

          String author = properties.getProperty("author");
          ModuleManifest manifest =
              new ModuleManifest(name, entryPoint, dependencies, author, supportedLanguages);
          registerModule(manifest, classpathRoot);
        }
      }
      createLanguageList();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    } catch (URISyntaxException e) {
      throw new UnexpectedException(e);
    }
  }

  public void registerModule(ModuleManifest manifest, URI classpathRoot) {
    modules.put(manifest.getName(), new Module(manifest, classpathRoot));
  }

  private void createLanguageList() {
    // create available language list
    List<Language> availableLanguages = new ArrayList<>();
    for (Entry<String, Module> entry : modules.entrySet()) {
      Language[] languages = entry.getValue().getManifest().getSupportedLanguages();
      if (languages != null) {
        langLoop: for (Language language : languages) {
          for (Language l : availableLanguages) {
            if (l.getId().equals(language.getId())) {
              break langLoop;
            }
          }
          availableLanguages.add(language);
        }
      }
    }
    Collections.sort(availableLanguages, new Comparator<Language>() {
      @Override
      public int compare(Language o1, Language o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    this.availableLanguages = Collections.unmodifiableList(availableLanguages);
  }

  public void loadModules() {
    for (Entry<String, Module> entry : modules.entrySet()) {
      loadModule(entry.getKey());
    }
    if (Env.isDebug()) {
      for (Entry<String, Module> entry : modules.entrySet()) {
        if (entry.getValue().getStatus() != Module.LOADED) {
          throw new UnexpectedException("Module:" + entry.getKey() + " is not loaded");
        }
      }
    }
  }

  public Module getModule(String name) {
    return modules.get(name);
  }

  private void loadModule(String name) {

    Module mod = modules.get(name);

    if (mod.getStatus() != Module.NOT_YET) {
      return;
    }

    mod.setStatus(Module.LOADING);

    String[] dependencies = mod.getManifest().getDependencies();
    if (dependencies != null) {
      for (String depId : dependencies) {
        loadModule(depId);
      }
    }

    ModuleEntryPoint entryPoint = mod.getEntryPoint();
    entryPoint.activate(mod.getManifest());
    mod.setStatus(Module.LOADED);
  }

  public boolean isLoaded(String name) {
    Module mod = modules.get(name);
    return mod != null && mod.getStatus() == Module.LOADED;
  }
}
