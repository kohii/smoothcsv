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
package com.smoothcsv.framework.component.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.framework.selector.CssSelector;

import lombok.Getter;

/**
 * @author kohii
 *
 */
public class SmoothComponentManager {

  private static List<SmoothComponent> visibleComponents = new ArrayList<>();

  private static List<Consumer<List<SmoothComponent>>> visibleComponentChangeListeners =
      new ArrayList<>();

  @Getter
  private static boolean adjusting = false;

  static void addVisibleComponent(SmoothComponent component) {
    if (visibleComponents.contains(component)) {
      return;
    }
    visibleComponents.add(component);
    fireVisibleComponentChange();
  }

  static void removeVisibleComponent(SmoothComponent component) {
    boolean removed = visibleComponents.remove(component);
    if (removed) {
      fireVisibleComponentChange();
    }
  }

  static void fireVisibleComponentChange() {
    if (!adjusting) {
      for (Consumer<List<SmoothComponent>> runnable : visibleComponentChangeListeners) {
        runnable.accept(visibleComponents);
      }
    }
  }

  public static void startAdjustingComponents() {
    if (adjusting) {
      throw new IllegalStateException();
    }
    adjusting = true;
  }

  public static void stopAdjustingComponents() {
    if (!adjusting) {
      throw new IllegalStateException();
    }
    adjusting = false;
    fireVisibleComponentChange();
  }

  public static void addVisibleComponentChangeListener(Consumer<List<SmoothComponent>> l) {
    visibleComponentChangeListeners.add(l);
  }

  public static boolean isComponentVisible(CssSelector selector) {
    for (SmoothComponent component : visibleComponents) {
      if (selector.matches(component)) {
        return true;
      }
    }
    return false;
  }

  public static SmoothComponent findOne(CssSelector selector) {
    for (SmoothComponent component : visibleComponents) {
      if (selector.matches(component)) {
        return component;
      }
    }
    return null;
  }

  public static List<SmoothComponent> find(CssSelector selector) {
    List<SmoothComponent> result = new ArrayList<>();
    for (SmoothComponent component : visibleComponents) {
      if (selector.matches(component)) {
        result.add(component);
      }
    }
    return result;
  }
}
