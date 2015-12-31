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
package com.smoothcsv.framework.util;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.KeyStroke;

import com.smoothcsv.commons.utils.StringUtils;

/**
 *
 * @author kohii
 */
public class KeyStrokeUtils {

  public static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

  public static String getKeyStrokeText(KeyStroke keyStroke) {
    return getKeyStrokeText(keyStroke.getKeyCode(), keyStroke.getModifiers());
  }

  public static String getKeyStrokeText(int keyCode, int modifiers) {
    String md = KeyEvent.getKeyModifiersText(modifiers);
    String kc = KeyEvent.getKeyText(keyCode);
    return StringUtils.isEmpty(md) ? kc : md + '+' + kc;
  }

  public static String stringify(KeyStroke keyStroke) {
    if (keyStroke == null) {
      return null;
    }

    int kc = keyStroke.getKeyCode();
    int modifiers = keyStroke.getModifiers();

    StringBuilder buf = new StringBuilder();

    if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
      buf.append("cmd+");
    }
    if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
      buf.append("ctrl+");
    }
    if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
      buf.append("alt+");
    }
    if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
      buf.append("shift+");
    }
    if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
      buf.append("altgraph+");
    }

    buf.append(getKeyText(kc));

    return buf.toString();
  }

  public static KeyStroke parse(String s) {
    StringTokenizer st = new StringTokenizer(s, "+");
    int count = st.countTokens();
    int mod = 0;
    int keyCode = 0;
    for (int i = 0; i < count; i++) {
      String token = st.nextToken();
      if (i < count - 1) {
        switch (token) {
          case "shortcut":
            mod |= SHORTCUT_KEY_MASK;
            break;
          case "shift":
            mod |= InputEvent.SHIFT_DOWN_MASK;
            break;
          case "ctrl":
          case "control":
            mod |= InputEvent.CTRL_DOWN_MASK;
            break;
          case "cmd":
            mod |= InputEvent.META_DOWN_MASK;
            break;
          case "alt":
            mod |= InputEvent.ALT_DOWN_MASK;
            break;
          case "altgraph":
            mod |= InputEvent.ALT_GRAPH_DOWN_MASK;
            break;
          default:
            throw new IllegalArgumentException(s);
        }
      } else {
        keyCode = getKeyCode(token);
      }
    }
    return KeyStroke.getKeyStroke(keyCode, mod);
  }

  private static int getKeyCode(String s) {
    int len = s.length();
    if (len == 0) {
      return 0;
    }
    if (len == 1) {
      char c = s.charAt(0);
      if ('a' <= c && c <= 'z') {
        return (int) Character.toUpperCase(c);
      } else if ('0' <= c && c <= '9') {
        return (int) c;
      }
    }

    switch (s) {
      case "comma":
        return KeyEvent.VK_COMMA;
      case "period":
        return KeyEvent.VK_PERIOD;
      case "slash":
        return KeyEvent.VK_SLASH;
      case "semicolon":
        return KeyEvent.VK_SEMICOLON;
      case "equals":
        return KeyEvent.VK_EQUALS;
      case "leftbracket":
        return KeyEvent.VK_OPEN_BRACKET;
      case "rightbracket":
        return KeyEvent.VK_CLOSE_BRACKET;
      case "backslash":
        return KeyEvent.VK_BACK_SLASH;
      case "enter":
        return KeyEvent.VK_ENTER;
      case "backspace":
        return KeyEvent.VK_BACK_SPACE;
      case "tab":
        return KeyEvent.VK_TAB;
      case "clear":
        return KeyEvent.VK_CLEAR;
      case "shift":
        return KeyEvent.VK_SHIFT;
      case "ctrl":
      case "control":
        return KeyEvent.VK_CONTROL;
      case "alt":
        return KeyEvent.VK_ALT;
      case "pause":
        return KeyEvent.VK_PAUSE;
      case "capslock":
        return KeyEvent.VK_CAPS_LOCK;
      case "escape":
        return KeyEvent.VK_ESCAPE;
      case "space":
        return KeyEvent.VK_SPACE;
      case "pageup":
        return KeyEvent.VK_PAGE_UP;
      case "pagedown":
        return KeyEvent.VK_PAGE_DOWN;
      case "end":
        return KeyEvent.VK_END;
      case "home":
        return KeyEvent.VK_HOME;
      case "left":
        return KeyEvent.VK_LEFT;
      case "up":
        return KeyEvent.VK_UP;
      case "right":
        return KeyEvent.VK_RIGHT;
      case "down":
        return KeyEvent.VK_DOWN;
      case "multiply":
        return KeyEvent.VK_MULTIPLY;
      case "add":
        return KeyEvent.VK_ADD;
      case "separator":
        return KeyEvent.VK_SEPARATOR;
      case "subtract":
        return KeyEvent.VK_SUBTRACT;
      case "decimal":
        return KeyEvent.VK_DECIMAL;
      case "divide":
        return KeyEvent.VK_DIVIDE;
      case "delete":
        return KeyEvent.VK_DELETE;
      case "numlock":
        return KeyEvent.VK_NUM_LOCK;
      case "scrolllock":
        return KeyEvent.VK_SCROLL_LOCK;
      case "f1":
        return KeyEvent.VK_F1;
      case "f2":
        return KeyEvent.VK_F2;
      case "f3":
        return KeyEvent.VK_F3;
      case "f4":
        return KeyEvent.VK_F4;
      case "f5":
        return KeyEvent.VK_F5;
      case "f6":
        return KeyEvent.VK_F6;
      case "f7":
        return KeyEvent.VK_F7;
      case "f8":
        return KeyEvent.VK_F8;
      case "f9":
        return KeyEvent.VK_F9;
      case "f10":
        return KeyEvent.VK_F10;
      case "f11":
        return KeyEvent.VK_F11;
      case "f12":
        return KeyEvent.VK_F12;
      case "f13":
        return KeyEvent.VK_F13;
      case "f14":
        return KeyEvent.VK_F14;
      case "f15":
        return KeyEvent.VK_F15;
      case "f16":
        return KeyEvent.VK_F16;
      case "f17":
        return KeyEvent.VK_F17;
      case "f18":
        return KeyEvent.VK_F18;
      case "f19":
        return KeyEvent.VK_F19;
      case "f20":
        return KeyEvent.VK_F20;
      case "f21":
        return KeyEvent.VK_F21;
      case "f22":
        return KeyEvent.VK_F22;
      case "f23":
        return KeyEvent.VK_F23;
      case "f24":
        return KeyEvent.VK_F24;
      default:
        throw new IllegalArgumentException(s);
    }
  }

  private static String getKeyText(int keyCode) {
    if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9 || keyCode >= KeyEvent.VK_A
        && keyCode <= KeyEvent.VK_Z) {
      return String.valueOf(Character.toLowerCase((char) keyCode));
    }

    switch (keyCode) {
      case KeyEvent.VK_COMMA:
        return "comma";
      case KeyEvent.VK_PERIOD:
        return "period";
      case KeyEvent.VK_SLASH:
        return "slash";
      case KeyEvent.VK_SEMICOLON:
        return "semicolon";
      case KeyEvent.VK_EQUALS:
        return "equals";
      case KeyEvent.VK_OPEN_BRACKET:
        return "leftbracket";
      case KeyEvent.VK_CLOSE_BRACKET:
        return "rightbracket";
      case KeyEvent.VK_BACK_SLASH:
        return "backslash";
      case KeyEvent.VK_ENTER:
        return "enter";
      case KeyEvent.VK_BACK_SPACE:
        return "backspace";
      case KeyEvent.VK_TAB:
        return "tab";
      case KeyEvent.VK_CLEAR:
        return "clear";
      case KeyEvent.VK_SHIFT:
        return "shift";
      case KeyEvent.VK_CONTROL:
        return "ctrl";
      case KeyEvent.VK_ALT:
        return "alt";
      case KeyEvent.VK_PAUSE:
        return "pause";
      case KeyEvent.VK_CAPS_LOCK:
        return "capslock";
      case KeyEvent.VK_ESCAPE:
        return "escape";
      case KeyEvent.VK_SPACE:
        return "space";
      case KeyEvent.VK_PAGE_UP:
        return "pageup";
      case KeyEvent.VK_PAGE_DOWN:
        return "pagedown";
      case KeyEvent.VK_END:
        return "end";
      case KeyEvent.VK_HOME:
        return "home";
      case KeyEvent.VK_LEFT:
        return "left";
      case KeyEvent.VK_UP:
        return "up";
      case KeyEvent.VK_RIGHT:
        return "right";
      case KeyEvent.VK_DOWN:
        return "down";
      case KeyEvent.VK_MULTIPLY:
        return "multiply";
      case KeyEvent.VK_ADD:
        return "add";
      case KeyEvent.VK_SEPARATOR:
        return "separator";
      case KeyEvent.VK_SUBTRACT:
        return "subtract";
      case KeyEvent.VK_DECIMAL:
        return "decimal";
      case KeyEvent.VK_DIVIDE:
        return "divide";
      case KeyEvent.VK_DELETE:
        return "delete";
      case KeyEvent.VK_NUM_LOCK:
        return "numlock";
      case KeyEvent.VK_SCROLL_LOCK:
        return "scrolllock";
      case KeyEvent.VK_F1:
        return "f1";
      case KeyEvent.VK_F2:
        return "f2";
      case KeyEvent.VK_F3:
        return "f3";
      case KeyEvent.VK_F4:
        return "f4";
      case KeyEvent.VK_F5:
        return "f5";
      case KeyEvent.VK_F6:
        return "f6";
      case KeyEvent.VK_F7:
        return "f7";
      case KeyEvent.VK_F8:
        return "f8";
      case KeyEvent.VK_F9:
        return "f9";
      case KeyEvent.VK_F10:
        return "f10";
      case KeyEvent.VK_F11:
        return "f11";
      case KeyEvent.VK_F12:
        return "f12";
      case KeyEvent.VK_F13:
        return "f13";
      case KeyEvent.VK_F14:
        return "f14";
      case KeyEvent.VK_F15:
        return "f15";
      case KeyEvent.VK_F16:
        return "f16";
      case KeyEvent.VK_F17:
        return "f17";
      case KeyEvent.VK_F18:
        return "f18";
      case KeyEvent.VK_F19:
        return "f19";
      case KeyEvent.VK_F20:
        return "f20";
      case KeyEvent.VK_F21:
        return "f21";
      case KeyEvent.VK_F22:
        return "f22";
      case KeyEvent.VK_F23:
        return "f23";
      case KeyEvent.VK_F24:
        return "f24";
      default:
        throw new IllegalArgumentException();
    }
    //
    // if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
    // char c = (char) (keyCode - KeyEvent.VK_NUMPAD0 + '0');
    // return "NUMPAD" + c;
    // }
    //
    // return "unknown(0x" + Integer.toString(keyCode, 16) + ")";
  }
}
