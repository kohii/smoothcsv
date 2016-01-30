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
package com.smoothcsv.core.filter;

import java.awt.BorderLayout;
import java.awt.Dialog;

import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;

/**
 * @author kohii
 */
public class ConditionDialog extends DialogBase {

  private static final long serialVersionUID = 7029324868151593734L;
  private ConditionPanel conditionPanel;

  public ConditionDialog(Dialog owner, FilterConditionItem item) {
    super(owner, "Condition");
    getContentPanel().setLayout(new BorderLayout(0, 0));

    conditionPanel = new ConditionPanel(this);
    getContentPanel().add(conditionPanel);
    if (item != null) {
      conditionPanel.setFilterConditionItem(item);
    }

    pack();
    setLocationRelativeTo(owner);
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    return conditionPanel.validateInput();
  }

  public FilterConditionItem getItem() {
    return conditionPanel.getFilterConditionItem();
  }
}
