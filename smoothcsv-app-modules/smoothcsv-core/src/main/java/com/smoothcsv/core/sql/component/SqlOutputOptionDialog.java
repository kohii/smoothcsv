package com.smoothcsv.core.sql.component;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.core.component.CsvPropertiesDialog;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetSupport;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.ExButtonGroup;
import com.smoothcsv.swing.components.ExRadioButton;
import lombok.Getter;


public class SqlOutputOptionDialog extends DialogBase {

  public static final int NEW_TAB = 0;
  public static final int CURRENT_TAB = 1;
  public static final int FILE = 2;

  @Getter
  private int option = -1;

  private ExRadioButton radioButton_2;
  private ExRadioButton radioButton;
  @Getter
  private CsvMeta csvMeta;
  private JLabel msgLabel;

  @Getter
  private boolean useHeader;

  public SqlOutputOptionDialog(Dialog owner) {
    super(owner, SmoothCsvApp.getApplication().getName());

    getContentPanel().setLayout(new BorderLayout(10, 10));

    msgLabel = new JLabel("");
    getContentPanel().add(msgLabel, BorderLayout.NORTH);

    JPanel panel = new JPanel();
    getContentPanel().add(panel, BorderLayout.CENTER);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[]{0, 0, 0, 0};
    gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
    gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0,
        Double.MIN_VALUE};
    gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
        Double.MIN_VALUE};
    panel.setLayout(gbl_panel);

    radioButton = new ExRadioButton<>(NEW_TAB, SCBundle.get("key.sql.output.newTab"));

    label_1 = new JLabel(SCBundle.get("key.sql.outputDest") + ":");
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.anchor = GridBagConstraints.WEST;
    gbc_label_1.gridwidth = 3;
    gbc_label_1.insets = new Insets(5, 0, 5, 0);
    gbc_label_1.gridx = 0;
    gbc_label_1.gridy = 0;
    panel.add(label_1, gbc_label_1);

    label = new JLabel("　　");
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.insets = new Insets(0, 0, 5, 5);
    gbc_label.gridx = 0;
    gbc_label.gridy = 1;
    panel.add(label, gbc_label);
    GridBagConstraints gbc_radioButton = new GridBagConstraints();
    gbc_radioButton.gridwidth = 2;
    gbc_radioButton.anchor = GridBagConstraints.WEST;
    gbc_radioButton.gridx = 1;
    gbc_radioButton.gridy = 1;
    panel.add(radioButton, gbc_radioButton);

    // radioButton_1 = new JRadioButton("");
    // GridBagConstraints gbc_radioButton_1 = new GridBagConstraints();
    // gbc_radioButton_1.gridwidth = 2;
    // gbc_radioButton_1.anchor = GridBagConstraints.WEST;
    // gbc_radioButton_1.gridx = 1;
    // gbc_radioButton_1.gridy = 2;
    // panel.add(radioButton_1, gbc_radioButton_1);

    radioButton_2 = new ExRadioButton<>(FILE, SCBundle.get("key.sql.output.writeFile"));
    GridBagConstraints gbc_radioButton_2 = new GridBagConstraints();
    gbc_radioButton_2.insets = new Insets(0, 0, 5, 5);
    gbc_radioButton_2.anchor = GridBagConstraints.WEST;
    gbc_radioButton_2.gridx = 1;
    gbc_radioButton_2.gridy = 2;
    panel.add(radioButton_2, gbc_radioButton_2);

    ExButtonGroup<Integer> buttonGroup = new ExButtonGroup<>(
        radioButton,
        radioButton_2);
    // buttonGroup.setAction(okAction);

    btnNewButton = new JButton(SCBundle.get("key.properties") + "...");
    btnNewButton.setEnabled(false);
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        CsvPropertiesDialog dialog = new CsvPropertiesDialog(SqlOutputOptionDialog.this,
            SCBundle.get("key.properties"), false, false, false);
        if (csvMeta == null) {
          csvMeta = CsvSheetSupport.getDefaultCsvMeta();
        }
        dialog.setCsvProperties(csvMeta);

        if (dialog.showDialog() == DialogOperation.OK) {
          csvMeta = dialog.getCsvMeta();
        }
      }
    });
    GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
    gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
    gbc_btnNewButton.anchor = GridBagConstraints.WEST;
    gbc_btnNewButton.gridx = 2;
    gbc_btnNewButton.gridy = 3;
    panel.add(btnNewButton, gbc_btnNewButton);

    label_2 = new JLabel(SCBundle.get("key.sql.outputOption") + ":");
    GridBagConstraints gbc_label_2 = new GridBagConstraints();
    gbc_label_2.anchor = GridBagConstraints.WEST;
    gbc_label_2.gridwidth = 3;
    gbc_label_2.insets = new Insets(0, 0, 5, 0);
    gbc_label_2.gridx = 0;
    gbc_label_2.gridy = 4;
    panel.add(label_2, gbc_label_2);

    chckbxUseHeader = new JCheckBox(SCBundle.get("key.sql.useSelectColumnNamesAsHeaderNames"));
    GridBagConstraints gbc_chckbxSelect = new GridBagConstraints();
    gbc_chckbxSelect.insets = new Insets(0, 0, 10, 0);
    gbc_chckbxSelect.anchor = GridBagConstraints.WEST;
    gbc_chckbxSelect.gridwidth = 2;
    gbc_chckbxSelect.gridx = 1;
    gbc_chckbxSelect.gridy = 5;
    panel.add(chckbxUseHeader, gbc_chckbxSelect);

    // Action
    radioButton_2.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          btnNewButton.setEnabled(true);
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
          btnNewButton.setEnabled(false);
        }
      }
    });

    setResizable(false);
  }

  @Override
  public void setVisible(boolean arg0) {
    if (arg0) {
      csvMeta = CsvSheetSupport.getDefaultCsvMeta();
      // radioButton_1.setEnabled(C001Global.tabbedPane.getTabCount() !=
      // 0);
      // if (!radioButton_1.isEnabled() && radioButton_1.isSelected()) {
      // radioButton.setSelected(true);
      // }
      pack();
      setLocationRelativeTo(getOwner());
    }
    super.setVisible(arg0);
  }

  public void setMessage(String msg) {
    msgLabel.setText(msg);
  }

  @Override
  protected boolean processOperation(DialogOperation selectedOperation) {
    if (selectedOperation == DialogOperation.OK) {
      if (radioButton.isSelected()) {
        option = NEW_TAB;
        // } else if (radioButton_1.isSelected()) {
        // option = CURRENT_TAB;
      } else if (radioButton_2.isSelected()) {
        option = FILE;
      }

      useHeader = chckbxUseHeader.isSelected();
    }
    return super.processOperation(selectedOperation);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    csvMeta = null;
    return super.clone();
  }

  private static final long serialVersionUID = 1L;
  private JButton btnNewButton;
  private JCheckBox chckbxUseHeader;
  private JLabel label_1;
  private JLabel label_2;
  private JLabel label;
}
