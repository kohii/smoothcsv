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
package command.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JFileChooser;

import com.smoothcsv.commons.encoding.FileEncoding;
import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.IORuntimeException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.CharsetUtils;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.core.component.ReadCsvPropertiesDialog;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvFileChooser;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.csv.detector.CsvPropertiesDetectorImpl;
import com.smoothcsv.csv.prop.CsvProperties;
import com.smoothcsv.csv.reader.CsvReadOption;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCTabbedPane;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.setting.Settings;
import command.grid.AutofitColumnWidthCommand;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
public class OpenFileCommand extends Command {

  public static final String HOW_TO_DETECT_PROPERTIES_AUTO = "auto";
  public static final String HOW_TO_DETECT_PROPERTIES_MANUAL = "manual";
  public static final String HOW_TO_DETECT_PROPERTIES_DEFAULT = "defalut";

  @Override
  public void run() {
    chooseAndOpenFile(null);
  }

  public void chooseAndOpenFile(File currentDir) {
    File file = chooseFile(currentDir);
    run(file, HOW_TO_DETECT_PROPERTIES_MANUAL, SCTabbedPane.LAST);
  }

  public void chooseAndOpenFile(File currentDir, String howToDetectProperties) {
    File file = chooseFile(currentDir);
    run(file, howToDetectProperties, SCTabbedPane.LAST);
  }

  public void run(File file) {
    String howToDetectProperties =
        CoreSettings.getInstance().get(CoreSettings.HOW_TO_DETECT_PROPERTIES);
    run(file, howToDetectProperties, SCTabbedPane.LAST);
  }


  public static void run(File file, String howToDetectProperties, int index) {

    if (!file.exists() || !file.isFile()) {
      throw new AppException("WSCA0012", FileUtils.getCanonicalPath(file));
    }

    List<BaseTabView<?>> views = SCApplication.components().getTabbedPane().getAllViews();
    for (BaseTabView<?> view : views) {
      if (view instanceof CsvSheetView) {
        CsvSheetView csvView = (CsvSheetView) view;
        if (file.equals(csvView.getViewInfo().getFile())) {
          SCApplication.components().getTabbedPane().setSelectedComponent(csvView);
          return;
        }
      }
    }

    Settings settings = CoreSettings.getInstance();
    if (settings.getBoolean(CoreSettings.ALERT_ON_OPENING_HUGE_FILE)) {
      int fileSize = (int) (file.length() / 1024 / 1024);
      int threshold = settings.getInteger(CoreSettings.ALERT_THRESHOLD);
      if (threshold <= fileSize) {
        boolean ok = MessageDialogs.confirm("ISCA0003", fileSize);
        if (!ok) {
          throw new CancellationException();
        }
      }
    }

    CsvMeta properties;
    if (howToDetectProperties.equals(HOW_TO_DETECT_PROPERTIES_MANUAL)) {
      properties = chooseProperties();
    } else if (howToDetectProperties.equals(HOW_TO_DETECT_PROPERTIES_AUTO)) {
      properties = CsvSheetSupport.getAutoDetectEnabledCsvMeta();
    } else {
      properties = CsvSheetSupport.getDefaultCsvMeta();
    }

    determineCsvMetaContent(file, properties);

    run(file, properties, null, index);
  }

  static void determineCsvMetaContent(File file, CsvMeta properties) {
    if (properties.isCharsetNotDetermined()) {
      // detect charset
      FileEncoding encoding = CharsetUtils.detect(file, 12000);
      properties.setEncoding(encoding);
      properties.setCharsetNotDetermined(false);
    }

    if (properties.isDelimiterNotDetermined() || properties.isQuoteNotDetermined()) {
      CsvPropertiesDetectorImpl detector = new CsvPropertiesDetectorImpl();

      // TODO Should I use BufferedReader?
      try (InputStreamReader r =
               new InputStreamReader(new FileInputStream(file), properties.getEncoding().getCharset())) {
        char[] chars = new char[8192];
        int nread;
        if ((nread = r.read(chars)) != -1) {
          String s = new String(chars, 0, nread);
          // TODO improve the process of detection
          if (properties.isDelimiterNotDetermined()) {
            if (StringUtils.endsWithIgnoreCase(file.getName(), ".tsv")) {
              properties.setDelimiter('\t');
              CsvProperties p = detector.detectProperties(s, properties.getDelimiter());
              p = escapeNull(p);
              properties.setQuote(p.getQuoteChar());
            } else if (StringUtils.endsWithIgnoreCase(file.getName(), ".csv")) {
              properties.setDelimiter(',');
              CsvProperties p = detector.detectProperties(s, properties.getDelimiter());
              p = escapeNull(p);

              properties.setQuote(p.getQuoteChar());
            } else {
              CsvProperties p = detector.detectProperties(s);
              p = escapeNull(p);
              properties.setDelimiter(p.getDelimiter());
              properties.setQuote(p.getQuoteChar());
            }
          } else {
            CsvProperties p = detector.detectProperties(s, properties.getDelimiter());
            p = escapeNull(p);
            properties.setQuote(p.getQuoteChar());
          }
        } else {
          properties.setDelimiter(CsvProperties.DEFAULT.getDelimiter());
          properties.setQuote(CsvProperties.DEFAULT.getQuoteChar());
        }
      } catch (IOException e) {
        throw new IORuntimeException(e);
      }
      properties.setDelimiterNotDetermined(false);
      properties.setQuoteNotDetermined(false);
    }
  }

  public static CsvMeta chooseProperties() {
    ReadCsvPropertiesDialog dialog = new ReadCsvPropertiesDialog(SmoothCsvApp.components().getFrame(), "Open");
    if (dialog.showDialog() != DialogOperation.OK) {
      throw new CancellationException();
    }
    return dialog.getCsvMeta();
  }

  private static CsvProperties escapeNull(CsvProperties p) {
    return p != null ? p : CsvSheetSupport.getDefaultCsvMeta().toCsvProperties();
  }

  public static void run(CsvSheetViewInfo viewInfo, CsvGridSheetModel model, int index) {
    try {
      SmoothComponentManager.startAdjustingComponents();
      CsvSheetView csvGridSheetView = new CsvSheetView(viewInfo, model);
      SCApplication.components().getTabbedPane().addTab(csvGridSheetView, index);
    } finally {
      SmoothComponentManager.stopAdjustingComponents();
    }
    if (CoreSettings.getInstance()
        .getBoolean(CoreSettings.AUTO_FIT_COLUMN_WIDTH_AFTER_OPENING_FILE)) {
      new AutofitColumnWidthCommand().execute();
    }
  }

  public static void run(File file, CsvMeta properties, CsvReadOption options, int index) {
    CsvSheetViewInfo viewInfo = new CsvSheetViewInfo(file, properties, options);
    CsvGridSheetModel model = CsvSheetSupport.createModelFromFile(file, properties, options);
    run(viewInfo, model, index);
  }

  private File chooseFile(File currentDir) {
    CsvFileChooser fileChooser = CsvFileChooser.getInstance();
    if (currentDir != null && currentDir.isDirectory()) {
      fileChooser.setCurrentDirectory(currentDir);
    }
    switch (fileChooser.showOpenDialog()) {
      case JFileChooser.APPROVE_OPTION:
        File file = fileChooser.getSelectedFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) {
          throw new AppException("WSCC0001", file);
        }
        return file;
      case JFileChooser.CANCEL_OPTION:
        throw new CancellationException();
      default:
        throw new UnexpectedException();
    }
  }
}
