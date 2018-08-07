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
package com.smoothcsv.swing.components;

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class ExLabel extends JPanel {
  public ExLabel(String text, JComponent... components) {
    setBorder(null);
    setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    String[] texts = text.split("\\{\\}");
    for (int i = 0; i < texts.length; i++) {
      add(new JLabel(texts[i]));
      if (i < components.length) {
        add(components[i]);
      }
    }
  }
}
