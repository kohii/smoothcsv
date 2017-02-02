package com.smoothcsv.core.update;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.core.util.AppUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.PlainLabel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
public class UpdateInfoDialog extends DialogBase {

  public static DialogOperation DOWNLOAD = new DialogOperation(SCBundle.get("key.update.download"));
  public static DialogOperation REMIND_ME_LATER = new DialogOperation(SCBundle.get("key.update.remindMeLater"));
  public static DialogOperation IGNORE = new DialogOperation(SCBundle.get("key.update.ignore"));
  public static DialogOperation CLOSE = new DialogOperation("", false);

  public enum Choice {
    DOWNLOAD, REMIND_ME_LATER, IGNORE
  }

  private String newVersion;

  public UpdateInfoDialog(String currentVersion, String newVersion, String message) {
    super(SCApplication.components().getFrame(), "Update Info");

    this.newVersion = newVersion;

    getContentPanel().add(new PlainLabel(SCBundle.get("key.update.message")), BorderLayout.NORTH);
    JPanel mainPanel = new JPanel();
    GridBagLayout layout = new GridBagLayout();
    layout.columnWeights = new double[]{0, 1};
    mainPanel.setLayout(layout);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    mainPanel.add(new PlainLabel(SCBundle.get("key.update.currentVersion") + ":  "), gbc);

    JLabel currentVersionLabel = new PlainLabel(currentVersion);
    gbc.gridx = 1;
    gbc.gridy = 0;
    mainPanel.add(currentVersionLabel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    mainPanel.add(new PlainLabel(SCBundle.get("key.update.newVersion") + ":  "), gbc);

    JLabel newVersionLabel = new PlainLabel(newVersion);
    gbc.gridx = 1;
    gbc.gridy = 1;
    mainPanel.add(newVersionLabel, gbc);

    getContentPanel().add(mainPanel, BorderLayout.CENTER);

    if (StringUtils.isNotBlank(message)) {
      getContentPanel().add(new PlainLabel(message), BorderLayout.SOUTH);
    }

    setCloseAction(CLOSE);
  }

  @Override
  protected DialogOperation[] getDialogOperations() {
    return new DialogOperation[]{DOWNLOAD, REMIND_ME_LATER, IGNORE, CLOSE};
  }

  @SneakyThrows
  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    Choice choice;
    if (selectedOperation == DOWNLOAD) {
      choice = Choice.DOWNLOAD;
      Desktop.getDesktop().browse(new URI(AppUtils.createUrl("download/latest")));
    } else if (selectedOperation == REMIND_ME_LATER) {
      choice = Choice.REMIND_ME_LATER;
    } else if (selectedOperation == IGNORE) {
      choice = Choice.IGNORE;
    } else {
      choice = null;
    }

    UpdateSettings settings = UpdateSettings.getInstance();
    settings.save(UpdateSettings.LAST_CHOICE, choice);
    settings.save(UpdateSettings.LAST_VERSION, newVersion);
    return true;
  }
}
