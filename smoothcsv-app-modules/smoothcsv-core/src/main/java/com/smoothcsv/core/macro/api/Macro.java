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
package com.smoothcsv.core.macro.api;

import com.smoothcsv.core.macro.SCAppMacroRuntime;
import com.smoothcsv.core.macro.apiimpl.APIBase;

import java.io.File;

/**
 * Represents a macro.
 *
 * @author kohii
 */
public class Macro extends APIBase {

  /**
   * Constructs Macro from the given pathname.
   *
   * @param pathname the pathname
   * @return a Macro instance
   */
  public static Macro fromFile(String pathname) {
    return new Macro(new File(pathname));
  }

  /**
   * Constructs Macro from the given source.
   *
   * @param source the source code
   * @return a Macro instance
   */
  public static Macro fromString(String source) {
    return new Macro(source);
  }

  private com.smoothcsv.core.macro.Macro macroImpl;

  private Macro(File file) {
    macroImpl = new com.smoothcsv.core.macro.Macro(file);
  }

  private Macro(String source) {
    macroImpl = new com.smoothcsv.core.macro.Macro(source);
  }

  /**
   * Executes the macro.
   */
  public void execute() {
    SCAppMacroRuntime.getMacroRuntime().execute(macroImpl);
  }
}
