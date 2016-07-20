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

import com.smoothcsv.core.celleditor.SCTextArea;
import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.core.util.SCAppearanceManager;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCContentPane;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.menu.CommandMenuItem;
import com.smoothcsv.swing.icon.AwesomeIcon;
import command.grid.StartEditCommand;
import command.grid.StopEditCommand;
import lombok.Getter;
import lombok.Setter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvGridSheetCellValuePanel extends JPanel implements FocusListener, DocumentListener {

  @Getter
  private static CsvGridSheetCellValuePanel instance = new CsvGridSheetCellValuePanel();

  private JDialog dialog;

  @Getter
  private boolean floating = false;

  @Getter
  private boolean valuePanelVisible = false;

  @Getter
  private final ValuePanelTextArea textArea;

  @Getter
  private final UndoManager undoManager = new UndoManager();

  private CsvGridSheetCellValuePanel() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.getDefaultBorderColor()));
    JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, UIConstants.getDefaultBorderColor()));

    textArea = new ValuePanelTextArea(this);
    textArea.setBorder(null);
    textArea.setLineWrap(true);
    scrollPane.setViewportView(textArea);

    add(scrollPane);

    SCToolBar toolBar = createToolBar();
    add(toolBar, BorderLayout.WEST);

    textArea.getDocument().addDocumentListener(this);
    textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
      }
    });


    textArea.addFocusListener(this);

    reloadPanelHeight();
  }

  public void reloadPanelHeight() {
    int h = CoreSettings.getInstance().getInteger(CoreSettings.VALUE_PANEL_HEIGHT);
    Dimension size = getSize();
    size.height = textArea.getLineHeight() * h + 1;
    setPreferredSize(size);
    revalidate();
  }

  private SCToolBar createToolBar() {

    SCToolBar toolBar = new SCToolBar();
    toolBar.setDefaultButtonBorder(null);

    JPopupMenu popupMenu = new JPopupMenu();

    CommandMenuItem menuItemExpand = new CommandMenuItem(CoreBundle.get("key.expand"),
        "value_panel:Expand", null, AwesomeIcon.create(AwesomeIcon.FA_EXPAND), true, true);
    popupMenu.add(menuItemExpand);

    CommandMenuItem menuItemCompress = new CommandMenuItem(CoreBundle.get("key.compress"),
        "value_panel:Compress", null, AwesomeIcon.create(AwesomeIcon.FA_COMPRESS), true, true);
    popupMenu.add(menuItemCompress);

    CommandMenuItem menuItemToggleFloating =
        new CommandMenuItem(CoreBundle.get("key.toggleFloating"), "value_panel:ToggleFloating", null, null, true, true);
    popupMenu.add(menuItemToggleFloating);

    JToggleButton dropDownButton =
        new JToggleButton(AwesomeIcon.create(AwesomeIcon.FA_CHEVRON_DOWN, 14));
    dropDownButton.setFocusable(false);
    dropDownButton.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          popupMenu.show(dropDownButton, 0, dropDownButton.getHeight());
        }
      }
    });

    popupMenu.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        dropDownButton.setSelected(true);
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        dropDownButton.setSelected(false);
      }

      public void popupMenuCanceled(PopupMenuEvent e) {
        dropDownButton.setSelected(false);
      }
    });

    toolBar.add(dropDownButton);

    // toolBar.add("value-panel:expand", AwesomeIcon.FA_EXPAND, "Expand");
    // toolBar.add("value-panel:compress", AwesomeIcon.FA_COMPRESS, "Compress");

    return toolBar;
  }

  public void showCellValue(String text) {
    textArea.setText(text);
    textArea.setCaretPosition(0);
  }

  public void toggleFloating() {
    if (!valuePanelVisible) {
      return;
    }
    floating = !floating;
    if (floating) {
      dialog = new JDialog(SCApplication.components().getFrame(), false);
      SCContentPane contentPane = new SCContentPane("floatingvaluepanel-body");
      contentPane.addPseudoClass("floaring");
      dialog.setContentPane(contentPane);
      dialog.setFocusableWindowState(false);
      dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      Dimension d = getSize();
      d.height = Math.max(d.height, textArea.getLineHeight() * 3 + 1);
      dialog.getContentPane().add(this);
      setPreferredSize(d);
      dialog.pack();
      dialog.setLocationRelativeTo(SCApplication.components().getFrame());
      dialog.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          toggleFloating();
        }

        @Override
        public void windowOpened(WindowEvent e) {
          dialog.setFocusableWindowState(true);
        }
      });
      dialog.setVisible(true);
    } else {
      dialog.getContentPane().remove(this);
      BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
      if (view != null && view instanceof CsvSheetView) {
        CsvSheetView csvView = (CsvSheetView) view;
        csvView.add(getInstance(), BorderLayout.NORTH);
        if (csvView.getGridSheetPane().isEditing()) {
          new StopEditCommand().execute();
        }
      }
      dialog.dispose();
      dialog = null;
      reloadPanelHeight();
    }
    SCApplication.components().getFrame().revalidate();
  }

  public void setValuePanelVisible(boolean b) {
    valuePanelVisible = b;

    if (b) {
      BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
      if (view != null) {
        view.add(getInstance(), BorderLayout.NORTH);
      }
    } else {
      if (floating) {
        toggleFloating();
      }
      BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
      if (view != null) {
        view.remove(getInstance());
      }
    }
    SCApplication.components().getTabbedPane().revalidate();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void changedUpdate(DocumentEvent e) {

  }

  @Override
  public void focusGained(FocusEvent e) {
    BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
    if (view != null && view instanceof CsvSheetView) {
      CsvGridSheetPane gridSheetPane = ((CsvSheetView) view).getGridSheetPane();
      if (!gridSheetPane.isEditing()) {
        new StartEditCommand().run(gridSheetPane, false);
      }
    }
  }

  @Override
  public void focusLost(FocusEvent e) {
  }

  public static class ValuePanelTextArea extends SCTextArea implements SmoothComponent {

    @Getter
    @Setter
    private boolean keyRecording;

    @Getter
    private final CsvGridSheetCellValuePanel valuePanel;

    public ValuePanelTextArea(CsvGridSheetCellValuePanel valuePanel) {
      super("value-panel");
      setFont(SCAppearanceManager.getCelleditorFont());
      this.valuePanel = valuePanel;
    }

    @Override
    public void replaceSelection(String content) {
      super.replaceSelection(content);
      if (keyRecording) {
        MacroRecorder.getInstance().recordKeyTyping(content);
      }
    }
  }
}
