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

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.commons.utils.SerializeUtils;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.SmoothCsvReader;
import com.smoothcsv.csv.reader.CsvReaderOptions;
import com.smoothcsv.framework.util.DirectoryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kohii
 */
public class CsvSheetSupport {

  private static final Logger LOG = LoggerFactory.getLogger(DirectoryResolver.class);

  private static CsvMeta defaultCsvMeta;

  public static CsvMeta getDefaultCsvMeta() {
    if (defaultCsvMeta == null) {
      File file = getDefaultCsvMetaFile();
      if (FileUtils.canRead(file)) {
        try {
          defaultCsvMeta = (CsvMeta) SerializeUtils.deserialize(file);
        } catch (Exception e) {
          if (!file.delete()) {
            LOG.warn("Could not delete file {}", file);
          }
        }
      }
      if (defaultCsvMeta == null) {
        defaultCsvMeta = new CsvMeta();
      }
    }
    return defaultCsvMeta.clone();
  }

  public static void setDefaultCsvMeta(CsvMeta csvMeta) {
    defaultCsvMeta = csvMeta;
    try {
      SerializeUtils.serialize(getDefaultCsvMetaFile(), csvMeta);
    } catch (Exception ignore) {
    }
  }

  public static CsvMeta getAutoDetectEnabledCsvMeta() {
    CsvMeta csvMeta = new CsvMeta();
    csvMeta.setCharsetNotDetermined(true);
    csvMeta.setDelimiterNotDetermined(true);
    csvMeta.setQuoteNotDetermined(true);
    csvMeta.setCharset(null);
    csvMeta.setNewlineCharNotDetermined(true);
    return csvMeta;
  }

  public static void detectProperties(CsvMeta csvMeta, File file) {
    if (!csvMeta.isCharsetNotDetermined() && !csvMeta.isDelimiterNotDetermined()
        && !csvMeta.isQuoteNotDetermined() && !csvMeta.isNewlineCharNotDetermined()) {
      return;
    }

  }

  private static File getDefaultCsvMetaFile() {
    return new File(DirectoryResolver.instance().getSettingDirectory(), "DefaultCsvMeta");
  }

  public static CsvGridSheetModel createModelFromFile(File file, CsvMeta csvMeta,
                                                      CsvReaderOptions options) {

    if (options == null) {
      options = CsvReaderOptions.DEFAULT;
    }

    try (SmoothCsvReader reader =
             new SmoothCsvReader(new InputStreamReader(new FileInputStream(file), csvMeta.getCharset()),
                 csvMeta, options)) {
      List<List> data = new ArrayList<>();
      List r;
      while ((r = reader.readRow()) != null) {
        data.add(r);
      }
      if (data.isEmpty()) {
        data.add(new ArrayList<>());
      }
      CsvGridSheetModel gsm = new CsvGridSheetModel(data, data.size(), Math.max(1, reader.getMaxColumnCount()));
      if (csvMeta.isNewlineCharNotDetermined()) {
        if (reader.getFirstNewlineCharacter() != null) {
          csvMeta.setNewlineCharacter(reader.getFirstNewlineCharacter());
        }
        csvMeta.setNewlineCharNotDetermined(false);
      }
      return gsm;
    } catch (IOException ex) {
      throw new UnexpectedException(ex);
    }
  }

  public static CsvGridSheetModel createModel(int rows, int columns, CsvMeta csvMeta) {

    List<List> data = new ArrayList<>();
    for (int r = 0; r < rows; r++) {
      List rowData = new ArrayList(columns);
      for (int i = 0; i < columns; i++) {
        rowData.add("");
      }
      data.add(rowData);
    }
    CsvGridSheetModel gsm = new CsvGridSheetModel(data, data.size(), columns);
    return gsm;
  }
}
