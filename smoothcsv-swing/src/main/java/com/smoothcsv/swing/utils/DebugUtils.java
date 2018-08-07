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
package com.smoothcsv.swing.utils;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * @author kohii
 */
public class DebugUtils {


  public static void sysoutInputMap(JComponent com) {
    InputMap im;
    KeyStroke[] keys;

    im = com.getInputMap(JComponent.WHEN_FOCUSED);
    keys = im.allKeys();

    System.out.println("WHEN_FOCUSED");
    if (keys != null) {
      for (KeyStroke ks : keys) {
        System.out.println(ks.toString() + "\t" + im.get(ks));
      }
    }

    im = com.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    keys = im.allKeys();

    System.out.println("WHEN_ANCESTOR_OF_FOCUSED_COMPONENT");
    if (keys != null) {
      for (KeyStroke ks : keys) {
        System.out.println(ks.toString() + "\t" + im.get(ks));
      }
    }

    im = com.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    keys = im.allKeys();

    System.out.println("WHEN_IN_FOCUSED_WINDOW");
    if (keys != null) {
      for (KeyStroke ks : keys) {
        System.out.println(ks.toString() + "\t" + im.get(ks));
      }
    }
  }

  public static void sysoutActionMap(JComponent com) {
    final ActionMap am = com.getActionMap();
    Object[] keys = am.allKeys();

    for (Object ks : keys) {
      System.out.println(ks.toString() + "\t" + am.get(ks));
    }
  }
}
