package com.smoothcsv.core.sql.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;
import lombok.Getter;

/**
 * @author kohii
 */
public class SqlTableDefinitions {

  @Getter
  private static final SqlTableDefinitions instance = new SqlTableDefinitions();

  private static Map<Integer, SqlCsvSheetTableInfo> sqlTableInfoCache = new HashMap<>();
  private static int seq = 1;

  @Getter
  private List<SqlCsvSheetTableInfo> tableInfoList;

  public void reloadAllCsvSheets() {
    List<CsvSheetView> views =
        SCApplication.components().getTabbedPane().getAllViews(CsvSheetView.class);

    List<SqlCsvSheetTableInfo> tableInfoList = new ArrayList<>();
    for (CsvSheetView csvSheetView : views) {
      SqlCsvSheetTableInfo tableInfo = sqlTableInfoCache.computeIfAbsent(csvSheetView.getViewId(), viewId -> new SqlCsvSheetTableInfo(
          csvSheetView, "t" + (seq++)));
      tableInfo.adjustColumns();
      tableInfoList.add(tableInfo);
    }
    this.tableInfoList = Collections.unmodifiableList(tableInfoList);
  }

  public SqlCsvSheetTableInfo getTableInfoByViewId(int viewId) {
    for (SqlCsvSheetTableInfo tableInfo : tableInfoList) {
      if (tableInfo.getCsvSheet().getViewId() == viewId) {
        return tableInfo;
      }
    }
    return null;
  }
}
