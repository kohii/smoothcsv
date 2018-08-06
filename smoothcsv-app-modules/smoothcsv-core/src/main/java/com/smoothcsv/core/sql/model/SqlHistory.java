package com.smoothcsv.core.sql.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.csv.prop.CsvProperties;
import com.smoothcsv.csv.prop.QuoteEscapeRule;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.ArrayCsvWriter;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;

@Slf4j
public class SqlHistory {

  private static final CsvProperties CSV_PROPERTIES
      = CsvProperties.of(',', '"', QuoteEscapeRule.repeatQuoteChar());

  private List<ImmutablePair<Long, String>> sqlData = new ArrayList<>();

  @Getter
  private static SqlHistory instance = new SqlHistory();

  private final File file = new File(DirectoryResolver.instance().getSessionDirectory(), "sql-history.sql");

  private boolean needToSave = false;

  private int cursor = -1;

  public void save() {
    if (needToSave) {
      FileUtils.ensureWritable(file);
      try (ArrayCsvWriter writer = new ArrayCsvWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), CSV_PROPERTIES)) {
        for (ImmutablePair<Long, String> entry : sqlData) {
          writer.writeRow(new String[]{entry.getLeft().toString(), entry.getRight()});
        }
      } catch (IOException ex) {
        throw new UnexpectedException(ex);
      }
    }
    needToSave = false;
  }

  public void put(long time, String sql) {
    sql = sql.trim();
    String pre = sqlData.size() == 0 ? null : sqlData.get(
        sqlData.size() - 1).getValue();
    if (!sql.equals(pre)) {
      sqlData.add(ImmutablePair.of(time, sql));
      if (20 < sqlData.size()) {
        sqlData.remove(0);
      }
    }
    needToSave = true;
  }

  public String get(int i) {
    if (cursor == -1) {
      return "";
    }
    return sqlData.get(size() - 1 - i).getValue();
  }

  public String get() {
    return sqlData.get(cursor).getValue();
  }

  public void resetCursor() {
    cursor = 0;
  }

  public boolean hasNext() {
    if (sqlData.size() < cursor + 2) {
      return false;
    } else {
      return true;
    }
  }

  public boolean hasPrev() {
    if (cursor == -1) {
      return false;
    } else {
      return true;
    }
  }

  public String next() {
    cursor++;
    return get(cursor);
  }

  public String prev() {
    cursor--;
    return get(cursor);
  }

  public int size() {
    return sqlData.size();
  }

  public void removeAll() {
    sqlData.clear();
    needToSave = true;
    cursor = -1;
  }

  public void load() {

    sqlData.clear();
    if (!file.exists()) {
      return;
    }

    try (InputStream in = new FileInputStream(file);
         ArrayCsvReader reader = new ArrayCsvReader(new InputStreamReader(in, StandardCharsets.UTF_8),
             CSV_PROPERTIES, CsvSupport.SKIP_EMPTYROW_OPTION, 3)) {
      String[] rowData;
      while ((rowData = reader.readRow()) != null) {
        sqlData.add(ImmutablePair.of(Long.parseLong(rowData[0]), rowData[1]));
      }
    } catch (Exception e) {
      log.error("Cannot read file: {}", file, e);
    }
  }

  public List<ImmutablePair<Long, String>> getAll() {
    return sqlData;
  }

  public void setCursor(int cursor) {
    this.cursor = cursor;
  }
}
