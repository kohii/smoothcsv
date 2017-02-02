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
package com.smoothcsv.core.find;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.smoothcsv.commons.constants.Orientation;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.util.DirectoryResolver;
import com.smoothcsv.swing.components.History;
import com.smoothcsv.swing.components.HistoryTextBox;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionEvent;
import com.smoothcsv.swing.gridsheet.event.GridSheetSelectionListener;
import com.smoothcsv.swing.gridsheet.model.GridSheetSelectionModel;
import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.icon.AwesomeIconConstants;
import com.smoothcsv.swing.utils.SwingUtils;
import command.find.CountCommand;
import command.find.HideCommand;
import command.find.NextCommand;
import command.find.PreviousCommand;
import command.find.ReplaceAllCommand;
import command.find.ReplaceNextCommand;
import command.find.ReplacePreviousCommand;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class FindAndReplacePanel extends JPanel implements SmoothComponent,
    GridSheetSelectionListener {

  private static FindAndReplacePanel instance;

  public static FindAndReplacePanel getInstance() {
    if (instance == null) {
      instance = new FindAndReplacePanel();
    }
    return instance;
  }

  @Getter
  private SmoothComponentSupport componentSupport = new SmoothComponentSupport(this, "find-panel");

  private HistoryTextBox findWhatTextField;
  private HistoryTextBox replaceTextField;
  private JToggleButton btnCaseSensitive;
  private JToggleButton btnRegex;
  private JToggleButton btnWhole;
  private JToggleButton btnPreserveCase;

  private JCheckBox inSelectionChk;

  private JComboBox<Orientation> orientationCmb;

  private CsvSheetView selectedView;

  private FindAndReplacePanel() {

    setFocusCycleRoot(true);

    setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
    gridBagLayout.columnWeights =
        new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JButton btnClose = new JButton(AwesomeIcon.create(AwesomeIconConstants.FA_TIMES_CIRCLE));
    SwingUtils.removeButtonDecoration(btnClose);
    btnClose.setFocusable(false);
    btnClose.setToolTipText(CoreBundle.get("key.close"));
    btnClose.setBorder(null);
    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(HideCommand.class);
      }
    });
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.insets = new Insets(5, 5, 5, 5);
    gbc_button.gridx = 0;
    gbc_button.gridy = 0;
    add(btnClose, gbc_button);

    JLabel lblFind = new JLabel(CoreBundle.get("key.findWhat") + ":");
    GridBagConstraints gbc_lblFind = new GridBagConstraints();
    gbc_lblFind.anchor = GridBagConstraints.WEST;
    gbc_lblFind.gridx = 1;
    gbc_lblFind.gridy = 0;
    add(lblFind, gbc_lblFind);

    File historyDir = DirectoryResolver.instance().getSessionDirectory();
    History findHistory = new History(new File(historyDir, "find.history"), true);
    findWhatTextField = new HistoryTextBox(findHistory);
    findWhatTextField.disableEnterPressedKeyBinding();
    findWhatTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(NextCommand.class);
      }
    });
    JTextComponent findTextComp =
        (JTextComponent) findWhatTextField.getEditor().getEditorComponent();
    SwingUtils.addTextUpdateListener(findTextComp, e -> {
      FindAndReplaceParams.getInstance().setFindWhat(findTextComp.getText());
    });
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 2;
    gbc_textField.gridy = 0;
    add(findWhatTextField, gbc_textField);

    JButton btnFindPrevious = new JButton(AwesomeIcon.create(AwesomeIconConstants.FA_ARROW_LEFT));
    btnFindPrevious.setFocusable(false);
    btnFindPrevious.setToolTipText(CoreBundle.get("key.findPrev"));
    btnFindPrevious.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(PreviousCommand.class);
      }
    });
    GridBagConstraints gbc_btnFindPrevious = new GridBagConstraints();
    gbc_btnFindPrevious.anchor = GridBagConstraints.WEST;
    gbc_btnFindPrevious.gridx = 3;
    gbc_btnFindPrevious.gridy = 0;
    add(btnFindPrevious, gbc_btnFindPrevious);

    JButton btnFindNext = new JButton(AwesomeIcon.create(AwesomeIconConstants.FA_ARROW_RIGHT));
    btnFindNext.setFocusable(false);
    btnFindNext.setToolTipText(CoreBundle.get("key.findNext"));
    btnFindNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(NextCommand.class);
      }
    });
    GridBagConstraints gbc_btnNext = new GridBagConstraints();
    gbc_btnNext.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnNext.gridx = 4;
    gbc_btnNext.gridy = 0;
    add(btnFindNext, gbc_btnNext);

    JButton countBtn = new JButton(CoreBundle.get("key.count"));
    countBtn.setFocusable(false);
    countBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(CountCommand.class);
      }
    });
    GridBagConstraints gbc_countBtn = new GridBagConstraints();
    gbc_countBtn.fill = GridBagConstraints.HORIZONTAL;
    gbc_countBtn.gridx = 5;
    gbc_countBtn.gridy = 0;
    add(countBtn, gbc_countBtn);


    JToolBar toolBar = new JToolBar();
    toolBar.setFocusable(false);
    toolBar.setBorder(null);
    toolBar.setFloatable(false);
    GridBagConstraints gbc_toolBar = new GridBagConstraints();
    gbc_toolBar.anchor = GridBagConstraints.WEST;
    gbc_toolBar.gridx = 6;
    gbc_toolBar.gridy = 0;
    add(toolBar, gbc_toolBar);

    // toolBar.addSeparator();

    btnCaseSensitive = new JToggleButton("Aa");
    btnCaseSensitive.setFocusable(false);
    btnCaseSensitive.setToolTipText(CoreBundle.get("key.caseSensitive"));
    btnCaseSensitive.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        FindAndReplaceParams.getInstance().setCaseSensitive(btnCaseSensitive.isSelected());
      }
    });
    toolBar.add(btnCaseSensitive);

    btnRegex = new JToggleButton(".*");
    btnRegex.setFocusable(false);
    btnRegex.setToolTipText(CoreBundle.get("key.regex"));
    btnRegex.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        FindAndReplaceParams.getInstance().setUseRegex(btnRegex.isSelected());
      }
    });
    toolBar.add(btnRegex);

    btnWhole = new JToggleButton("“”");
    btnWhole.setFocusable(false);
    btnWhole.setToolTipText(CoreBundle.get("key.matchWholeCell"));
    btnWhole.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        FindAndReplaceParams.getInstance().setMatchWholeCell(btnWhole.isSelected());
      }
    });
    toolBar.add(btnWhole);

    // btnInSelection = new JToggleButton(AwesomeIcon.create(AwesomeIconConstants.FA_CIRCLE_THIN));
    // toolBar.add(btnInSelection);

    JLabel lblReplaceWith = new JLabel(CoreBundle.get("key.replaceWith") + ":");
    GridBagConstraints gbc_lblReplaceWith = new GridBagConstraints();
    gbc_lblReplaceWith.anchor = GridBagConstraints.WEST;
    gbc_lblReplaceWith.gridx = 1;
    gbc_lblReplaceWith.gridy = 1;
    add(lblReplaceWith, gbc_lblReplaceWith);

    History replaceHistory = new History(new File(historyDir, "replace.history"), true);
    replaceTextField = new HistoryTextBox(replaceHistory);
    replaceTextField.disableEnterPressedKeyBinding();
    replaceTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(ReplaceNextCommand.class);
      }
    });
    JTextComponent replaceTextComp =
        (JTextComponent) replaceTextField.getEditor().getEditorComponent();
    SwingUtils.addTextUpdateListener(replaceTextComp, e -> {
      FindAndReplaceParams.getInstance().setReplaceWith(replaceTextComp.getText());
    });
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_1.gridx = 2;
    gbc_textField_1.gridy = 1;
    add(replaceTextField, gbc_textField_1);

    JButton btnReplacePrev = new JButton(AwesomeIcon.create(AwesomeIconConstants.FA_ARROW_LEFT));
    btnReplacePrev.setFocusable(false);
    btnReplacePrev.setToolTipText(CoreBundle.get("key.replacePrev"));
    btnReplacePrev.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(ReplacePreviousCommand.class);
      }
    });
    GridBagConstraints gbc_btnReplace = new GridBagConstraints();
    gbc_btnReplace.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnReplace.gridx = 3;
    gbc_btnReplace.gridy = 1;
    add(btnReplacePrev, gbc_btnReplace);

    JButton btnReplaceNext = new JButton(AwesomeIcon.create(AwesomeIconConstants.FA_ARROW_RIGHT));
    btnReplaceNext.setFocusable(false);
    btnReplaceNext.setToolTipText(CoreBundle.get("key.replaceNext"));
    btnReplaceNext.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(ReplaceNextCommand.class);
      }
    });
    GridBagConstraints gbc_btnReplaceAll = new GridBagConstraints();
    gbc_btnReplaceAll.anchor = GridBagConstraints.WEST;
    gbc_btnReplaceAll.gridx = 4;
    gbc_btnReplaceAll.gridy = 1;
    add(btnReplaceNext, gbc_btnReplaceAll);

    JButton btnReplaceAll = new JButton(CoreBundle.get("key.replaceAll"));
    btnReplaceAll.setFocusable(false);
    btnReplaceAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CommandRegistry.instance().runCommand(ReplaceAllCommand.class);
      }
    });
    GridBagConstraints gbcBtnReplace = new GridBagConstraints();
    gbcBtnReplace.anchor = GridBagConstraints.WEST;
    gbcBtnReplace.gridx = 5;
    gbcBtnReplace.gridy = 1;
    add(btnReplaceAll, gbcBtnReplace);

    JToolBar toolBar_1 = new JToolBar();
    toolBar_1.setFocusable(false);
    toolBar_1.setBorder(null);
    toolBar_1.setFloatable(false);
    GridBagConstraints gbc_toolBar_1 = new GridBagConstraints();
    gbc_toolBar_1.anchor = GridBagConstraints.WEST;
    gbc_toolBar_1.gridx = 6;
    gbc_toolBar_1.gridy = 1;
    add(toolBar_1, gbc_toolBar_1);

    // toolBar_1.addSeparator();

    btnPreserveCase = new JToggleButton("A=A");
    btnPreserveCase.setFocusable(false);
    btnPreserveCase.setToolTipText(CoreBundle.get("key.preserveCase"));
    btnPreserveCase.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        FindAndReplaceParams.getInstance().setPreserveCase(btnPreserveCase.isSelected());
      }
    });
    toolBar_1.add(btnPreserveCase);

    Font f = btnCaseSensitive.getFont();
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, f.getSize());
    btnCaseSensitive.setFont(font);
    btnRegex.setFont(font);
    btnWhole.setFont(font);
    btnPreserveCase.setFont(font);

    JPanel panel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel.getLayout();
    flowLayout.setHgap(0);
    flowLayout.setVgap(0);
    flowLayout.setAlignment(FlowLayout.LEADING);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.gridwidth = 5;
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 2;
    gbc_panel.gridy = 2;
    add(panel, gbc_panel);

    JLabel lblDirection = new JLabel(CoreBundle.get("key.orientation") + ":");
    panel.add(lblDirection);

    orientationCmb = new JComboBox<Orientation>(Orientation.values());
    orientationCmb.setFocusable(false);
    orientationCmb.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        FindAndReplaceParams.getInstance().setDirection(
            (Orientation) orientationCmb.getSelectedItem());
      }
    });
    panel.add(orientationCmb);

    JLabel emptyLabel = new JLabel("    ");
    panel.add(emptyLabel);

    inSelectionChk = new JCheckBox(CoreBundle.get("key.inSelection"));
    inSelectionChk.setFocusable(false);
    inSelectionChk.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        FindAndReplaceParams.getInstance().setInSelection(inSelectionChk.isSelected());
      }
    });
    panel.add(inSelectionChk);

    JLabel emptyLabel2 = new JLabel("    ");
    panel.add(emptyLabel2);

    setFocusCycleRoot(true);
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());

    init();
  }

  private void init() {
    FindAndReplaceParams params = FindAndReplaceParams.getInstance();
    params.setFindWhat(((JTextComponent) findWhatTextField.getEditor().getEditorComponent())
        .getText());
    params.setReplaceWith(((JTextComponent) replaceTextField.getEditor().getEditorComponent())
        .getText());

    btnCaseSensitive.setSelected(params.isCaseSensitive());
    btnRegex.setSelected(params.isUseRegex());
    btnWhole.setSelected(params.isMatchWholeCell());
    btnPreserveCase.setSelected(params.isPreserveCase());
    inSelectionChk.setSelected(params.isInSelection());
    orientationCmb.setSelectedItem(params.getOrientation());

    FindAndReplaceParams.getInstance().addConditionChangeListener(
        () -> {
          if (!isVisible()) {
            return;
          }
          if (FindAndReplaceParams.getInstance().isUseRegex()) {
            Regex regex = FindAndReplaceParams.getInstance().getRegex();
            if (!regex.isValid()) {
              findWhatTextField.setToolTipText(regex.getError());
              ((JTextComponent) findWhatTextField.getEditor().getEditorComponent())
                  .setBackground(Color.PINK);
              return;
            }
          }
          findWhatTextField.setToolTipText(null);
          ((JTextComponent) findWhatTextField.getEditor().getEditorComponent())
              .setBackground(Color.WHITE);
          BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
          if (view != null && view instanceof CsvSheetView) {
            ((CsvSheetView) view).getGridSheetPane().getTable().repaint();
          }
        });
  }

  public void open() {
    if (selectedView != null) {
      selectedView.getGridSheetPane().getSelectionModel().removeGridSelectionListener(this);
    }
    selectedView = (CsvSheetView) SCApplication.components().getTabbedPane().getSelectedView();
    selectedView.add(this, BorderLayout.SOUTH);
    selectedView.revalidate();
    selectedView.getGridSheetPane().getSelectionModel().addGridSelectionListener(this);
    selectionChanged(new GridSheetSelectionEvent(selectedView.getGridSheetPane()
        .getSelectionModel(), 0, 0, 0, 0, false));
  }

  public void close() {
    if (selectedView != null) {
      selectedView.getGridSheetPane().getSelectionModel().removeGridSelectionListener(this);
      selectedView = null;
    }
    Container parent = getParent();
    parent.remove(this);
    parent.revalidate();
    parent.requestFocusInWindow();
  }

  public void initFocus() {
    findWhatTextField.getEditor().selectAll();
    findWhatTextField.requestFocusInWindow();
  }

  public void setFindWhatText(String s) {
    JTextComponent findTextComp =
        (JTextComponent) findWhatTextField.getEditor().getEditorComponent();
    findTextComp.setText(s);
  }

  public void saveFindWhatText() {
    findWhatTextField.saveCurrentItem();
  }

  public void saveReplaceText() {
    replaceTextField.saveCurrentItem();
  }

  @Override
  public void selectionChanged(GridSheetSelectionEvent e) {
    boolean isSingleCellSelected = ((GridSheetSelectionModel) e.getSource()).isSingleCellSelected();
    FindAndReplaceParams.getInstance().setInSelectionCheckboxEnabled(!isSingleCellSelected);
    inSelectionChk.setEnabled(!isSingleCellSelected);
  }
}
