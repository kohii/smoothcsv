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
package com.smoothcsv.debug.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.ConditionPool;

/**
 *
 * @author kohii
 */
public class PrintConditionsCommand extends Command {

  static Logger LOG = LoggerFactory.getLogger(PrintConditionsCommand.class);

  @Override
  public void run() {
    List<Condition> conditions = ConditionPool.instance().getAll();
    StringBuilder sb = new StringBuilder();
    for (Condition c : conditions) {
      sb.append(c.getName());
      sb.append("\t");
      sb.append(c.getValue());
      sb.append("\n");
    }
    LOG.debug(sb.toString());

  }
}
