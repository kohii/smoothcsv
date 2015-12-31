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

import java.util.function.Supplier;

import com.smoothcsv.commons.collections.ArrayMap;
import com.smoothcsv.commons.data.KeyValue;

/**
 *
 * @author kohii
 */
public class EventListenerSupportImpl implements EventListenerSupport {

  private final ArrayMap<Class<? extends SCEvent>, SCListener> listeners = new ArrayMap<>();

  @Override
  public <E extends SCEvent> void on(Class<E> eventClass, SCListener<E> listener) {
    if (!listeners.containsValue(listener)) {
      listeners.put(eventClass, listener);
    }
  }

  @Override
  public <T extends SCListener<?>> T off(T listener) {
    int listenersSize = listeners.size();
    for (int i = 0; i < listenersSize; i++) {
      KeyValue<Class<? extends SCEvent>, SCListener> entry = listeners.get(i);
      if (entry.getValue() == listener) {
        listeners.remove(i);
        return listener;
      }
    }
    return null;
  }

  @Override
  public <E extends SCEvent> void off(Class<E> eventClass) {
    listeners.remove(eventClass);
  }

  @Override
  public void invokeListeners(SCEvent event) {
    Class<? extends SCEvent> eventClass = event.getClass();
    int listenersSize = listeners.size();
    for (int i = 0; i < listenersSize; i++) {
      KeyValue<Class<? extends SCEvent>, SCListener> entry = listeners.get(i);
      if (entry.getKey() == eventClass) {
        entry.getValue().call(event);
      }
    }
  }

  @Override
  public <E extends SCEvent> void invokeListeners(Class<E> eventClass, Supplier<E> eventFactory) {
    E event = null;
    int listenersSize = listeners.size();
    for (int i = 0; i < listenersSize; i++) {
      KeyValue<Class<? extends SCEvent>, SCListener> entry = listeners.get(i);
      if (entry.getKey() == eventClass) {
        if (event == null) {
          event = eventFactory.get();
        }
        entry.getValue().call(event);
      }
    }
  }
}
