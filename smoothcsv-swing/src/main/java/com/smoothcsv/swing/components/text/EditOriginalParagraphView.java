package com.smoothcsv.swing.components.text;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;

/**
 * @author kohii
 */
public class EditOriginalParagraphView extends ParagraphView {

  private final ExTextPaneConfig config;

  public EditOriginalParagraphView(Element elem, ExTextPaneConfig config) {
    super(elem);
    this.config = config;
  }

  public void paint(Graphics g, Shape a) {
    super.paint(g, a);
    if (getEndOffset() != getDocument().getEndPosition().getOffset()) {
      // last line
      paintOriginalParagraph(g, a);
    }
  }

  private void paintOriginalParagraph(Graphics g, Shape a) {
    if (config == null || !config.showEOL()) {
      return;
    }
    try {
      Shape paragraph = modelToView(getEndOffset(), a,
          Position.Bias.Backward);
      Rectangle rec = (paragraph == null) ? a.getBounds() : paragraph
          .getBounds();
      final int X = rec.x;
      final int Y = rec.y;
      final int HEIGHT = rec.height;
      final int WIDTH = HEIGHT * 5 / 7;

      g.setColor(config.getColor());

      int x0 = X + WIDTH / 10;
      int x1 = X + WIDTH * 3 / 4;
      int y0 = Y + HEIGHT / 4;
      int y1 = Y + HEIGHT * 3 / 4;
      int w = WIDTH / 4;

      g.drawLine(x1, y0, x1, y1);
      g.drawLine(x0, y1, x1, y1);
      g.drawLine(x0, y1, x0 + w, y1 + w);
      g.drawLine(x0, y1, x0 + w, y1 - w);

    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void layout(int width, int height) {
    if (config.isWordWrap()) {
      super.layout(width, height);
    } else {
      super.layout(Short.MAX_VALUE, height);
    }
  }
}