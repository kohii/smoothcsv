package com.smoothcsv.core.menu;

import com.smoothcsv.core.macro.Macro;
import com.smoothcsv.core.macro.SCAppMacroRuntime;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JMenuItem;

/**
 * @author kohii
 */
public class RunMacroMenuItem extends JMenuItem {

  private final File file;

  public RunMacroMenuItem(File file) {
    this.file = file;

    String pathname;
    try {
      pathname = file.getCanonicalPath();
    } catch (IOException e) {
      pathname = "[UNKNOWN]";
    }
    setText(pathname);
  }

  @Override
  protected void fireActionPerformed(ActionEvent event) {
    Macro macro = new Macro(file);
    SCAppMacroRuntime.getMacroRuntime().execute(macro);
  }
}