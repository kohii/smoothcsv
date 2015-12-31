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
package com.smoothcsv.framework.setting;

/**
 *
 * @author kohii
 */
public class SettingsMeta {

  private final String name;
  private final boolean saveImmediately;

  public SettingsMeta(String name, boolean saveImmediately) {
    this.name = name;
    this.saveImmediately = saveImmediately;
  }

  public SettingsMeta(String name) {
    this(name, false);
  }

  public String getName() {
    return name;
  }

  public boolean isSaveImmediately() {
    return saveImmediately;
  }
}
