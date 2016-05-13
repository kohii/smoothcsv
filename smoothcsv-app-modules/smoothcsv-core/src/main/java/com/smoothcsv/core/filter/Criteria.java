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

import com.smoothcsv.commons.utils.EnumStringSupport;

/**
 * @author kohii
 */
public enum Criteria {

  // @formatter:off
  EQUALS,
  DOES_NOT_EQUAL,
  MATCHES_THE_REGEX_OF,
  STARTS_WITH,
  DOES_NOT_START_WITH,
  ENDS_WITH,
  DOES_NOT_END_WITH,
  CONTAINS,
  DOES_NOT_CONTAIN,
  IS_A_NUMBER_GREATER_THAN,
  IS_A_NUMBER_LESS_THAN,
  IS_A_NUMBER_EQUAL_TO_OR_GREATER_THAN,
  IS_A_NUMBER_EQUAL_TO_OR_LESS_THAN,
  IS_IN,
  IS_NOT_IN,
  IS_EMPTY,
  IS_NOT_EMPTY,
  IS_A_NUMERIC,
  IS_NOT_A_NUMERIC,
  IS_A_STRING_GREATER_THAN,
  IS_A_STRING_LESS_THAN,
  EXISTS,
  DOES_NOT_EXISTS;
  // @formatter:on

  @Override
  public String toString() {
    return EnumStringSupport.getString(this);
  }
}
