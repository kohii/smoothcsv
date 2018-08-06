package com.smoothcsv.core.sql.engine.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.smoothcsv.commons.exception.UnexpectedException;
import org.apache.commons.lang3.StringUtils;

public class CsvsqlEngine {

  ResourceBundle bundle = ResourceBundle.getBundle("application");

  private String driverClassName = bundle.getString("jdbc.driverClassName");

  private String workDatabasePath;

  private String user = bundle.getString("jdbc.username");

  private String password = bundle.getString("jdbc.password");

  private String url;
  private Connection conn;

  private PreparedStatement pstmt = null;
  private ResultSet rs = null;

  public CsvsqlEngine(String workDatabasePath) {
    if (StringUtils.isNotEmpty(workDatabasePath)) {
      this.workDatabasePath = workDatabasePath;
    } else {
      this.workDatabasePath = bundle.getString("jdbc.defaultDbName");
    }
  }

  public void open() throws SQLException {
    url = MessageFormat.format(bundle.getString("jdbc.url"), workDatabasePath);

    try {
      Class.forName(driverClassName);

      conn = DriverManager.getConnection(url, user, password);
    } catch (ClassNotFoundException e) {
      throw new UnexpectedException(e);
    }
  }

  public void close() throws SQLException {
    if (conn != null) {
      conn.close();
      conn = null;
    }
  }

  public void executeQuery(String sql, Consumer<ResultSet> resultHandler,
                           Object... args) throws InterruptedException, SQLException {

    try {

      pstmt = conn.prepareStatement(sql);
      for (int i = 0; i < args.length; i++) {
        Object obj = args[i];
        pstmt.setObject(i + 1, obj);
      }

      rs = pstmt.executeQuery();

      if (conn == null) {
        throw new InterruptedException();
      }
      resultHandler.accept(rs);

    } finally {
      closeQuery();
    }
  }

  public ResultSet executeQuery(String sql, Object... args) throws InterruptedException, SQLException {

    pstmt = conn.prepareStatement(sql);
    for (int i = 0; i < args.length; i++) {
      Object obj = args[i];
      pstmt.setObject(i + 1, obj);
    }

    rs = pstmt.executeQuery();

    if (conn == null) {
      throw new InterruptedException();
    }

    return rs;
  }

  public void closeQuery() throws SQLException {
    if (pstmt != null) {
      pstmt.close();
      pstmt = null;
    }
    if (rs != null) {
      rs.close();
      rs = null;
    }
  }

  public boolean execute(String sql, Object... args) throws SQLException {
    PreparedStatement pstmt = null;

    try {

      pstmt = conn.prepareStatement(sql);
      for (int i = 0; i < args.length; i++) {
        Object obj = args[i];
        pstmt.setObject(i + 1, obj);
      }

      return pstmt.execute();

    } finally {
      if (pstmt != null) {
        pstmt.close();
        pstmt = null;
      }
    }
  }

  public String getWorkDatabasePath() {
    return workDatabasePath;
  }

  public void setWorkDatabasePath(String workDatabasePath) {
    this.workDatabasePath = workDatabasePath;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isOpened() {
    return conn != null;
  }

}
