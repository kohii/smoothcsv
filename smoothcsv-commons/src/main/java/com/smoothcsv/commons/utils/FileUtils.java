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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.csv.prop.LineSeparator;

public class FileUtils {

  public static void ensureWritable(File file) {
    if (!file.exists()) {
      return;
    }
    if (!file.canWrite()) {
      file.setWritable(true);
    }
  }

  public static void ensureDirectoryExists(File dir) {
    if (!dir.exists() || !dir.isDirectory()) {
      if (!dir.mkdirs()) {
        throw new UnexpectedException("Could not make a directory. name=" + getCanonicalPath(dir));
      }
    }
  }

  public static boolean canRead(File f) {
    return f.exists() && f.isFile() && f.canRead();
  }

  public static void write(List<String> content, File file, String charset) throws IOException {
    try (Writer writer =
             new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {
      String lf = LineSeparator.DEFAULT.stringValue();
      for (int i = 0; i < content.size(); i++) {
        writer.write(content.get(i));
        writer.write(lf);
      }
      writer.flush();
    }
  }

  public static void write(String content, File file, String charset) throws IOException {
    try (Writer writer =
             new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))) {
      writer.write(content);
      writer.flush();
    }
  }

  public static void append(List<String> content, File file, String charset) throws IOException {
    if (!file.exists()) {
      write(content, file, charset);
    } else {
      Files.write(file.toPath(), content, Charset.forName(charset), StandardOpenOption.APPEND);
    }
  }

  public static List<String> read(String filePath, String charset) throws IOException {
    return read(new File(filePath), charset);
  }

  public static List<String> read(File file, String charset) throws IOException {
    return Files.readAllLines(file.toPath(), Charset.forName(charset));
  }

  public static List<String> read(File file, String charset, final int limit) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8"))) {
      return reader.lines().limit(limit).collect(Collectors.toList());
    }
  }

  public static String readAll(File file, String charset) throws IOException {
    byte[] b = Files.readAllBytes(file.toPath());
    return charset == null ? new String(b) : new String(b, charset);
  }

  public static String getCanonicalPath(File file) {
    try {
      return file.getCanonicalPath();
    } catch (IOException e) {
      return file.getAbsolutePath();
    }
  }

  public static File getLatestFileFromDir(File dir) {
    return getLatestFileFromDir(dir, null);
  }

  public static File getLatestFileFromDir(File dir, File except) {
    File[] files = dir.listFiles();
    if (files == null || files.length == 0) {
      return null;
    }

    File lastModifiedFile = null;
    for (int i = 0; i < files.length; i++) {
      if (files[i].equals(except)) {
        continue;
      }
      if (lastModifiedFile == null || lastModifiedFile.lastModified() < files[i].lastModified()) {
        lastModifiedFile = files[i];
      }
    }
    return lastModifiedFile;
  }
}
