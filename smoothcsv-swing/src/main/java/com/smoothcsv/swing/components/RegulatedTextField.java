/*
 * Copyright 2016 kohii
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.smoothcsv.swing.components;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.smoothcsv.commons.utils.ArrayUtils;
import com.smoothcsv.commons.utils.StringUtils;
import lombok.Getter;

/**
 * @author kohii
 */
public class RegulatedTextField extends JTextField {

  private static final long serialVersionUID = 6786559558337430076L;

  public static enum Type {
    ANY, NUMERIC, HEX, ALPHABET, LETTER, UPPER, LOWER, CUSTOM
  }

  ;

  @Getter
  private final int maxLength;
  @Getter
  private final Type type;
  private final char[] customCharArray;

  public RegulatedTextField(int length) {
    this(Type.ANY, length);
  }

  public RegulatedTextField(Type type) {
    this(type, Integer.MAX_VALUE);
  }

  public RegulatedTextField(Type type, int length) {
    this.type = type;
    this.maxLength = length;
    customCharArray = null;
    setDocument(new RegulatedDocument());
    if (length != Integer.MAX_VALUE) {
      setColumns(length);
    }
  }

  public RegulatedTextField(char[] charArray) {
    this.maxLength = Integer.MAX_VALUE;
    this.customCharArray = charArray;
    this.type = Type.CUSTOM;
    setDocument(new RegulatedDocument());
  }

  private class RegulatedDocument extends PlainDocument {

    private static final long serialVersionUID = -2004410832474396830L;

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {

      int length = getLength();
      int insertableLength = maxLength - length;
      if (insertableLength == 0) {
        throw new BadLocationException(str, offset);
      }

      String insertStr = str;
      if (str.length() > insertableLength) {
        insertStr = insertStr.substring(0, insertableLength);
      }
      insertStr = validate(insertStr, offset);
      super.insertString(offset, insertStr, a);
    }

    private String validate(String s, int offset) throws BadLocationException {
      switch (type) {
        case ANY:
          return s;
        case NUMERIC:
          if (StringUtils.isNumber(s)) {
            return s;
          } else {
            throw new BadLocationException(s, offset);
          }
        case ALPHABET:
          return s;
        case LOWER:
          return s.toLowerCase();
        case UPPER:
          return s.toUpperCase();
        case CUSTOM:
          if (contains(s, customCharArray)) {
            return s;
          } else {
            throw new BadLocationException(s, offset);
          }
        case HEX:
        case LETTER:
        default:
          throw new IllegalArgumentException(String.valueOf(type));
      }
    }
  }

  public static boolean contains(String s, char[] array) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (ArrayUtils.contains(array, c)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setEnabled(boolean enabled) {
    setEditable(enabled);
    super.setEnabled(enabled);
  }

  public void setText(Integer i) {
    super.setText(i == null ? "" : i.toString());
  }
}
