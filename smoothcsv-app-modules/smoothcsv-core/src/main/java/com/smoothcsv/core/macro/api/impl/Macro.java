/*
 * Copyright 2015 kohii.
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
package com.smoothcsv.core.macro.api.impl;

import java.io.File;

import com.smoothcsv.core.macro.SCAppMacroRuntime;

/**
 * 
 * @author kohii
 *
 */
public class Macro extends APIBase {

  public static Macro fromFile(String pathname) {
    return new Macro(new File(pathname));
  }

  public static Macro fromString(String source) {
    return new Macro(source);
  }

  private com.smoothcsv.core.macro.Macro macro;

  public Macro(File file) {
    macro = new com.smoothcsv.core.macro.Macro(file);
  }

  public Macro(String source) {
    macro = new com.smoothcsv.core.macro.Macro(source);
  }

  public void execute() {
    SCAppMacroRuntime.getMacroRuntime().execute(macro);
  }
}
