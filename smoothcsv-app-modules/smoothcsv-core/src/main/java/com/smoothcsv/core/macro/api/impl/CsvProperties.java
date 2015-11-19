/*
 * Copyright 2014 kohii.
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
package com.smoothcsv.core.macro.api.impl;

import java.nio.charset.Charset;

import com.smoothcsv.commons.utils.CharsetUtils;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.csv.CsvQuoteApplyRule;
import com.smoothcsv.csv.NewlineCharacter;

/**
 * @author kohii
 *
 */
public class CsvProperties extends APIBase {

  public static final String AUTO = "auto";

  /**
   * Suppresses all quoting.
   */
  public static final int NO_QUOTE = 1;
  /**
   * Quotes all values.
   */
  public static final int QUOTES_ALL = 2;
  /**
   * Quotes only when the value contains delimiter, quote, or escape.
   */
  public static final int QUOTES_IF_NECESSARY = 3;

  public static final String CR = NewlineCharacter.CR.stringValue();
  public static final String LF = NewlineCharacter.LF.stringValue();
  public static final String CRLF = NewlineCharacter.CRLF.stringValue();

  /**
   * @return
   */
  public static CsvProperties defaultProperties() {
    return new CsvProperties();
  }

  private String delimiter;
  private String quote;
  private String escape;
  private String charset;
  private boolean hasBOM;
  private String newlineCharacter;
  private int quoteOption;

  public CsvProperties() {
    this(CsvSheetSupport.getDefaultCsvMeta());
  }

  /**
   * @param csvMeta
   */
  CsvProperties(CsvMeta csvMeta) {
    this.delimiter = String.valueOf(csvMeta.getDelimiter());
    this.quote = String.valueOf(csvMeta.getQuote());
    this.escape = String.valueOf(csvMeta.getEscape());
    this.charset = csvMeta.getCharset().toString();
    this.hasBOM = csvMeta.hasBom();
    this.newlineCharacter = csvMeta.getNewlineCharacter().stringValue();
    switch (csvMeta.getQuoteOption()) {
      case NO_QUOTE:
        this.quoteOption = NO_QUOTE;
        break;
      case QUOTES_ALL:
        this.quoteOption = QUOTES_ALL;
        break;
      case QUOTES_IF_NECESSARY:
        this.quoteOption = QUOTES_IF_NECESSARY;
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Returns the character to separate each field.
   * 
   * @return the character to separate each field
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * Sets the character to separate each field.
   * 
   * @param delimiter the character to separate each field
   */
  public void setDelimiter(String delimiter) {
    if (delimiter.length() != 1) {
      throw new IllegalArgumentException("delimiter's length must be 1.");
    }
    this.delimiter = delimiter;
  }

  /**
   * Returns the character to quote a field.
   * 
   * @return the character to quote a field
   */
  public String getQuote() {
    return quote;
  }

  /**
   * Sets the character to quote a field.
   * 
   * @param quote the character to quote a field
   */
  public void setQuote(String quote) {
    if (quote.length() != 1) {
      throw new IllegalArgumentException("quote character's length must be 1.");
    }
    this.quote = quote;
  }

  /**
   * Returns the character to escape quote characters. If this character equals '\0', quote
   * characters must be represented by a pair of quote characters.
   * 
   * @return the character to escape quote characters
   */
  public String getEscape() {
    return escape;
  }

  /**
   * Sets the character to escape quote characters. If this character equals '\0', quote characters
   * must be represented by a pair of quote characters.
   * 
   * @param escape the character to escape quote characters
   */
  public void setEscape(String escape) {
    if (escape.length() != 1) {
      throw new IllegalArgumentException("escape character's length must be 1.");
    }
    this.escape = escape;
  }

  /**
   * Returns the charset that is used for saving file.
   * 
   * @return the charset
   */
  public String getCharset() {
    return charset;
  }

  /**
   * Sets the charset that is used for saving file.
   * 
   * @param charset the charset to set
   */
  public void setCharset(String charset) {
    if (!CharsetUtils.isAvailable(charset)) {
      throw new IllegalArgumentException("charset:" + charset);
    }
    this.charset = charset;
  }

  /**
   * Sets if the BOM (Byte Order Mark) will be inserted. If hasBOM is true and a UTF codec is used,
   * the BOM (Byte Order Mark) will be inserted before any data has been written to the file.
   * 
   * @param hasBOM true if the BOM (Byte Order Mark) will be inserted
   */
  public void setHasBOM(boolean hasBOM) {
    this.hasBOM = hasBOM;
  }

  /**
   * Returns if the BOM (Byte Order Mark) will be inserted. If hasBOM is true and a UTF codec is
   * used, the BOM (Byte Order Mark) will be inserted before any data has been written to the file.
   * 
   * @return true if the BOM (Byte Order Mark) will be inserted
   */
  public boolean hasBOM() {
    return hasBOM;
  }

  /**
   * Returns the newline character that is used for saving file.
   * 
   * @return the newline character
   */
  public String getNewlineCharacter() {
    return newlineCharacter;
  }

  /**
   * Sets the newline character that is used for saving file.
   * 
   * @param newlineCharacter the newline character to set
   */
  public void setNewlineCharacter(String newlineCharacter) {
    if (!CR.equals(newlineCharacter) && !LF.equals(newlineCharacter)
        && !CRLF.equals(newlineCharacter)) {
      throw new IllegalArgumentException("newlineCharacter:" + newlineCharacter);
    }
    this.newlineCharacter = newlineCharacter;
  }

  /**
   * Returns the rule which indicates how to apply quote to a value. The value is either of the
   * below.
   * <ul>
   * <li>{@link #NO_QUOTE}</li>
   * <li>{@link #QUOTES_ALL}</li>
   * <li>{@link #QUOTES_IF_NECESSARY}</li>
   * </ul>
   * 
   * @return the quoteOption
   */
  public int getQuoteOption() {
    return quoteOption;
  }

  /**
   * Sets the rule which indicates how to apply quote to a value. The value must be either of the
   * below.
   * <ul>
   * <li>{@link #NO_QUOTE}</li>
   * <li>{@link #QUOTES_ALL}</li>
   * <li>{@link #QUOTES_IF_NECESSARY}</li>
   * </ul>
   * 
   * @param quoteOption the quoteOption to set
   */
  public void setQuoteOption(int quoteOption) {
    if (quoteOption != NO_QUOTE && quoteOption != QUOTES_ALL && quoteOption != QUOTES_IF_NECESSARY) {
      throw new IllegalArgumentException("quoteOption:" + quoteOption);
    }
    this.quoteOption = quoteOption;
  }

  CsvMeta toCsvMeta() {
    CsvMeta csvMeta = new CsvMeta();
    csvMeta.setDelimiter(delimiter.charAt(0));
    csvMeta.setQuote(quote.charAt(0));
    csvMeta.setEscape(escape.charAt(0));
    csvMeta.setCharset(Charset.forName(charset));
    csvMeta.setHasBom(hasBOM);
    csvMeta.setNewlineCharacter(NewlineCharacter.of(newlineCharacter));
    CsvQuoteApplyRule quoteApplyRule;
    switch (quoteOption) {
      case NO_QUOTE:
        quoteApplyRule = CsvQuoteApplyRule.NO_QUOTE;
        break;
      case QUOTES_ALL:
        quoteApplyRule = CsvQuoteApplyRule.QUOTES_ALL;
        break;
      case QUOTES_IF_NECESSARY:
        quoteApplyRule = CsvQuoteApplyRule.QUOTES_IF_NECESSARY;
        break;
      default:
        throw new IllegalStateException("" + quoteOption);
    }
    csvMeta.setQuoteOption(quoteApplyRule);
    return csvMeta;
  }
}
