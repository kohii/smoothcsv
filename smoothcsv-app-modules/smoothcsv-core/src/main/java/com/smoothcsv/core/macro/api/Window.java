package com.smoothcsv.core.macro.api;

/**
 * The window object represents the window of SmoothCSV application.
 *
 * @author kohei
 */
public interface Window {

  /**
   * Displays a message dialog with the specified message and an OK button.
   *
   * @param message the text to be displayed in the dialog
   */
  void alert(String message);

  /**
   * Displays a modal dialog with a message and two buttons, OK and Cancel.
   *
   * @param message the text to be displayed in the dialog
   * @return true if OK was selected.
   */
  boolean confirm(String message);

  /**
   * Displays a dialog with a message prompting the user to input some text.
   *
   * @param message the text to be displayed in the dialog
   * @return the text entered by the user, or null.
   */
  String prompt(String message);

  /**
   * Displays a dialog with a message prompting the user to input some text.
   *
   * @param message      the text to be displayed in the dialog
   * @param defaultValue the default value displayed in the text input field
   * @return the text entered by the user, or null.
   */
  String prompt(String message, Object defaultValue);
}
