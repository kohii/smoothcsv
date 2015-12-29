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
package com.smoothcsv.framework.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.smoothcsv.commons.exception.UnexpectedException;

/**
 * @author kohii
 *
 */
public class Conditions {

  public static final Condition ALWAYS = new Condition() {
    @Override
    protected void activate() {}

    @Override
    protected boolean computeValue() {
      return true;
    }
  };

  public static final Condition NEVER = new Condition() {
    @Override
    protected void activate() {}

    @Override
    protected boolean computeValue() {
      return false;
    }
  };

  public static Map<String, Condition> conditionMap = new HashMap<>();

  static {
    conditionMap.put("always", ALWAYS);
    conditionMap.put("never", NEVER);
  }

  public static Map<String, Function<String, Condition>> conditionFactories = new HashMap<>();

  private static boolean initialized = false;

  public static final void initializeConditions() {
    if (initialized) {
      return;
    }
    for (Entry<String, Condition> entry : conditionMap.entrySet()) {
      entry.getValue().initialize();
    }
    initialized = true;
  }

  public static void register(String name, Condition condition) {
    conditionMap.put(name, condition);
    maybeInit(condition);
  }

  public static void register(String name, Function<String, Condition> conditionFactory) {
    conditionFactories.put(name, conditionFactory);
  }

  public static Condition getCondition(String name) {
    if (name == null || name.isEmpty()) {
      return ALWAYS;
    }

    Condition condition = conditionMap.get(name);
    if (condition != null) {
      return condition;
    }

    if (name.startsWith("!")) {
      Condition originalCondition = conditionMap.get(name.substring(1));
      if (originalCondition != null) {
        condition = new NotCondition(originalCondition);
        maybeInit(condition);
        conditionMap.put(name, condition);
        return condition;
      }
    }

    String originalName = name;
    boolean isNegative = false;
    if (name.startsWith("!")) {
      originalName = name.substring(1);
      isNegative = true;
    }

    String namePart;
    String argPart;
    int argPartStartsFrom = name.indexOf('(');
    if (argPartStartsFrom > 0 && originalName.endsWith(")")) {
      namePart = originalName.substring(0, argPartStartsFrom);
      argPart = originalName.substring(argPartStartsFrom + 1, originalName.length() - 1);
    } else {
      namePart = originalName;
      argPart = "";
    }

    {
      Function<String, Condition> factory = conditionFactories.get(namePart);
      if (factory != null) {
        condition = factory.apply(argPart);
        if (condition != null) {
          maybeInit(condition);
          conditionMap.put(originalName, condition);
          if (isNegative) {
            condition = new NotCondition(condition);
            maybeInit(condition);
            conditionMap.put(name, condition);
          }
          return condition;
        }
      }
    }

    {
      Function<String, Condition> factory = conditionFactories.get("*");
      if (factory != null) {
        condition = factory.apply(argPart);
        if (condition != null) {
          maybeInit(condition);
          conditionMap.put(originalName, condition);
          if (isNegative) {
            condition = new NotCondition(condition);
            maybeInit(condition);
            conditionMap.put(name, condition);
          }
          return condition;
        }
      }
    }

    throw new UnexpectedException("name=" + name);
  }

  private static void maybeInit(Condition condition) {
    if (initialized) {
      condition.initialize();
    }
  }

  public static Set<String> getConditionNames() {
    return conditionMap.keySet();
  }
}
