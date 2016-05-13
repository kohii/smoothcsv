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
package com.smoothcsv.framework.component.support;

import java.awt.event.MouseEvent;


/**
 * @author kohii
 */
public interface SmoothComponent {

  SmoothComponentSupport getComponentSupport();

  boolean isFocusOwner();

  default String getComponentType() {
    return getComponentSupport().getComponentType();
  }

  default String[] getStyleClasses() {
    return getComponentSupport().getStyleClasses();
  }

  default void setStyleClasses(String[] styleClasses) {
    getComponentSupport().setStyleClasses(styleClasses);
  }

  default void addStyleClass(String styleClass) {
    getComponentSupport().addStyleClass(styleClass);
  }

  default void removeStyleClass(String styleClass) {
    getComponentSupport().removeStyleClass(styleClass);
  }

  default String[] getPseudoClasses() {
    return getComponentSupport().getPseudoClasses();
  }

  default void addPseudoClass(String pseudoClass) {
    getComponentSupport().addPseudoClass(pseudoClass);
  }

  default void removePseudoClass(String pseudoClass) {
    getComponentSupport().removePseudoClass(pseudoClass);
  }

  default boolean beforeShowPopupMenu(MouseEvent e) {
    return true;
  }
}
