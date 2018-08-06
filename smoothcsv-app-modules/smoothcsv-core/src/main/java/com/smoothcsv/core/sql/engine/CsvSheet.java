package com.smoothcsv.core.sql.engine;

import java.awt.Component;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.sql.model.SqlColumnInfo;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlTableDefinitions;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.exception.AppException;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.tools.SimpleResultSet;
import org.h2.tools.SimpleRowSource;

public class CsvSheet implements SimpleRowSource {

  private int cursor = -1;
  private CsvGridSheetPane dataTable;
  private int rowCount = -1;
  private int columnCount = -1;

  public CsvSheet(CsvGridSheetPane dataTable) {
    this.dataTable = dataTable;
    rowCount = dataTable.getRowCount();
    columnCount = dataTable.getColumnCount();
    cursor = 0;
  }

  public static ResultSet csvSheet(Connection conn, String arg0)
      throws Exception {
    return csvSheet(conn, arg0, null);
  }

  public static ResultSet csvSheet(Connection conn, String arg0, String arg1)
      throws Exception {
    String name;
    int index = 1;
    if (arg1 == null) {
      name = arg0;
      if (StringUtils.isNumber(name)) {
        index = Integer.parseInt(name);
        name = null;
      }
    } else if (arg0 != null) {
      if (!StringUtils.isNumber(arg1)) {
        throw new AppException(
            "Invalid parameters in CSVSHEET()");
      }
      name = arg0;
      index = Integer.parseInt(arg1);
    } else {
      throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2,
          "CSVSHEET",
          "1 or 2");
    }
    CsvSheetView cvp = getTab(name, index);
    if (cvp == null) {
      throw new AppException("Tab not found corresponding to CSVSHEET('" + name + "', " + index + ")");
    }
    return tab(conn, cvp);
  }

  public static ResultSet csvSheetById(Connection conn, Integer viewId)
      throws Exception {
    SqlCsvSheetTableInfo tableInfo = SqlTableDefinitions.getInstance().getTableInfoByViewId(viewId);
    if (tableInfo == null) {
      throw new AppException("Tab not found corresponding to CSVSHEET_BY_ID(" + viewId + ")");
    }
    return tab(conn, tableInfo.getCsvSheet());
  }

  private static CsvSheetView getTab(String name, int index) {
    List<BaseTabView<?>> components = SmoothCsvApp.components().getTabbedPane().getAllViews();
    int i = 0;
    for (Component com : components) {
      if (com instanceof CsvSheetView) {
        CsvSheetView tmp = (CsvSheetView) com;
        String tabName = tmp.getViewInfo().getShortTitle();
        if (tabName.equals(name) || name == null) {
          i++;
          if (index == i) {
            return tmp;
          }
        }
      }
    }
    return null;
  }

  private static ResultSet tab(Connection conn, CsvSheetView cvp)
      throws Exception {


    String url = conn != null ? conn.getMetaData().getURL() : null;

    boolean isColumnConnection = "jdbc:columnlist:connection".equals(url);

    CsvGridSheetPane gridSheetPane = cvp.getGridSheetPane();

    SimpleResultSet rs;
    if (isColumnConnection) {
      rs = new SimpleResultSet();
    } else {
      rs = new SimpleResultSet(new CsvSheet(gridSheetPane));
    }

    SqlCsvSheetTableInfo tableInfo = SqlTableDefinitions.getInstance().getTableInfoByViewId(cvp.getViewId());
    List<SqlColumnInfo> columns = tableInfo.getColumns();
    for (SqlColumnInfo column : columns) {
      rs.addColumn(column.getName(), column.getType().getVendorTypeNumber(), Integer.MAX_VALUE, 0);
    }

    return rs;
  }

  @Override
  public Object[] readRow() {
    if (cursor == rowCount) {
      return null;
    }
    Object[] row = new Object[columnCount];
    for (int i = 0; i < columnCount; i++) {
      row[i] = dataTable.getValueAt(cursor, i);
    }
    cursor++;
    return row;
  }

  @Override
  public void close() {
    dataTable = null;
    cursor = -1;
  }

  @Override
  public void reset() {
    cursor = 0;
  }
}
