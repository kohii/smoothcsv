package com.smoothcsv.swing.components.text;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.text.ViewFactory;

/**
 * @author kohii
 */
public class ExTextPane extends JTextPane {

  private ExTextPaneConfig config;
  private int tabSize = 4;

  private int spaceWidth = -1;
  private int tabWidth = -1;
  private int lineHeight = -1;

  public ExTextPane(ExTextPaneConfig config) {
    this.config = config;
    init();
  }

  private void init() {
    revalidateTabSet();
    setEditorKit(new StyledEditorKit() {
      public ViewFactory getViewFactory() {
        return new EditOriginalViewFactory(config);
      }
    });
  }

  @Override
  public void setFont(Font font) {
    super.setFont(font);
    spaceWidth = -1;
    tabWidth = -1;
    lineHeight = -1;
    revalidateTabSet();
  }

  public int getSpaceWidth() {
    if (spaceWidth == -1) {
      FontMetrics fm = getFontMetrics(getFont());
      spaceWidth = fm.charWidth(' ');
    }
    return spaceWidth;
  }

  public int getTabWidth() {
    if (tabWidth == -1) {
      FontMetrics fm = getFontMetrics(getFont());
      tabWidth = fm.charWidth(' ') * tabSize;
    }
    return tabWidth;
  }

  public int getLineHeight() {
    if (lineHeight == -1) {
      FontMetrics fm = getFontMetrics(getFont());
      lineHeight = fm.getHeight();
    }
    return lineHeight;
  }

  private void revalidateTabSet() {
    TabStop[] tabs = new TabStop[60];
    int tabWidth = getTabWidth();
    for (int i = 0; i < tabs.length; i++) {
      tabs[i] = new TabStop((i + 1) * tabWidth);
    }
    TabSet tabSet = new TabSet(tabs);
    SimpleAttributeSet attrs = new SimpleAttributeSet();
    StyleConstants.setTabSet(attrs, tabSet);
//    int l = getDocument().getLength();
//    getStyledDocument().setParagraphAttributes(0, l, attrs, false);
  }

  public int getNumLines() {
    Document doc = getDocument();
    return doc.getDefaultRootElement().getElementIndex(doc.getLength()) + 1;
  }


  @Override
  public boolean getScrollableTracksViewportWidth() {
    if (config.isWordWrap()) {
      return super.getScrollableTracksViewportWidth();
    }
    Object parent = getParent();
    if (parent instanceof JViewport) {
      if (ui.getPreferredSize(this).width < ((JViewport) parent)
          .getWidth()) {
        return true;
      }
    }
    return false;
  }


  /**
   * @see javax.swing.JTextArea#replaceSelection(String)
   */
  public void replaceRange(String str, int start, int end) {
    if (end < start) {
      throw new IllegalArgumentException("end before start");
    }
    Document doc = getDocument();
    if (doc != null) {
      try {
        if (doc instanceof AbstractDocument) {
          ((AbstractDocument) doc).replace(start, end - start, str,
              null);
        } else {
          doc.remove(start, end - start);
          doc.insertString(start, str, null);
        }
      } catch (BadLocationException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    }
  }

  /**
   * @see javax.swing.JTextArea#getLineStartOffset(int)
   */
  public int getLineStartOffset(int line) throws BadLocationException {
    int lineCount = getLineCount();
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= lineCount) {
      throw new BadLocationException("No such line", getDocument().getLength() + 1);
    } else {
      Element map = getDocument().getDefaultRootElement();
      Element lineElem = map.getElement(line);
      return lineElem.getStartOffset();
    }
  }

  /**
   * @see javax.swing.JTextArea#getLineOfOffset(int)
   */
  public int getLineOfOffset(int offset) throws BadLocationException {
    Document doc = getDocument();
    if (offset < 0) {
      throw new BadLocationException("Can't translate offset to line", -1);
    } else if (offset > doc.getLength()) {
      throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
    } else {
      Element map = getDocument().getDefaultRootElement();
      return map.getElementIndex(offset);
    }
  }

  /**
   * @see javax.swing.JTextArea#getLineCount()
   */
  public int getLineCount() {
    Element map = getDocument().getDefaultRootElement();
    return map.getElementCount();
  }

  /**
   * @see javax.swing.JTextArea#getLineEndOffset(int)
   */
  public int getLineEndOffset(int line) throws BadLocationException {
    int lineCount = getLineCount();
    if (line < 0) {
      throw new BadLocationException("Negative line", -1);
    } else if (line >= lineCount) {
      throw new BadLocationException("No such line", getDocument().getLength() + 1);
    } else {
      Element map = getDocument().getDefaultRootElement();
      Element lineElem = map.getElement(line);
      int endOffset = lineElem.getEndOffset();
      // hide the implicit break at the end of the document
      return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
    }
  }
}
