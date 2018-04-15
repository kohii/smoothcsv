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
package com.smoothcsv.core.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.swing.components.LinkLabel;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

  public AboutDialog() {
    super(SCApplication.components().getFrame(), "About "
        + SCApplication.getApplication().getName());

    ResourceBundle bundle = ResourceBundle.getBundle("application");

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    getContentPane().setLayout(gridBagLayout);
    getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    ImageIcon imageIcon = SwingUtils.getImageIcon("/img/app/icon_128x128.png");
    Image image = imageIcon.getImage();
    Image newimg = image.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
    imageIcon = new ImageIcon(newimg);

    GridBagConstraints gbc_lblIcon = new GridBagConstraints();
    gbc_lblIcon.anchor = GridBagConstraints.NORTH;
    gbc_lblIcon.gridheight = 4;
    gbc_lblIcon.insets = new Insets(5, 5, 5, 20);
    gbc_lblIcon.gridx = 0;
    gbc_lblIcon.gridy = 0;
    getContentPane().add(new JLabel(imageIcon), gbc_lblIcon);

    JLabel lblAppname = new JLabel(SCApplication.getApplication().getName());
    SwingUtils.expandFontSize(lblAppname, 1.2f);
    GridBagConstraints gbc_lblAppname = new GridBagConstraints();
    gbc_lblAppname.anchor = GridBagConstraints.WEST;
    gbc_lblAppname.insets = new Insets(0, 0, 10, 0);
    gbc_lblAppname.gridx = 1;
    gbc_lblAppname.gridy = 0;
    getContentPane().add(lblAppname, gbc_lblAppname);

    String version = "Version " + bundle.getString("version.name");
    if (Env.isDebug()) {
      version += " (debug)";
    }

    JLabel lblVersion = new JLabel(version);
    GridBagConstraints gbc_lblVersion = new GridBagConstraints();
    gbc_lblVersion.anchor = GridBagConstraints.WEST;
    gbc_lblVersion.insets = new Insets(0, 0, 5, 0);
    gbc_lblVersion.gridx = 1;
    gbc_lblVersion.gridy = 1;
    getContentPane().add(lblVersion, gbc_lblVersion);

    String url = bundle.getString("site.url");
    JLabel lblSite = new LinkLabel(url, url);
    GridBagConstraints gbc_lblSite = new GridBagConstraints();
    gbc_lblSite.anchor = GridBagConstraints.WEST;
    gbc_lblSite.insets = new Insets(0, 0, 5, 0);
    gbc_lblSite.gridx = 1;
    gbc_lblSite.gridy = 2;
    getContentPane().add(lblSite, gbc_lblSite);

    JLabel lblCopyright =
        new JLabel("Copyright(C) " + bundle.getString("copyright.since") + " by "
            + bundle.getString("author.name"));
    GridBagConstraints gbc_lblCopyright = new GridBagConstraints();
    gbc_lblCopyright.insets = new Insets(0, 0, 10, 10);
    gbc_lblCopyright.anchor = GridBagConstraints.WEST;
    gbc_lblCopyright.gridx = 1;
    gbc_lblCopyright.gridy = 3;
    getContentPane().add(lblCopyright, gbc_lblCopyright);

    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    getRootPane().getActionMap().put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }
}
