package com.smoothcsv.core.sql.engine;

import java.awt.Dialog;
import java.sql.ResultSet;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import com.smoothcsv.core.component.AsyncTaskDialog;
import com.smoothcsv.core.sql.engine.core.Csvsql;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.util.SCBundle;
import org.apache.commons.lang3.mutable.MutableObject;

public class SqlTaskWorker {

  private AsyncTaskDialog.Monitor monitor;

  private String sql;
  private ResultSet resultSet;

  public SqlTaskWorker(Dialog owner, String sql) {
    this.sql = sql;
    monitor = new AsyncTaskDialog.Monitor(owner, SCBundle.get("key.sql.runningQuery"), true);
    monitor.setDialogSize(200, 150);
    monitor.setText1(SCBundle.get("key.sql.runningQuery"));
  }

  public boolean executeQuery() {

    Throwable throwable = null;
    try {

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          monitor.begin();
        }
      });

      final MutableObject<Integer> status = new MutableObject<>(0);
      final MutableObject<Throwable> error = new MutableObject<>();

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            resultSet = Csvsql.executeQuery(sql);
            status.setValue(1);
          } catch (Exception e) {
            status.setValue(2);
            error.setValue(e);
          }
        }
      }).start();
      while (status.getValue() == 0) {
        if (monitor.isCanceled()) {
          Csvsql.close();
          status.setValue(2);
          break;
        }
        Thread.sleep(500);
      }
      if (status.getValue() == 2) {
        if (error.getValue() != null) {
          if (!monitor.isCanceled()) {
            throw error.getValue();
          }
        } else {
          throw new InterruptedException();
        }
      }
      return status.getValue() == 1;

    } catch (InterruptedException e) {
      return false;
    } catch (Throwable t) {
      throwable = t;
      return false;
    } finally {
      Throwable _throwable = throwable;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (monitor.isDialogVisible()) {
            monitor.end();
          }
          if (_throwable != null) {
            ErrorHandlerFactory.getErrorHandler().handle(_throwable);
          }
        }
      });
    }
  }

  public void handleResult(Consumer<ResultSet> handler) {
    handler.accept(resultSet);
  }

  /**
   * @return resultSet
   */
  public ResultSet getResultSet() {
    return resultSet;
  }
}