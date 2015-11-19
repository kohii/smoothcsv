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

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 *
 * @author kohii
 */
public class SCBundle {

  private static final Properties PROPS = new Properties();

  public static void register(String resourceName) {
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(resourceName);
      Enumeration<String> keys = bundle.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        PROPS.setProperty(key, bundle.getString(key));
      }
    } catch (MissingResourceException e) {
      // do nothing
    }
  }

  public static String get(String key) {
    return PROPS.getProperty(key);
  }

  public static String get(String key, Object... args) {
    String prop = get(key);
    if (prop != null) {
      return MessageFormat.format(prop, args);
    }
    return null;
  }
}
