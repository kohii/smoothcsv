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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.exception.CommandNotFoundException;

/**
 * @author kohii
 *
 */
public final class CommandRepository {

  private static CommandRepository commandRepository = new CommandRepository();

  /**
   * @return the instance
   */
  public static CommandRepository instance() {
    return commandRepository;
  }

  private final Map<String, CommandDef> commandDefs = new HashMap<>(300);

  public void register(String id, Command command) {
    register(id, command, null);
  }

  public void register(String id, Command command, Condition enableWhen) {
    commandDefs.put(id, new CommandDef(id, enableWhen, command));
  }

  public void register(String id, Condition enableWhen) {
    commandDefs.put(id, new CommandDef(id, enableWhen));
  }

  public void register(String id) {
    commandDefs.put(id, new CommandDef(id, null));
  }

  public void register(String id, Condition enableWhen, String ref) {
    commandDefs.put(id, new CommandDef(id, enableWhen, ref));
  }

  public boolean runCommand(String id) {
    try {
      CommandDef def = getDef(id);
      if (def.isEnabled()) {
        def.getCommand().execute();
        return true;
      }
    } catch (RuntimeException e) {
      ErrorHandlerFactory.getErrorHandler().handle(e);
    }
    return false;
  }

  public boolean runCommand(String id, Object... arguments) {
    try {
      CommandDef def = getDef(id);
      if (def.isEnabled()) {
        def.getCommand().execute(arguments);
        return true;
      }
    } catch (RuntimeException e) {
      ErrorHandlerFactory.getErrorHandler().handle(e);
    }
    return false;
  }


  public boolean runCommandIfExists(String id) {
    if (!contains(id)) {
      return false;
    }
    try {
      CommandDef def = getDef(id);
      if (def.isEnabled()) {
        def.getCommand().execute();
        return true;
      }
    } catch (RuntimeException e) {
      ErrorHandlerFactory.getErrorHandler().handle(e);
    }
    return false;
  }

  public boolean isEnabled(String id) {
    return getDef(id).isEnabled();
  }

  public boolean isValid(String id) {
    if (contains(id)) {
      return true;
    }
    if (id.indexOf(',') >= 0) {
      String[] ids = StringUtils.split(id, ',');
      for (String s : ids) {
        if (!contains(s)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public Command getCommand(String commandId) {
    CommandDef def = getDef(commandId);
    return def.isEnabled() ? def.getCommand() : null;
  }

  public Command getCommandOrNull(String commandId) {
    CommandDef def = commandDefs.get(commandId);
    if (def == null) {
      return null;
    }
    return def.isEnabled() ? def.getCommand() : null;
  }

  public CommandDef getDef(String commandId) {
    CommandDef ret = commandDefs.get(commandId);
    if (ret == null) {
      throw new CommandNotFoundException(commandId);
    }
    return ret;
  }

  public boolean contains(String id) {
    return commandDefs.containsKey(id);
  }

  public List<CommandDef> getEnabledCommands() {
    List<CommandDef> ret = new ArrayList<>();
    for (Entry<String, CommandDef> entry : this.commandDefs.entrySet()) {
      if (entry.getValue().isEnabled()) {
        ret.add(entry.getValue());
      }
    }
    Collections.sort(ret, new Comparator<CommandDef>() {
      @Override
      public int compare(CommandDef o1, CommandDef o2) {
        return o1.getCommandId().compareTo(o2.getCommandId());
      }
    });
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    commandDefs.forEach((id, commandDef) -> {
      String className = commandDef.getCommand().getClass().getName();
      buf.append(id).append('\t').append(className).append("\n");
    });
    return buf.toString();
  }
}
