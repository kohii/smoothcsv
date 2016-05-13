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
package com.smoothcsv.framework.selector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author kohii
 */
public class SelectorFactory {

  private static final int SELECTOR_TYPE_TYPE = 0;
  private static final int SELECTOR_TYPE_CLASS = 1;
  private static final int SELECTOR_TYPE_PSEUDO = 2;

  private static Map<String, CssSelector> cache = new HashMap<>();

  public static CssSelector parseQuery(String query) {
    CssSelector selector = cache.get(query);
    if (selector != null) {
      return selector;
    }

    if (query.indexOf(',') >= 0) {
      StringTokenizer st = new StringTokenizer(query, ",");
      List<CssSelector> selectors = new ArrayList<>(3);
      while (st.hasMoreTokens()) {
        String s = st.nextToken().trim();
        selectors.add(parseQuery(s));
      }
      return new OrSelector(selectors.toArray(new CssSelector[selectors.size()]));
    }

    List<CssSelector> selectors = new ArrayList<>(3);
    int type = SELECTOR_TYPE_TYPE;
    StringBuilder sb = new StringBuilder();
    for (int i = 0, len = query.length(); i < len; i++) {
      char c = query.charAt(i);
      switch (c) {
        case '.':
          if (sb.length() > 0) {
            selectors.add(createSelector(sb, type));
            sb.setLength(0);
          }
          type = SELECTOR_TYPE_CLASS;
          break;
        case ':':
          if (sb.length() > 0) {
            selectors.add(createSelector(sb, type));
            sb.setLength(0);
          }
          if (i + 5 < len && query.substring(i + 1, i + 4).equals("not")) {
            int endOfNotQuery = query.indexOf(')', i + 5);
            selectors.add(new NotSelector(parseQuery(query.substring(i + 5, endOfNotQuery))));
            i = endOfNotQuery;
          }
          type = SELECTOR_TYPE_PSEUDO;
          break;
        default:
          sb.append(c);
          break;
      }
    }
    if (sb.length() > 0) {
      selectors.add(createSelector(sb, type));
    }
    if (selectors.size() == 1) {
      return selectors.get(0);
    }
    return new AndSelector(selectors.toArray(new CssSelector[selectors.size()]));
  }

  private static CssSelector createSelector(StringBuilder sb, int type) {
    String text = sb.toString();
    switch (type) {
      case SELECTOR_TYPE_TYPE:
        return new TypeSelector(text);
      case SELECTOR_TYPE_CLASS:
        return new StyleClassSelector(sb.toString());
      case SELECTOR_TYPE_PSEUDO:
        if ("focus".equals(text)) {
          return new FocusOwnerSelector();
        }
        return new PseudoClassSelector(text);
      default:
        throw new IllegalArgumentException("type=" + type + ", text=" + text);
    }
  }
}
