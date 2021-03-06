package com.smoothcsv.core.menu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenuItem;

import command.app.OpenFileCommand;

/**
 * @author kohii
 */
public class OpenFileMenuItem extends JMenuItem {

  private final File file;

  public OpenFileMenuItem(File file) {
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
    new OpenFileCommand().run(file);
  }
}