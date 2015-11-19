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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCTabbedPane;

/**
 * @author kohii
 *
 */
public interface Conditions {

  public static final Condition ALWAYS = new Condition("always") {
    @Override
    protected void activate() {
      setValue(true);
    }
  };

  public static final Condition NEVER = new Condition("never") {
    @Override
    protected void activate() {
      setValue(false);
    }
  };

  public static final Condition WHEN_VIEW_EXISTS = new Condition("viewExists") {
    @Override
    protected void activate() {
      SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
      tabbedPane.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          setValue(tabbedPane.getSelectedView() != null);
        }
      });
      setValue(tabbedPane.getSelectedView() != null);
    };
  };

  // public static final Condition WHEN_SELECTED_VIEW_CAN_SAVE = new Condition("canSave") {
  //
  // private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
  // @Override
  // public void propertyChange(PropertyChangeEvent evt) {
  // revalidate();
  // }
  // };
  //
  // @Override
  // public boolean test() {
  // BaseTabView<?> tab = SCApplication.getComponentManager().getTabbedPane().getSelectedView();
  // return tab != null && tab.getViewInfo().canSave();
  // }
  //
  // protected void activate() {
  // EventListenerSupport listeners =
  // SCApplication.getComponentManager().getTabbedPane().listeners();
  // listeners.on(
  // SCTabbedPane.ViewChangeEvent.class,
  // e -> {
  // if (e.getOldView() != null) {
  // e.getOldView().getViewInfo().getPropertyChangeSupport()
  // .removePropertyChangeListener("canSave", propertyChangeListener);
  // }
  // e.getOldView().getViewInfo().getPropertyChangeSupport()
  // .addPropertyChangeListener("canSave", propertyChangeListener);
  // });
  // };
  // };
}
