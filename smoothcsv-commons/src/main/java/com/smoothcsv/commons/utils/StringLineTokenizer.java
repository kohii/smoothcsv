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
public class StringLineTokenizer {

  private String str;
  private int length = 0;
  private int index = 0;
  private boolean skipLF = false;
  private boolean end = false;
  private boolean ignoreLastLF;

  public StringLineTokenizer(String str) {
    this(str, false);
  }

  public StringLineTokenizer(String str, boolean ignoreLastLineSeparator) {
    this.str = str;
    this.length = str.length();
    this.ignoreLastLF = ignoreLastLineSeparator;
  }

  public String nextToken() {

    int start = index;

    if (length == 0) {
      if (end) {
        return null;
      }
      end = true;
      return "";
    }
    if (length <= index) {
      if (end) {
        return null;
      }
      if (ignoreLastLF) {
        return null;
      }
      end = true;
      if (str.charAt(length - 1) == '\n') {
        if (skipLF) {
          return null;
        }
        return "";
      } else if (str.charAt(length - 1) == '\r') {
        return "";
      }
      return null;
    }

    if (skipLF) {
      if (str.charAt(index) == '\n') {
        start++;
        index++;
      }
      if (index == length) {
        if (ignoreLastLF) {
          return null;
        }
        end = true;
      }
      skipLF = false;
    }

    for (; index < length; index++) {
      char c = str.charAt(index);
      if (c == '\r') {
        skipLF = true;
        break;
      } else if (c == '\n') {
        break;
      }
    }

    index++;

    return str.substring(start, start == index ? index : index - 1);
  }
}
