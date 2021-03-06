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
package command.value_panel;

import java.io.IOException;
import java.util.Map;

import com.smoothcsv.commons.exception.IORuntimeException;
import com.smoothcsv.commons.utils.JsonUtils;
import com.smoothcsv.core.command.VisibleComponentCommandBase;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellValuePanel.ValuePanelTextArea;
import com.smoothcsv.framework.Env;

/**
 * @author kohii
 */
public class ActionCommand extends VisibleComponentCommandBase<ValuePanelTextArea> {

  private String actionName;

  public ActionCommand(Map<String, Object> options) {
    this(getActionFromOptions(options));
  }

  public ActionCommand(String actionName) {
    super("value-panel");
    this.actionName = actionName;
  }

  private static String getActionFromOptions(Map<String, Object> options) {
    if (Env.getOS() == Env.OS_MAC) {
      String action = (String) options.get("mac_action");
      if (action != null) {
        return action;
      }
    }
    if (Env.getOS() == Env.OS_WINDOWS) {
      String action = (String) options.get("win_action");
      if (action != null) {
        return action;
      }
    }
    String action = (String) options.get("action");
    if (action != null) {
      return action;
    }
    try {
      throw new IllegalArgumentException(JsonUtils.stringify(options));
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }

  @Override
  public void run(ValuePanelTextArea component) {
    component.invokeOriginalAction(actionName);
  }
}
