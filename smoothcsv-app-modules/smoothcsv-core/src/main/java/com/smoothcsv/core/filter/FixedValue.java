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
package com.smoothcsv.core.filter;

import java.math.BigDecimal;
import java.util.List;

import com.smoothcsv.core.find.Regex;

/**
 * @author kohii
 */
public class FixedValue implements IValue {

  private final String text;

  private BigDecimal numericVal;
  private Regex regexVal;

  public FixedValue(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return '"' + text + '"';
  }

  @Override
  public String getValue(List<String> list) {
    return text;
  }

  @Override
  public BigDecimal getNumericValue(List<String> list) {
    if (numericVal == null) {
      numericVal = IValue.super.getNumericValue(list);
    }
    return numericVal;
  }

  @Override
  public Regex getRegexValue(List<String> list, boolean caseSensitive) {
    if (regexVal == null) {
      String stringVal = getValue(list);
      regexVal = new Regex(stringVal, caseSensitive);
    }
    return regexVal;
  }

  @Override
  public String getText() {
    return text;
  }
}
