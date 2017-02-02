package com.smoothcsv.core.macro.apiimpl;

import javax.swing.JOptionPane;

import com.smoothcsv.core.macro.api.Window;
import lombok.Getter;

/**
 * @author kohii
 */
public class WindowImpl extends APIBase implements Window {

  @Getter
  private static final WindowImpl instance = new WindowImpl();

  private WindowImpl() {
  }

  @Override
  public void alert(String message) {
    JOptionPane.showMessageDialog(null, message);
  }

  @Override
  public boolean confirm(String message) {
    int result = JOptionPane.showConfirmDialog(null, message, null, JOptionPane.OK_CANCEL_OPTION);
    return result == JOptionPane.OK_OPTION;
  }

  @Override
  public String prompt(String message) {
    return prompt(message, null);
  }

  @Override
  public String prompt(String message, Object defaultValue) {
    return JOptionPane.showInputDialog(null, message, defaultValue == null ? "" : defaultValue);
  }
}
