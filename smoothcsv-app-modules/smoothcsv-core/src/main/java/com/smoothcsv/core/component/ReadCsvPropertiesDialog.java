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
package com.smoothcsv.core.component;

import java.awt.Dialog;

import javax.swing.JPanel;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class ReadCsvPropertiesDialog extends CsvPropertiesDialog {

  /**
   * @param parent
   * @param title
   * @param autoDeterminedOptionEnabled
   * @param readMode
   * @param showSizeOption
   */
  public ReadCsvPropertiesDialog(Dialog parent, String title) {
    super(parent, title, true, true, false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.core.component.CsvPropertiesDialog#initialize()
   */
  @Override
  protected void initialize() {
    super.initialize();
    JPanel panel = getContentPanel();
    // panel.add(createReadOptionPanel());
  }

  /**
   * @return
   */
  private JPanel createReadOptionPanel() {
    // TODO
    return null;
  }
}
