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
package com.smoothcsv.framework.event;

import java.util.function.Consumer;

import com.smoothcsv.commons.collections.ArrayMap;
import com.smoothcsv.commons.data.KeyValue;

/**
 *
 * @author kohii
 */
public class NamedEventListenerSupportImpl implements NamedEventListenerSupport {

  private final ArrayMap<String, Consumer<String>> listeners = new ArrayMap<>();

  @Override
  public void on(String eventName, Consumer<String> listener) {
    if (!listeners.containsValue(listener)) {
      listeners.put(eventName, listener);
    }
  }

  @Override
  public Consumer<String> off(Consumer<String> listener) {
    int listenersSize = listeners.size();
    for (int i = 0; i < listenersSize; i++) {
      KeyValue<String, Consumer<String>> entry = listeners.get(i);
      if (entry.getValue() == listener) {
        listeners.remove(i);
        return listener;
      }
    }
    return null;
  }

  @Override
  public Consumer<String> off(String eventName) {
    Consumer<String> ret = null;
    int listenersSize = listeners.size();
    for (int i = listenersSize - 1; i >= 0; i--) {
      KeyValue<String, Consumer<String>> entry = listeners.get(i);
      if (entry.getKey().equals(eventName)) {
        listeners.remove(i);
        ret = entry.getValue();
      }
    }
    return ret;
  }

  @Override
  public void invokeListeners(String eventName) {
    int listenersSize = listeners.size();
    for (int i = 0; i < listenersSize; i++) {
      KeyValue<String, Consumer<String>> entry = listeners.get(i);
      String key = entry.getKey();
      if (key.equals(eventName) || key.equals("*")) {
        entry.getValue().accept(eventName);
      }
    }
  }
}
