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
import java.util.regex.Pattern;

/**
 * @author kohii
 */
public class NamePatternUtils {

  private static final char[] REGEX_ESCAPE_CHARS = new char[]{'\\', '*', '+', '.', '?', '{', '}',
      '(', ')', '[', ']', '^', '$', '-', '|'};

  public static Pattern getPattern(String patternString) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < patternString.length(); i++) {
      char c = patternString.charAt(i);
      if (c == '?') {
        sb.append(".");
      } else if (c == '*') {
        sb.append(".*");
      } else {
        if (ArrayUtils.contains(REGEX_ESCAPE_CHARS, c)) {
          sb.append('\\');
        }
        sb.append(c);
      }
    }
    return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
  }

  public static Pattern[] gePatterns(String patternStrings) {
    List<Pattern> ret = new ArrayList<>();
    for (String patternString : patternStrings.split(",")) {
      String trimedPattern = patternString.trim();
      if (trimedPattern.length() > 0) {
        ret.add(getPattern(trimedPattern));
      }
    }
    return ret.toArray(new Pattern[ret.size()]);
  }
}
