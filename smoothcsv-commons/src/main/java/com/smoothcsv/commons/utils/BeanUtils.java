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
package com.smoothcsv.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.smoothcsv.commons.exception.UnexpectedException;

/**
 * @author kohii
 */
public class BeanUtils {

  public static void copyProperties(Object src, Object dest) {
    Method[] srcMethods = src.getClass().getMethods();

    for (Method method : srcMethods) {
      String methodName = method.getName();
      if (methodName.startsWith("get") && !methodName.equals("getClass")
          && method.getParameterCount() == 0) {
        try {
          Class<?> type = method.getReturnType();
          Method setter = dest.getClass().getMethod("set" + methodName.substring(3), type);
          Object val = method.invoke(src);
          setter.invoke(dest, val);
        } catch (NoSuchMethodException ex) {
          // ignore
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException ex) {
          throw new UnexpectedException(ex);
        }
      }
    }
  }

  public static Method getGetter(Class<?> clazz, String fieldName) {
    try {
      return clazz.getMethod("get" + StringUtils.capitalize(fieldName));
    } catch (NoSuchMethodException | SecurityException e) {
      throw new UnexpectedException(e);
    }
  }

  public static Method getSetter(Class<?> clazz, String fieldName) {
    try {
      return clazz.getMethod("set" + StringUtils.capitalize(fieldName));
    } catch (NoSuchMethodException | SecurityException e) {
      throw new UnexpectedException(e);
    }
  }
}
