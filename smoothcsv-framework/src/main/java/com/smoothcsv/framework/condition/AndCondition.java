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
import java.util.function.Consumer;

/**
 * @author kohii
 *
 */
public class AndCondition extends Condition {

  private final Condition condition0;

  private final Condition condition1;

  public AndCondition(String name, Condition condition0, Condition condition1) {
    super(name);
    this.condition0 = condition0;
    this.condition1 = condition1;

    Consumer<ConditionValueChangeEvent> revalidateListener =
        new Consumer<Condition.ConditionValueChangeEvent>() {
          @Override
          public void accept(ConditionValueChangeEvent e) {
            setValue(condition0.getValue() && condition1.getValue());
          }
        };

    condition0.addValueChangedListener(revalidateListener);
    condition1.addValueChangedListener(revalidateListener);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof AndCondition) {
      AndCondition anotherCondition = (AndCondition) obj;
      return condition0.equals(anotherCondition.condition0)
          && condition1.equals(anotherCondition.condition1);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 67 * hash + Objects.hashCode(this.condition0);
    hash = 67 * hash + Objects.hashCode(this.condition1);
    return hash;
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
}
