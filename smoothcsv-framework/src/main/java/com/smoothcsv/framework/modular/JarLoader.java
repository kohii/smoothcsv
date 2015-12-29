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
package com.smoothcsv.framework.modular;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author kohii
 */
public class JarLoader {

  public static void load(String pathname) throws IOException {
    load(new File(pathname));
  }

  public static void load(File file) throws IOException {
    load(file.toURI());
  }

  public static void load(URI u) throws IOException {
    URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    try {
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
      method.setAccessible(true);
      method.invoke(sysloader, new Object[] {u});
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new IOException("Error, could not load Jar file.", e);
    }

  }
}
