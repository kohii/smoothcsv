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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author kohii
 */
public class CharsetUtils {

  public static final Charset UTF8 = Charset.forName("UTF-8");

  private static final byte[] UTF8_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0xEF, (byte) 0xBB,
      (byte) 0xBF};
  private static final char UTF8_BYTE_ORDER_MARK_CHAR = 0xFEFF;

  public static final Charset SYSTEM_DEFAULT_CHARSET = Charset.defaultCharset();

  private static Charset defaultCharset = SYSTEM_DEFAULT_CHARSET;

  public static boolean isUtf8Bom(char c) {
    return c == UTF8_BYTE_ORDER_MARK_CHAR;
  }

  public static boolean startsWithUtf8Bom(String utf8text) {
    return StringUtils.isNotEmpty(utf8text) && isUtf8Bom(utf8text.charAt(0));
  }

  public static boolean startsWithUtf8Bom(byte[] bytes) {
    if (bytes.length >= 3) {
      for (int i = 0; i < UTF8_BYTE_ORDER_MARK_BYTES.length; i++) {
        if (UTF8_BYTE_ORDER_MARK_BYTES[i] != bytes[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public static CharsetInfo detect(File file) {
    return detect(file, Integer.MAX_VALUE);
  }

  public static CharsetInfo detect(File file, int limitByteSize) {
    CharsetInfo ret = new CharsetInfo();
    FileInputStream fis = null;

    try {
      fis = new FileInputStream(file);
      final int byteBufferSize = 2048;
      byte[] buf = new byte[byteBufferSize];

      boolean isFirstLine = true;
      UniversalDetector detector = new UniversalDetector(null);
      int readSize = 0;
      int nread;
      while ((nread = fis.read(buf)) > 0 && !detector.isDone() && readSize < limitByteSize) {
        detector.handleData(buf, 0, nread);
        if (isFirstLine) {
          isFirstLine = false;
          ret.hasBom = startsWithUtf8Bom(buf);
        }
        readSize += nread;
      }
      detector.dataEnd();
      ret.charset = detector.getDetectedCharset();
    } catch (Exception ignore) {
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException ignore) {
        }
      }
    }
    if (ret.charset == null) {
      ret.charset = defaultCharset.name();
    }
    if (equals(ret.charset, "windows-31J")) {
      ret.charset = "Shift_JIS";
    }
    ret.hasBom = ret.hasBom && equals(ret.charset, "UTF-8");
    return ret;
  }

  public static boolean isAvailable(String name) {
    try {
      return Charset.isSupported(name);
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean equals(String name0, String name1) {
    return equals(Charset.forName(name0), Charset.forName(name1));
  }

  public static boolean equals(Charset charset0, String name1) {
    return equals(charset0, Charset.forName(name1));
  }

  public static boolean equals(Charset charset0, Charset charset1) {
    return charset0.equals(charset1);
  }

  public static String getDisplayName(Charset charset, boolean hasBom) {
    if (equals(charset, "MS932")) {
      return "Shift_JIS";
    } else if (equals(charset, "UTF-8") && hasBom) {
      return charset.displayName() + "(with BOM)";
    } else {
      return charset.displayName();
    }
  }

  public static String convertSJIS(String charset) {
    if (equals(charset, "Shift_JIS") && isAvailable("MS932")) {
      return "MS932";
    } else {
      return charset;
    }
  }

  public static Charset getDefaultCharset() {
    return defaultCharset;
  }

  public static void setDefaultCharset(Charset defaultCharset) {
    CharsetUtils.defaultCharset = defaultCharset;
  }

  public static class CharsetInfo {

    public String charset;
    public boolean hasBom = false;
  }

}
