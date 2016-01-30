/*
 * Copyright 2015 kohii.
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
 *
 */
public interface IValue {

  String getValue(List<String> list);

  default BigDecimal getNumericValue(List<String> list) {
    String stringVal = getValue(list);
    try {
      return stringVal.isEmpty() ? null : new BigDecimal(stringVal);
    } catch (RuntimeException e) {
      return null;
    }
  }

  Regex getRegexValue(List<String> list, boolean caseSensitive);

  String getText();
}
