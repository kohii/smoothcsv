package com.smoothcsv.core.macro.api;

import com.smoothcsv.core.macro.apiimpl.APIBase;

import javax.swing.JOptionPane;

/**
 * The window object represents the window of SmoothCSV application.
 *
 * @author kohii
 */
public class Window extends APIBase {

  private Window() {}

  /**
   * Displays a message dialog with the specified message and an OK button.
   *
   * @param message the text to be displayed in the dialog
   */
  public static void alert(String message) {
    JOptionPane.showMessageDialog(null, message);
  }

  /**
   * Displays a modal dialog with a message and two buttons, OK and Cancel.
   *
   * @param message the text to be displayed in the dialog
   * @return true if OK was selected.
   */
  public static boolean confirm(String message) {
    int result = JOptionPane.showConfirmDialog(null, message, null, JOptionPane.OK_CANCEL_OPTION);
    return result == JOptionPane.OK_OPTION;
  }

  /**
   * Displays a dialog with a message prompting the user to input some text.
   *
   * @param message the text to be displayed in the dialog
   * @return the text entered by the user, or null.
   */
  public static String prompt(String message) {
    return prompt(message, null);
  }

  /**
   * Displays a dialog with a message prompting the user to input some text.
   *
   * @param message      the text to be displayed in the dialog
   * @param defaultValue the default value displayed in the text input field
   * @return the text entered by the user, or null.
   */
  public static String prompt(String message, Object defaultValue) {
    return JOptionPane.showInputDialog(null, message, defaultValue == null ? "" : defaultValue);
  }
}
