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
package com.smoothcsv.framework.component.dialog;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.smoothcsv.framework.SCApplication;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class GenericMessageDialog extends DialogBase {

  public static void alert(String message) {
    showDialog(message, DialogOperation.OK);
  }

  public static boolean confirm(String message) {
    return showDialog(message, DialogOperation.OK, DialogOperation.CANCEL) == DialogOperation.OK;
  }

  public static DialogOperation showDialog(String message, DialogOperation... availableOperations) {
    GenericMessageDialog dialog = new GenericMessageDialog() {
      @Override
      protected DialogOperation[] getDialogOperations() {
        return availableOperations;
      }
    };
    dialog.setMessage(message);
    dialog.pack();
    return dialog.showDialog();
  }

  private JTextArea messageComponent;

  private GenericMessageDialog() {
    super((JFrame) null, SCApplication.getApplication().getName());
    setMinimumSize(new Dimension(400, 130));
    setMaximumSize(new Dimension(700, 400));
  }

  public void setMessage(String message) {
    messageComponent.setText(message);
    messageComponent.setCaretPosition(0);
  }

  @Override
  protected JPanel createContentPanel() {
    JPanel panel = super.createContentPanel();
    messageComponent = new JTextArea();
    messageComponent.setEditable(false);
    messageComponent.setFocusable(false);
    messageComponent.setBackground(null);
    messageComponent.setLineWrap(true);
    JScrollPane messageScrollPane = new JScrollPane(messageComponent,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    messageScrollPane.setBorder(null);
    messageScrollPane.setBackground(null);
    messageScrollPane.getViewport().setBackground(null);
    panel.add(messageScrollPane);
    return panel;
  }
}
