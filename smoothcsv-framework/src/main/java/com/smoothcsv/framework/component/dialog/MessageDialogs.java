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

import java.awt.Component;

import javax.swing.JOptionPane;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.HtmlUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
public class MessageDialogs {

  public static void alert(String msgId, Object... args) {
    alert(null, msgId, args);
  }

  /**
   * @param parentComponent
   * @param msgId
   * @param args
   */
  private static void alert(Component parentComponent, String msgId, Object... args) {
    showMessageString(parentComponent, MessageBundles.getString(msgId, args), getMessageType(msgId));
  }

  /**
   * @param parentComponent
   * @param msg
   * @param messageType
   */
  public static void showMessageString(Component parentComponent, String msg, int messageType) {
    if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
      SwingUtils.beep();
    }
    if (parentComponent == null) {
      parentComponent = SCApplication.components().getFrame();
    }
    JOptionPane.showMessageDialog(parentComponent, msg(msg), SCApplication.getApplication()
        .getName(), messageType);
  }

  public static boolean confirm(String msgId, Object... args) {
    return confirm(null, msgId, args);
  }

  public static boolean confirm(Component parentComponent, String msgId, Object... args) {
    int messageType = getMessageType(msgId);
    if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
      SwingUtils.beep();
    }
    String message = MessageBundles.getString(msgId, args);
    if (parentComponent == null) {
      parentComponent = SCApplication.components().getFrame();
    }
    int ret =
        JOptionPane.showConfirmDialog(parentComponent, msg(message), SCApplication.getApplication()
            .getName(), JOptionPane.YES_NO_OPTION, messageType);
    return ret == JOptionPane.OK_OPTION;
  }


  public static DialogOperation confirm2(String msgId, Object... args) {
    return confirm2(null, msgId, args);
  }

  public static DialogOperation confirm2(Component parentComponent, String msgId, Object... args) {
    int messageType = getMessageType(msgId);
    if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
      SwingUtils.beep();
    }
    String message = MessageBundles.getString(msgId, args);
    if (parentComponent == null) {
      parentComponent = SCApplication.components().getFrame();
    }
    int ret =
        JOptionPane.showConfirmDialog(parentComponent, msg(message), SCApplication.getApplication()
            .getName(), JOptionPane.YES_NO_CANCEL_OPTION, messageType);
    switch (ret) {
      case JOptionPane.YES_OPTION:
        return DialogOperation.YES;
      case JOptionPane.NO_OPTION:
        return DialogOperation.NO;
      case JOptionPane.CANCEL_OPTION:
      case JOptionPane.CLOSED_OPTION:
        return DialogOperation.CANCEL;
      default:
        throw new UnexpectedException();
    }
  }


  public static String prompt(String msgId, Object... args) {
    return prompt(null, msgId, args);
  }

  public static String prompt(Component parentComponent, String msgId, Object... args) {
    int messageType = getMessageType(msgId);
    if (messageType == JOptionPane.ERROR_MESSAGE || messageType == JOptionPane.WARNING_MESSAGE) {
      SwingUtils.beep();
    }
    String message = MessageBundles.getString(msgId, args);
    if (parentComponent == null) {
      parentComponent = SCApplication.components().getFrame();
    }
    String ret =
        JOptionPane.showInputDialog(parentComponent, msg(message), SCApplication.getApplication()
            .getName(), messageType);
    return ret;
  }

  private static String msg(String message) {
    return "<html><body><p style='width:300px;'>" + HtmlUtils.escapeHtml(message)
        + "</p></body></html>";
  }

  private static int getMessageType(String msgId) {
    if (msgId.startsWith("msg.info")) {
      return JOptionPane.INFORMATION_MESSAGE;
    } else if (msgId.startsWith("msg.warn")) {
      return JOptionPane.WARNING_MESSAGE;
    } else if (msgId.startsWith("msg.error")) {
      return JOptionPane.ERROR_MESSAGE;
    }
    return JOptionPane.INFORMATION_MESSAGE;
  }
}
