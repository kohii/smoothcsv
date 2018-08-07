package com.smoothcsv.swing.components.text;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.LabelView;

/**
 * @author kohii
 */
public class EditOriginalLabelView extends LabelView {
  private final ExTextPaneConfig config;

  public EditOriginalLabelView(Element elem, ExTextPaneConfig config) {
    super(elem);
    this.config = config;
  }

  public void paint(Graphics g, Shape a) {
    super.paint(g, a);
    paintOriginalParagraph(g, a);
  }

  private void paintOriginalParagraph(Graphics g, Shape a) {
    Rectangle rec = (a instanceof Rectangle) ? (Rectangle) a : a
        .getBounds();
    final int X = rec.x;
    final int Y = rec.y;
    final int HEIGHT = rec.height;
    final int WIDTH = HEIGHT / 2;
    final Color defaultColor = g.getColor();

    String text = getText(getStartOffset(), getEndOffset()).toString();
    FontMetrics fontMetrics = g.getFontMetrics();
    int space = 0;

    g.setColor(config.getColor());

    for (int i = 0; i < text.length(); i++) {
      int ori_x = X + space + 2;
      int ori_y = Y + 2;

      char word = text.charAt(i);

      switch (word) {
        case '　':
          g.drawRect(ori_x, ori_y, WIDTH * 2 - 6, HEIGHT - 6);
          break;
        // case ' ':
        // g.drawLine(ori_x, ori_y + HEIGHT - 8, ori_x, ori_y + HEIGHT - 6);
        // g.drawLine(ori_x + WIDTH - 4, ori_y + HEIGHT - 8, ori_x + WIDTH
        // - 4, ori_y + HEIGHT - 6);
        // g.drawLine(ori_x, ori_y + HEIGHT - 6, ori_x + WIDTH - 4, ori_y
        // + HEIGHT - 6);
        // break;
        case ' ':
          if (config.showSpace()) {
            g.fillOval(ori_x - 1, ori_y + HEIGHT * 1 / 3, 3, 3);
          }
          break;
        // case ' ':
        // g.drawLine(ori_x, ori_y + HEIGHT - 8, ori_x, ori_y + HEIGHT - 6);
        // g.drawLine(ori_x + WIDTH - 4, ori_y + HEIGHT - 8, ori_x + WIDTH
        // - 4, ori_y + HEIGHT - 6);
        // g.drawLine(ori_x, ori_y + HEIGHT - 6, ori_x + WIDTH - 4, ori_y
        // + HEIGHT - 6);
        // break;
        case '\t':
          // // g.drawLine(ori_x, ori_y + HEIGHT / 2, ori_x + WIDTH / 2,
          // ori_y
          // // + HEIGHT / 2);
          if (config.showTab()) {
            g.drawLine(ori_x + WIDTH / 6, ori_y + HEIGHT / 2 - HEIGHT / 6,
                ori_x + WIDTH / 2, ori_y + HEIGHT / 2);
            g.drawLine(ori_x + WIDTH / 6, ori_y + HEIGHT / 2 + HEIGHT / 6,
                ori_x + WIDTH / 2, ori_y + HEIGHT / 2);
          }
          break;

        // g.drawLine(ori_x, ori_y + HEIGHT/2, ori_x + WIDTH/2, ori_y +
        // HEIGHT/2);
        // g.drawLine(ori_x + WIDTH/3, ori_y + HEIGHT/2 - HEIGHT/6, ori_x +
        // WIDTH/2, ori_y + HEIGHT/2);
        // g.drawLine(ori_x + WIDTH/3, ori_y + HEIGHT/2 + HEIGHT/6 , ori_x +
        // WIDTH/2, ori_y + HEIGHT/2);
        // break;

      }

      if (word != '\t') {
        space += fontMetrics.stringWidth("" + word);
      } else {
        space += (int) getTabExpander().nextTabStop(
            (float) (X + space), i)
            - (X + space);
      }
    }

    g.setColor(defaultColor);
  }
}