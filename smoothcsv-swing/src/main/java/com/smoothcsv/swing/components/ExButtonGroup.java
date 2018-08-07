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

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;

import lombok.Setter;

/**
 * @author kohii
 */
public class ExButtonGroup<V> extends ButtonGroup {

  private static final long serialVersionUID = -692927183972912721L;

  private ExRadioButton<V>[] radioButtons;

  private boolean wrap = true;

  @Setter
  private ActionListener action;

  @SafeVarargs
  public ExButtonGroup(final ExRadioButton<V>... radioButtons) {
    this.radioButtons = radioButtons;
    if (radioButtons == null) {
      throw new IllegalArgumentException();
    }

    // Listeners
    KeyListener keyListener = new KeyListener() {

      @Override
      public void keyTyped(KeyEvent e) {}

      @Override
      public void keyReleased(KeyEvent e) {}

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.isAltDown()) {
          return;
        }
        int currentSelectedIdx = getSelectedIndex();
        if (currentSelectedIdx == -1) {
          return;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_KP_LEFT
            || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP) {
          // UP

          ExRadioButton<V> radioButton = next(currentSelectedIdx, false);
          radioButton.setSelected(true);
          radioButton.requestFocusInWindow();

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_KP_RIGHT
            || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
          // DOWN
          ExRadioButton<V> radioButton = next(currentSelectedIdx, true);
          radioButton.setSelected(true);
          radioButton.requestFocusInWindow();
        }
      }
    };

    MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() % 2 == 0) {
          if (action != null) {
            action.actionPerformed(null);
          }
        }
      }
    };

    for (ExRadioButton<V> jRadioButton : radioButtons) {
      add(jRadioButton);
      if (radioButtons.length > 1) {
        jRadioButton.addKeyListener(keyListener);
      }
      jRadioButton.addMouseListener(mouseListener);
    }
    radioButtons[0].setSelected(true);
  }

  private ExRadioButton<V> next(int current, boolean b) {
    int cursor = current;
    for (int i = 0; i < radioButtons.length; i++) {
      if (b) {
        cursor++;
        if (cursor == radioButtons.length) {
          if (wrap) {
            cursor = 0;
          } else {
            return radioButtons[current];
          }
        }
      } else {
        cursor--;
        if (cursor == -1) {
          if (wrap) {
            cursor = radioButtons.length - 1;
          } else {
            return radioButtons[current];
          }
        }
      }

      ExRadioButton<V> radio = radioButtons[cursor];
      if (radio.isEnabled()) {
        return radio;
      }
    }
    return radioButtons[current];
  }

  public void addSelectionListener(Consumer<ExRadioButton<V>> listener) {
    ItemListener itemListener = new ItemListener() {
      @SuppressWarnings("unchecked")
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          listener.accept((ExRadioButton<V>) e.getSource());
        }
      }
    };
    for (ExRadioButton<V> button : radioButtons) {
      button.addItemListener(itemListener);
    }
  }

  public int getSelectedIndex() {
    for (int i = 0; i < radioButtons.length; i++) {
      if (radioButtons[i].isSelected()) {
        return i;
      }
    }
    return -1;
  }

  public boolean setSelectedIndex(int idx) {
    if (idx < radioButtons.length) {
      radioButtons[idx].setSelected(true);
      radioButtons[idx].requestFocusInWindow();
      return true;
    }
    return false;
  }

  public ExRadioButton<V> getSelectedButton() {
    int idx = getSelectedIndex();
    if (idx < 0) {
      return null;
    } else {
      return getRadioButton(idx);
    }
  }

  public ExRadioButton<V> getRadioButton(int index) {
    return radioButtons[index];
  }

  public ExRadioButton<V>[] getRadioButtons() {
    return radioButtons;
  }

  public boolean isWrap() {
    return wrap;
  }

  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }

  public void setEnabled(boolean b) {
    for (ExRadioButton<V> jRadioButton : radioButtons) {
      jRadioButton.setEnabled(b);
    }
  }

  public V getSelectedValue() {
    int i = getSelectedIndex();
    if (i < 0) {
      return null;
    } else {
      return radioButtons[i].getValue();
    }
  }

  public void setSelectedValue(V value) {
    for (int i = 0; i < radioButtons.length; i++) {
      if (value.equals(radioButtons[i].getValue())) {
        radioButtons[i].setSelected(true);
      }
    }
  }
}
