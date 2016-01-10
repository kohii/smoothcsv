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
package com.smoothcsv.core.constants;

/**
 *
 * @author kohii
 */
public interface CoreSettingKeys {

  interface Core {
    // String $ = "core";
    String ALERT_ON_OPENING_HUGE_FILE = "alertOnOpeningHugeFile";
    String ALERT_THRESHOLD = "alertThreshold";
    String AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE = "autoFitColumnWidthAfterOpeningFile";
    String AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE = "autoFitColumnWidthWithLimitedRowSize";
    String CELL_PADDING_BOTTOM = "cellPaddingBottom";
    String CELL_PADDING_TOP = "cellPaddingTop";
    String DEFAULT_COLUMN_SIZE = "defaultColumnSize";
    String DEFAULT_FONT_SIZE = "defaultFontSize";
    String DEFAULT_ROW_SIZE = "defaultRowSize";
    String GRID_FONT_SIZE = "gridFontSize";
    String HOW_TO_DETECT_PROPERTIES = "howToDetectProperties";
    String LANGUAGE = "language";
    String LIMIT_WIDTH_WHEN_AUTO_FITTING = "limitWidthWhenAutoFitting";
    String MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING = "maxColumnWidthPerWindowWhenAutoFitting";
    String QUOTE_RULE_FOR_COPYING = "quoteRuleForCopying";
    String ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING = "rowSizeToScanWhenAutoFitting";
    String SIZE_OF_UNDOING = "sizeOfUndoing";
    String STATUSBAR_VISIBLE = "statusbarVisible";
    String STATUS_BAR_FONT_SIZE = "statusBarFontSize";
    String TOOLBAR_VISIBLE = "toolbarVisible";
    String VALUEPANEL_HEIGHT = "valuepanelHeight";
    String VALUEPANEL_VISIBLE = "valuepanelVisible";
    String VALUE_PANEL_FONT_SIZE = "valuePanelFontSize";
    String PASTE_REPEATEDLY = "pasteRepeatedly";
  }

  interface CsvProperties {
    String $ = "csvprop";
    String ENCODING_OPTIONS = "csvprop.encodingOptions";
    String DELIMITER_CHAR_OPTIONS = "csvprop.delimiterCharOptions";
    String QUOTE_CHAR_OPTIONS = "csvprop.quoteCharOptions";
  }
}
