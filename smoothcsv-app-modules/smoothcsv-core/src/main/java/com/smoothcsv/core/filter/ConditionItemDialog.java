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
package com.smoothcsv.core.filter;

import java.awt.BorderLayout;
import java.awt.Dialog;

import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;

/**
 * @author kohii
 */
public class ConditionItemDialog extends DialogBase {

  private static final long serialVersionUID = 7029324868151593734L;
  private ConditionItemPanel conditionItemPanel;

  public ConditionItemDialog(Dialog owner, FilterConditionItem item) {
    super(owner, "Condition");
    getContentPanel().setLayout(new BorderLayout(0, 0));

    conditionItemPanel = new ConditionItemPanel(this);
    getContentPanel().add(conditionItemPanel);
    if (item != null) {
      conditionItemPanel.setFilterConditionItem(item);
    }

    pack();
    setLocationRelativeTo(owner);
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      return conditionItemPanel.validateInput();
    } else {
      return true;
    }
  }

  public FilterConditionItem getItem() {
    return conditionItemPanel.getFilterConditionItem();
  }
}
