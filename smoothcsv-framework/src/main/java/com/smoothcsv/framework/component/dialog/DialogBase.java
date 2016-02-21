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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.error.ErrorHandlerFactory;

@SuppressWarnings("serial")
public abstract class DialogBase extends JDialog {

  private JPanel contentPanel;

  private JPanel buttonPanel;

  private DialogOperation selectedOperation;

  private DialogOperationAction[] actions;

  private JButton defaultButton;

  private boolean autoPack = false;

  private boolean canBeActive = false;

  public DialogBase(Frame owner, String title) {
    super(owner, title, true);
    initialize();
    setLocationRelativeTo(owner);
  }

  public DialogBase(Dialog owner, String title) {
    super(owner, title, true);
    initialize();
    setLocationRelativeTo(owner);
  }

  /**
   * Creates the dialog.
   */
  private void initialize() {
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.setFocusable(false);
    if (contentPane instanceof JComponent) {
      ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder());
    }

    actions = createButtonActions(getDialogOperations());

    contentPanel = createContentPanel();
    contentPanel.setFocusable(false);
    contentPane.add(contentPanel, BorderLayout.CENTER);

    buttonPanel = createButtonPanel(actions);
    if (buttonPanel != null) {
      buttonPanel.setFocusable(false);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }

    installOperationActions(actions);
  }

  protected JPanel getContentPanel() {
    return contentPanel;
  }

  protected JPanel createContentPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    return panel;
  }

  protected JPanel createButtonPanel(DialogOperationAction[] actions) {
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

    for (int i = 0; i < actions.length; i++) {
      DialogOperationAction dialogOperationAction = actions[i];
      JButton button = new JButton(dialogOperationAction);
      buttonPane.add(button);
      if (i == 0) {
        setDefaultButton(button);
      }
    }

    return buttonPane;
  }

  protected JPanel installOperationActions(DialogOperationAction[] actions) {
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

    for (int i = 0; i < actions.length; i++) {
      DialogOperationAction dialogOperationAction = actions[i];
      if (dialogOperationAction.operation == DialogOperation.CANCEL) {
        getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
        getRootPane().getActionMap().put("Cancel", dialogOperationAction);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            dialogOperationAction.actionPerformed(null);
          }
        });
      }
    }

    return buttonPane;
  }

  protected DialogOperation[] getDialogOperations() {
    return new DialogOperation[] {DialogOperation.OK, DialogOperation.CANCEL};
  }

  protected DialogOperationAction[] createButtonActions(DialogOperation... operations) {
    DialogOperationAction[] actions = new DialogOperationAction[operations.length];
    for (int i = 0; i < actions.length; i++) {
      actions[i] = new DialogOperationAction(operations[i]);
    }
    return actions;
  }

  public DialogOperation getSelectedOperation() {
    return selectedOperation;
  }

  protected boolean processOperation(DialogOperation selectedOperation) {
    return true;
  }

  private void setDefaultButton(JButton defaultButton) {
    this.defaultButton = defaultButton;
    getRootPane().setDefaultButton(defaultButton);
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      if (StringUtils.isEmpty(getTitle())) {
        setTitle(SCApplication.getApplication().getName());
      }

      selectedOperation = null;
      if (defaultButton != null) {
        setDefaultButton(defaultButton);
      }
      if (autoPack) {
        pack();
      }
      setLocationRelativeTo(getParent());
    }
    super.setVisible(b);
  }

  public DialogOperation showDialog() {
    if (!isModal()) {
      throw new IllegalStateException("The dialog must be modal when 'showDialog()' is called.");
    }
    setVisible(true);
    return getSelectedOperation();
  }

  protected void invokeOperationAction(DialogOperation operation) {
    for (DialogOperationAction action : actions) {
      if (action.getOperation() == operation) {
        action.actionPerformed(null);
        return;
      }
    }
    throw new IllegalArgumentException(operation + "");
  }

  /**
   * @param autoPack the autoPack to set
   */
  public void setAutoPack(boolean autoPack) {
    this.autoPack = autoPack;
  }

  public boolean getCanBeActive() {
    return canBeActive;
  }

  public void setCanBeActive(boolean canBeActive) {
    this.canBeActive = canBeActive;
  }

  protected class DialogOperationAction extends AbstractAction {

    private final DialogOperation operation;

    public DialogOperationAction(DialogOperation operation) {
      putValue(NAME, operation.getText());
      this.operation = operation;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        selectedOperation = operation;
        if (processOperation(selectedOperation)) {
          DialogBase.this.dispose();
        }
      } catch (Throwable t) {
        ErrorHandlerFactory.getErrorHandler().handle(t);
      }
    }

    public DialogOperation getOperation() {
      return operation;
    }
  }
}
