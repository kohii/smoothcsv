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
package com.smoothcsv.core.component;

import java.awt.BorderLayout;
import java.awt.Container;

import com.smoothcsv.core.macro.component.MacroToolsPanel;
import com.smoothcsv.core.macro.component.MacroToolsWrapperPanel;
import com.smoothcsv.framework.component.ComponentManager;

/**
 * @author kohii
 */
public class SmoothCsvComponentManager extends ComponentManager {

  private MacroToolsWrapperPanel macroToolsWrapper;

  private MacroToolsPanel macroTools;

  private boolean macroToolsVisible = false;

  public void setMacroToolsVisible(boolean b) {
    Container contentPane = getFrame().getContentPane();
    if (b) {
      contentPane.remove(getTabbedPane());
      MacroToolsPanel macroTools = getMacroTools();
      macroToolsWrapper = new MacroToolsWrapperPanel(getTabbedPane(), macroTools);
      contentPane.add(macroToolsWrapper, BorderLayout.CENTER);
      macroTools.requestFocusInWindow();
    } else {
      contentPane.remove(macroToolsWrapper);
      contentPane.add(getTabbedPane(), BorderLayout.CENTER);
      getTabbedPane().requestFocusInWindow();
      macroToolsWrapper = null;
    }
    contentPane.revalidate();
    contentPane.repaint();
    macroToolsVisible = b;
  }

  public MacroToolsPanel getMacroTools() {
    if (macroTools == null) {
      macroTools = new MacroToolsPanel();
    }
    return macroTools;
  }

  public boolean isMacroToolsVisible() {
    return macroToolsVisible;
  }
}
