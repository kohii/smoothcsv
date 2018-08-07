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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

import com.smoothcsv.swing.utils.SwingUtils;


public class HistoryTextBox extends JComboBox<String> {

  private static final long serialVersionUID = -6651965860649511254L;

  protected UndoManager undoManager = new UndoManager();

  protected History history;

  @SuppressWarnings("serial")
  public HistoryTextBox(History history) {
    setEditable(true);
    this.history = history;
    setMaximumRowCount(history.getMaxSize());
    updateItem();

    if (getEditor().getEditorComponent() instanceof JTextField) {
      JTextField textField = (JTextField) getEditor().getEditorComponent();

      // textField.setDocument(new PlainDocument() {
      // @Override
      // public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
      // {
      // super.insertString(offs, StringUtils.convertLineSeparater(str, ""), a);
      // }
      // });

      SwingUtils.installUndoManager(textField, undoManager);
    }

    KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    getInputMap().remove(escKey);
    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escKey, "hidePopup");
    getActionMap().put("hidePopup", new AbstractAction("hidePopup") {
      @Override
      public void actionPerformed(ActionEvent e) {
        hidePopup();
      }

      @Override
      public boolean isEnabled() {
        return isPopupVisible();
      }
    });
  }

  // public void register(String s) {
  // history.put(s);
  // }

  public void flush() {
    history.flush();
  }

  public void clear() {
    history.clear();
    getEditor().setItem("");
  }

  public void saveCurrentItem() {
    Object item = getEditor().getItem();
    history.put(item.toString());
    updateItem();
  }

  public void updateItem() {
    Object item = getEditor().getItem();
    HistoryTextBox.this.removeAllItems();
    int size = history.size();
    for (int i = 0; i < size; i++) {
      HistoryTextBox.this.addItem(history.get(i));
    }
    getEditor().setItem(item);
  }

  @Override
  public void addActionListener(ActionListener a) {
    ((JTextField) getEditor().getEditorComponent()).addActionListener(a);
  }

  public UndoManager getUndoManager() {
    return undoManager;
  }

  public void disableEnterPressedKeyBinding() {
    KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    JComponent ec = (JComponent) getEditor().getEditorComponent();

    Object actionKey;
    actionKey = getInputMap().get(ks);
    getActionMap().remove(actionKey);
    actionKey = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(ks);
    getActionMap().remove(actionKey);

    actionKey = ec.getInputMap().get(ks);
    ec.getActionMap().remove(actionKey);
    actionKey = ec.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(ks);
    ec.getActionMap().remove(actionKey);
  }
}
