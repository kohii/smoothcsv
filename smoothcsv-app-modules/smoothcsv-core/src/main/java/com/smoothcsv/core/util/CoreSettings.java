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
package com.smoothcsv.core.util;

import com.smoothcsv.framework.setting.Settings;

import lombok.Getter;

/**
 * @author kohii
 *
 */
public class CoreSettings extends Settings {

  public static final String LANGUAGE = "language";
  public static final String STATUSBAR_VISIBLE = "statusbarVisible";
  public static final String TOOLBAR_VISIBLE = "toolbarVisible";
  public static final String VALUE_PANEL_VISIBLE = "valuePanelVisible";
  public static String UI_CELLEDITOR_BACKGROUND = "ui.celleditor.background";
  public static String UI_CELLEDITOR_CARET = "ui.celleditor.caret";
  public static String UI_CELLEDITOR_FONT_NAME = "ui.celleditor.fontName";
  public static String UI_CELLEDITOR_FONT_SIZE = "ui.celleditor.fontSize";
  public static String UI_CELLEDITOR_FOREGROUND = "ui.celleditor.foreground";
  public static String UI_DEFAULT_BACKGROUND = "ui.default.background";
  public static String UI_DEFAULT_FONT_NAME = "ui.default.fontName";
  public static String UI_DEFAULT_FONT_SIZE = "ui.default.fontSize";
  public static String UI_DEFAULT_FOREGROUND = "ui.default.foreground";
  public static String UI_GRID_BACKGROUND = "ui.grid.background";
  public static String UI_GRID_FONT_NAME = "ui.grid.fontName";
  public static String UI_GRID_FONT_SIZE = "ui.grid.fontSize";
  public static String UI_GRID_FOREGROUND = "ui.grid.foreground";
  public static String UI_GRID_HEADER_BACKGROUND = "ui.gridHeader.background";
  public static String UI_GRID_HEADER_FOREGROUND = "ui.gridHeader.foreground";
  public static String UI_GRID_HEADER_LINE_COLOR = "ui.gridHeader.lineColor";
  public static String UI_GRID_HEADER_SELECTED_BACKGROUND = "ui.gridHeader.selected.background";
  public static String UI_GRID_HEADER_SELECTED_FOREGROUND = "ui.gridHeader.selected.foreground";
  public static String UI_GRID_LINE_COLOR = "ui.grid.lineColor";
  public static String UI_GRID_SELECTION_ALPHA = "ui.grid.selection.alpha";
  public static String UI_GRID_SELECTION_BACKGROUND = "ui.grid.selection.background";
  public static String UI_GRID_SELECTION_BORDER_COLOR = "ui.grid.selection.borderColor";
  public static String UI_INLINE_CELLEDITOR_BACKGROUND = "ui.inline-celleditor.background";
  public static String UI_INLINE_CELLEDITOR_CARET = "ui.inline-celleditor.caret";
  public static String UI_INLINE_CELLEDITOR_FONT_NAME = "ui.inline-celleditor.fontName";
  public static String UI_INLINE_CELLEDITOR_FONT_SIZE = "ui.inline-celleditor.fontSize";
  public static String UI_INLINE_CELLEDITOR_FOREGROUND = "ui.inline-celleditor.foreground";
  public static String UI_STATUSBAR_FONT_NAME = "ui.statusbar.fontName";
  public static String UI_STATUSBAR_FONT_SIZE = "ui.statusbar.fontSize";
  public static final String ALERT_ON_OPENING_HUGE_FILE = "alertOnOpeningHugeFile";
  public static final String ALERT_THRESHOLD = "alertThreshold";
  public static final String HOW_TO_DETECT_PROPERTIES = "howToDetectProperties";
  public static final String DEFAULT_ROW_SIZE = "defaultRowSize";
  public static final String DEFAULT_COLUMN_SIZE = "defaultColumnSize";
  public static final String SIZE_OF_UNDOING = "sizeOfUndoing";
  public static final String VALUE_PANEL_HEIGHT = "valuePanelHeight";
  public static final String AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE =
      "autoFitColumnWidthAfterOpeningFile";
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
