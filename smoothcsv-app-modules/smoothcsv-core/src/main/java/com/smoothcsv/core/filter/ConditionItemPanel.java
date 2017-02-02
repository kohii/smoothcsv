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
package com.smoothcsv.core.filter;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.smoothcsv.core.find.Regex;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.components.RegulatedTextField;
import com.smoothcsv.swing.icon.AwesomeIcon;
import org.apache.commons.lang3.StringUtils;

public class ConditionItemPanel extends JPanel {

  private static final long serialVersionUID = 1639607L;
  private JTextField textField;
  private JComboBox<Criteria> criteriaCombo;
  private JPanel lowerPanel;
  private JLabel checkBox_3;
  private JCheckBox colValCheckBox;
  private ComparedValuePanel comparedValuePanel;

  public ConditionItemPanel(final Dialog parent) {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    textField = new RegulatedTextField(RegulatedTextField.Type.NUMERIC, 8);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 5);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    add(textField, gbc_textField);
    textField.setColumns(10);

    JLabel label = new JLabel(SCBundle.get("key.filter.nthColumn"));
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.anchor = GridBagConstraints.WEST;
    gbc_label.insets = new Insets(0, 0, 5, 5);
    gbc_label.gridx = 2;
    gbc_label.gridy = 0;
    add(label, gbc_label);

    criteriaCombo = new JComboBox<>();
    for (Criteria criteria : Criteria.values()) {
      criteriaCombo.addItem(criteria);
    }

    GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
    gbc_comboBox_1.anchor = GridBagConstraints.WEST;
    gbc_comboBox_1.insets = new Insets(10, 10, 15, 0);
    gbc_comboBox_1.gridwidth = 3;
    gbc_comboBox_1.gridx = 1;
    gbc_comboBox_1.gridy = 1;
    add(criteriaCombo, gbc_comboBox_1);

    lowerPanel = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.gridwidth = 3;
    gbc_panel.insets = new Insets(0, 0, 5, 0);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 1;
    gbc_panel.gridy = 2;
    add(lowerPanel, gbc_panel);
    lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));

    comparedValuePanel = new ComparedValuePanel();
    lowerPanel.add(comparedValuePanel);

    JPanel panel_1 = new JPanel();
    lowerPanel.add(panel_1);
    GridBagLayout gbl_panel_1 = new GridBagLayout();
    gbl_panel_1.columnWidths = new int[]{21, 0, 0, 0, 0, 0};
    gbl_panel_1.rowHeights = new int[]{21, 0};
    gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
    panel_1.setLayout(gbl_panel_1);

    checkBox_3 = new JLabel(AwesomeIcon.create(AwesomeIcon.FA_PLUS_CIRCLE));
    checkBox_3.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          addComparedValuePanel();
          parent.pack();
        }
      }
    });
    GridBagConstraints gbc_checkBox_3 = new GridBagConstraints();
    gbc_checkBox_3.insets = new Insets(0, 0, 0, 15);
    gbc_checkBox_3.anchor = GridBagConstraints.WEST;
    gbc_checkBox_3.gridx = 0;
    gbc_checkBox_3.gridy = 0;
    panel_1.add(checkBox_3, gbc_checkBox_3);

    colValCheckBox = new JCheckBox(SCBundle.get("key.filter.cellValue"));
    GridBagConstraints gbc_checkBox = new GridBagConstraints();
    gbc_checkBox.insets = new Insets(0, 0, 0, 5);
    gbc_checkBox.gridx = 1;
    gbc_checkBox.gridy = 0;
    panel_1.add(colValCheckBox, gbc_checkBox);

    casecheckBox = new JCheckBox(SCBundle.get("key.caseSensitive"));
    GridBagConstraints gbc_checkBox_00 = new GridBagConstraints();
    gbc_checkBox_00.insets = new Insets(0, 0, 0, 5);
    gbc_checkBox_00.gridx = 2;
    gbc_checkBox_00.gridy = 0;
    panel_1.add(casecheckBox, gbc_checkBox_00);

    criteriaCombo.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() != ItemEvent.SELECTED) {
          return;
        }
        Criteria criteria = criteriaCombo.getItemAt(criteriaCombo.getSelectedIndex());
        setMultiTextbox(criteria.equals(Criteria.IS_IN) || criteria.equals(Criteria.IS_NOT_IN));

        lowerPanel.setVisible(!contains(criteria, Criteria.IS_A_NUMERIC, Criteria.IS_NOT_A_NUMERIC,
            Criteria.IS_EMPTY, Criteria.IS_NOT_EMPTY, Criteria.EXISTS, Criteria.DOES_NOT_EXISTS));

        parent.pack();
      }
    });

    colValCheckBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        Component[] components = lowerPanel.getComponents();
        for (Component component : components) {
          if (component instanceof ComparedValuePanel) {
            ((ComparedValuePanel) component).setColumnVisible(colValCheckBox.isSelected());
          }
        }
      }
    });
    criteriaCombo.setSelectedIndex(1);
    criteriaCombo.setSelectedIndex(0);
  }

  boolean tran = false;

  private static boolean contains(Object o, Object... os) {
    for (Object object : os) {
      if (o == object) {
        return true;
      }
    }
    return false;
  }

  private void setMultiTextbox(boolean b) {
    Component[] components = lowerPanel.getComponents();
    int idx = 0;
    for (int i = 0; i < components.length; i++) {
      Component component = components[i];
      if (component instanceof ComparedValuePanel) {
        ((ComparedValuePanel) component).setButtonVisivle(b);
        if (!b && idx != 0) {
          lowerPanel.remove(component);
        }
        idx++;
      }
    }
    checkBox_3.setVisible(b);
  }

  private ComparedValuePanel addComparedValuePanel() {
    ComparedValuePanel textBoxPanel = new ComparedValuePanel();
    textBoxPanel.setColumnVisible(colValCheckBox.isSelected());
    lowerPanel.add(textBoxPanel, lowerPanel.getComponentCount() - 1);
    lowerPanel.revalidate();
    return textBoxPanel;
  }

  @SuppressWarnings("serial")
  class ComparedValuePanel extends JPanel {

    private JTextField textField_1;
    private JLabel checkBox_3;
    private JLabel label_1;

    public ComparedValuePanel() {
      GridBagLayout gbl_panel_1 = new GridBagLayout();
      gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0};
      gbl_panel_1.rowHeights = new int[]{0, 0};
      gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
      gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
      setLayout(gbl_panel_1);

      checkBox_3 = new JLabel(AwesomeIcon.create(AwesomeIcon.FA_MINUS_CIRCLE));
      checkBox_3.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
            lowerPanel.remove(ComparedValuePanel.this);
            lowerPanel.revalidate();
            if (lowerPanel.getComponentCount() == 1) {
              addComparedValuePanel();
            }
          }
        }
      });
      GridBagConstraints gbc_checkBox_3 = new GridBagConstraints();
      gbc_checkBox_3.anchor = GridBagConstraints.WEST;
      gbc_checkBox_3.gridx = 0;
      gbc_checkBox_3.gridy = 0;
      add(checkBox_3, gbc_checkBox_3);

      textField_1 = new JTextField();
      GridBagConstraints gbc_textField_1 = new GridBagConstraints();
      gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
      gbc_textField_1.gridx = 1;
      gbc_textField_1.gridy = 0;
      add(textField_1, gbc_textField_1);
      textField_1.setColumns(10);

      label_1 = new JLabel(SCBundle.get("key.filter.nthColumn"));
      label_1.setVisible(false);
      GridBagConstraints gbc_label_1 = new GridBagConstraints();
      gbc_label_1.gridx = 2;
      gbc_label_1.gridy = 0;
      add(label_1, gbc_label_1);
    }

    void setButtonVisivle(boolean b) {
      checkBox_3.setVisible(b);
    }

    void setColumnVisible(boolean b) {
      label_1.setVisible(b);
    }
  }

  private FilterConditionItem filterConditionItem;
  private JCheckBox casecheckBox;

  public boolean validateInput() {

    String left = textField.getText();
    if (StringUtils.isBlank(left)) {
      MessageDialogs.alert("WSCA0006");
      return false;

    }
    int leftColumnNumber = Integer.parseInt(left);

    if (leftColumnNumber <= 0) {
      MessageDialogs.alert("WSCA0007", SCBundle.get("key.columnNumber"));
      return false;
    }

    CellValue l = new CellValue(leftColumnNumber);

    String[] right = getRight();

    boolean columnVal =
        lowerPanel.isVisible() && colValCheckBox.isEnabled() && colValCheckBox.isSelected();
    boolean caseSensitive =
        lowerPanel.isVisible() && casecheckBox.isEnabled() && casecheckBox.isSelected();

    Criteria criteria = (Criteria) criteriaCombo.getSelectedItem();

    FilterConditionItem filterConditionItem;
    switch (criteria) {
      case IS_EMPTY:
      case IS_NOT_EMPTY:
      case IS_A_NUMERIC:
      case IS_NOT_A_NUMERIC:
      case EXISTS:
      case DOES_NOT_EXISTS:
        filterConditionItem = new FilterConditionItem(l, criteria);
        break;
      case MATCHES_THE_REGEX_OF:
        Regex regex = new Regex(right[0], caseSensitive);
        if (!regex.isValid()) {
          MessageDialogs.alert("WSCA0009", regex.getError());
          return false;
        }
        filterConditionItem =
            new FilterConditionItem(l, criteria, new FixedValue(right[0]), caseSensitive);
        break;
      default:
        IValue[] r = new IValue[right.length];
        for (int i = 0; i < r.length; i++) {
          if (columnVal) {
            if (!com.smoothcsv.commons.utils.StringUtils.isNumber(right[i])) {
              MessageDialogs.alert("WSCA0008", SCBundle.get("key.columnNumber"));
              return false;
            }
            int ri = Integer.parseInt(right[i]);
            if (ri <= 0) {
              MessageDialogs.alert("WSCA0007", SCBundle.get("key.columnNumber"));
              return false;
            }
            r[i] = new CellValue(ri);
          } else {
            switch (criteria) {
              case IS_A_NUMBER_GREATER_THAN:
              case IS_A_NUMBER_LESS_THAN:
              case IS_A_NUMBER_EQUAL_TO_OR_GREATER_THAN:
              case IS_A_NUMBER_EQUAL_TO_OR_LESS_THAN:
                if (!StringUtils.isNumeric(right[i])) {
                  MessageDialogs.alert("WSCA0010");
                  return false;
                }
                break;
              default:
                break;
            }
            r[i] = new FixedValue(right[i]);
          }
        }
        filterConditionItem = new FilterConditionItem(l, criteria, r, caseSensitive);
        break;
    }

    this.filterConditionItem = filterConditionItem;

    return true;
  }

  public FilterConditionItem getFilterConditionItem() {
    return filterConditionItem;
  }

  public void setFilterConditionItem(FilterConditionItem filterConditionItem) {
    this.filterConditionItem = filterConditionItem;
    textField.setText(String.valueOf(filterConditionItem.getLeft().getText()));
    criteriaCombo.setSelectedItem(filterConditionItem.getCriteria());
    colValCheckBox.setSelected(filterConditionItem.getRight() != null
        && filterConditionItem.getRight()[0] instanceof CellValue);
    casecheckBox.setSelected(!filterConditionItem.isCaseSensitive());
    if (filterConditionItem.getCriteria() == Criteria.IS_IN
        || filterConditionItem.getCriteria() == Criteria.IS_NOT_IN) {
      setMultiTextbox(true);
      ((ComparedValuePanel) lowerPanel.getComponent(0)).textField_1
          .setText(filterConditionItem.getRight()[0].getText());
      for (int i = 0; i < filterConditionItem.getRight().length; i++) {
        IValue val = filterConditionItem.getRight()[i];
        if (i == 0) {
          ((ComparedValuePanel) lowerPanel.getComponent(0)).textField_1.setText(val.getText());
        } else {
          addComparedValuePanel().textField_1.setText(val.getText());
        }
      }
    } else {
      setMultiTextbox(false);
      ((ComparedValuePanel) lowerPanel.getComponent(0)).textField_1
          .setText(filterConditionItem.getRight()[0].getText());
    }

  }

  public String[] getRight() {
    List<String> tmp = new ArrayList<>();
    Component[] components = lowerPanel.getComponents();
    for (int i = 0; i < components.length; i++) {
      Component component = components[i];
      if (component instanceof ComparedValuePanel) {
        tmp.add(((ComparedValuePanel) component).textField_1.getText());
      }
    }
    return tmp.toArray(new String[0]);
  }
}
