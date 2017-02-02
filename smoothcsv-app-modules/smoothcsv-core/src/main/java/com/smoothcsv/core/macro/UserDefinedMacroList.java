package com.smoothcsv.core.macro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.ArrayCsvWriter;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.framework.util.DirectoryResolver;

/**
 * @author kohii
 */
public class UserDefinedMacroList {

  private static UserDefinedMacroList instance;

  public static UserDefinedMacroList getInstance() {
    if (instance == null) {
      instance = new UserDefinedMacroList();
    }
    return instance;
  }

  private File confFile = new File(DirectoryResolver.instance().getSettingDirectory(), "macros.tsv");

  private final List<MacroInfo> macroInfoList = new ArrayList<>();

  private List<Consumer<List<MacroInfo>>> listeners;

  public UserDefinedMacroList() {
    load();
  }

  public List<MacroInfo> getMacroInfoList() {
    return Collections.unmodifiableList(macroInfoList);
  }

  public void add(File... files) {
    for (File file : files) {
      if (!file.exists() || !file.isFile() || !file.canRead()) {
        throw new AppException("WSCC0001", file);
      }
      boolean alreadyExists = false;
      for (MacroInfo mi : macroInfoList) {
        if (mi.getFile().equals(file)) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        macroInfoList.add(new MacroInfo(file));
      }
    }
    sortList();
    save();
  }

  public void remove(int index) {
    macroInfoList.remove(index);
    save();
  }

  public void addListener(Consumer<List<MacroInfo>> listener) {
    if (listeners == null) {
      listeners = new ArrayList<>(2);
    }
    listeners.add(listener);
  }

  public void save() {
    FileUtils.ensureWritable(confFile);
    try (OutputStream os = new FileOutputStream(confFile);
         ArrayCsvWriter writer =
             new ArrayCsvWriter(new OutputStreamWriter(os, "UTF-8"), CsvSupport.TSV_PROPERTIES)) {
      for (MacroInfo macroInfo : macroInfoList) {
        writer.writeRow(new String[]{macroInfo.getFilePath()});
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }

    for (Consumer<List<MacroInfo>> listener : listeners) {
      listener.accept(macroInfoList);
    }
  }

  private void load() {
    macroInfoList.clear();
    if (confFile.exists()) {
      try (InputStream in = new FileInputStream(confFile);
           ArrayCsvReader reader =
               new ArrayCsvReader(new InputStreamReader(in, "UTF-8"), CsvSupport.TSV_PROPERTIES,
                   CsvSupport.SKIP_EMPTYROW_OPTION, 2)) {
        String[] rowData;
        while ((rowData = reader.readRow()) != null) {
          macroInfoList.add(new MacroInfo(rowData[0]));
        }
        sortList();
      } catch (IOException e) {
        throw new UnexpectedException(e);
      }
    }
  }

  private void sortList() {
    Collections.sort(macroInfoList);
  }
}
