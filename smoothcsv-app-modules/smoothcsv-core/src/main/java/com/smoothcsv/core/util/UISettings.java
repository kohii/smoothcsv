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
public class UISettings extends Settings {

  public static final String DEFAULT_FONT_SIZE = "default.fontSize";
  public static final String DEFAULT_FONT_NAME = "default.fontName";
  public static final String DEFAULT_FOREGROUND = "default.foreground";
  public static final String DEFAULT_BACKGROUND = "default.background";
  public static final String STATUSBAR_FONT_SIZE = "statusbar.fontSize";
  public static final String STATUSBAR_FONT_NAME = "statusbar.fontName";
  public static final String GRID_FONT_SIZE = "grid.fontSize";
  public static final String GRID_FONT_NAME = "grid.fontName";
  public static final String GRID_FOREGROUND = "grid.foreground";
  public static final String GRID_BACKGROUND = "grid.background";
  public static final String GRID_LINE_COLOR = "grid.lineColor";
  public static final String GRID_FROZEN_LINE_COLOR = "grid.frozenLineColor";
  public static final String GRID_SELECTION_BACKGROUND = "grid.selection.background";
  public static final String GRID_SELECTION_BORDER_COLOR = "grid.selection.borderColor";
  public static final String GRID_SELECTION_ALPHA = "grid.selection.alpha";
  public static final String GRID_FIND_HILIGHT = "grid.findHighlight";
  public static final String GRID_HEADER_FOREGROUND = "gridHeader.foreground";
  public static final String GRID_HEADER_BACKGROUND = "gridHeader.background";
  public static final String GRID_HEADER_SELECTED_FOREGROUND = "gridHeader.selected.foreground";
  public static final String GRID_HEADER_SELECTED_BACKGROUND = "gridHeader.selected.background";
  public static final String GRID_HEADER_FOCUSED_FOREGROUND = "gridHeader.focused.foreground";
  public static final String GRID_HEADER_FOCUSED_BACKGROUND = "gridHeader.focused.background";
  public static final String CELLEDITOR_FONT_SIZE = "celleditor.fontSize";
  public static final String CELLEDITOR_FONT_NAME = "celleditor.fontName";
  public static final String CELLEDITOR_FOREGROUND = "celleditor.foreground";
  public static final String CELLEDITOR_BACKGROUND = "celleditor.background";
  public static final String CELLEDITOR_CARET_COLOR = "celleditor.caretColor";
  public static final String INLINE_CELLEDITOR_FONT_SIZE = "inline-celleditor.fontSize";
  public static final String INLINE_CELLEDITOR_FONT_NAME = "inline-celleditor.fontName";
  public static final String INLINE_CELLEDITOR_FOREGROUND = "inline-celleditor.foreground";
  public static final String INLINE_CELLEDITOR_BACKGROUND = "inline-celleditor.background";
  public static final String INLINE_CELLEDITOR_CARET_COLOR = "inline-celleditor.caretColor";

  @Getter
  private static UISettings instance = new UISettings();

  private UISettings() {
    super("ui");
  }
}
