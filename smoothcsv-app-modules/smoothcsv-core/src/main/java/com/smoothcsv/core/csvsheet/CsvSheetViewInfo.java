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
package com.smoothcsv.core.csvsheet;

import java.io.File;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.RecentFilesHistory;
import com.smoothcsv.csv.reader.CsvReadOption;
import com.smoothcsv.framework.component.view.ViewInfo;

/**
 * @author kohii
 */
public class CsvSheetViewInfo extends ViewInfo {

  private File file;

  private CsvMeta csvMeta;

  private CsvReadOption options;

  public CsvSheetViewInfo(File file, CsvMeta csvMeta, CsvReadOption options) {
    this.csvMeta = csvMeta;
    this.options = options;
    setFile(file);
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    File old = this.file;
    this.file = file;
    propertyChangeSupport.firePropertyChange("file", old, file);
    if (file != null) {
      RecentFilesHistory.getInstance().put(FileUtils.getCanonicalPath(file));
    }
  }

  public CsvMeta getCsvMeta() {
    return csvMeta;
  }

  public void setCsvMeta(CsvMeta csvMeta) {
    CsvMeta old = this.csvMeta;
    this.csvMeta = csvMeta;
    propertyChangeSupport.firePropertyChange("csvMeta", old, csvMeta);
  }

  public CsvReadOption getOptions() {
    return options;
  }

  public void setOptions(CsvReadOption options) {
    CsvReadOption old = this.options;
    this.options = options;
    propertyChangeSupport.firePropertyChange("options", old, options);
  }
}
