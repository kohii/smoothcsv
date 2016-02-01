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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.ExButtonGroup;
import com.smoothcsv.swing.components.ExRadioButton;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class FilterOperationPanel extends JPanel {
  private ExButtonGroup<Integer> buttonGroup;

  @SuppressWarnings("unchecked")
  public FilterOperationPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    setBorder(BorderFactory.createTitledBorder(""));

    ExRadioButton<Integer> deleteUnmatchRadio =
        new ExRadioButton<>(FilterConditions.FILTER_OPERATION_DELETE_UNMATCH,
            SCBundle.get("key.filterOpe.deleteUnmatch"));
    add(deleteUnmatchRadio);

    ExRadioButton<Integer> deleteMatchRadio = new ExRadioButton<>(
        FilterConditions.FILTER_OPERATION_DELETE_MATCH, SCBundle.get("key.filterOpe.deleteMatch"));
    add(deleteMatchRadio);

    ExRadioButton<Integer> newTabUnmatchRadio =
        new ExRadioButton<>(FilterConditions.FILTER_OPERATION_NEW_TAB_UNMATCH,
            SCBundle.get("key.filterOpe.newTabUnmatch"));
    add(newTabUnmatchRadio);

    ExRadioButton<Integer> newTabMatchRadio = new ExRadioButton<>(
        FilterConditions.FILTER_OPERATION_NEW_TAB_MATCH, SCBundle.get("key.filterOpe.newTabMatch"));
    add(newTabMatchRadio);

    buttonGroup = new ExButtonGroup<Integer>(deleteUnmatchRadio, deleteMatchRadio,
        newTabUnmatchRadio, newTabMatchRadio);
  }

  public int getSeletedOperation() {
    return buttonGroup.getSelectedValue();
  }
}
