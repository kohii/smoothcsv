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
package com.smoothcsv.swing.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import lombok.Getter;


public class History {

  private static final String CHARSET = "UTF-8";

  private static final int DEFAULT_MAX_SIZE = 15;

  @Getter
  private static final List<History> allHistories = new ArrayList<>();

  private LinkedList<String> values;

  private final File file;

  private final boolean autoFlush;

  private int maxSize;

  private final boolean disallowEmpty;

  private List<Consumer<List<String>>> listeners;

  public History(File file, boolean autoFlush) {
    this(file, autoFlush, DEFAULT_MAX_SIZE);
  }

  public History(File file, boolean autoFlush, int maxSize) {
    this(file, autoFlush, maxSize, false);
  }

  public History(File file, boolean autoFlush, int maxSize, boolean disallowEmpty) {
    this.maxSize = maxSize;
    this.autoFlush = autoFlush;
    this.file = file;
    this.disallowEmpty = disallowEmpty;
    if (!file.exists() || !file.canRead()) {
      values = new LinkedList<>();
    } else {
      try {
        values = new LinkedList<>(FileUtils.read(file, CHARSET));
        if (disallowEmpty) {
          for (Iterator<String> it = values.iterator(); it.hasNext(); ) {
            if (it.next().isEmpty()) {
              it.remove();
            }
          }
        }
      } catch (IOException e) {
        values = new LinkedList<>();
      }
      trimToMaxSize();
    }
    allHistories.add(this);
  }

  public boolean put(String value) {
    if (disallowEmpty && value.isEmpty()) {
      return false;
    }
    boolean replaced = values.remove(value);
    values.addFirst(value);
    trimToMaxSize();
    if (autoFlush) {
      flush();
    }
    if (listeners != null) {
      for (Consumer<List<String>> listener : listeners) {
        listener.accept(values);
      }
    }
    return !replaced;
  }

  public void clear() {
    values.clear();
  }

  public void remove(String s) {
    values.remove(s);
  }

  public void flush() {
    FileUtils.ensureWritable(file);
    try {
      FileUtils.write(values, file, CHARSET);
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public int size() {
    return values.size();
  }

  @SuppressWarnings("unchecked")
  public List<String> getAll() {
    return new ArrayList<>(values);
  }

  public String get(int i) {
    return values.get(i);
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
    trimToMaxSize();
  }

  public void addListener(Consumer<List<String>> l) {
    if (listeners == null) {
      listeners = new ArrayList<>(2);
    }
    listeners.add(l);
  }

  public void removeListener(Consumer<List<String>> l) {
    if (listeners != null) {
      listeners.remove(l);
    }
  }

  public boolean flushesAutomatically() {
    return autoFlush;
  }

  private void trimToMaxSize() {
    int over = values.size() - maxSize;
    if (0 < over) {
      values.subList(0, over).clear();
    }
  }
}
