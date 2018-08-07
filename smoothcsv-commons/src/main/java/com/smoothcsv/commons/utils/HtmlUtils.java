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
package com.smoothcsv.commons.utils;

/**
 * @author kohii
 */
public class HtmlUtils {

  public static String escapeHtml(String text) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    boolean cr = false;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
        case '&':
          sb.append("&amp;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '"':
          sb.append("&quot;");
          break;
        case '\r':
          sb.append("<br>");
          break;
        case '\n':
          if (!cr) {
            sb.append("<br>");
          }
          break;
        default:
          sb.append(text.charAt(i));
          break;
      }
      cr = c == '\r';
    }
    return sb.toString();
  }

  public static String createLinkHtml(String url, String name) {
    return "<html><a href=\"" + url + "\">" + escapeHtml(name) + "</a></html>";
  }
}
