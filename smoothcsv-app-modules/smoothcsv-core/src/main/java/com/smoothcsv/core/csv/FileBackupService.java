package com.smoothcsv.core.csv;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.framework.util.DigestUtils;
import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author kohii
 */
@Slf4j
public class FileBackupService {

  @Getter
  private static FileBackupService instance = new FileBackupService();

  private Thread deleteDuplicateThread;

  public File backup(File file, boolean copy) {
    CoreSettings settings = CoreSettings.getInstance();
    if (!settings.getBoolean(CoreSettings.AUTO_BACKUP_ON_OVERWRITE)) {
      return null;
    }
    try {
      File backupFile = createBackupFile(file, file.lastModified());
      if (backupFile.exists()) {
        return backupFile;
      }

      if (copy) {
        Files.copy(file.toPath(), backupFile.toPath());
      } else {
        if (!file.renameTo(backupFile)) {
          return null;
        }
      }
      file.setLastModified(System.currentTimeMillis());

      synchronized (this) {

        if (deleteDuplicateThread != null) {
          deleteDuplicateThread.join();
        }

        if (settings.getBoolean(CoreSettings.NO_BACKUP_IF_SAME)) {
          // Delete the backup if it's same as last backup (Async)
          deleteDuplicateThread = new Thread(new Runnable() {
            @Override
            public void run() {
              File dir = getBackupFileDirectory(file);
              File lastBackupFile = FileUtils.getLatestFileFromDir(dir, backupFile);
              if (lastBackupFile == null) {
                return;
              }
              try {
                if (org.apache.commons.io.FileUtils.contentEquals(lastBackupFile, backupFile)) {
                  backupFile.delete();
                }
              } catch (IOException e) {
                log.error("", e);
              }
            }
          });
          deleteDuplicateThread.start();
        }
      }

      deleteOldBackups();

      return backupFile;
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }


  public void deleteAll() {
    File[] dirs = DirectoryResolver.instance().getBackupDirectory().listFiles(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });

    for (File dir : dirs) {
      try {
        org.apache.commons.io.FileUtils.deleteDirectory(dir);
      } catch (IOException e) {
        log.error("", e);
      }
    }
  }

  public void deleteOldBackups() {
    CoreSettings settings = CoreSettings.getInstance();
    if (!settings.getBoolean(CoreSettings.DELETE_OLD_BACKUPS)) {
      return;
    }
    int hours = settings.getInteger(CoreSettings.DELETE_BACKUP_N_HOURS_AGO, 24);
    deleteBackupsBefore(hours);
  }

  public void deleteBackupsBefore(int hours) {
    long threshold = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(hours);
    File[] dirs = DirectoryResolver.instance().getBackupDirectory().listFiles(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });
    for (File dir : dirs) {
      dir.listFiles(new FileFilter() {
        @Override
        public boolean accept(File f) {
          if (f.lastModified() < threshold) {
            f.delete();
          }
          return false;
        }
      });
      if (dir.list().length == 0) {
        dir.delete();
      }
    }
  }

  public File getLastBackup(File file) {
    File dir = getBackupFileDirectory(file);
    return FileUtils.getLatestFileFromDir(dir);
  }

  private File createBackupFile(File file, long lastModified) {
    File dir = getBackupFileDirectory(file);
    String backupFileName = String.valueOf(lastModified);
    return new File(dir, backupFileName);
  }

  private File getBackupFileDirectory(File file) {
    String path = FileUtils.getCanonicalPath(file);
    String hash;
    try {
      hash = DigestUtils.md5Hex(path);
    } catch (NoSuchAlgorithmException e) {
      throw new UnexpectedException(e);
    }
    File dir = DirectoryResolver.instance().getBackupDirectory();
    dir = new File(dir, hash);
    if (!dir.exists()) {
      FileUtils.ensureDirectoryExists(dir);
    }
    return dir;
  }
}
