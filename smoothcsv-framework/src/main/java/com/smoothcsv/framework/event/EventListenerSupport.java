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
package com.smoothcsv.framework.event;

import java.util.function.Supplier;

public interface EventListenerSupport {

  <E extends SCEvent> void on(Class<E> eventClass, SCListener<E> listener);

  <T extends SCListener<?>> T off(T listener);

  <E extends SCEvent> void off(Class<E> eventClass);

  void invokeListeners(SCEvent event);

  <E extends SCEvent> void invokeListeners(Class<E> eventClass, Supplier<E> eventFactory);

}
