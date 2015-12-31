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
public interface AppSettingKeys {

  interface Core {
    String $ = "core";
    String STATUSBAR_VISIBLE = "core.statusbarVisible";
    String TOOLBAR_VISIBLE = "core.toolbarVisible";
    String VALUEPANEL_VISIBLE = "core.valuepanelVisible";
  }

  interface Session {
    String $ = "session";
    String WINDOW_WIDTH = "session.windowWidth";
    String WINDOW_HEIGHT = "session.windowHeight";
    String WINDOW_X = "session.windowX";
    String WINDOW_Y = "session.windowY";
    String LAST_USED_DIRECTORY = "session.lastUsedDirectory";
    String LAST_USED_DIRECTORY_JS = "session.lastUsedDirectoryJS";
  }

  interface CsvProperties {
    String $ = "csvprop";
    String ENCODING_OPTIONS = "csvprop.encodingOptions";
    String DELIMITER_CHAR_OPTIONS = "csvprop.delimiterCharOptions";
    String QUOTE_CHAR_OPTIONS = "csvprop.quoteCharOptions";
  }

  interface File {
    String $ = "file";
    String HOW_TO_DETECT_PROPERTIES = "file.howToDetectProperties";
    String ALERT_ON_OPENING_HUGE_FILE = "file.alertOnOpeningHugeFile";
    String ALERT_THRESHOLD = "file.alertThreshold";
    String DEFAULT_ROW_SIZE = "file.defaultRowSize";
    String DEFAULT_COLUMN_SIZE = "file.defaultColumnSize";
  }

  interface Editor {
    String $ = "editor";
    String SIZE_OF_UNDOING = "editor.sizeOfUndoing";
    String AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE = "editor.autoFitColumnWidthAfterOpeningFile";
    String AUTO_FIT_COLUMN_WIDTH_WITH_LIMITED_ROW_SIZE =
        "editor.autoFitColumnWidthWithLimitedRowSize";
    String ROW_SIZE_TO_SCAN_WHEN_AUTO_FITTING = "editor.rowSizeToScanWhenAutoFitting";
    String LIMIT_WIDTH_WHEN_AUTO_FITTING = "editor.limitWidthWhenAutoFitting";
    String MAX_COLUMN_WIDTH_PER_WINDOW_WHEN_AUTO_FITTING =
        "editor.maxColumnWidthPerWindowWhenAutoFitting";
    String QUOTE_RULE_FOR_COPYING = "editor.quoteRuleForCopying";
    String PASTE_REPEATEDLY = "editor.pasteRepeatedly";
    String VALUEPANEL_HEIGHT = "editor.valuepanelHeight";
  }

  interface Find {
    String $ = "find";
    String CASE_SENSITIVE = "find.caseSensitive";
    String USE_REGEX = "find.useRegex";
    String MATCH_WHOLE_CELL = "find.matchWholeCell";
    String IN_SELECTION = "find.inSelection";
    String DIRECTION = "find.direction";
    String PRESERVE_CASE = "find.preserveCase";
  }
}
