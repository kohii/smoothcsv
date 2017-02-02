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
package com.smoothcsv.framework.setting;

import java.io.File;

import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.Getter;

/**
 * @author kohii
 */
public class Session {

  @Getter
  private static Session session = new Session();

  private final SCProperties properties;

  private Session() {
    File file = new File(DirectoryResolver.instance().getSessionDirectory(), "session.prefs");
    properties = new SCProperties(file);
  }

  public String get(String key, String defaultValue) {
    return properties.get(key, defaultValue);
  }

  public Integer getInteger(String key, Integer defaultValue) {
    return properties.getInteger(key, defaultValue);
  }

  public Long getLong(String key, Long defaultValue) {
    return properties.getLong(key, defaultValue);
  }

  public Boolean getBoolean(String key, Boolean defaultValue) {
    return properties.getBoolean(key, defaultValue);
  }

  public String save(String key, Object value) {
    return properties.save(key, value);
  }

  public String save(String key, String value) {
    return properties.save(key, value);
  }

  public void flush() {
    properties.store();
  }
}
