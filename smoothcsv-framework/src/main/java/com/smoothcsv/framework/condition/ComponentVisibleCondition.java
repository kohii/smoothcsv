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

import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;

/**
 * @author kohii
 *
 */
public class ComponentVisibleCondition extends Condition {

  private final CssSelector selector;

  public ComponentVisibleCondition(String name, String selectorQuery) {
    super(name);
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
    ComponentVisibleCondition other = (ComponentVisibleCondition) obj;
    if (selector == null) {
      if (other.selector != null)
        return false;
    } else if (!selector.equals(other.selector))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.framework.condition.Condition#attachToEvent()
   */
  @Override
  protected void activate() {
    SmoothComponentManager.addVisibleComponentChangeListener(new Consumer<List<SmoothComponent>>() {
      @Override
      public void accept(List<SmoothComponent> visibleComps) {
        setValue(SmoothComponentManager.isComponentVisible(selector));
      }
    });

    setValue(SmoothComponentManager.isComponentVisible(selector));
  }
}
