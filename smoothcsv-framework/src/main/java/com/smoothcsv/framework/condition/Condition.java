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
package com.smoothcsv.framework.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.framework.event.SCEvent;

/**
 * @author kohii
 */
public abstract class Condition {

  public static class ConditionValueChangeEvent implements SCEvent {
    public boolean newValue;
  }

  private boolean notifyEnabled = false;

  private boolean value = false;

  private final List<Consumer<ConditionValueChangeEvent>> listeners = new ArrayList<>();

  public final void addValueChangedListener(Consumer<ConditionValueChangeEvent> l) {
    listeners.add(l);
  }

  public final boolean removeValueChangedListener(Consumer<ConditionValueChangeEvent> l) {
    return listeners.remove(l);
  }

  protected void initialize() {
    activate();
    notifyEnabled = true;
    revalidate();
  }

  protected abstract void activate();

  protected final void notifyListeners() {
    if (!notifyEnabled) {
      return;
    }
    ConditionValueChangeEvent e = new ConditionValueChangeEvent();
    e.newValue = value;
    for (Consumer<ConditionValueChangeEvent> l : listeners) {
      l.accept(e);
    }
  }

  protected abstract boolean computeValue();

  protected final void revalidate() {
    setValue(computeValue());
  }

  public final boolean getValue() {
    return value;
  }

  protected final void setValue(boolean value) {
    if (this.value != value) {
      this.value = value;
      notifyListeners();
    }
  }
}
