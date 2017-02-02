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
package com.smoothcsv.core.command;

import javax.swing.JComponent;

import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;

/**
 * @author kohii
 */
public abstract class VisibleComponentCommandBase<T extends SmoothComponent> extends Command {

  private final CssSelector selector;

  public VisibleComponentCommandBase(String cssSelector) {
    selector = SelectorFactory.parseQuery(cssSelector);
  }

  /*
   * (non-Javadoc) Ï
   * 
   * @see com.smoothcsv.framework.commands.Command#run()
   */
  @SuppressWarnings("unchecked")
  @Override
  public final void run() {
    if (!SmoothComponentManager.isComponentVisible(selector)) {
      abort();
    }
    T comp = (T) SmoothComponentManager.findOne(selector);
    run(comp);
    ((JComponent) comp).requestFocus();
  }

  public abstract void run(T component);
}
