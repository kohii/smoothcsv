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
package com.smoothcsv.core.filter;

import com.smoothcsv.commons.constants.OperatorSymbol;
import com.smoothcsv.csv.NewlineCharacter;

import java.util.List;

/**
 * @author kohii
 */
public class FilterConditionGroup {

  private OperatorSymbol operatorSymbol;

  private FilterConditionGroup[] filterConditionGroups;

  public FilterConditionGroup(OperatorSymbol operatorSymbol,
                              FilterConditionGroup[] conditionContainers) {
    this.operatorSymbol = operatorSymbol;
    this.filterConditionGroups = conditionContainers;
  }

  public FilterConditionGroup() {}

  public boolean matches(List<String> list) {
    if (filterConditionGroups == null || filterConditionGroups.length == 0) {
      return true;
    }
    if (operatorSymbol.equals(OperatorSymbol.AND)) {
      for (FilterConditionGroup c : filterConditionGroups) {
        boolean b = c.matches(list);
        if (!b) {
          return false;
        }
      }
      return true;
    } else {
      for (FilterConditionGroup c : filterConditionGroups) {
        boolean b = c.matches(list);
        if (b) {
          return true;
        }
      }
      return false;
    }
  }

  @Override
  public String toString() {
    if (hasChildren()) {
      return toString(0);
    } else {
      return "（検索条件がありません。）";
    }
  }

  public boolean hasChildren() {
    return filterConditionGroups != null && filterConditionGroups.length > 0;
  }

  protected String toString(int depth) {

    String indent = getIndentText(depth);

    StringBuilder ret = new StringBuilder();
    if (depth != 0) {
      ret.append('(').append(NewlineCharacter.DEFAULT.stringValue());
    }
    for (int i = 0; i < filterConditionGroups.length; i++) {
      if (i != 0) {
        ret.append(NewlineCharacter.DEFAULT.stringValue());
        ret.append(indent).append(operatorSymbol);
        ret.append(NewlineCharacter.DEFAULT.stringValue());
      }
      FilterConditionGroup group = filterConditionGroups[i];
      ret.append(indent).append(group.toString(depth + 1));
    }
    ret.append(NewlineCharacter.DEFAULT.stringValue());
    if (depth != 0) {
      ret.append(getIndentText(depth - 1)).append(')');
    }
    return ret.toString();
  }

  private String getIndentText(int depth) {
    String indent = "";
    for (int i = 0; i < depth; i++) {
      indent += "    ";
    }
    return indent;
  }
}
