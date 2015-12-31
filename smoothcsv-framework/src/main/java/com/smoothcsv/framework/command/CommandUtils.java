/*
 * Copyright 2015 kohii
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
package com.smoothcsv.framework.command;

import com.smoothcsv.framework.error.ErrorHandlerFactory;


/**
 *
 * @author kohii
 */
public class CommandUtils {

  public static void runCommand(Command command) {
    try {
      command.execute();
    } catch (Throwable t) {
      ErrorHandlerFactory.getErrorHandler().handle(t);
    }
  }

  static String createCommandId(Class<? extends Command> commandClass) {
    String className = commandClass.getSimpleName();
    if (className.endsWith("Command")) {
      return stripSuffix(className, "Command");
    }
    throw new IllegalArgumentException("The name of command classes must ends width 'Command'.");
  }

  private static String stripSuffix(String className, String suffix) {
    StringBuilder buf = new StringBuilder(className);
    int len = className.length();
    buf.delete(len - suffix.length(), len);
    buf.setCharAt(0, Character.toLowerCase(className.charAt(0)));
    return buf.toString();
  }
}
