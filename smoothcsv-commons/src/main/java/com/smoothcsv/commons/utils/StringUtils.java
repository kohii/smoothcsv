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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kohii
 */
public class StringUtils {

  private static final Pattern LEADING_AND_TRAILING_BLANKS = Pattern.compile("^[ 　\t]+|[ 　\t]+$");

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  public static boolean isEmpty(String s) {
    return s == null || s.length() == 0;
  }

  public static boolean isNotEmpty(String s) {
    return s != null && s.length() > 0;
  }

  public static boolean isNumber(final String s) {
    if (s == null || s.length() == 0) {
      return false;
    }

    int size = s.length();
    for (int i = 0; i < size; i++) {
      char chr = s.charAt(i);
      if (chr < '0' || '9' < chr) {
        return false;
      }
    }

    return true;
  }

  public static boolean isInteger(final String s) {
    if (s == null || s.length() == 0) {
      return false;
    }

    int size = s.length();
    for (int i = 0; i < size; i++) {
      char chr = s.charAt(i);
      if (chr < '0' || '9' < chr) {
        if (i != 0 || chr != '-') {
          return false;
        }
      }
    }

    return true;
  }

  public static boolean isDecimal(final String s) {
    if (s == null || s.length() == 0) {
      return false;
    }

    boolean hasDot = false;

    int size = s.length();
    for (int i = 0; i < size; i++) {
      char chr = s.charAt(i);
      if ((chr < '0' || '9' < chr) && chr != '.') {
        if (!(i == 0 && chr == '-')) {
          return false;
        }
      }
      if (chr == '.') {
        if (hasDot) {
          return false;
        }
        hasDot = true;
      }
    }

    return true;
  }

  public static String trimBlank(final String text) {
    if (text == null) {
      return null;
    }
    Matcher matcher = LEADING_AND_TRAILING_BLANKS.matcher(text);
    return matcher.replaceAll("");
  }

  public static String convertLineSeparater(String text, String lineSeparator) {
    if (text == null) {
      return null;
    }
    int len = text.length();
    StringBuilder sb = null;
    boolean cr = false;
    for (int i = 0; i < len; i++) {
      char c = text.charAt(i);
      if (cr) {
        if (c == '\n') {
          continue;
        }
        cr = false;
      }
      if (c == '\r' || c == '\n') {
        if (sb == null) {
          sb = new StringBuilder();
          for (int j = 0; j < i; j++) {
            sb.append(text.charAt(j));
          }
        }
        sb.append(lineSeparator);
        if (c == '\r') {
          cr = true;
        }
      } else {
        if (sb != null) {
          sb.append(c);
        }
      }
    }
    return sb == null ? text : sb.toString();
  }

  public static List<String> tokenizeByLineSeparator(String text) {
    if (text == null) {
      return null;
    }
    StringLineTokenizer tokenizer = new StringLineTokenizer(text, true);
    String line;
    List<String> ret = new ArrayList<>();
    while ((line = tokenizer.nextToken()) != null) {
      ret.add(line);
    }
    return ret;
  }

  public static boolean containsLineSeparator(String str) {
    if (isEmpty(str)) {
      return false;
    }
    for (int i = 0, len = str.length(); i < len; i++) {
      char c = str.charAt(i);
      if (c == '\r' || c == '\n') {
        return true;
      }
    }
    return false;
  }

  public static String[] split(String s, char... delims) {
    if (isEmpty(s)) {
      return EMPTY_STRING_ARRAY;
    }
    List<String> list = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (ArrayUtils.contains(delims, c)) {
        list.add(sb.length() == 0 ? "" : sb.toString());
        sb.setLength(0);
      } else {
        sb.append(c);
      }
    }
    list.add(sb.length() == 0 ? "" : sb.toString());
    return (String[]) list.toArray(new String[list.size()]);
  }

  public static String[] split(String s, char delim) {
    if (isEmpty(s)) {
      return EMPTY_STRING_ARRAY;
    }
    List<String> list = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == delim) {
        list.add(sb.length() == 0 ? "" : sb.toString());
        sb.setLength(0);
      } else {
        sb.append(c);
      }
    }
    list.add(sb.length() == 0 ? "" : sb.toString());
    return (String[]) list.toArray(new String[list.size()]);
  }

  public static String decapitalize(final String name) {
    if (isEmpty(name)) {
      return name;
    }
    char chars[] = name.toCharArray();
    if (chars.length >= 2 && Character.isUpperCase(chars[0]) && Character.isUpperCase(chars[1])) {
      return name;
    }
    chars[0] = Character.toLowerCase(chars[0]);
    return new String(chars);
  }

  public static String capitalize(final String name) {
    if (isEmpty(name)) {
      return name;
    }
    char chars[] = name.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static String camelize(String s, char c) {
    if (s == null) {
      return null;
    }
    s = s.toLowerCase();
    String[] array = split(s, c);
    if (array.length == 1) {
      return capitalize(s);
    }
    StringBuilder buf = new StringBuilder(40);
    for (int i = 0; i < array.length; ++i) {
      buf.append(capitalize(array[i]));
    }
    return buf.toString();
  }

  public static String camelize(String s) {
    return camelize(s, '_');
  }

  public static String decamelize(final String s) {
    return decamelize(s, '_');
  }

  public static String decamelize(final String s, char separator) {
    if (s == null) {
      return null;
    }
    if (s.length() == 1) {
      return s.toLowerCase();
    }
    StringBuilder buf = new StringBuilder(40);
    int pos = 0;
    for (int i = 1; i < s.length(); ++i) {
      if (Character.isUpperCase(s.charAt(i))) {
        if (buf.length() != 0) {
          buf.append(separator);
        }
        buf.append(s.substring(pos, i).toLowerCase());
        pos = i;
      }
    }
    if (buf.length() != 0) {
      buf.append(separator);
    }
    buf.append(s.substring(pos, s.length()).toLowerCase());
    return buf.toString();
  }

  /**
   * Returns the number of lines in the text.
   *
   * @param s text
   * @return the number of lines
   */
  public static int calcLineCount(String s) {
    if (StringUtils.isEmpty(s)) {
      return 1;
    }
    char[] chars = s.toCharArray();
    int ret = 1;
    boolean isPrevCr = false;
    for (int i = 0, ln = chars.length; i < ln; i++) {
      char c = chars[i];
      if (c == '\r') {
        isPrevCr = true;
        ret++;
      } else {
        if (c == '\n' && !isPrevCr) {
          ret++;
        }
        isPrevCr = false;
      }
    }
    return ret;
  }

  /**
   * Returns text length. <br>
   * Treat half width char as 1 and full width char as 2.
   *
   * @param text text
   * @return length
   */
  public static int textWidth(String text) {
    int ret = 0;
    for (int i = 0, ln = text.length(); i < ln; i++) {
      char c = text.charAt(i);
      if ((c <= '\u007e') || // half alpha numeric
          (c == '\u00a5') || // \
          (c == '\u203e') || // ~
          (c >= '\uff61' && c <= '\uff9f') // half width kana
      ) {
        ret += 1;
      } else {
        ret += 2;
      }
    }
    return ret;
  }

  public static String omitLines(String s, int maxLines) {
    int lineCount = 1;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '\n') {
        lineCount++;
        if (lineCount > maxLines) {
          return s.substring(0, i + 1) + "...";
        }
      }
    }
    return s;
  }
}
