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
package com.smoothcsv.core.csvsheet;

import java.io.File;
import java.util.function.Function;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.framework.component.dialog.BasicFileChooser;
import com.smoothcsv.framework.setting.SettingManager;

public class CsvFileChooser extends BasicFileChooser {

  private static final long serialVersionUID = -6977376491214445715L;

  private static CsvFileChooser instance;

  public static CsvFileChooser getInstance() {
    if (instance == null) {
      instance = new CsvFileChooser();
      FileFilter csvfilter = new FileNameExtensionFilter("CSV(*.csv)", "csv");
      instance.addChoosableFileFilter(csvfilter);
      FileFilter tsvfilter = new FileNameExtensionFilter("TSV(*.tsv)", "tsv");
      instance.addChoosableFileFilter(tsvfilter);
      FileFilter txtfilter = new FileNameExtensionFilter("TEXT(*.txt)", "txt");
      instance.addChoosableFileFilter(txtfilter);
      instance.setFileFilter(csvfilter);

      instance.addOnApproveSelection(new Function<File, Boolean>() {
        @Override
        public Boolean apply(File t) {
          SettingManager.save(AppSettingKeys.Session.LAST_USED_DIRECTORY, instance
              .getCurrentDirectory().getAbsolutePath());
          return true;
        }
      });
      String initDir = SettingManager.get(AppSettingKeys.Session.LAST_USED_DIRECTORY);
      if (StringUtils.isNotEmpty(initDir)) {
        File file = new File(initDir);
        if (file.exists()) {
          instance.setCurrentDirectory(file);
        }
      }
    }
    return instance;
  }
}
