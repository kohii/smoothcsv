package com.smoothcsv.swing.components.text;

import javax.swing.text.AbstractDocument;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * @author kohii
 */
public class EditOriginalViewFactory implements ViewFactory {

  private final ExTextPaneConfig config;

  public EditOriginalViewFactory(ExTextPaneConfig config) {
    this.config = config;
  }

  @Override
  public View create(Element elem) {
    String kind = elem.getName();

    if (kind != null) {
      if (kind.equals(AbstractDocument.ContentElementName)) {
        return new EditOriginalLabelView(elem, config);
      } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
        return new EditOriginalParagraphView(elem, config);
      } else if (kind.equals(AbstractDocument.SectionElementName)) {
        return new EditOriginalBoxView(elem, View.Y_AXIS, config);
      } else if (kind.equals(StyleConstants.ComponentElementName)) {
        return new ComponentView(elem);
      } else if (kind.equals(StyleConstants.IconElementName)) {
        return new IconView(elem);
      }
    }
    return new LabelView(elem);
  }
}