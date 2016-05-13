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

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author kohii
 */
public class Settings extends SCProperties {

  private static final String SUFFIX = ".prefs";

  @Getter
  private final String name;

  private Properties defaultData;

  protected Settings(String name) {
    super(new File(DirectoryResolver.instance().getSettingDirectory(), name.concat(SUFFIX)));
    this.name = name;
    setSaveImmedisately(true);
    load();
  }

  public Map<String, String> getAll() {
    ensureDefaultDataLoaded();
    Map<String, String> map =
        defaultData.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(),
            e -> e.getValue() == null ? null : e.getValue().toString()));
    map.putAll(super.getAll());
    return map;
  }

  @Override
  protected String getDefault(String key) {
    ensureDefaultDataLoaded();
    if (!defaultData.containsKey(key)) {
      throw new IllegalArgumentException("key=" + key);
    }
    String val = (String) defaultData.get(key);
    return val;
  }

  private void ensureDefaultDataLoaded() {
    if (defaultData == null) {
      defaultData = new Properties();

      InputStream in;
      Class<?> clz = getClass();
      String tmpName = "/settings/" + name;

      Locale locale = Locale.getDefault();

      in = clz.getResourceAsStream(tmpName + '_' + locale.getLanguage() + SUFFIX);
      if (in == null) {
        in = clz.getResourceAsStream(tmpName.concat(SUFFIX));
      }

      if (in != null) {
        try (Reader reader = new InputStreamReader(in, CHARSET)) {
          defaultData.load(reader);
        } catch (IOException ex) {
          throw new UnexpectedException(ex);
        }
      }
    }
  }
}
