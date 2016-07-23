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
package com.smoothcsv.core.macro.apiimpl;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.macro.MacroUtils;
import com.smoothcsv.core.macro.api.App;
import com.smoothcsv.core.macro.api.CellEditor;
import com.smoothcsv.core.macro.api.CsvProperties;
import com.smoothcsv.core.macro.api.CsvSheet;
import com.smoothcsv.core.macro.api.Range;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCTabbedPane;
import command.app.NewFileCommand;
import command.app.OpenFileCommand;
import lombok.Getter;

import java.io.File;
import java.util.ResourceBundle;

/**
 * @author kohii
 */
public class AppImpl extends APIBase implements App {

  @Getter
  private static final AppImpl instance = new AppImpl();

  private AppImpl() {}

  @Override
  public String getName() {
    return SCApplication.getApplication().getName();
  }

  @Override
  public String getVersion() {
    return ResourceBundle.getBundle("application").getString("version.name");
  }

  @Override
  public void create() {
    new NewFileCommand().run();
  }

  @Override
  public void create(int rows, int columns) {
    create(rows, columns, CsvProperties.defaultProperties());
  }

  @Override
  public void create(int rows, int columns, CsvProperties properties) {
    NewFileCommand.run(rows, columns, MacroUtils.toCsvMeta(properties), SCTabbedPane.LAST);
  }

  @Override
  public void open(String pathname) {
    open(pathname, CsvProperties.defaultProperties());
  }

  @Override
  public void open(String pathname, CsvProperties properties) {
    OpenFileCommand.run(new File(pathname), MacroUtils.toCsvMeta(properties), null, SCTabbedPane.LAST);
  }

  @Override
  public CsvSheet getActiveSheet() {
    CsvSheetView csvSheetView =
        (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    return csvSheetView == null ? null : new CsvSheetImpl(csvSheetView.getViewId());
  }

  @Override
  public void setActiveSheet(CsvSheet csvSheet) {
    csvSheet.activate();
  }

  @Override
  public Range getActiveRange() {
    CsvSheet activeSheet = getActiveSheet();
    return activeSheet == null ? null : activeSheet.getActiveRange();
  }

  @Override
  public CellEditor getActiveCellEditor() {
    return getActiveCellEditor(false);
  }

  @Override
  public CellEditor getActiveCellEditor(boolean startEdit) {
    CsvSheetView csvSheetView =
        (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    if (csvSheetView == null) {
      return null;
    }
    if (!csvSheetView.getGridSheetPane().isEditing()) {
      if (startEdit) {
        csvSheetView.getGridSheetPane().getTable().startEdit();
      } else {
        return null;
      }
    }
    return new CellEditorImpl(new CsvSheetImpl(csvSheetView.getViewId()));
  }

  @Override
  public CsvSheet[] getSheets() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    int compCount = tabbedPane.getTabCount();
    CsvSheet[] sheets = new CsvSheet[compCount];
    for (int i = 0; i < compCount; i++) {
      sheets[i] = new CsvSheetImpl(tabbedPane.getComponentAt(i).getViewId());
    }
    return sheets;
  }
}
