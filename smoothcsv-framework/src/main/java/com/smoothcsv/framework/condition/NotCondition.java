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

import java.util.Objects;

import com.smoothcsv.commons.utils.StringUtils;

/**
 * @author kohii
 *
 */
public class NotCondition extends Condition {

  private final Condition condition;

  public NotCondition(Condition condition, String name) {
    super(name);
    this.condition = condition;

    condition.addValueChangedListener(e -> setValue(!condition.getValue()));
  }

  public NotCondition(Condition condition) {
    this(condition, "not" + StringUtils.capitalize(condition.getName()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.framework.condition.Condition#activate()
   */
  @Override
  protected void activate() {
    // do nothing
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof NotCondition) {
      NotCondition anotherCondition = (NotCondition) obj;
      return condition == anotherCondition.condition;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 128 + Objects.hashCode(condition);
    return hash;
  }
}
