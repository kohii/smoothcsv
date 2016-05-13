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

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.exception.AppException;
import lombok.Getter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

import java.io.File;
import java.io.IOException;

public class Macro {

  @Getter
  private File file;

  private String source;

  private Script script;

  private long lastModified = 0L;

  public Macro(String source) {
    this.source = source;
  }

  public Macro(File file) {
    this.file = file;
  }

  public String getSource() {
    if (file != null) {
      if (!FileUtils.canRead(file)) {
        throw new AppException("WSCC0002", file.getAbsolutePath());
      } else if (file.lastModified() != lastModified) {
        lastModified = file.lastModified();
        try {
          source = FileUtils.readAll(file, "UTF-8");
        } catch (IOException e) {
          throw new AppException("WSCC0003", source);
        }
      }
    }
    return source;
  }

  public Script getScript(Context context) {
    String oldSource = source;
    String newSource = getSource();
    if (script == null || oldSource != source) {
      script = context.compileString(newSource, getName(), 1, null);
    }
    return script;
  }

  public void clear() {
    if (file != null) {
      source = null;
      lastModified = 0L;
    }
    script = null;
  }

  public String getName() {
    return file == null ? "(noname)" : file.getName();
  }
}
