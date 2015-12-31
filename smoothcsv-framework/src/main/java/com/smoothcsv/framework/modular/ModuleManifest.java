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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kohii
 *
 */
@Getter
public class ModuleManifest {
  private final String name;
  private final String entryPoint;
  private final String[] dependencies;
  private final String author;
  private final Language[] supportedLanguages;

  public ModuleManifest(String name, String entryPoint, String[] dependencies, String author,
      Language[] supportedLanguages) {
    this.name = name;
    this.entryPoint = entryPoint;
    this.dependencies = dependencies;
    this.author = author;
    this.supportedLanguages = supportedLanguages;
  }

  @AllArgsConstructor
  @Getter
  public static class Language {
    public static final Language EN = new Language("en", "English");
    private final String id;
    private final String name;
  }
}
