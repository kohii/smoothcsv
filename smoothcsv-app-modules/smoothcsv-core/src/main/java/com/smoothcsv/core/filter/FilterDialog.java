/*
 * Copyright 2015 kohii.
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

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.util.SCBundle;

/**
 * @author kohii
 *
 */
public class FilterDialog extends DialogBase {

  private static final long serialVersionUID = -2551065574232435723L;

  private final FilterOperationPanel operationPanel;
  private final FilterConditionPanel conditionPanel;

  /**
   * @param owner
   */
  public FilterDialog() {
    super(SCApplication.components().getFrame(), SCBundle.get("key.filter"));

    getContentPanel().setLayout(new BorderLayout(0, 0));

    operationPanel = new FilterOperationPanel();
    getContentPanel().add(operationPanel, BorderLayout.SOUTH);

    conditionPanel = new FilterConditionPanel();
    getContentPanel().add(conditionPanel);
    setAutoPack(true);
  }

  public FilterConditions getFilterConditions() {
    return new FilterConditions(conditionPanel.getConditions(),
        operationPanel.getSeletedOperation());
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      if (!conditionPanel.getConditions().hasChildren()) {
        MessageDialogs.alert("WSCA0008", SCBundle.get("key.filter.cond"));
        return false;
      }
    }
    return super.processOperation(selectedOperation);
  }
}
