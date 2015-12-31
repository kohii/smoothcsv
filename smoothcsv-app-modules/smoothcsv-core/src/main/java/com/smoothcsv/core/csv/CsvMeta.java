/*
 * Copyright 2015 kohii
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
package com.smoothcsv.core.csv;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.CharsetUtils;
import com.smoothcsv.csv.CsvProperties;
import com.smoothcsv.csv.CsvQuoteApplyRule;
import com.smoothcsv.csv.NewlineCharacter;
import com.smoothcsv.framework.util.SCBundle;

/**
 *
 * @author kohii
 */
public class CsvMeta extends CsvProperties implements Cloneable {

  @Getter
  @Setter
  private transient boolean charsetNotDetermined = false;

  @Getter
  @Setter
  private transient boolean delimiterNotDetermined = false;

  @Getter
  @Setter
  private transient boolean quoteNotDetermined = false;

  @Getter
  @Setter
  private transient boolean newlineCharNotDetermined = false;

  @Setter
  @Getter
  private Charset charset = CharsetUtils.getDefaultCharset();

  @Setter
  private boolean hasBom = false;

  @Setter
  @Getter
  private CsvQuoteApplyRule quoteOption = CsvQuoteApplyRule.QUOTES_ALL;

  @Setter
  @Getter
  private NewlineCharacter newlineCharacter = NewlineCharacter.DEFAULT;

  public boolean hasBom() {
    return hasBom;
  }

  @Override
  public CsvMeta clone() {
    try {
      return (CsvMeta) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new UnexpectedException(e);
    }
  }

  public String toDisplayString(int rowSize, int columnSize) {
    StringBuilder sb = new StringBuilder();
    sb.append(toDisplayString());
    sb.append(SCBundle.get("key.rowCount")).append('=').append(rowSize).append('\n');
    sb.append(SCBundle.get("key.rowCount")).append('=').append(columnSize);
    return sb.toString();
  }

  public String toDisplayString() {
    StringBuilder sb = new StringBuilder();
    appendKeyValue(sb, SCBundle.get("key.encoding"),
        CharsetUtils.getDisplayName(getCharset(), hasBom()));
    appendKeyValue(sb, SCBundle.get("key.newlineCharacter"), getNewlineCharacter().toString());
    appendKeyValue(sb, SCBundle.get("key.delimiterChar"), getDelimiter());
    appendKeyValue(sb, SCBundle.get("key.quoteChar"), getQuote());
    if (getEscape() != '\0') {
      appendKeyValue(sb, SCBundle.get("key.escapeChar"), getEscape());
    }
    return sb.toString();
  }

  public String csvPropsToDisplayString() {
    StringBuilder sb = new StringBuilder();
    appendKeyValue(sb, SCBundle.get("key.delimiterChar"), getDelimiter());
    appendKeyValue(sb, SCBundle.get("key.quoteChar"), getQuote());
    if (getEscape() != '\0') {
      appendKeyValue(sb, SCBundle.get("key.escapeChar"), getEscape());
    }
    return sb.toString();
  }

  private static void appendKeyValue(StringBuilder sb, String key, String value) {
    sb.append(key).append('=').append(value).append('\n');
  }

  private static void appendKeyValue(StringBuilder sb, String key, char value) {
    sb.append(key).append('=');
    if (value == ',') {
      sb.append(SCBundle.get("key.comma"));
    } else if (value == '\t') {
      sb.append(SCBundle.get("key.tab"));
    } else if (value == '\0') {
      sb.append("None");
    } else {
      sb.append('[').append(value).append(']');
    }
    sb.append('\n');
  }
}
