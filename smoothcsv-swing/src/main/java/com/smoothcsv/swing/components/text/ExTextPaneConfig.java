package com.smoothcsv.swing.components.text;

import java.awt.Color;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
@AllArgsConstructor
@Setter
public class ExTextPaneConfig {

  @Getter
  private Color color;

  @Getter
  private boolean wordWrap;

  private boolean showSpace;
  private boolean showTab;
  private boolean showEOL;
  private boolean showEOF;

  public boolean showSpace() {
    return showSpace;
  }

  public boolean showTab() {
    return showTab;
  }

  public boolean showEOL() {
    return showEOL;
  }

  public boolean showEOF() {
    return showEOF;
  }
}
