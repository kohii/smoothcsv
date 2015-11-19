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
package com.smoothcsv.framework.setting;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kohii
 */
public class SettingManager {

  private static final Map<String, Settings> CACHE = new HashMap<>();

  public static Settings getSettings(String name) {
    Settings settings = CACHE.get(name);
    if (settings == null) {
      settings = new Settings(name);
      CACHE.put(name, settings);
    }
    return settings;
  }

  public static String get(String key) {
    Settings settings = getSettings(getSettingsNamePart(key));
    return settings.get(key);
  }

  public static Integer getInteger(String key) {
    Settings settings = getSettings(getSettingsNamePart(key));
    return settings.getInteger(key);
  }

  public static Boolean getBoolean(String key) {
    Settings settings = getSettings(getSettingsNamePart(key));
    return settings.getBoolean(key);
  }

  public static String save(String key, Object value) {
    Settings settings = getSettings(getSettingsNamePart(key));
    return settings.save(key, value);
  }

  private static String getSettingsNamePart(String key) {
    int index = key.indexOf('.');
    if (index < 0) {
      throw new IllegalArgumentException(key);
    }
    return key.substring(0, index);
  }
}
