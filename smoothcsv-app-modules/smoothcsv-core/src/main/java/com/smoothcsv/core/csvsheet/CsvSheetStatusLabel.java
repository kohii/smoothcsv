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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;

import com.smoothcsv.commons.utils.CharsetUtils;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCStatusBar;
import com.smoothcsv.framework.component.SCTabbedPane.ViewChangeEvent;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.swing.components.AnchorLabel;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetFocusListener;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;

import command.csvsheet.ShowPropertiesCommand;

/**
 * @author kohii
 *
 */
public class CsvSheetStatusLabel implements Runnable, PropertyChangeListener,
    GridSheetFocusListener {

  private static final CsvSheetStatusLabel INSTANCE = new CsvSheetStatusLabel();

  private final JLabel selectionPosLabel = new JLabel();
  private final AnchorLabel charsetLabel = new AnchorLabel();
  private final AnchorLabel lfLabel = new AnchorLabel();
  private final AnchorLabel propLabel = new AnchorLabel();

  /**
   * @return the instance
   */
  public static CsvSheetStatusLabel instance() {
    return INSTANCE;
  }

  public CsvSheetStatusLabel() {
    charsetLabel.setOnClick(this);
    lfLabel.setOnClick(this);
    propLabel.setOnClick(this);
  }

  public void install() {
    SCApplication.components().getTabbedPane().listeners()
        .on(ViewChangeEvent.class, new SCListener<ViewChangeEvent>() {
          @Override
          public void call(ViewChangeEvent event) {
            CsvSheetView oldView = (CsvSheetView) event.getOldView();
            CsvSheetView newView = (CsvSheetView) event.getNewView();
            if (oldView != null) {
              GridSheetSelectionModel sm = oldView.getGridSheetPane().getSelectionModel();
              sm.removeGridFocusListener(CsvSheetStatusLabel.this);
              oldView.getViewInfo().getPropertyChangeSupport()
                  .removePropertyChangeListener("csvMeta", CsvSheetStatusLabel.this);
            }
            if (newView != null) {
              GridSheetSelectionModel sm = newView.getGridSheetPane().getSelectionModel();
              sm.addGridFocusListener(CsvSheetStatusLabel.this);
              newView.getViewInfo().getPropertyChangeSupport()
                  .addPropertyChangeListener("csvMeta", CsvSheetStatusLabel.this);

              updateProperties(newView.getViewInfo().getCsvMeta());
              updateAnchorPos(sm.getRowAnchorIndex(), sm.getColumnAnchorIndex());
            } else {
              updateProperties(null);
              updateAnchorPos();
            }
          }
        });

    SCStatusBar statusBar = SCApplication.components().getStatusBar();
    statusBar.addStatusComponent(selectionPosLabel);
    statusBar.addStatusComponent(charsetLabel);
    statusBar.addStatusComponent(lfLabel);
    statusBar.addStatusComponent(propLabel);
  }

  private void updateProperties(CsvMeta csvMeta) {
    if (csvMeta == null) {
      selectionPosLabel.setText(" ");
      charsetLabel.setText(" ");
      lfLabel.setText(" ");
      propLabel.setText(" ");
      return;
    }
    charsetLabel.setText(CharsetUtils.getDisplayName(csvMeta.getCharset(), csvMeta.hasBom()));
    lfLabel.setText(csvMeta.getNewlineCharacter().toString());

    StringBuilder sb = new StringBuilder();
    sb.append(CoreBundle.get("key.delimiterChar"));
    if (csvMeta.getDelimiter() == '\t') {
      sb.append("=TAB");
    } else {
      sb.append("=[").append(csvMeta.getDelimiter()).append("]");
    }
    sb.append(" ").append(CoreBundle.get("key.quoteChar")).append("=[").append(csvMeta.getQuote())
        .append(']');
    propLabel.setText(sb.toString());
  }

  private void updateAnchorPos(int r, int c) {
    selectionPosLabel.setText((r + 1) + ":" + (c + 1));
  }

  private void updateAnchorPos() {
    selectionPosLabel.setText(" ");
  }

  // on label click
  @Override
  public void run() {
    new ShowPropertiesCommand().execute();
  }

  // on properties edited
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    updateProperties((CsvMeta) evt.getNewValue());
  }

  // on anchor selection changed
  @Override
  public void valueChanged(GridSheetFocusEvent e) {
    updateAnchorPos(e.getRow(), e.getColumn());
  }
}
