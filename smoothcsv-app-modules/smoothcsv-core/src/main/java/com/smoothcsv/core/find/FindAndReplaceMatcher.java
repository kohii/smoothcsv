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
package com.smoothcsv.core.find;

import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
public class FindAndReplaceMatcher {

  public static boolean matches(String value, FindAndReplaceParams params) {
    if (params.isUseRegex()) {
      Regex regex = params.getRegex();
      if (!regex.isValid()) {
        return false;
      }
      if (params.isMatchWholeCell()) {
        return regex.getPattern().matcher(value).matches();
      } else if (!StringUtils.isEmpty(params.getFindWhat())) {
        return regex.getPattern().matcher(value).find();
      }
    } else {
      String keyword = params.getFindWhat();
      if (params.isMatchWholeCell()) {
        return params.isCaseSensitive() ? keyword.equals(value) : keyword.equalsIgnoreCase(value);
      } else if (!StringUtils.isEmpty(params.getFindWhat())) {
        return params.isCaseSensitive() ? value.contains(keyword) : StringUtils.containsIgnoreCase(
            value, keyword);
      }
    }
    return false;
  }

  public static String replace(String value, FindAndReplaceParams params) {

    if (params.isUseRegex()) {
      String replaceWith =
          params.getReplaceWith().replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
      Matcher m = params.getRegex().getPattern().matcher(value);
      if (params.isMatchWholeCell()) {
        if (m.matches()) {
          if (params.isPreserveCase()) {
            return replacePreservingCase(value, m, replaceWith);
          } else {
            return replaceWith;
          }
        }
        return null;
      } else if (!StringUtils.isEmpty(params.getFindWhat())) {
        if (params.isPreserveCase()) {
          return replacePreservingCase(value, m, replaceWith);
        } else {
          return params.getRegex().getPattern().matcher(value).replaceAll(replaceWith);
        }
      }
    } else {
      if (params.isMatchWholeCell()) {
        if (params.isCaseSensitive() ? params.getFindWhat().equals(value) : params.getFindWhat()
            .equalsIgnoreCase(value)) {
          if (params.isPreserveCase()) {
            return copyCase(value, params.getReplaceWith());
          } else {
            return params.getReplaceWith();
          }
        }
        return null;
      } else if (!StringUtils.isEmpty(params.getFindWhat())) {
        if (params.isPreserveCase()) {
          return replacePreservingCase(value, params.getFindWhat(), params.getReplaceWith(),
              params.isCaseSensitive());
        } else {
          return StringUtils.replace(value, params.getFindWhat(), params.getReplaceWith());
        }
      }
    }
    return null;
  }

  private static String replacePreservingCase(String value, Matcher m, String replaceWith) {
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, m.group().toUpperCase());
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private static String replacePreservingCase(String value, String findWhat, String replaceWith,
      boolean caseSensitive) {
    int valueLen = value.length();
    char[] chars = replaceWith.toCharArray();
    for (int i = 0; i < chars.length && i < valueLen; i++) {
      char origin = value.charAt(i);
      if (Character.isUpperCase(origin)) {
        chars[i] = Character.toUpperCase(chars[i]);
      } else if (Character.isLowerCase(origin)) {
        chars[i] = Character.toLowerCase(chars[i]);
      }
    }
    replaceWith = new String(chars);
    if (caseSensitive) {
      return value.replace(findWhat, replaceWith);
    } else {
      StringBuilder sb = new StringBuilder(value);
      int idx = 0;
      while ((idx = StringUtils.indexOfIgnoreCase(sb, findWhat, idx)) >= 0) {
        sb.replace(idx, idx + findWhat.length(), replaceWith);
      }
      return sb.toString();
    }
  }

  private static String copyCase(String from, String to) {
    int fromLen = from.length();
    char[] chars = to.toCharArray();
    for (int i = 0; i < chars.length && i < fromLen; i++) {
      char origin = from.charAt(i);
      if (Character.isUpperCase(origin)) {
        chars[i] = Character.toUpperCase(chars[i]);
      } else if (Character.isLowerCase(origin)) {
        chars[i] = Character.toLowerCase(chars[i]);
      }
    }
    return new String(chars);
  }
}
