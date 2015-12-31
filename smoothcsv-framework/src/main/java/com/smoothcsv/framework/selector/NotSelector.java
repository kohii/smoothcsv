/*
 * Copyright 2015 kohii
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
package com.smoothcsv.framework.selector;

import lombok.EqualsAndHashCode;

import com.smoothcsv.framework.component.support.SmoothComponent;

/**
 * @author kohii
 *
 */
@EqualsAndHashCode
public class NotSelector implements CssSelector {

  private final CssSelector selector;

  /**
   * @param selector
   */
  public NotSelector(CssSelector selector) {
    this.selector = selector;
  }

  @Override
  public boolean matches(SmoothComponent component) {
    return !selector.matches(component);
  }

  @Override
  public String toString() {
    return ":not(" + selector.toString() + ")";
  }
}
