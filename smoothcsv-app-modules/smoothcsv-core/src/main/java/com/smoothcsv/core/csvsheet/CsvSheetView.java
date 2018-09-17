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
package com.smoothcsv.core.csvsheet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;

import com.smoothcsv.core.ApplicationStatus;
import com.smoothcsv.core.csvsheet.edits.GridSheetUndoManager;
import com.smoothcsv.core.find.FindAndReplacePanel;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusListener;
import com.smoothcsv.swing.gridsheet.model.DefaultGridSheetSelectionModel;
import command.app.CloseCommand;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvSheetView extends BaseTabView<CsvSheetViewInfo> {

  @Getter
  private CsvGridSheetPane gridSheetPane;

  public CsvSheetView(CsvSheetViewInfo viewInfo, CsvGridSheetModel model) {
    super(viewInfo);
    init(model);
    PropertyChangeListener filePropListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        updateTitle();
      }
    };
    viewInfo.getPropertyChangeSupport().addPropertyChangeListener("file", filePropListener);
    filePropListener.propertyChange(null);

    GridSheetUndoManager undoManager = gridSheetPane.getUndoManager();
    undoManager.listeners().on(GridSheetUndoManager.StateChangeEvent.class, e -> {
      JButton closeButton = getTabComponent().getCloseButton();
      if (e.isSavePoint()) {
        closeButton.setIcon(BaseTabView.CloseTabIcon.INSTANCE);
      } else {
        closeButton.setIcon(DirtyCloseTabIcon.INSTANCE);
      }

      if (Env.getOS() == Env.OS_WINDOWS) {
        updateTitle();
      }
    });
  }

  private void updateTitle() {
    CsvSheetViewInfo viewInfo = getViewInfo();
    File file = viewInfo.getFile();
    String shortTitle;
    String fullTitle;
    if (file != null) {
      shortTitle = file.getName();
      try {
        fullTitle = file.getCanonicalPath();
      } catch (IOException e) {
        fullTitle = shortTitle;
      }
    } else {
      shortTitle = fullTitle = CoreBundle.get("key.untitled");
    }

    if (Env.getOS() == Env.OS_WINDOWS) {
      if (!gridSheetPane.getUndoManager().isSavepoint()) {
        fullTitle += " *";
      }
    }

    viewInfo.setShortTitle(shortTitle);
    viewInfo.setFullTitle(fullTitle);
  }

  @Override
  protected SmoothComponentSupport createComponentSupport() {
    return new SmoothComponentSupport(this, "csv-sheet");
  }

  private void init(CsvGridSheetModel model) {
    gridSheetPane = new CsvGridSheetPane(this, model);
    add(gridSheetPane);

    gridSheetPane.getSelectionModel().addGridFocusListener(new GridSheetFocusListener() {
      @Override
      public void valueChanged(GridSheetFocusEvent e) {
        showCellValueOnValuePanel();
      }
    });

    gridSheetPane.getTable().addPropertyChangeListener("gridCellEditor",
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getNewValue() == null) {
              showCellValueOnValuePanel();
            }
          }
        });
  }

  @Override
  protected void onCloseIconClicked() {
    CloseCommand.close(this);
  }

  @Override
  public boolean requestFocusInWindow() {
    return gridSheetPane.getTable().requestFocusInWindow();
  }

  @Override
  public void requestFocus() {
    gridSheetPane.getTable().requestFocus();
  }

  @Override
  protected void onTabActivated() {
    super.onTabActivated();
    if (ApplicationStatus.getInstance().isFindAndReplacePanelVisible()) {
      FindAndReplacePanel findAndReplacePanel = FindAndReplacePanel.getInstance();
      findAndReplacePanel.open();
    }
    showCellValueOnValuePanel();
  }

  @Override
  protected void onTabDeactivated() {
    getGridSheetPane().stopCellEditingIfEditing();
  }

  public void showCellValueOnValuePanel() {
    DefaultGridSheetSelectionModel sm = gridSheetPane.getSelectionModel();
    int r = sm.getRowAnchorIndex();
    int c = sm.getColumnAnchorIndex();
    String val;
    if (r < 0 || c < 0) {
      val = "";
    } else {
      val = (String) gridSheetPane.getModel().getValueAt(r, c);
    }
    CsvGridSheetCellValuePanel.getInstance().showCellValue(val);
  }

  protected static class DirtyCloseTabIcon extends BaseTabView.CloseTabIcon {

    private static Color COLOR = new Color(100, 140, 255);

    protected static final DirtyCloseTabIcon INSTANCE = new DirtyCloseTabIcon();

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.translate(x, y);
      g.setColor(COLOR);
      g2.setStroke(new BasicStroke(2F));
      g.drawOval(5, 5, 6, 6);
      g.translate(-x, -y);
    }
  }
}
