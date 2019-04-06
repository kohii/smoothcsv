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
package command.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.IgnorableException;
import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.ReflectionUtils;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.SqlEditorCommandBase;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csv.SmoothCsvWriter;
import com.smoothcsv.core.csvsheet.CsvFileChooser;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvSheetViewInfo;
import com.smoothcsv.core.sql.component.SqlEditor;
import com.smoothcsv.core.sql.component.SqlOutputOptionDialog;
import com.smoothcsv.core.sql.component.SqlToolsDialog;
import com.smoothcsv.core.sql.engine.SqlTaskWorker;
import com.smoothcsv.core.sql.engine.core.Csvsql;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlHistory;
import com.smoothcsv.core.sql.model.SqlTableDefinitions;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.csv.reader.CsvReadOption;
import com.smoothcsv.csv.writer.CsvWriteOption;
import com.smoothcsv.framework.component.SCTabbedPane;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.util.MessageBundles;
import command.app.OpenFileCommand;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.h2.jdbc.JdbcResultSet;
import org.h2.result.ResultInterface;

/**
 * @author kohii
 */
public class RunCommand extends SqlEditorCommandBase {

  @Override
  public void run(SqlEditor component) {
    SqlToolsDialog parentDialog = component.getDialog();
    parentDialog.stopTableNameEdition();

    String sql = component.getSQL();
    if (StringUtils.isEmpty(sql)) {
      throw new AppException("WSCA0008", "SQL");
    }

    try {
      Statement stmt = CCJSqlParserUtil.parse(sql);
      if (!(stmt instanceof Select)) {
        throw new AppException("WSCA0013");
      }
    } catch (JSQLParserException e) {
      throw new AppException("WSCA0014");
    }

    final SqlTaskWorker worker = new SqlTaskWorker(parentDialog, sql);
    new Thread(new Runnable() {
      @Override
      public void run() {
        initViews();

        boolean doContinue = worker.executeQuery();
        if (doContinue) {
          SqlHistory sqlHistory = SqlHistory.getInstance();
          sqlHistory.put(System.currentTimeMillis(), sql);
          sqlHistory.resetCursor();
          SwingUtilities
              .invokeLater(new Runnable() {
                @Override
                public void run() {

                  try {

                    ResultSet rs = worker.getResultSet();

                    SqlOutputOptionDialog outputOptionDialog = new SqlOutputOptionDialog(parentDialog);

                    int rowCount = getRowCount(rs);

                    if (rowCount == 0) {
                      throw new AppException("ISCA0013", rowCount);
                    }

                    outputOptionDialog.setMessage(MessageBundles.getString(
                        "ISCA0013",
                        String.valueOf(rowCount)));
                    if (outputOptionDialog.showDialog() == DialogOperation.OK) {
                      CsvMeta csvMeta = outputOptionDialog.getCsvMeta();

                      ResultSetMetaData resultSetMetaData = rs.getMetaData();
                      int columnCount = resultSetMetaData.getColumnCount();
                      String[] columnNames = new String[columnCount];
                      for (int i = 0; i < columnCount; i++) {
                        columnNames[i] = resultSetMetaData
                            .getColumnLabel(i + 1);
                      }

                      if (outputOptionDialog.getOption() == SqlOutputOptionDialog.FILE) {

                        CsvFileChooser fileChooser = CsvFileChooser.getInstance();

                        String name = CoreBundle.get("key.untitled");
                        switch (csvMeta.getDelimiter()) {
                          case ',':
                            name += ".csv";
                            break;
                          case '\t':
                            name += ".tsv";
                            break;
                          default:
                            name += ".txt";
                            break;
                        }
                        File file = new File(fileChooser.getCurrentDirectory(), name);
                        fileChooser.setSelectedFile(file);

                        switch (fileChooser.showSaveDialog()) {
                          case JFileChooser.APPROVE_OPTION:
                            file = fileChooser.getSelectedFile();
                            if (file.exists() && !file.canWrite()) {
                              try {
                                throw new AppException("WSCA0002", file.getCanonicalPath());
                              } catch (IOException e) {
                                throw new UnexpectedException();
                              }
                            }
                            break;
                          case JFileChooser.CANCEL_OPTION:
                            throw new CancellationException();
                          default:
                            throw new UnexpectedException();
                        }

                        try (SmoothCsvWriter writer =
                                 new SmoothCsvWriter(
                                     new OutputStreamWriter(new FileOutputStream(file), csvMeta.getCharset()),
                                     csvMeta,
                                     CsvWriteOption.of(csvMeta.getQuoteOption()))) {
                          if (outputOptionDialog.isUseHeader()) {
                            writer.writeRow(Arrays.asList(columnNames));
                          }

                          while (rs.next()) {
                            List<String> values = new ArrayList<>(columnCount);
                            for (int i = 0; i < columnCount; ++i) {
                              String val = rs.getString(i + 1);
                              values.add(val);
                            }
                            writer.writeRow(values);
                          }

                          MessageDialogs.alert("ISCA0014");
                        } catch (IOException | RuntimeException e) {
                          throw new UnexpectedException(e);
                        }

                      } else {
                        // Open in new tab

                        List<List<String>> rows = new ArrayList<>();

                        while (rs.next()) {
                          String[] row = new String[columnCount];
                          for (int i = 0; i < columnCount; ++i) {
                            row[i] = rs.getString(i + 1);
                          }
                          rows.add(Arrays.asList(row));
                        }

                        parentDialog.setVisible(false);

                        CsvSheetViewInfo viewInfo = new CsvSheetViewInfo(
                            null,
                            csvMeta,
                            CsvReadOption.DEFAULT
                        );
                        CsvGridSheetModel model;
                        if (outputOptionDialog.isUseHeader()) {
                          model = new CsvGridSheetModel(
                              rows,
                              rowCount,
                              columnCount,
                              Arrays.asList(columnNames)
                          );
                        } else {
                          model = new CsvGridSheetModel(
                              rows,
                              rowCount,
                              columnCount
                          );
                        }

                        OpenFileCommand.run(
                            viewInfo,
                            model,
                            SCTabbedPane.LAST
                        );
                      }
                    } else {
                      throw new IgnorableException();
                    }

                  } catch (Throwable t) {
                    ErrorHandlerFactory.getErrorHandler().handle(t);
                  } finally {
                    try {
                      Csvsql.closeQuery();
                    } catch (RuntimeException ignore) {
                    }
                  }
                }
              });
        } else {
          try {
            Csvsql.closeQuery();
          } catch (RuntimeException ignore) {
          }
        }
      }
    }).start();
  }

  private static int getRowCount(ResultSet resultSet) {
    JdbcResultSet jdbcResultSet = ((JdbcResultSet) resultSet);
    ResultInterface result = ReflectionUtils.get(
        jdbcResultSet,
        "result",
        ResultInterface.class);
    return result.getRowCount();
  }

  private void initViews() {
    dropAllViews();
    List<SqlCsvSheetTableInfo> tableInfoList = SqlTableDefinitions.getInstance().getTableInfoList();
    for (SqlCsvSheetTableInfo tableInfo : tableInfoList) {
      defineView(tableInfo.getName(), tableInfo.getCsvSheet().getViewId());
    }
  }

  private void dropAllViews() {
    try (ResultSet rs = Csvsql.executeQuery("SHOW TABLES")) {
      while (rs.next()) {
        String viewName = rs.getString(1);
        Csvsql.execute("DROP VIEW `" + viewName + "`");
      }
    } catch (InterruptedException | SQLException e) {
      throw new UnexpectedException(e);
    }
  }

  private void defineView(String viewName, int viewId) {
    try {
      Csvsql.execute(
          "CREATE VIEW `"
              + viewName
              + "` AS"
              + " SELECT * FROM "
              + "CSVSHEET_BY_ID(" + viewId + ")");
    } catch (SQLException e) {
      throw new UnexpectedException(e);
    }
  }
}
