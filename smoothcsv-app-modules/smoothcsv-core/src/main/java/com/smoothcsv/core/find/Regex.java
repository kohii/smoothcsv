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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import lombok.Getter;

/**
 * @author kohii
 *
 */
public class Regex {

  @Getter
  private final Pattern pattern;
  @Getter
  private final String error;

  public Regex(String text, boolean caseSensitive) {
    Pattern pattern = null;
    String error = null;
    try {
      pattern = Pattern.compile(text, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
    } catch (PatternSyntaxException e) {
      error = e.getLocalizedMessage();
    }
    this.pattern = pattern;
    this.error = error;
  }

  public boolean isValid() {
    return error == null;
  }

}
