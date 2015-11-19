/*
 * Copyright 2014 kohii.
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
package com.smoothcsv.framework.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kohii
 *
 */
public class ConditionPool {

  private static ConditionPool instance = new ConditionPool();


  /**
   * @return the instance
   */
  public static ConditionPool instance() {
    return instance;
  }

  private boolean initialized = false;

  private ConditionPool() {}

  private final List<Condition> conditions = new ArrayList<>();

  final void register(Condition condition) {
    if (initialized) {
      condition.initialize();
    }
    conditions.add(condition);
  }

  public final void initializeConditions() {
    if (initialized) {
      return;
    }
    for (Condition con : conditions) {
      con.initialize();
    }
    initialized = true;
  }

  public List<Condition> getAll() {
    return Collections.unmodifiableList(conditions);
  }
}
