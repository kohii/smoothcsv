package com.smoothcsv.commons.utils;

import java.lang.reflect.Field;

/**
 * @author kohii
 */
public class ReflectionUtils {

  public static Object get(Object obj, String propName) {
    Class<?> clazz = obj.getClass();
    try {
      Field field = clazz.getDeclaredField(propName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Object obj, String propName, Class<T> clazz) {
    return (T) get(obj, propName);
  }
}
