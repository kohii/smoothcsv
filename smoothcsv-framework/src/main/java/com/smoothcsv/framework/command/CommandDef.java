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
package com.smoothcsv.framework.command;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.JsonUtils;
import com.smoothcsv.framework.condition.Condition;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author kohii
 */
public class CommandDef {
  private final String commandId;
  private Command command;
  private final Condition enableWhen;
  private final String commandRef;
  private Boolean enabled;
  private String displayName;

  public CommandDef(String commandId, Condition enableWhen, Command instance) {
    this.commandId = commandId;
    this.enableWhen = enableWhen;
    this.command = instance;
    this.commandRef = null;
  }

  public CommandDef(String commandId, Condition enableWhen, String commandRef) {
    this.commandId = commandId;
    this.enableWhen = enableWhen;
    this.commandRef = commandRef;
  }

  public CommandDef(String commandId, Condition enableWhen) {
    this(commandId, enableWhen, (String) null);
  }

  /**
   * @return the command
   */
  @SuppressWarnings("unchecked")
  public Command getCommand() {
    if (command == null) {
      try {

        if (StringUtils.isEmpty(commandRef)) {
          Class<? extends Command> commandClass =
              (Class<? extends Command>) Class.forName(createCommandClassName(commandId));
          command = commandClass.newInstance();

        } else {
          String ref = StringUtils.isEmpty(commandRef) ? commandId : commandRef;
          int indexOfSpace = ref.indexOf(' ');
          String classPart;
          String argsPart;
          if (indexOfSpace >= 0) {
            classPart = ref.substring(0, indexOfSpace);
            argsPart = ref.substring(indexOfSpace + 1).trim();
          } else {
            classPart = ref;
            argsPart = null;
          }
          if (argsPart == null) {
            Class<? extends Command> commandClass =
                (Class<? extends Command>) Class.forName(createCommandClassName(ref));
            command = commandClass.newInstance();
          } else {
            Map<String, Object> argMap = JsonUtils.parse(argsPart, Map.class);
            Class<? extends Command> commandClass =
                (Class<? extends Command>) Class.forName(classPart);
            Constructor<? extends Command> constructor = commandClass.getConstructor(Map.class);
            command = constructor.newInstance(argMap);
          }
        }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
          | IOException | NoSuchMethodException | InvocationTargetException | RuntimeException e) {
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
    return enableWhen == null || enableWhen.getValue();
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

  public String getDisplayName() {
    if (displayName == null) {
      int splitterIndex = commandId.indexOf(':');
      String firstPart = commandId.substring(0, splitterIndex + 1);
      String secondPart = commandId.substring(splitterIndex + 1);

      StringBuilder sb = new StringBuilder();

      String[] firstParts = StringUtils.split(firstPart, '_');
      for (String part : firstParts) {
        sb.append(StringUtils.capitalize(part)).append(' ');
      }

      String[] secondParts = StringUtils.splitByCharacterTypeCamelCase(secondPart);
      for (int i = 0; i < secondParts.length; i++) {
        if (i != 0) {
          sb.append(' ');
        }
        String part = secondParts[i];
        sb.append(StringUtils.capitalize(part));
      }
      return sb.toString();
    }
    return displayName;
  }

  private static String createCommandClassName(String commandId) {
    StringBuilder buf = new StringBuilder("command.");
    int sepIndex = commandId.indexOf(':');
    buf.append(commandId.substring(0, sepIndex));
    buf.append('.');
    buf.append(commandId.substring(sepIndex + 1));
    buf.append("Command");
    return buf.toString();
  }
}
