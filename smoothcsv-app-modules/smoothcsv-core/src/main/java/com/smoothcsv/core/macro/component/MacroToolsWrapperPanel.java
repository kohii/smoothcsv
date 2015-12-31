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
package com.smoothcsv.core.macro.component;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.smoothcsv.framework.component.SCTabbedPane;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class MacroToolsWrapperPanel extends JPanel {
  public MacroToolsWrapperPanel(SCTabbedPane tabbedPane, MacroToolsPanel macroTools) {
    setFocusable(false);
    setBorder(null);
    setLayout(new BorderLayout(0, 0));

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, macroTools);
    splitPane.setDividerSize(5);
    splitPane.setFocusable(false);
    splitPane.setBorder(null);
    splitPane.setResizeWeight(0.6);
    add(splitPane, BorderLayout.CENTER);
  }
}
