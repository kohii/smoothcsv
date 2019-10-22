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
import java.nio.charset.Charset;

import com.smoothcsv.commons.encoding.FileEncoding;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author kohii
 */
@Slf4j
public class CharsetUtils {

  public static final byte[] UTF8_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
  private static final byte[] UTF16BE_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0xFE, (byte) 0xFF};
  private static final byte[] UTF16LE_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0xFF, (byte) 0xFE};
  private static final byte[] UTF32BE_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFE, (byte) 0xFF};
  private static final byte[] UTF32LE_BYTE_ORDER_MARK_BYTES = new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00};
  private static final char UTF8_BYTE_ORDER_MARK_CHAR = 0xFEFF;

  public static boolean isUtf8Bom(char c) {
    return c == UTF8_BYTE_ORDER_MARK_CHAR;
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

  public static FileEncoding detect(File file) {
    return detect(file, Integer.MAX_VALUE);
  }

  public static FileEncoding detect(File file, int limitByteSize) {

    String charset = null;
    byte[] first4Bytes = null;

    try (FileInputStream fis = new FileInputStream(file)) {
      final int byteBufferSize = 4096;
      byte[] buf = new byte[byteBufferSize];

      boolean isFirstLine = true;
      UniversalDetector detector = new UniversalDetector(null);
      int readSize = 0;
      int nread;
      while ((nread = fis.read(buf)) > 0 && !detector.isDone() && readSize < limitByteSize) {
        detector.handleData(buf, 0, nread);
        if (isFirstLine) {
          isFirstLine = false;

          if (ArrayUtils.startsWith(buf, UTF8_BYTE_ORDER_MARK_BYTES)) {
            return FileEncoding.UTF_8_WITH_BOM;
          }
          if (ArrayUtils.startsWith(buf, UTF16BE_BYTE_ORDER_MARK_BYTES)) {
            return FileEncoding.UTF_16BE_WITH_BOM;
          }
          if (ArrayUtils.startsWith(buf, UTF16LE_BYTE_ORDER_MARK_BYTES)) {
            return FileEncoding.UTF_16LE_WITH_BOM;
          }
          if (ArrayUtils.startsWith(buf, UTF32BE_BYTE_ORDER_MARK_BYTES)) {
            return FileEncoding.UTF_32BE_WITH_BOM;
          }
          if (ArrayUtils.startsWith(buf, UTF32LE_BYTE_ORDER_MARK_BYTES)) {
            return FileEncoding.UTF_32LE_WITH_BOM;
          }
        }
        readSize += nread;
      }
      detector.dataEnd();
      charset = detector.getDetectedCharset();
    } catch (Exception e) {
      log.error("Cannot detect file encoding", e);
    }

    if (charset == null) {
      return FileEncoding.getDefault();
    }
    if (equals(charset, "Shift_JIS")) {
      charset = "MS932";
    }

    return FileEncoding.of(Charset.forName(charset)).orElse(FileEncoding.getDefault());
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
}
