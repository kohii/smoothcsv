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
package com.smoothcsv.core.macro;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.smoothcsv.commons.encoding.FileEncoding;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.macro.api.CsvProperties;
import com.smoothcsv.csv.prop.LineSeparator;
import com.smoothcsv.csv.prop.QuoteApplyRule;

/**
 * @author kohii
 */
public class MacroUtils {

  public static CsvMeta toCsvMeta(CsvProperties prop) {
    CsvMeta csvMeta = new CsvMeta();
    csvMeta.setDelimiter(prop.getDelimiter().charAt(0));
    csvMeta.setQuote(prop.getQuote().charAt(0));
    csvMeta.setEscape(prop.getEscape().charAt(0));
    csvMeta.setEncoding(FileEncoding.forName(prop.getEncoding()).get());
    csvMeta.setLineSeparator(LineSeparator.of(prop.getNewlineCharacter()));
    QuoteApplyRule quoteApplyRule;
    switch (prop.getQuoteOption()) {
      case CsvProperties.NO_QUOTE:
        quoteApplyRule = QuoteApplyRule.NO_QUOTE;
        break;
      case CsvProperties.QUOTES_ALL:
        quoteApplyRule = QuoteApplyRule.QUOTES_ALL;
        break;
      case CsvProperties.QUOTES_IF_NECESSARY:
        quoteApplyRule = QuoteApplyRule.QUOTES_IF_NECESSARY;
        break;
      default:
        throw new IllegalStateException("" + prop.getQuoteOption());
    }
    csvMeta.setQuoteOption(quoteApplyRule);
    csvMeta.setAppendsNewLineAtEOF(prop.appendsNewLineAtEOF());
    return csvMeta;
  }

  public static CsvProperties toCsvProperties(CsvMeta csvMeta) {
    try {
      Constructor<CsvProperties> constructor = CsvProperties.class.getConstructor(CsvMeta.class);
      return constructor.newInstance(csvMeta);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new UnexpectedException(e);
    }
  }
}
