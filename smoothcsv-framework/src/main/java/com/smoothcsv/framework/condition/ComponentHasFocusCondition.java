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

import java.awt.Component;
import java.util.Objects;
import java.util.function.Consumer;

import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.component.support.SCFocusManager.FocusOwnerChangeEvent;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;

/**
 *
 * @author kohii
 */
public class ComponentHasFocusCondition extends Condition {

  private final CssSelector selector;

  public ComponentHasFocusCondition(String name, String selectorQuery) {
    super(name);
    this.selector = SelectorFactory.parseQuery(selectorQuery);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ComponentHasFocusCondition other = (ComponentHasFocusCondition) obj;
    if (selector == null) {
      if (other.selector != null)
        return false;
    } else if (!selector.equals(other.selector))
      return false;
    return true;
  }


  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.selector);
    return hash;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.framework.condition.Condition#attachToEvent()
   */
  @Override
  protected void activate() {
    SCFocusManager.addListener(new Consumer<SCFocusManager.FocusOwnerChangeEvent>() {
      @Override
      public void accept(FocusOwnerChangeEvent evt) {
        Component fo = evt.getNewFocusOwner();
        setValue(fo != null && fo instanceof SmoothComponent
            && selector.matches((SmoothComponent) fo));
      }
    });

    Component fo = SCFocusManager.getFocusOwner();
    setValue(fo != null && fo instanceof SmoothComponent && selector.matches((SmoothComponent) fo));
  }
}
