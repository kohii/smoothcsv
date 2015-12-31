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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.components.LineBreakableTextField;

import lombok.Getter;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class MacroConsolePanel extends JPanel implements ActionListener, SmoothComponent {

  @Getter
  private SmoothComponentSupport componentSupport = new SmoothComponentSupport(this,
      "macro-console");

  private LineBreakableTextField inputTextArea;
  private JTextArea consoleTextArea;
  private int maxLineCount = 100;

  private Consumer<String> inputHandler;

  private LinkedList<String> history = new LinkedList<>();
  private int maxHistorySize = 20;
  private int historyIndex = -1;

  public MacroConsolePanel() {
    setBorder(null);
    setBackground(Color.WHITE);
    setLayout(new BorderLayout(0, 0));

    Border border =
        BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.getDefaultBorderColor());

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setMinimumSize(new Dimension(50, 20));
    scrollPane.setBorder(null);

    consoleTextArea = new JTextArea();
    consoleTextArea.setMargin(new Insets(3, 3, 3, 3));
    consoleTextArea.setTabSize(4);
    consoleTextArea.setLineWrap(true);
    consoleTextArea.setEditable(false);
    // consoleTextArea.setFocusable(false);
    scrollPane.setViewportView(consoleTextArea);
    add(scrollPane, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    panel.setBorder(border);
    panel.setBackground(Color.WHITE);
    add(panel, BorderLayout.SOUTH);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] {0, 0, 0};
    gbl_panel.rowHeights = new int[] {0, 0};
    gbl_panel.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
    gbl_panel.rowWeights = new double[] {1.0, Double.MIN_VALUE};
    panel.setLayout(gbl_panel);

    JLabel label = new JLabel(">");
    label.setForeground(new Color(0, 128, 128));
    label.setBackground(Color.WHITE);
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.NORTH;
    gbc_label_1.insets = new Insets(0, 3, 0, 3);
    gbc_label_1.gridx = 0;
    gbc_label_1.gridy = 0;
    panel.add(label, gbc_label_1);

    JScrollPane inputTextAreaScrollPane = new JScrollPane();
    inputTextAreaScrollPane.setMinimumSize(new Dimension(50, 20));
    inputTextAreaScrollPane.setBorder(null);

    inputTextArea = new LineBreakableTextField(true);
    Object upActionName =
        inputTextArea.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
    Object downActionName =
        inputTextArea.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
    inputTextArea.setLineWrap(true);
    inputTextArea.setWrapStyleWord(true);
    inputTextArea.setTabSize(4);
    inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        adjustInputTextAreaSize();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        adjustInputTextAreaSize();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        adjustInputTextAreaSize();
      }
    });
    inputTextArea.addActionListener(this);
    inputTextArea.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "restore-prev-input");
    inputTextArea.getActionMap().put("restore-prev-input", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int lineCount = inputTextArea.getLineCount();
          if (lineCount == 1) {
            restoreInputFromHistory(-1);
            return;
          } else {
            int caretIndex = inputTextArea.getCaretPosition();
            if (caretIndex < inputTextArea.getLineEndOffset(0)) {
              restoreInputFromHistory(-1);
              return;
            }
          }
        } catch (BadLocationException e1) {
          throw new RuntimeException(e1);
        }
        inputTextArea.getActionMap().get(upActionName).actionPerformed(e);
      }
    });
    inputTextArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
        "restore-next-input");
    inputTextArea.getActionMap().put("restore-next-input", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int lineCount = inputTextArea.getLineCount();
          if (lineCount == 1) {
            restoreInputFromHistory(+1);
            return;
          } else {
            int caretIndex = inputTextArea.getCaretPosition();
            if (caretIndex >= inputTextArea.getLineEndOffset(lineCount - 2)) {
              restoreInputFromHistory(+1);
              return;
            }
          }
        } catch (BadLocationException e1) {
          throw new RuntimeException(e1);
        }
        inputTextArea.getActionMap().get(downActionName).actionPerformed(e);
      }
    });
    inputTextAreaScrollPane.setViewportView(inputTextArea);
    GridBagConstraints gbc_inputTextArea = new GridBagConstraints();
    gbc_inputTextArea.fill = GridBagConstraints.BOTH;
    gbc_inputTextArea.gridx = 1;
    gbc_inputTextArea.gridy = 0;
    panel.add(inputTextAreaScrollPane, gbc_inputTextArea);

    // SCToolBar toolBar = new SCToolBar();
    // add(toolBar, BorderLayout.NORTH);
  }

  private void adjustInputTextAreaSize() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Container parent = inputTextArea.getParent().getParent();
        int preferredHeight =
            Math.min(inputTextArea.getLineHeight() * 3, inputTextArea.getPreferredSize().height);
        Dimension size = parent.getSize();
        if (preferredHeight == size.height) {
          return;
        }
        size.height = preferredHeight;
        parent.setPreferredSize(size);
        parent.getParent().revalidate();
      }
    });
  }

  private void restoreInputFromHistory(int direction) {
    int index = historyIndex + direction;
    if (index < 0 || history.size() < index) {
      return;
    }
    if (history.size() == index) {
      inputTextArea.setText("");
    } else {
      inputTextArea.setText(history.get(index));
      if (direction > 0) {
        inputTextArea.setCaretPosition(inputTextArea.getDocument().getLength());
      } else {
        if (inputTextArea.getLineCount() <= 1) {
          inputTextArea.setCaretPosition(inputTextArea.getDocument().getLength());
        } else {
          try {
            inputTextArea.setCaretPosition(inputTextArea.getLineEndOffset(0) - 1);
          } catch (BadLocationException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    adjustInputTextAreaSize();
    historyIndex = index;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String input = inputTextArea.getText();
    if (inputTextArea.getLineCount() <= 1) {
      consoleTextArea.append("> " + input + '\n');
    } else {
      consoleTextArea.append("> " + input.replaceAll("\r\n|\r|\n", " ") + '\n');
    }
    inputTextArea.setText("");
    adjustInputTextAreaSize();
    if (!input.isEmpty()) {
      history.add(input);
      while (history.size() > maxHistorySize) {
        history.removeFirst();
      }
    }
    historyIndex = history.size();
    if (inputHandler != null) {
      inputHandler.accept(input);
    }
    int lineCount = consoleTextArea.getLineCount();
    if (lineCount > maxLineCount) {
      try {
        int end = consoleTextArea.getLineEndOffset(lineCount - maxLineCount);
        consoleTextArea.replaceRange("", 0, end);
      } catch (BadLocationException e1) {
        throw new RuntimeException(e1);
      }
    }
    scrollToBottom();
    requestFocusInWindow();
  }

  public void append(String str) {
    consoleTextArea.append(str + '\n');
    scrollToBottom();
  }

  public void scrollToBottom() {
    consoleTextArea.setCaretPosition(consoleTextArea.getDocument().getLength());

  }

  public void setInputHandler(Consumer<String> inputHandler) {
    this.inputHandler = inputHandler;
  }

  @Override
  public boolean requestFocusInWindow() {
    return inputTextArea.requestFocusInWindow();
  }
}
