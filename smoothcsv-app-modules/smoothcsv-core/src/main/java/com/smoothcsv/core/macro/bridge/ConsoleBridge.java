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
package com.smoothcsv.core.macro.bridge;

import java.lang.reflect.Array;
import java.util.IllegalFormatException;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.ScriptRuntime;

import com.smoothcsv.core.component.SmoothCsvComponentManager;
import com.smoothcsv.framework.SCApplication;

/**
 * @author kohii
 *
 */
public class ConsoleBridge {

  public static final Object CONSOLE_LOG_INVOKED = new Object() {
    public String toString() {
      return "";
    };
  };

  private ConsoleBridge() {}

  public static Object logString(String s) {
    SmoothCsvComponentManager componentManager =
        (SmoothCsvComponentManager) SCApplication.components();
    componentManager.getMacroTools().getConsolePanel().append(s == null ? "null" : s);
    return CONSOLE_LOG_INVOKED;
  }

  public static Object log(Object[] args) {
    if (args.length == 0) {
      return logString("undefined");
    }
    if (args.length == 1) {
      return logString(toString(args[0]));
    }
    if (args[0] != null && args[0] instanceof CharSequence) {
      // format
      // FIXME the below supports only %s
      String s = args[0].toString();
      if (s.indexOf('%') >= 0) {
        Object[] fmtArgs = new Object[args.length - 1];
        for (int i = 0; i < fmtArgs.length; i++) {
          fmtArgs[i] = toString(args[i + 1]);
        }
        try {
          String formatted = String.format(s, fmtArgs);
          return logString(formatted);
        } catch (IllegalFormatException ignore) {
          ignore.printStackTrace();
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      if (i != 0) {
        sb.append(" ");
      }
      sb.append(toString(args[i]));
    }
    return logString(sb.toString());
  }

  private static String toString(Object obj) {
    if (obj == null) {
      return "null";
    }
    if (obj instanceof String) {
      return (String) obj;
    }
    if (obj instanceof NativeJavaArray) {
      Object array = ((NativeJavaArray) obj).unwrap();
      int len = Array.getLength(array);
      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < len; i++) {
        if (i != 0) {
          sb.append(",");
        }
        sb.append(toString(Array.get(array, i)));
      }
      return sb.append("]").toString();
    }
    if (obj instanceof BaseFunction) {
      String funcName = ((BaseFunction) obj).getFunctionName();
      if (StringUtils.isEmpty(funcName)) {
        return "function()";
      }
      return "function " + funcName + "()";
    }
    String str = ScriptRuntime.toString(obj);
    if (obj instanceof NativeArray) {
      return "[" + str + "]";
    }
    return str;
  }
}
