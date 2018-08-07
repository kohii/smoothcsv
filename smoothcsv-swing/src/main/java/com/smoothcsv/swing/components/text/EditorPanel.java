package com.smoothcsv.swing.components.text;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import lombok.Getter;

/**
 * @author kohii
 */
public class EditorPanel extends JPanel implements DocumentListener {

  private static final long serialVersionUID = -2039675113444621061L;

  private final ExTextPane textPane;
  private JTextPane linePane;
  private int beforeMaxLine;

  @Getter
  private JScrollPane scrollPane;

  public EditorPanel(ExTextPane textPane) {
    super(new BorderLayout());
    beforeMaxLine = 0;

    this.scrollPane = createScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    this.textPane = textPane;
    scrollPane.setViewportView(textPane);
    textPane.getDocument().addDocumentListener(this);
  }

  protected JScrollPane createScrollPane() {
    JScrollPane scrollPane = new JScrollPane(textPane);
    scrollPane.setBackground(Color.WHITE);
    scrollPane.setBorder(new LineBorder(Color.WHITE));
    scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
    return scrollPane;
  }

  private void setLinePane(JTextPane linePane) {
    this.linePane = linePane;
    linePane.setEditable(false);
    linePane.setFocusable(false);
    linePane.setVisible(false);
    scrollPane.setRowHeaderView(linePane);
  }

  private void updateLinePaneDocument() {
    if (linePane == null) {
      return;
    }
    int maxLine = textPane.getNumLines();

    if (beforeMaxLine != maxLine) {
      beforeMaxLine = maxLine;
      int tarSpace = (int) Math.log10(maxLine);
      Document doc = new DefaultStyledDocument();
      SimpleAttributeSet attr = new SimpleAttributeSet();

      linePane.setDocument(doc);

      try {
        for (int i = 0; i < maxLine; i++) {
          String str = String.format("%0" + (tarSpace + 1) + "d",
              i + 1);
          boolean space = true;
          for (int j = 0; j < str.length(); j++) {
            if (str.charAt(j) == '0' && space) {
              attr.addAttribute(StyleConstants.Foreground,
                  Color.WHITE);
              space = true;
            } else {
              attr.addAttribute(StyleConstants.Foreground,
                  Color.GRAY);
              space = false;
            }
            doc.insertString(doc.getLength(), "" + str.charAt(j),
                attr);
          }
          doc.insertString(doc.getLength(), "\n", attr);
        }
      } catch (BadLocationException ignored) {
      }
    }
  }


  public void changedUpdate(DocumentEvent event) {
    updateLinePaneDocument();
  }

  public void insertUpdate(DocumentEvent event) {
    updateLinePaneDocument();
  }

  public void removeUpdate(DocumentEvent event) {
    updateLinePaneDocument();
  }

}
