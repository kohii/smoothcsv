/*
 * Copyright 2014 kohii.
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

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.condition.Condition;

/**
 * @author kohii
 *
 */
public class CommandDef {
  private final String commandId;
  private Command command;
  private final Condition enableWhen;
  private Boolean enabled;

  public CommandDef(String commandId, Command instance, Condition enableWhen) {
    this.commandId = commandId;
    this.command = instance;
    this.enableWhen = enableWhen;
  }

  public CommandDef(String commandId, Condition enableWhen) {
    this.commandId = commandId;
    this.enableWhen = enableWhen;
  }

  /**
   * @param commandIds
   * @return the command
   */
  public Command getCommand() {
    if (command == null) {
      try {

        @SuppressWarnings("unchecked")
        Class<? extends Command> commandClass =
            (Class<? extends Command>) Class.forName(createClassName(commandId));
        command = commandClass.newInstance();

      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        throw new UnexpectedException(e);
      }
    }
    return command;
  }

  /**
   * @return the enableWhen
   */
  public Condition getEnableWhen() {
    return enableWhen;
  }

  public boolean isEnabled() {
    if (enabled != null && !enabled) {
      return false;
    }
    Condition enableWhen = getEnableWhen();
    return enableWhen == null ? true : enableWhen.getValue();
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * @return the commandId
   */
  public String getCommandId() {
    return commandId;
  }

  private static String createClassName(String commandId) {
    StringBuilder buf = new StringBuilder("command.");
    int sepIndex = commandId.indexOf(':');
    buf.append(commandId.substring(0, sepIndex).replace('-', '_'));
    buf.append('.');
    buf.append(StringUtils.capitalize(commandId.substring(sepIndex + 1)));
    buf.append("Command");
    return buf.toString();
  }
}
