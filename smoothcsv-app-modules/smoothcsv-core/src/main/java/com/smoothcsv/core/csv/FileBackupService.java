package com.smoothcsv.core.csv;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.util.DigestUtils;
import com.smoothcsv.framework.util.DirectoryResolver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;

/**
 * @author kohii
 */
@Slf4j
public class FileBackupService {

  @Getter
  private static FileBackupService instance = new FileBackupService();

  private Thread deleteDuplicateThread;

  public File backup(File file, boolean copy) {
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

      synchronized (this) {

        if (deleteDuplicateThread != null) {
          deleteDuplicateThread.join();
        }

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

      return backupFile;
    } catch (Exception e) {
      log.error("", e);
      return null;
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
