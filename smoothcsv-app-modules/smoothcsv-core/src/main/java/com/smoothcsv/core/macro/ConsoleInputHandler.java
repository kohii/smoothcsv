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

import java.util.function.Consumer;

import com.smoothcsv.core.macro.bridge.ConsoleBridge;
import org.mozilla.javascript.NativeJavaObject;

/**
 * @author kohii
 */
public class ConsoleInputHandler implements Consumer<String> {

  @Override
  public void accept(String inputText) {
    if (inputText == null || inputText.isEmpty()) {
      return;
    }
    Object result = SCAppMacroRuntime.getMacroRuntime().execute(new Macro(inputText));
    if ((result instanceof NativeJavaObject)
        && ((NativeJavaObject) result).unwrap() == ConsoleBridge.CONSOLE_LOG_INVOKED) {
      // do nothing
    } else {
      ConsoleBridge.log(new Object[]{result});
    }
  }
}
