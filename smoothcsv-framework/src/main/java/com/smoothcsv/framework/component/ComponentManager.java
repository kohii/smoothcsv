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
package com.smoothcsv.framework.component;

import java.awt.BorderLayout;
import java.awt.Container;

/**
 * @author kohii
 */
public class ComponentManager {

  private SCFrame frame;

  private SCMenuBar menuBar;

  private SCToolBar toolBar;

  private SCStatusBar statusBar;

  private SCTabbedPane tabbedPane;

  public ComponentManager() {}

  public void initComponents() {

    // Create components
    frame = createFrame();
    menuBar = createMenuBar();
    toolBar = createToolBar();
    statusBar = createStatusBar();
    tabbedPane = createTabbedPane();

    // Assemble components
    assembleComponents();
  }

  protected SCFrame createFrame() {
    return new SCFrame();
  }

  protected SCMenuBar createMenuBar() {
    return new SCMenuBar();
  }

  protected SCToolBar createToolBar() {
    return new SCToolBar();
  }

  protected SCStatusBar createStatusBar() {
    return new SCStatusBar();
  }

  protected SCTabbedPane createTabbedPane() {
    return new SCTabbedPane("tab");
  }

  protected void assembleComponents() {
    frame.setJMenuBar(menuBar);
    Container contentPane = frame.getContentPane();
    contentPane.add(toolBar, BorderLayout.NORTH);
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    contentPane.add(statusBar, BorderLayout.SOUTH);
  }

  public SCFrame getFrame() {
    return frame;
  }

  public SCMenuBar getMenuBar() {
    return menuBar;
  }

  public SCToolBar getToolBar() {
    return toolBar;
  }

  public SCStatusBar getStatusBar() {
    return statusBar;
  }

  public SCTabbedPane getTabbedPane() {
    return tabbedPane;
  }
}
