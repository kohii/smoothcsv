package com.smoothcsv.swing.components.text;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.Position;

/**
 * @author kohii
 */
public class EditOriginalBoxView extends BoxView {
  private final ExTextPaneConfig config;

  public EditOriginalBoxView(Element elem, int y, ExTextPaneConfig config) {
    super(elem, y);
    this.config = config;
  }

  public void paint(Graphics g, Shape a) {
    super.paint(g, a);
    paintOriginalParagraph(g, a);
  }

  private void paintOriginalParagraph(Graphics g, Shape a) {
    if (config == null || !config.showEOF()) {
      return;
    }
    try {
      Shape paragraph = modelToView(getEndOffset(), a, Position.Bias.Backward);
      Rectangle rec = (paragraph == null) ? a.getBounds() : paragraph.getBounds();
      final int X = rec.x;
      final int Y = rec.y;
      final int HEIGHT = rec.height;
      final Color defaultColor = g.getColor();

      g.setColor(config.getColor());
      g.drawString("[EOF]", X, Y + HEIGHT - 3);

      g.setColor(defaultColor);
    } catch (BadLocationException ignored) {
    }

  }
}