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

import lombok.Getter;

/**
 * @author kohii
 */
public class FilterConditions {

  public static final int FILTER_OPERATION_DELETE_UNMATCH = 1;
  public static final int FILTER_OPERATION_DELETE_MATCH = 2;
  public static final int FILTER_OPERATION_NEW_TAB_UNMATCH = 3;
  public static final int FILTER_OPERATION_NEW_TAB_MATCH = 4;

  @Getter
  private FilterConditionGroup condition;

  @Getter
  private int operation;

  /**
   * @param condition
   * @param operation
   */
  public FilterConditions(FilterConditionGroup condition, int operation) {
    this.condition = condition;
    this.operation = operation;
  }
}
