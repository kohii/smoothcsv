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

import java.awt.Toolkit;

/**
 *
 * @author kohii
 */
public class PlatformUtils {

  private static final String OS = System.getProperty("os.name");

  private static final boolean WINDOWS = OS.startsWith("Windows");
  private static final boolean MAC = OS.startsWith("Mac");
  private static final boolean LINUX = OS.startsWith("Linux");

  /**
   * Returns true if the operating system is a form of Windows.
   */
  public static boolean isWindows() {
    return WINDOWS;
  }

  /**
   * Returns true if the operating system is a form of Mac OS.
   */
  public static boolean isMac() {
    return MAC;
  }

  /**
   * Returns true if the operating system is a form of Linux.
   */
  public static boolean isLinux() {
    return LINUX;
  }

  public static int getShortcutMask() {
    return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  }
}
