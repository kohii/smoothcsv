package com.smoothcsv.core;

import java.io.IOException;
import java.util.HashMap;

import com.smoothcsv.commons.utils.JsonUtils;
import com.smoothcsv.core.csv.FileBackupService;
import com.smoothcsv.core.preference.EditorPrefPanel;
import com.smoothcsv.core.preference.GeneralPrefPanel;
import com.smoothcsv.core.preference.KeyBindingsPrefPanel;
import com.smoothcsv.framework.preference.PrefPage;
import com.smoothcsv.framework.preference.PreferenceManager;
import command.app.CheckForUpdatesCommand;

/**
 * @author kohii
 */
public class AfterStartupTask implements Runnable {

  @Override
  public void run() {
    PreferenceManager.getInstance()
        .addPrefPage(new PrefPage("key.pref.title.general", GeneralPrefPanel.class));
    PreferenceManager.getInstance()
        .addPrefPage(new PrefPage("key.pref.title.editor", EditorPrefPanel.class));
    PreferenceManager.getInstance()
        .addPrefPage(new PrefPage("key.pref.title.keyBindings", KeyBindingsPrefPanel.class));

    // Execute JsonUtils.stringify() so that it can perform faster next time
    try {
      JsonUtils.stringify(new HashMap<>());
    } catch (IOException ignore) {}

    FileBackupService.getInstance().deleteOldBackups();

    CheckForUpdatesCommand.run(false);
  }
}
