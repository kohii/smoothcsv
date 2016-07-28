package com.smoothcsv.core.util;

import java.util.ResourceBundle;

/**
 * @author kohii
 */
public class AppUtils {

  public static String createUrl(String path) {
    if (path.startsWith("/")) {
      throw new IllegalArgumentException(path);
    }
    ResourceBundle bundle = ResourceBundle.getBundle("application");
    String url = bundle.getString("site.url");
    return url + (url.endsWith("/") ? "" : "/") + path;
  }
}
