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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.smoothcsv.commons.functions.IntRangeConsumer;

public class ArrayUtils {

  public static String[] EMPTY_STRING_ARRAY = new String[0];

  /**
   * [1,3,4,5,7,8,10] -> [1],[3-5],[7-8],[10]
   *
   * @param consumer
   * @param array
   */
  public static void processIntArrayAsBlock(IntRangeConsumer consumer, int[] array) {
    processIntArrayAsBlock(consumer, array, false);
  }

  /**
   * [1,3,4,5,7,8,10] -> [10],[7-8],[3-5],[1]
   *
   * @param consumer
   * @param array
   * @param reverse
   */
  public static void processIntArrayAsBlock(IntRangeConsumer consumer, int[] array,
                                            boolean reverse) {
    if (array.length == 0) {
      return;
    }
    int tmp = -100;
    int bef = -100;
    int prev = array.length;

    if (!reverse) {
      for (int i = 0; i < prev; i++) {
        int j = array[i];
        if (tmp == -100) {
          tmp = j;
        } else if (bef + 1 != j) {
          consumer.accept(tmp, bef);
          tmp = j;
        }
        bef = j;
      }
      consumer.accept(tmp, bef);

    } else {
      for (int i = prev - 1; 0 <= i; i--) {
        int j = array[i];
        if (tmp == -100) {
          tmp = j;
        } else if (bef - 1 != j) {
          consumer.accept(bef, tmp);
          tmp = j;
        }
        bef = j;
      }
      consumer.accept(bef, tmp);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T, R> R[] map(T[] array, Function<T, R> function) {
    Object[] newArray = new Object[array.length];
    for (int i = 0; i < array.length; i++) {
      newArray[i] = function.apply(array[i]);
    }
    return (R[]) newArray;
  }

  public static <T> boolean contains(T[] array, T obj) {
    for (T t : array) {
      if (obj == null) {
        if (t == null) {
          return true;
        }
      } else {
        if (obj.equals(t)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean containsIgnoreCase(String[] array, String obj) {
    for (String s : array) {
      if (obj == null) {
        if (s == null) {
          return true;
        }
      } else {
        if (obj.equalsIgnoreCase(s)) {
          return true;
        }
      }
    }
    return false;
  }

  public static <T> boolean notContains(T[] array, T obj) {
    for (T t : array) {
      if (obj == null) {
        if (t == null) {
          return false;
        }
      } else {
        if (obj.equals(t)) {
          return false;
        }
      }
    }
    return true;
  }

  public static boolean notContainsIgnoreCase(String[] array, String obj) {
    for (String s : array) {
      if (obj == null) {
        if (s == null) {
          return false;
        }
      } else {
        if (obj.equalsIgnoreCase(s)) {
          return false;
        }
      }
    }
    return true;
  }

  public static boolean contains(char[] array, char c) {
    for (char c2 : array) {
      if (c == c2) {
        return true;
      }
    }
    return false;
  }

  public static boolean contains(int[] array, int i) {
    for (int i2 : array) {
      if (i == i2) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] createAndFill(int size, T data) {
    Object[] array = new Object[size];
    if (data != null) {
      Arrays.fill(array, data);
    }
    return (T[]) array;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] add(final T[] array, final T element) {
    T[] newArray;
    newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length + 1);
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[newArray.length - 1] = element;
    return newArray;
  }

  public static <T> T[] remove(final T[] array, final T element) {
    List<T> list = new ArrayList<>(Arrays.asList(array));
    list.removeAll(Arrays.asList(element));
    return list.toArray(array);
  }

  public static <T> ArrayList<T> toArrayList(T[] array) {
    ArrayList<T> list = new ArrayList<>(array.length);
    list.addAll(Arrays.asList(array));
    return list;
  }

  public static boolean startsWith(byte[] bytes, byte[] prefix) {
    if (prefix.length > bytes.length) {
      return false;
    }
    for (int i = 0; i < prefix.length; i++) {
      if (prefix[i] != bytes[i]) {
        return false;
      }
    }
    return true;
  }
}
