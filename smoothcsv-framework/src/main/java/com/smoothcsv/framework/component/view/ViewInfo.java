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
package com.smoothcsv.framework.component.view;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author kohii
 */
public abstract class ViewInfo {

  protected final PropertyChangeSupport propertyChangeSupport;

  private String shortTitle;
  private String fullTitle;

  public ViewInfo() {
    this.propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public PropertyChangeSupport getPropertyChangeSupport() {
    return propertyChangeSupport;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public void setShortTitle(String shortTitle) {
    String old = this.shortTitle;
    this.shortTitle = shortTitle;
    propertyChangeSupport.firePropertyChange("shortTitle", old, shortTitle);
  }

  public String getFullTitle() {
    return fullTitle;
  }

  public void setFullTitle(String fullTitle) {
    String old = this.fullTitle;
    this.fullTitle = fullTitle;
    propertyChangeSupport.firePropertyChange("fullTitle", old, fullTitle);
  }
}
