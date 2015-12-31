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
package com.smoothcsv.framework.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 * @author kohii
 */
public class MessageBundles {

  private static List<String> bundleNames = new ArrayList<>();

  static {
    // Default bundle
    bundleNames.add("message");
  }

  private MessageBundles() {}

  public static void register(String bundleName) {
    bundleNames.add(bundleName);
  }

  public static String getString(String key) {
    for (String bundleName : bundleNames) {
      ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
      if (bundle != null) {
        try {
          return bundle.getString(key);
        } catch (MissingResourceException e) {
          // ignore
        }
      }
    }
    return null;
  }

  public static String getString(String key, Object... args) {
    String string = getString(key);
    if (string != null) {
      return MessageFormat.format(string, args);
    }
    return null;
  }
}
