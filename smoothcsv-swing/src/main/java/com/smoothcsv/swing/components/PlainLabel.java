package com.smoothcsv.swing.components;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
public class PlainLabel extends JLabel {

  public PlainLabel(String text, Icon icon, int horizontalAlignment) {
    super(text, icon, horizontalAlignment);
    SwingUtils.disableHtml(this);
  }

  public PlainLabel(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    SwingUtils.disableHtml(this);
  }

  public PlainLabel(String text) {
    super(text);
    SwingUtils.disableHtml(this);
  }

  public PlainLabel(Icon image, int horizontalAlignment) {
    super(image, horizontalAlignment);
    SwingUtils.disableHtml(this);
  }

  public PlainLabel(Icon image) {
    super(image);
    SwingUtils.disableHtml(this);
  }

  public PlainLabel() {
    SwingUtils.disableHtml(this);
  }
}
