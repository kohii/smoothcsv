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
package com.smoothcsv.framework.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class SCStatusBar extends JPanel {

  private static final long serialVersionUID = 1519182619888030042L;

  private final JToolBar toolBar;

  private final JLabel messageLabel;
  private String message;

  private Thread revertingMessageThread;

  public SCStatusBar() {

    setFocusable(false);
    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

    Font font = getFont();
    font = font.deriveFont((float) font.getSize() - 1);
    setFont(font);

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {199, 27, 0};
    gridBagLayout.rowHeights = new int[] {16, 0};
    gridBagLayout.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    messageLabel = new JLabel();
    messageLabel.setFont(font);
    messageLabel.setFocusable(false);
    GridBagConstraints gbc_lblMsg = new GridBagConstraints();
    gbc_lblMsg.anchor = GridBagConstraints.WEST;
    gbc_lblMsg.insets = new Insets(0, 5, 0, 5);
    gbc_lblMsg.gridx = 0;
    gbc_lblMsg.gridy = 0;
    add(messageLabel, gbc_lblMsg);

    toolBar = new JToolBar();
    toolBar.setFocusable(false);
    toolBar.setFloatable(false);
    toolBar.add(Box.createGlue());
    GridBagConstraints gbc_toolBar = new GridBagConstraints();
    gbc_toolBar.anchor = GridBagConstraints.EAST;
    gbc_toolBar.gridx = 1;
    gbc_toolBar.gridy = 0;
    gbc_toolBar.weightx = 1.0;
    add(toolBar, gbc_toolBar);
  }

  public void setMessage(String message) {
    this.message = message;
    messageLabel.setText(message);
  }

  public synchronized void showTemporaryMessage(String message) {
    messageLabel.setText(message);
    // Revert 2 seconds later

    if (revertingMessageThread != null) {
      revertingMessageThread.interrupt();
    }

    revertingMessageThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(2500);
        } catch (InterruptedException e) {
          return;
        }
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            revertingMessageThread = null;
            messageLabel.setText(SCStatusBar.this.message);
          }
        });
      }
    });
    revertingMessageThread.start();
  }

  public void addStatusComponent(Component comp) {
    comp.setFocusable(false);
    comp.setFont(getFont());
    toolBar.addSeparator();
    toolBar.add(comp);
  }

  public void addStatusComponent(Component comp, int index) {
    comp.setFocusable(false);
    comp.setFont(getFont());
    toolBar.addSeparator();
    toolBar.add(comp, index + 1);
  }
}
