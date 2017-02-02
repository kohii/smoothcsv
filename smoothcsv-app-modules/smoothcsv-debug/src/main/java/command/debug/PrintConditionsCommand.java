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
package command.debug;

import java.util.Set;

import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.condition.Conditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kohii
 */
public class PrintConditionsCommand extends Command {

  static Logger LOG = LoggerFactory.getLogger(PrintConditionsCommand.class);

  @Override
  public void run() {
    Set<String> conditions = Conditions.getConditionNames();
    StringBuilder sb = new StringBuilder();
    for (String s : conditions) {
      sb.append(s);
      sb.append("\n");
    }
    LOG.debug(sb.toString());

  }
}
