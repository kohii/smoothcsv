package com.smoothcsv.core.csv;

import java.io.File;

import com.smoothcsv.framework.util.DirectoryResolver;
import com.smoothcsv.swing.components.History;
import lombok.Getter;

/**
 * @author kohii
 */
public class RecentFilesHistory extends History {

  @Getter
  private static final RecentFilesHistory instance = new RecentFilesHistory();

  private RecentFilesHistory() {
    super(new File(DirectoryResolver.instance().getSessionDirectory(), "recent_files.history"), false, 20);
  }
}
