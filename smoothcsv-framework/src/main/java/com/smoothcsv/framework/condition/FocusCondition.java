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

import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;

/**
 *
 * @author kohii
 */
public class FocusCondition extends Condition {

  private final CssSelector selector;

  public FocusCondition(String selectorQuery) {
    this.selector = SelectorFactory.parseQuery(selectorQuery);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FocusCondition) {
      FocusCondition con = (FocusCondition) obj;
      return selector.equals(con.selector);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(selector);
    return hash;
  }

  @Override
  protected void activate() {
    SCFocusManager.addListener(e -> revalidate());
  }

  @Override
  protected boolean computeValue() {
    Component focusOwner = SCFocusManager.getFocusOwner();
    if (focusOwner instanceof SmoothComponent) {
      SmoothComponent component = (SmoothComponent) focusOwner;
      return selector.matches(component);
    } else {
      return false;
    }
  }
}
