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
package com.smoothcsv.core.macro.apiimpl;

import com.smoothcsv.core.macro.api.Command;
import com.smoothcsv.framework.command.CommandRegistry;
import lombok.Getter;

/**
 * @author kohii
 */
public class CommandImpl extends APIBase implements Command {

  @Getter
  private static final Command instance = new CommandImpl();

  private CommandImpl() {
  }

  @Override
  public boolean run(String id) {
    return CommandRegistry.instance().runCommand(id);
  }
}
