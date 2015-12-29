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

import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;

/**
 * @author kohii
 *
 */
public class AvailableCondition extends Condition {

  private final CssSelector selector;

  public AvailableCondition(String selectorQuery) {
    this.selector = SelectorFactory.parseQuery(selectorQuery);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((selector == null) ? 0 : selector.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AvailableCondition other = (AvailableCondition) obj;
    if (selector == null) {
      if (other.selector != null)
        return false;
    } else if (!selector.equals(other.selector))
      return false;
    return true;
  }

  @Override
  protected void activate() {
    SmoothComponentManager.addVisibleComponentChangeListener(e -> revalidate());
  }

  @Override
  protected boolean computeValue() {
    return SmoothComponentManager.isComponentVisible(selector);
  }
}
