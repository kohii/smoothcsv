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
package com.smoothcsv.core.macro;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 *
 */
@Getter
@Setter
public class MacroInfo implements Comparable<MacroInfo> {

  private File file;
  private String filePath;

  public MacroInfo(File file) {
    this.file = file;
  }

  public MacroInfo(String filepath) {
    this(new File(filepath));
  }

  public String getFileName() {
    return file.getName();
  }

  public String getFilePath() {
    if (filePath == null) {
      try {
        filePath = file.getCanonicalPath();
      } catch (IOException e) {
        filePath = file.getAbsolutePath();
      }
    }
    return filePath;
  }

  @Override
  public int compareTo(MacroInfo o) {
    return getFileName().compareToIgnoreCase(o.getFileName());
  }
}
