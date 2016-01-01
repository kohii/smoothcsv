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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author kohii
 *
 */
public class CommandActionListener implements ActionListener {

  private final String commandId;

  public CommandActionListener(String commandId) {
    this.commandId = commandId;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    CommandRegistry.instance().runCommand(commandId);
  }
}
