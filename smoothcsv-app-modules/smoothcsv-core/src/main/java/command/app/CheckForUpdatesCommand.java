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
package command.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.smoothcsv.commons.utils.JsonUtils;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.update.UpdateInfoDialog;
import com.smoothcsv.core.update.UpdateSettings;
import com.smoothcsv.core.util.AppUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.util.MessageBundles;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author kohii
 */
public class CheckForUpdatesCommand extends Command {

  private static Thread thread;

  @Override
  public void run() {
    run(true);
  }

  public static void run(boolean invokedManually) {
    if (thread != null) {
      return;
    }

    thread = new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          ResourceBundle bundle = ResourceBundle.getBundle("application");

          Map<String, Object> map = getLatestVersion();
          String newVersion = map != null ? (String) map.get("newVersion") : null;
          if (StringUtils.isEmpty(newVersion)) {
            if (invokedManually) {
              showError();
            }
            return;
          }

          UpdateSettings settings = UpdateSettings.getInstance();
          settings.save(UpdateSettings.LAST_CHECKED, System.currentTimeMillis());

          String currentVersion = bundle.getString("version.name");
          String lang = Locale.getDefault().getLanguage();
          String message = (String) map.get("message_" + lang);
          if (StringUtils.isEmpty(message)) {
            message = (String) map.get("message");
          }

          if (!currentVersion.equalsIgnoreCase(newVersion)) {

            if (!invokedManually) {
              String choice = settings.get(UpdateSettings.LAST_CHOICE, "");
              String lastVersion = settings.get(UpdateSettings.LAST_VERSION, "");
              if (UpdateInfoDialog.Choice.IGNORE.name().equals(choice) && newVersion.equals(lastVersion)) {
                return;
              }
            }

            showResult(currentVersion, newVersion, message);
          } else {
            if (invokedManually) {
              showResult(currentVersion, newVersion, message);
            }
          }
        } finally {
          thread = null;
        }
      }
    });
    thread.start();
  }

  private static Map<String, Object> getLatestVersion() {
    try {
      URL url = new URL(AppUtils.createUrl("v1/versions/latest-mac.json"));
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setReadTimeout(30000);
      http.connect();
      int statusCode = http.getResponseCode();
      if (statusCode < 200 || 299 < statusCode) {
        return null;
      }
      StringBuilder sb = new StringBuilder();
      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(http.getInputStream()))) {
        String buf;
        while ((buf = br.readLine()) != null) {
          sb.append(buf);
        }
      }
      String responseBody = sb.toString();
      if (StringUtils.isEmpty(responseBody)) {
        return null;
      }
      return JsonUtils.parse(responseBody, new TypeReference<Map<String, Object>>() {});

    } catch (Exception e) {
      return null;
    }
  }

  private static void showResult(String currentVersion, String newVersion, String message) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (!currentVersion.equalsIgnoreCase(newVersion)) {
          UpdateInfoDialog dialog = new UpdateInfoDialog(currentVersion, newVersion, message);
          dialog.pack();
          dialog.setLocationRelativeTo(dialog.getParent());
          dialog.setVisible(true);
        } else {
          MessageDialogs.showMessageString(SCApplication.components().getFrame(),
              MessageBundles.getString("ISCA0012"), JOptionPane.INFORMATION_MESSAGE);
        }
      }
    });
  }

  private static void showError() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        MessageDialogs.alert("WSCA0011");
      }
    });
  }
}
