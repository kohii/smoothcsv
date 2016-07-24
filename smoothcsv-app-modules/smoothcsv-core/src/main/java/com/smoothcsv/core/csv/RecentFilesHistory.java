package com.smoothcsv.core.csv;

import com.smoothcsv.framework.util.DirectoryResolver;
import com.smoothcsv.swing.components.History;
import lombok.Getter;

import java.io.File;

/**
 * @author kohei
 */
public class RecentFilesHistory extends History {

  @Getter
  private static final RecentFilesHistory instance = new RecentFilesHistory();

  private RecentFilesHistory() {
    super(new File(DirectoryResolver.instance().getSessionDirectory(), "recent_files.history"), false, 20);
  }
}
