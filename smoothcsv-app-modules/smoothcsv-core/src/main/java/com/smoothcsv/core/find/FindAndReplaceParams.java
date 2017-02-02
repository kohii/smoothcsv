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
package com.smoothcsv.core.find;

import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.core.constants.CoreSessionKeys;
import com.smoothcsv.framework.setting.Session;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kohii
 */
public class FindAndReplaceParams {

  @Getter
  private static FindAndReplaceParams instance = new FindAndReplaceParams();

  private List<Runnable> conditionChangeListeners = new ArrayList<>(3);

  @Getter
  private String findWhat;

  @Getter
  private boolean caseSensitive;

  @Getter
  private boolean useRegex;

  @Getter
  private boolean matchWholeCell;

  @Getter
  private boolean inSelection;

  @Getter
  private Orientation orientation;

  @Getter
  @Setter
  private String replaceWith;

  @Getter
  private boolean preserveCase;

  @Getter
  @Setter
  private boolean inSelectionCheckboxEnabled;

  private Regex regex;

  private FindAndReplaceParams() {
    Session settings = Session.getSession();

    caseSensitive = settings.getBoolean(CoreSessionKeys.CASE_SENSITIVE, false);
    useRegex = settings.getBoolean(CoreSessionKeys.USE_REGEX, false);
    matchWholeCell = settings.getBoolean(CoreSessionKeys.MATCH_WHOLE_CELL, false);
    inSelection = settings.getBoolean(CoreSessionKeys.IN_SELECTION, true);
    orientation =
        Orientation.valueOf(settings.get(CoreSessionKeys.DIRECTION, Orientation.HORIZONTAL.name()));
    preserveCase = settings.getBoolean(CoreSessionKeys.PRESERVE_CASE, false);
  }

  public void setFindWhat(String findWhat) {
    this.findWhat = findWhat;
    fireConditionChange();
  }

  public void setCaseSensitive(boolean caseSensitive) {
    Session.getSession().save(CoreSessionKeys.CASE_SENSITIVE, caseSensitive);
    this.caseSensitive = caseSensitive;
    fireConditionChange();
  }

  public void setUseRegex(boolean useRegex) {
    Session.getSession().save(CoreSessionKeys.USE_REGEX, useRegex);
    this.useRegex = useRegex;
    fireConditionChange();
  }

  public void setMatchWholeCell(boolean matchWholeCell) {
    Session.getSession().save(CoreSessionKeys.MATCH_WHOLE_CELL, matchWholeCell);
    this.matchWholeCell = matchWholeCell;
    fireConditionChange();
  }

  public void setInSelection(boolean inSelection) {
    Session.getSession().save(CoreSessionKeys.IN_SELECTION, inSelection);
    this.inSelection = inSelection;
  }

  public void setDirection(Orientation direction) {
    Session.getSession().save(CoreSessionKeys.DIRECTION, direction);
    this.orientation = direction;
  }

  public void setPreserveCase(boolean preserveCase) {
    Session.getSession().save(CoreSessionKeys.PRESERVE_CASE, preserveCase);
    this.preserveCase = preserveCase;
  }

  private void fireConditionChange() {
    regex = null;
    for (Runnable l : conditionChangeListeners) {
      l.run();
    }
  }

  public void addConditionChangeListener(Runnable l) {
    conditionChangeListeners.add(l);
  }

  public Regex getRegex() {
    if (!useRegex) {
      throw new IllegalStateException();
    }
    if (regex == null) {
      regex = new Regex(findWhat, caseSensitive);
    }
    return regex;
  }
}
