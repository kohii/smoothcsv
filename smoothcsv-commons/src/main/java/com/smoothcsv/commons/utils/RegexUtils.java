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

/**
 * @author kohii
 */
public class RegexUtils {

  private static final char[] REGEX_ESCAPE_CHARS = new char[]{'\\', '*', '+', '.', '?', '{', '}',
      '(', ')', '[', ']', '^', '$', '-', '|'};

  public static String escapeRegex(String text) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (ArrayUtils.contains(REGEX_ESCAPE_CHARS, c)) {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }
}
