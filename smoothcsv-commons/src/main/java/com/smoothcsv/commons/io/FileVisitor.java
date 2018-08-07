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
package com.smoothcsv.commons.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.smoothcsv.commons.functions.FileHandler;

/**
 * @author kohii
 */
public class FileVisitor {

  private final File file;

  private boolean containsHiddenFiles = false;

  private boolean containsSubDirectories = false;

  public FileVisitor(File file) {
    this.file = file;
  }

  public void setContainsHiddenFiles(boolean containsHiddenFiles) {
    this.containsHiddenFiles = containsHiddenFiles;
  }

  public void setContainsSubDirectories(boolean containsSubDirectories) {
    this.containsSubDirectories = containsSubDirectories;
  }

  public void visit(FileHandler handler) throws IOException {

    if (!file.exists() || !file.isDirectory()) {
      throw new FileNotFoundException(file.toString());
    }
    visit(handler, file);
  }

  protected void visit(FileHandler handler, File directory) throws IOException {
    File[] files = directory.listFiles();
    for (File f : files) {
      if (!containsHiddenFiles && f.isHidden()) {
        continue;
      }
      if (f.isFile()) {
        handler.handle(f);
      } else if (f.isDirectory()) {
        visit(handler, f);
      }
    }
  }
}
