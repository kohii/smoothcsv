package com.smoothcsv.core.sql.engine.core;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Csvsql {

  private static CsvsqlEngine csvsqlEngine = new CsvsqlEngine(null);
  private static List<String> aliasList = new ArrayList<>();

  static {
    final String dbName = "c" + System.currentTimeMillis();
    setWorkDatabasePath(new File(DirectoryResolver.instance().getTemporaryDirectory(), dbName));
    SmoothCsvApp.getApplication().listeners().on(SCApplication.ShutdownEvent.class, event -> {
      close();
    });
    createFunction("CSVSHEET", "com.smoothcsv.core.sql.engine.CsvSheet.csvSheet");
    createFunction("CSVSHEET_BY_ID", "com.smoothcsv.core.sql.engine.CsvSheet.csvSheetById");
  }

  public static ResultSet executeQuery(String sql, Object... args) throws InterruptedException {
    try {
      prepare();
      return csvsqlEngine.executeQuery(sql, args);
    } catch (InterruptedException e) {
      throw e;
    } catch (Exception e) {
      log.warn("", e);

      if (e instanceof SQLException) {
        throw new AppException("WSCA0015", StringUtils.omitLines(e.toString(), 10));
      } else if (e instanceof AppException) {
        throw (AppException) e;
      }
      throw new UnexpectedException(e);
    }
  }

  private static void prepare() throws SQLException {
    if (!csvsqlEngine.isOpened()) {
      csvsqlEngine.open();
      for (String createAliasSql : aliasList) {
        csvsqlEngine.execute(createAliasSql);
      }
      aliasList.clear();
    }
  }

  private static void createFunction(String name, String path) {
    String sql = "CREATE ALIAS " + name + " FOR \"" + path + '"';
    if (csvsqlEngine.isOpened()) {
      try {
        csvsqlEngine.execute(sql);
      } catch (SQLException e) {
        throw new UnexpectedException(e);
      }
    } else {
      aliasList.add(sql);
    }
  }

  public static boolean execute(String sql, Object... args) throws SQLException {
    prepare();
    return csvsqlEngine.execute(sql, args);
  }

  public static void setAutoClose() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        close();
      }
    });
  }

  public static void close() {
    try {
      csvsqlEngine.close();
    } catch (SQLException ignored) {
    }
  }

  public static void closeQuery() {
    try {
      csvsqlEngine.closeQuery();
    } catch (SQLException ignored) {
    }
  }

  public String getWorkDatabasePath() {
    return csvsqlEngine.getWorkDatabasePath();
  }

  public static void setWorkDatabasePath(File workDatabasePath) {
    try {
      csvsqlEngine.setWorkDatabasePath(workDatabasePath.getCanonicalPath());
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }
}
