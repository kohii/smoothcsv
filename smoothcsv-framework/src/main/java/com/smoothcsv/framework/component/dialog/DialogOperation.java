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
package com.smoothcsv.framework.component.dialog;

import com.smoothcsv.framework.util.SCBundle;

/**
 *
 * @author kohii
 */
public class DialogOperation {

  public static final DialogOperation YES = new DialogOperation(SCBundle.get("key.yes"));
  public static final DialogOperation NO = new DialogOperation(SCBundle.get("key.no"));
  public static final DialogOperation OK = new DialogOperation(SCBundle.get("key.ok"));
  public static final DialogOperation CANCEL = new DialogOperation(SCBundle.get("key.cancel"));

  private String text;

  public DialogOperation(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
