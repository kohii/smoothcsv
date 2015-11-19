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
package com.smoothcsv.core.find;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.framework.setting.Settings;

/**
 * @author kohii
 *
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

  private final Settings settings;

  private FindAndReplaceParams() {
    settings = SettingManager.getSettings(AppSettingKeys.Find.$);

    caseSensitive = settings.getBoolean(AppSettingKeys.Find.CASE_SENSITIVE);
    useRegex = settings.getBoolean(AppSettingKeys.Find.USE_REGEX);
    matchWholeCell = settings.getBoolean(AppSettingKeys.Find.MATCH_WHOLE_CELL);
    inSelection = settings.getBoolean(AppSettingKeys.Find.IN_SELECTION);
    orientation = Orientation.valueOf(settings.get(AppSettingKeys.Find.DIRECTION));
    preserveCase = settings.getBoolean(AppSettingKeys.Find.PRESERVE_CASE);
  }

  public void setFindWhat(String findWhat) {
    this.findWhat = findWhat;
    fireConditionChange();
  }

  public void setCaseSensitive(boolean caseSensitive) {
    settings.save(AppSettingKeys.Find.CASE_SENSITIVE, caseSensitive);
    this.caseSensitive = caseSensitive;
    fireConditionChange();
  }

  public void setUseRegex(boolean useRegex) {
    settings.save(AppSettingKeys.Find.USE_REGEX, useRegex);
    this.useRegex = useRegex;
    fireConditionChange();
  }

  public void setMatchWholeCell(boolean matchWholeCell) {
    settings.save(AppSettingKeys.Find.MATCH_WHOLE_CELL, matchWholeCell);
    this.matchWholeCell = matchWholeCell;
    fireConditionChange();
  }

  public void setInSelection(boolean inSelection) {
    settings.save(AppSettingKeys.Find.IN_SELECTION, inSelection);
    this.inSelection = inSelection;
  }

  public void setDirection(Orientation direction) {
    settings.save(AppSettingKeys.Find.DIRECTION, direction);
    this.orientation = direction;
  }

  public void setPreserveCase(boolean preserveCase) {
    settings.save(AppSettingKeys.Find.PRESERVE_CASE, preserveCase);
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
