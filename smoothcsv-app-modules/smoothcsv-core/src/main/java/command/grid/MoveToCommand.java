/*
 * Copyright 2015 kohii.
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
package command.grid;

import java.util.Map;

/**
 * @author kohii
 *
 */
public class MoveToCommand extends GridSheetSelectCommand {

  public MoveToCommand(Map<String, Object> options) {
    super(getDirectionX(options), getDirectionY(options), getExtend(options));
  }

  private static int getDirectionX(Map<String, Object> options) {
    Object d = options.get("to");
    if (d != null) {
      switch (d.toString()) {
        case "firstcell":
        case "firstcolumn":
          return TO_FIRST;
        case "lastcell":
        case "lastcolumn":
          return TO_LAST;
        default:
          break;
      }
    }
    return 0;
  }

  private static int getDirectionY(Map<String, Object> options) {
    Object d = options.get("to");
    if (d != null) {
      switch (d.toString()) {
        case "firstcell":
        case "firstrow":
          return TO_FIRST;
        case "lastcell":
        case "lastrow":
          return TO_LAST;
        default:
          break;
      }
    }
    return 0;
  }

  private static boolean getExtend(Map<String, Object> options) {
    Object e = options.get("extend");
    if (e != null) {
      if (e instanceof Boolean) {
        return (boolean) e;
      } else {
        return Boolean.valueOf(e.toString());
      }
    }
    return false;
  }
}
