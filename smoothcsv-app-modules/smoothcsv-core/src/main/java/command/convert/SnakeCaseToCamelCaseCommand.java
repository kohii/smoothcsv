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
package command.convert;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.ConvertCommandBase;

/**
 * @author kohii
 */
public class SnakeCaseToCamelCaseCommand extends ConvertCommandBase {

  @Override
  protected String convert(String s) {
    if (s == null) {
      return null;
    }
    s = s.toLowerCase();
    String[] array = StringUtils.split(s, '_', ' ', '-');
    if (array.length == 1) {
      return StringUtils.capitalize(s);
    }
    StringBuilder buf = new StringBuilder(s.length());
    for (int i = 0; i < array.length; ++i) {
      buf.append(StringUtils.capitalize(array[i]));
    }
    return buf.toString();
  }
}
