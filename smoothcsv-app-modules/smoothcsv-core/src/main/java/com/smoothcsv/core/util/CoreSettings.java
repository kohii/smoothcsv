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
package com.smoothcsv.core.util;

import com.smoothcsv.framework.setting.Settings;
import lombok.Getter;

/**
 * @author kohii
 */
public class CoreSettings extends Settings {

  public static final String LANGUAGE = "language";
  public static final String STATUSBAR_VISIBLE = "statusbarVisible";
  public static final String TOOLBAR_VISIBLE = "toolbarVisible";
  public static final String VALUE_PANEL_VISIBLE = "valuePanelVisible";
  public static final String AUTO_UPDATE_CHECK = "autoUpdateCheck";
  public static final String ALERT_ON_OPENING_HUGE_FILE = "alertOnOpeningHugeFile";
  public static final String ALERT_THRESHOLD = "alertThreshold";
  public static final String HOW_TO_DETECT_PROPERTIES = "howToDetectProperties";
  public static final String DEFAULT_ROW_SIZE = "defaultRowSize";
  public static final String DEFAULT_COLUMN_SIZE = "defaultColumnSize";
  public static final String SIZE_OF_UNDOING = "sizeOfUndoing";
  public static final String VALUE_PANEL_HEIGHT = "valuePanelHeight";
  public static final String SHOW_EOL = "showEOL";
  public static final String SHOW_EOF = "showEOF";
  public static final String AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE =
      "autoFitColumnWidthAfterOpeningFile";
  public static final String AUTO_FIT_COLUMN_WIDTH_AFTER_EDITING_CELL =
      "autoFitColumnWidthAfterEditingCell";
  public static final String AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE =
      "autoFitColumnWidthWithLimitedRowSize";
  public static final String ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING = "rowSizeToScanWhenAutoFitting";
  public static final String LIMIT_WIDTH_WHEN_AUTO_FITTING = "limitWidthWhenAutoFitting";
  public static final String MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING =
      "maxColumnWidthPerWindowWhenAutoFitting";
  public static final String QUOTE_RULE_FOR_COPYING = "quoteRuleForCopying";
  public static final String PASTE_REPEATEDLY = "pasteRepeatedly";


  @Getter
  private static CoreSettings instance = new CoreSettings();

  private CoreSettings() {
    super("core");
  }
}
