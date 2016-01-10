/*
 * Copyright 2015 kohii.
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.commons.utils.StringUtils;

/**
 * @author kohii
 *
 */
public class SCProperties {
  private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

  protected static final String CHARSET = "UTF-8";
  protected static final String NULL_VALUE = "<NULL>";

  private final File file;

  private Properties data;

  private boolean dirty = false;
  private boolean saveImmedisately = true;

  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  protected SCProperties(File file) {
    this.file = file;
    load();
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setSaveImmedisately(boolean saveImmedisately) {
    this.saveImmedisately = saveImmedisately;
    if (saveImmedisately) {
      store();
    }
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public String get(String key) {
    String val = (String) data.get(key);
    if (val == null || val.endsWith(NULL_VALUE)) {
      val = getDefault(key);
    }
    if (val == null || val.endsWith(NULL_VALUE)) {
      return null;
    }
    return val;
  }

  public String get(String key, String defaultValue) {
    String value = get(key);
    if (StringUtils.isEmpty(value)) {
      return defaultValue;
    }
    return value;
  }

  public Integer getInteger(String key) {
    String value = get(key);
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    try {
      return Integer.valueOf(value);
    } catch (RuntimeException e) {
      data.remove(key);
      String defaultVal = getDefault(key);
      return StringUtils.isEmpty(defaultVal) ? null : Integer.valueOf(defaultVal);
    }
  }

  public Integer getInteger(String key, Integer defaultValue) {
    Integer value = getInteger(key);
    return value == null ? defaultValue : value;
  }

  public Boolean getBoolean(String key) {
    String value = get(key);
    if (StringUtils.isEmpty(value)) {
      return null;
    }
    try {
      return Boolean.valueOf(value);
    } catch (RuntimeException e) {
      data.remove(key);
      String defaultVal = getDefault(key);
      return StringUtils.isEmpty(defaultVal) ? null : Boolean.valueOf(defaultVal);
    }
  }

  public Boolean getBoolean(String key, Boolean defaultValue) {
    Boolean value = getBoolean(key);
    return value == null ? defaultValue : value;
  }

  public Map<String, String> getAll() {
    Map<String, String> map = new HashMap<>();
    for (final String name : data.stringPropertyNames()) {
      map.put(name, data.getProperty(name));
    }
    return map;
  }

  @SuppressWarnings("rawtypes")
  public String save(String key, Object value) {
    String strVal;
    if (value == null) {
      strVal = null;
    } else if (value instanceof Enum) {
      strVal = ((Enum) value).name();
    } else {
      strVal = value.toString();
    }
    return save(key, strVal);
  }

  public void saveAll(Map<String, Object> map) {
    for (Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue() == null ? NULL_VALUE : entry.getValue().toString();
      String old = (String) data.put(key, value);
      if (value != old || (value != null && !value.equals(old))) {
        propertyChangeSupport.firePropertyChange(key, old, value);
        dirty = true;
      }
    }
    if (saveImmedisately) {
      store();
    }
  }

  public String save(String key, String value) {
    String old = (String) data.put(key, value == null ? NULL_VALUE : value);
    if (value != old || (value != null && !value.equals(old))) {
      dirty = true;
      if (saveImmedisately) {
        store();
      }
      propertyChangeSupport.firePropertyChange(key, old, value);
    }
    return old;
  }

  protected void firePropertyChange(String key, String old, String value) {
    propertyChangeSupport.firePropertyChange(key, old, value);
  }

  protected String getDefault(String key) {
    return null;
  }

  protected void load() {
    data = new Properties();
    if (FileUtils.canRead(file)) {
      try (Reader reader = new InputStreamReader(new FileInputStream(file), CHARSET)) {
        data.load(reader);
      } catch (IOException | RuntimeException ex) {
        LOG.warn("Cannot read settings from {}. Deletes this file.", file);
        if (!file.delete()) {
          LOG.warn("Cannot delete file {}.", file);
        }
      }
    }
  }

  protected void store() {
    if (dirty) {
      FileUtils.ensureWritable(file);
      try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), CHARSET)) {
        data.store(writer, null);
        dirty = false;
      } catch (IOException ex) {
        throw new UnexpectedException(ex);
      }
    }
  }

}
