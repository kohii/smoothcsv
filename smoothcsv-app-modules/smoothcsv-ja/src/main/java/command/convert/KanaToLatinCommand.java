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
package command.convert;

import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.Transliterator;
import com.smoothcsv.core.command.ConvertCommandBase;

/**
 * @author kohii
 *
 */
public class KanaToLatinCommand extends ConvertCommandBase {

  private Transliterator transliterator1 = Transliterator.getInstance("Hiragana-Latin");
  private Transliterator transliterator2 = Transliterator.getInstance("Katakana-Latin");

  @Override
  protected String convert(String val) {
    if (StringUtils.isEmpty(val)) {
      return val;
    }
    return transliterator1.transliterate(transliterator2.transliterate(val));
  }
}
