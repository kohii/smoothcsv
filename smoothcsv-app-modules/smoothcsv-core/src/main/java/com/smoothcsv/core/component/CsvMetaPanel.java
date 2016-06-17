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

import com.smoothcsv.commons.utils.ArrayUtils;
import com.smoothcsv.commons.utils.CharsetUtils;
import com.smoothcsv.commons.utils.CollectionUtils;
import com.smoothcsv.core.csv.AvailableCharsetDialog;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.core.util.CsvPropertySettings;
import com.smoothcsv.csv.CsvQuoteApplyRule;
import com.smoothcsv.csv.NewlineCharacter;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.util.DirectoryResolver;
import com.smoothcsv.swing.components.ExButtonGroup;
import com.smoothcsv.swing.components.ExRadioButton;
import com.smoothcsv.swing.components.History;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;

/**
 * @author kohii2
 */
public class CsvMetaPanel extends javax.swing.JPanel {

  private static final int ESCAPE_WITH_DUPLICATING_QUOTE = 1;
  private static final int ESCAPE_WITH_CHAR = 2;

  private final boolean autoDeterminedOptionEnabled;
  private final boolean readMode;

  private static final String AUTO;
  private static final String OTHERS;
  private static final String NONE;

  private static final History DELIMITER_HISTORY = new History(
      new File(DirectoryResolver.instance().getSessionDirectory(), "delimiter.history"), true, 10, true);

  private static final History QUOTE_HISTORY = new History(
      new File(DirectoryResolver.instance().getSessionDirectory(), "quote.history"), true, 10, true);

  private static final History ENCODING_HISTORY = new History(
      new File(DirectoryResolver.instance().getSessionDirectory(), "encoding.history"), true, 10, true);

  static {
    AUTO = CoreBundle.get("key.autoDetermined");
    OTHERS = CoreBundle.get("key.others") + "...";
    NONE = CoreBundle.get("key.none");
  }

  private ExButtonGroup<Integer> escapeType;
  private ExButtonGroup<CsvQuoteApplyRule> quoteType;

  private Object oldEncoding = null;
  private Object oldQuoteChar;
  private Object oldDelimiterChar;

  /**
   * Creates new form CsvMetaPanel
   *
   * @param autoDeterminedOptionEnabled
   * @wbp.parser.constructor
   */
  public CsvMetaPanel(boolean autoDeterminedOptionEnabled) {
    this(autoDeterminedOptionEnabled, false);
  }

  /**
   * Creates new form CsvMetaPanel
   *
   * @param autoDeterminedOptionEnabled
   * @param readMode
   */
  public CsvMetaPanel(boolean autoDeterminedOptionEnabled, boolean readMode) {
    this.autoDeterminedOptionEnabled = autoDeterminedOptionEnabled;
    this.readMode = readMode;
    initComponents();
    customizeComponents();
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT
   * modify this code. The content of this method is always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    buttonGroup1 = new javax.swing.ButtonGroup();
    encoding = new javax.swing.JComboBox();
    delimiterChar = new javax.swing.JComboBox();
    quoteLabel = new javax.swing.JLabel();
    quoteChar = new javax.swing.JComboBox();
    escapeTypeLabel = new javax.swing.JLabel();
    quoteTypeLabel = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    escapeChar = new com.smoothcsv.swing.components.RegulatedTextField(1);
    escapeCharLabel = new javax.swing.JLabel();
    newlineCharacterLabel = new javax.swing.JLabel();
    newlineCharacter = new javax.swing.JComboBox();
    hasBOM = new javax.swing.JCheckBox();
    charsetLabel = new javax.swing.JLabel();
    separatorLabel = new javax.swing.JLabel();
    dummy = new javax.swing.JLabel();

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
    gridBagLayout.rowWeights =
        new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    setLayout(gridBagLayout);

    encoding.setModel(new javax.swing.DefaultComboBoxModel(createEncodingItems()));
    encoding.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        encodingItemStateChanged(evt);
      }
    });
    encoding.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        encodingActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    add(encoding, gridBagConstraints);

    delimiterChar.setModel(new javax.swing.DefaultComboBoxModel(createDelimiterCharItems()));
    delimiterChar.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        delimiterCharItemStateChanged(evt);
      }
    });
    gridBagConstraints_1 = new java.awt.GridBagConstraints();
    gridBagConstraints_1.gridx = 2;
    gridBagConstraints_1.gridy = 1;
    gridBagConstraints_1.gridwidth = 2;
    gridBagConstraints_1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints_1.anchor = java.awt.GridBagConstraints.WEST;
    add(delimiterChar, gridBagConstraints_1);

    quoteLabel.setText(CoreBundle.get("key.quoteChar")); // NOI18N
    gridBagConstraints_2 = new java.awt.GridBagConstraints();
    gridBagConstraints_2.gridwidth = 2;
    gridBagConstraints_2.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_2.gridx = 0;
    gridBagConstraints_2.gridy = 2;
    gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
    add(quoteLabel, gridBagConstraints_2);

    quoteChar.setModel(new javax.swing.DefaultComboBoxModel(createQuoteCharItems()));
    quoteChar.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(java.awt.event.ItemEvent evt) {
        quoteCharItemStateChanged(evt);
      }
    });
    quoteChar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        quoteCharActionPerformed(evt);
      }
    });
    gridBagConstraints_3 = new java.awt.GridBagConstraints();
    gridBagConstraints_3.gridx = 2;
    gridBagConstraints_3.gridy = 2;
    gridBagConstraints_3.gridwidth = 2;
    gridBagConstraints_3.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints_3.anchor = java.awt.GridBagConstraints.WEST;
    add(quoteChar, gridBagConstraints_3);

    escapeTypeLabel.setText(CoreBundle.get("key.escapeRule")); // NOI18N
    gridBagConstraints_4 = new java.awt.GridBagConstraints();
    gridBagConstraints_4.gridwidth = 2;
    gridBagConstraints_4.insets = new Insets(0, 0, 5, 5);
    gridBagConstraints_4.gridx = 0;
    gridBagConstraints_4.gridy = 4;
    gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
    add(escapeTypeLabel, gridBagConstraints_4);
    escapeTypeDuplicate = new ExRadioButton<Integer>(ESCAPE_WITH_DUPLICATING_QUOTE);

    escapeTypeDuplicate.setText(CoreBundle.get("key.escapeRule.pair")); // NOI18N
    escapeTypeDuplicate.setToolTipText("");
    escapeTypeDuplicate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        escapeTypeDuplicateActionPerformed(evt);
      }
    });
    gridBagConstraints_7 = new java.awt.GridBagConstraints();
    gridBagConstraints_7.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_7.gridx = 1;
    gridBagConstraints_7.gridy = 5;
    gridBagConstraints_7.gridwidth = 4;
    gridBagConstraints_7.anchor = java.awt.GridBagConstraints.WEST;
    add(escapeTypeDuplicate, gridBagConstraints_7);
    escapeTypeEscapechar = new ExRadioButton<Integer>(ESCAPE_WITH_CHAR);

    escapeTypeEscapechar.setText(CoreBundle.get("key.escapeRule.escapeChar")); // NOI18N
    gridBagConstraints_6 = new java.awt.GridBagConstraints();
    gridBagConstraints_6.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_6.gridx = 1;
    gridBagConstraints_6.gridy = 6;
    gridBagConstraints_6.gridwidth = 4;
    gridBagConstraints_6.anchor = java.awt.GridBagConstraints.WEST;
    add(escapeTypeEscapechar, gridBagConstraints_6);

    quoteTypeLabel.setText(CoreBundle.get("key.quoteRule")); // NOI18N
    gridBagConstraints_5 = new java.awt.GridBagConstraints();
    gridBagConstraints_5.gridwidth = 2;
    gridBagConstraints_5.insets = new Insets(0, 0, 5, 5);
    gridBagConstraints_5.gridx = 0;
    gridBagConstraints_5.gridy = 8;
    gridBagConstraints_5.anchor = java.awt.GridBagConstraints.WEST;
    add(quoteTypeLabel, gridBagConstraints_5);
    quoteAll = new ExRadioButton(CsvQuoteApplyRule.QUOTES_ALL);

    quoteAll.setText(CoreBundle.get("key.quoteRule.all")); // NOI18N
    quoteAll.setLabel(CoreBundle.get("key.quoteRule.all")); // NOI18N
    quoteAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        quoteAllActionPerformed(evt);
      }
    });
    gridBagConstraints_8 = new java.awt.GridBagConstraints();
    gridBagConstraints_8.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_8.gridx = 1;
    gridBagConstraints_8.gridy = 9;
    gridBagConstraints_8.gridwidth = 4;
    gridBagConstraints_8.anchor = java.awt.GridBagConstraints.WEST;
    add(quoteAll, gridBagConstraints_8);
    quoteWhenNeeded = new ExRadioButton<>(CsvQuoteApplyRule.QUOTES_IF_NECESSARY);

    quoteWhenNeeded.setText(CoreBundle.get("key.quoteRule.needed")); // NOI18N
    quoteWhenNeeded.setActionCommand(CoreBundle.get("key.quoteRule.needed")); // NOI18N
    gridBagConstraints_9 = new java.awt.GridBagConstraints();
    gridBagConstraints_9.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_9.gridx = 1;
    gridBagConstraints_9.gridy = 10;
    gridBagConstraints_9.gridwidth = 4;
    gridBagConstraints_9.anchor = java.awt.GridBagConstraints.WEST;
    add(quoteWhenNeeded, gridBagConstraints_9);
    quoteRuleMacro = new ExRadioButton<>(null);

    quoteRuleMacro.setText(CoreBundle.get("key.quoteRule.macro")); // NOI18N
    gridBagConstraints_14 = new java.awt.GridBagConstraints();
    gridBagConstraints_14.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_14.gridx = 1;
    gridBagConstraints_14.gridy = 11;
    gridBagConstraints_14.gridwidth = 4;
    gridBagConstraints_14.anchor = java.awt.GridBagConstraints.LINE_START;
    add(quoteRuleMacro, gridBagConstraints_14);

    jPanel1.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
    add(jPanel1, gridBagConstraints);

    escapeChar.setColumns(2);
    escapeChar.setText("\\");
    escapeChar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        escapeCharActionPerformed(evt);
      }
    });
    gridBagConstraints_10 = new java.awt.GridBagConstraints();
    gridBagConstraints_10.insets = new Insets(0, 0, 5, 0);
    gridBagConstraints_10.gridx = 4;
    gridBagConstraints_10.gridy = 7;
    gridBagConstraints_10.anchor = java.awt.GridBagConstraints.WEST;
    add(escapeChar, gridBagConstraints_10);

    escapeCharLabel.setText(CoreBundle.get("key.escapeChar")); // NOI18N
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new Insets(0, 30, 5, 5);
    add(escapeCharLabel, gridBagConstraints);

    newlineCharacterLabel.setText(CoreBundle.get("key.newlineCharacter")); // NOI18N
    gridBagConstraints_17 = new java.awt.GridBagConstraints();
    gridBagConstraints_17.gridwidth = 2;
    gridBagConstraints_17.gridx = 0;
    gridBagConstraints_17.gridy = 12;
    gridBagConstraints_17.anchor = GridBagConstraints.NORTHWEST;
    gridBagConstraints_17.insets = new Insets(10, 0, 0, 5);
    add(newlineCharacterLabel, gridBagConstraints_17);

    newlineCharacter.setModel(new javax.swing.DefaultComboBoxModel(createNewlineCharItems()));
    newlineCharacter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        newlineCharacterActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 12;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = GridBagConstraints.NORTH;
    gridBagConstraints.insets = new Insets(10, 0, 0, 0);
    add(newlineCharacter, gridBagConstraints);

    hasBOM.setText(CoreBundle.get("key.useBOM")); // NOI18N
    hasBOM.setActionCommand(CoreBundle.get("key.useBOM")); // NOI18N
    hasBOM.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        hasBOMActionPerformed(evt);
      }
    });
    gridBagConstraints_11 = new java.awt.GridBagConstraints();
    gridBagConstraints_11.insets = new Insets(0, 0, 5, 0);
    gridBagConstraints_11.gridx = 4;
    gridBagConstraints_11.gridy = 0;
    gridBagConstraints_11.anchor = java.awt.GridBagConstraints.WEST;
    add(hasBOM, gridBagConstraints_11);

    charsetLabel.setText(CoreBundle.get("key.encoding")); // NOI18N
    gridBagConstraints_12 = new java.awt.GridBagConstraints();
    gridBagConstraints_12.gridwidth = 2;
    gridBagConstraints_12.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_12.gridx = 0;
    gridBagConstraints_12.gridy = 0;
    gridBagConstraints_12.anchor = java.awt.GridBagConstraints.LINE_START;
    add(charsetLabel, gridBagConstraints_12);

    separatorLabel.setText(CoreBundle.get("key.delimiterChar")); // NOI18N
    gridBagConstraints_13 = new java.awt.GridBagConstraints();
    gridBagConstraints_13.gridwidth = 2;
    gridBagConstraints_13.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_13.gridx = 0;
    gridBagConstraints_13.gridy = 1;
    gridBagConstraints_13.anchor = java.awt.GridBagConstraints.LINE_START;
    add(separatorLabel, gridBagConstraints_13);

    dummy.setText(" ");
    dummy.setMaximumSize(new java.awt.Dimension(4, 10));
    dummy.setMinimumSize(new java.awt.Dimension(4, 10));
    dummy.setPreferredSize(new java.awt.Dimension(4, 10));
    gridBagConstraints_15 = new java.awt.GridBagConstraints();
    gridBagConstraints_15.insets = new Insets(0, 0, 0, 5);
    gridBagConstraints_15.gridx = 1;
    gridBagConstraints_15.gridy = 3;
    gridBagConstraints_15.gridwidth = 3;
    add(dummy, gridBagConstraints_15);
  }// </editor-fold>//GEN-END:initComponents

  private void customizeComponents() {

    encoding.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        DefaultListCellRenderer rendererComponent =
            (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        if (value instanceof Charset) {
          Charset c = (Charset) value;
          rendererComponent.setText(c.displayName());
        }
        return rendererComponent;
      }
    });

    delimiterChar.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        DefaultListCellRenderer rendererComponent =
            (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        if (value.equals(',')) {
          rendererComponent.setText(CoreBundle.get("key.comma"));
        } else if (value.equals('\t')) {
          rendererComponent.setText(CoreBundle.get("key.tab"));
        } else if (value.equals(' ')) {
          rendererComponent.setText(CoreBundle.get("key.space"));
        }
        return rendererComponent;
      }
    });

    quoteChar.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {
        DefaultListCellRenderer rendererComponent =
            (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        if (value.equals('\0')) {
          rendererComponent.setText(NONE);
        }
        return rendererComponent;
      }
    });

    escapeType = new ExButtonGroup<Integer>(escapeTypeDuplicate, escapeTypeEscapechar);
    escapeType.addSelectionListener((radio) -> {
      updateFormEnabled();
    });
    quoteType = new ExButtonGroup<CsvQuoteApplyRule>(quoteAll, quoteWhenNeeded, quoteRuleMacro);

    updateFormEnabled();

    if (readMode) {
      quoteTypeLabel.setVisible(false);
      quoteAll.setVisible(false);
      quoteWhenNeeded.setVisible(false);
      // jPanel1.setVisible(false);;
      newlineCharacterLabel.setVisible(false);
      newlineCharacter.setVisible(false);
      quoteRuleMacro.setVisible(false);
      dummy.setVisible(false);
      // dummy2.setVisible(false);
    }
  }

  private Object[] createEncodingItems() {
    List<Object> encodings = new ArrayList<>();
    if (autoDeterminedOptionEnabled) {
      encodings.add(AUTO);
    }
    List<String> strItems = ArrayUtils.toArrayList(
        CsvPropertySettings.getInstance().get(CsvPropertySettings.ENCODING_OPTIONS).split(","));
    encodings.addAll(strItems.stream()
        .filter(CharsetUtils::isAvailable)
        .map(Charset::forName).collect(Collectors.toList()));
    encodings.addAll(ENCODING_HISTORY.getAll().stream()
        .filter(CharsetUtils::isAvailable)
        .map(Charset::forName).collect(Collectors.toList()));
    encodings.add(OTHERS);

    CollectionUtils.unique(encodings);

    return encodings.toArray();
  }

  private Object[] createQuoteCharItems() {
    List<Object> quotes = new ArrayList<>();
    if (autoDeterminedOptionEnabled) {
      quotes.add(AUTO);
    }
    quotes.addAll(toCharList(CsvPropertySettings.getInstance().get(CsvPropertySettings.QUOTE_CHAR_OPTIONS)));
    if (!quotes.contains('\0')) {
      quotes.add('\0');
    }
    quotes.addAll(QUOTE_HISTORY.getAll().stream().map(s -> s.charAt(0)).collect(Collectors.toList()));
    quotes.add(OTHERS);

    return CollectionUtils.unique(quotes).toArray();
  }

  private Object[] createDelimiterCharItems() {
    List<Object> delimiters = new ArrayList<>();
    if (autoDeterminedOptionEnabled) {
      delimiters.add(AUTO);
    }
    delimiters.addAll(toCharList(CsvPropertySettings.getInstance().get(CsvPropertySettings.DELIMITER_CHAR_OPTIONS)));
    delimiters.addAll(DELIMITER_HISTORY.getAll().stream().map(s -> s.charAt(0)).collect(Collectors.toList()));
    delimiters.add(OTHERS);

    return CollectionUtils.unique(delimiters).toArray();
  }

  private Object[] createNewlineCharItems() {
    NewlineCharacter[] newlines = NewlineCharacter.values();
    if (autoDeterminedOptionEnabled) {
      List<Object> newlineCharItems = new ArrayList<>();
      newlineCharItems.add(AUTO);
      newlineCharItems.addAll(Arrays.asList(newlines));
      return newlineCharItems.toArray();
    } else {
      return newlines;
    }
  }

  private List<Character> toCharList(String str) {
    return str.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
  }

  public void setEncoding(String name) {

    if (AUTO.equals(name) && AUTO.equals(encoding.getItemAt(0))) {
      encoding.setSelectedIndex(0);
      return;
    }
    if (!CharsetUtils.isAvailable(name)) {
      MessageDialogs.alert("WSCA0001", name);
      return;
    }
    Charset charset = Charset.forName(name);
    DefaultComboBoxModel model = (DefaultComboBoxModel) encoding.getModel();
    int index = model.getIndexOf(charset);
    if (index < 0) {
      model.insertElementAt(charset, model.getSize() - 1);
    }
    model.setSelectedItem(charset);
  }

  public void load(CsvMeta csvMeta) {

    if (csvMeta.isCharsetNotDetermined()) {
      setEncoding(AUTO);
    } else {
      setEncoding(csvMeta.getCharset().displayName());
      hasBOM.setSelected(csvMeta.hasBom());
    }

    if (csvMeta.isDelimiterNotDetermined()) {
      delimiterChar.setSelectedItem(AUTO);
    } else {
      setDelimiterChar(csvMeta.getDelimiter());
    }

    if (csvMeta.isQuoteNotDetermined()) {
      quoteChar.setSelectedItem(AUTO);
    } else {
      switch (csvMeta.getQuoteOption()) {
        case NO_QUOTE:
          quoteChar.setSelectedItem('\0');
          break;
        case QUOTES_ALL:
        case QUOTES_IF_NECESSARY:
          quoteType.setSelectedValue(csvMeta.getQuoteOption());
          setQuoteChar(csvMeta.getQuote());
          break;
        default:
          throw new IllegalStateException("" + csvMeta.getQuote());
      }
    }

    if (csvMeta.getEscape() == '\0') {
      escapeType.setSelectedValue(ESCAPE_WITH_DUPLICATING_QUOTE);
    } else {
      escapeType.setSelectedValue(ESCAPE_WITH_CHAR);
      escapeChar.setText(String.valueOf(csvMeta.getEscape()));
    }

    if (csvMeta.isNewlineCharNotDetermined()) {
      newlineCharacter.setSelectedItem(AUTO);
    } else {
      newlineCharacter.setSelectedItem(csvMeta.getNewlineCharacter());
    }
  }

  public void save(CsvMeta csvMeta) {
    // encoding
    if (AUTO.equals(encoding.getSelectedItem())) {
      csvMeta.setCharsetNotDetermined(true);
    } else {
      csvMeta.setCharset((Charset) encoding.getSelectedItem());
      csvMeta.setHasBom(hasBOM.isEnabled() && hasBOM.isSelected());
      ENCODING_HISTORY.put(((Charset) encoding.getSelectedItem()).displayName());
    }

    // delimiter
    Object delimiter = delimiterChar.getSelectedItem();
    if (AUTO.equals(delimiter)) {
      csvMeta.setDelimiterNotDetermined(true);
    } else if (delimiter instanceof Character) {
      csvMeta.setDelimiter((Character) delimiter);
      DELIMITER_HISTORY.put(delimiter.toString());
    } else {
      throw new IllegalStateException(delimiter.toString());
    }

    // quote character
    Object quote = quoteChar.getSelectedItem();
    if (AUTO.equals(quote)) {
      csvMeta.setQuoteNotDetermined(true);
    } else if (quote.equals(NONE)) {
      csvMeta.setQuoteOption(CsvQuoteApplyRule.NO_QUOTE);
      csvMeta.setQuote('\0');
    } else if (quote instanceof Character) {
      csvMeta.setQuote((Character) quote);
      CsvQuoteApplyRule quoteRule = quoteType.getSelectedValue();
      csvMeta.setQuoteOption(quoteRule);
      QUOTE_HISTORY.put(quote.toString());

      // escape rule
      if (escapeType.getSelectedValue() == ESCAPE_WITH_DUPLICATING_QUOTE) {
        csvMeta.setEscape('\0');
      } else {
        char escape = escapeChar.getText().charAt(0);
        csvMeta.setEscape(escape);
      }
    } else {
      throw new IllegalStateException(delimiter.toString());
    }

    // line feed
    Object newline = newlineCharacter.getSelectedItem();
    if (AUTO.equals(newline)) {
      csvMeta.setNewlineCharNotDetermined(true);
    } else {
      csvMeta.setNewlineCharacter((NewlineCharacter) newline);
    }
  }

  public void validateInput() {
    if (escapeTypeEscapechar.isSelected()) {
      if (escapeChar.getText().length() == 0) {
        throw new AppException("WSCC0009", CoreBundle.get("key.escapeChar"));
      }
    }
  }

  private void updateFormEnabled() {
    hasBOM.setEnabled(CharsetUtils.UTF8.equals(encoding.getSelectedItem()));
    boolean quoteEnabled = !Character.valueOf('\0').equals(quoteChar.getSelectedItem());
    quoteType.setEnabled(quoteEnabled);
    escapeType.setEnabled(quoteEnabled);
    escapeChar.setEnabled(quoteEnabled && escapeTypeEscapechar.isSelected());
    escapeCharLabel.setEnabled(escapeChar.isEnabled());
  }

  @Override
  public void setEnabled(boolean enabled) {
    charsetLabel.setEnabled(enabled);
    delimiterChar.setEnabled(enabled);
    dummy.setEnabled(enabled);
    encoding.setEnabled(enabled);
    escapeChar.setEnabled(enabled);
    escapeCharLabel.setEnabled(enabled);
    escapeTypeDuplicate.setEnabled(enabled);
    escapeTypeEscapechar.setEnabled(enabled);
    escapeTypeLabel.setEnabled(enabled);
    hasBOM.setEnabled(enabled);
    jPanel1.setEnabled(enabled);
    newlineCharacter.setEnabled(enabled);
    newlineCharacterLabel.setEnabled(enabled);
    quoteAll.setEnabled(enabled);
    quoteChar.setEnabled(enabled);
    quoteLabel.setEnabled(enabled);
    quoteRuleMacro.setEnabled(enabled);
    quoteTypeLabel.setEnabled(enabled);
    quoteWhenNeeded.setEnabled(enabled);
    separatorLabel.setEnabled(enabled);
    if (enabled) {
      updateFormEnabled();
    }
    super.setEnabled(enabled);
  }

  private void encodingActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_encodingActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_encodingActionPerformed

  private void hasBOMActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_hasBOMActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_hasBOMActionPerformed

  private void escapeCharActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_escapeCharActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_escapeCharActionPerformed

  private void escapeTypeDuplicateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_escapeTypeDuplicateActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_escapeTypeDuplicateActionPerformed

  private void quoteAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quoteAllActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_quoteAllActionPerformed

  private void newlineCharacterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newlineCharacterActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_newlineCharacterActionPerformed

  private void quoteCharActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_quoteCharActionPerformed
    // TODO add your handling code here:
  }// GEN-LAST:event_quoteCharActionPerformed

  private void encodingItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_encodingItemStateChanged
    if (evt.getStateChange() == ItemEvent.SELECTED) {
      Object item = evt.getItem();
      if (item.equals(OTHERS)) {
        encoding.setEnabled(false);
        AvailableCharsetDialog charsetDialog = new AvailableCharsetDialog((JDialog) null);
        if (charsetDialog.showDialog() == DialogOperation.OK) {
          String charset = charsetDialog.getCharset();
          if (charset != null) {
            setEncoding(charset);
          }
        } else {
          if (oldEncoding == null) {
            encoding.setSelectedIndex(0);
          } else {
            encoding.setSelectedItem(oldEncoding);
          }
        }
        encoding.setEnabled(true);
      }

      updateFormEnabled();
    } else if (evt.getStateChange() == ItemEvent.DESELECTED && !evt.getItem().equals(OTHERS)) {
      oldEncoding = evt.getItem();
    }

  }// GEN-LAST:event_encodingItemStateChanged

  private void quoteCharItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_quoteCharItemStateChanged
    String none = CoreBundle.get("key.none");
    if (evt.getStateChange() == ItemEvent.SELECTED) {

      if (evt.getItem().equals(OTHERS)) {
        quoteChar.setEnabled(false);
        String tmp = MessageDialogs.prompt("ISCA0001", CoreBundle.get("key.quoteChar"));
        quoteChar.setEnabled(true);
        if (tmp == null || tmp.length() == 0) {
          // Revert selection
          if (oldQuoteChar == null) {
            quoteChar.setSelectedIndex(0);
          } else {
            quoteChar.setSelectedItem(oldQuoteChar);
          }
        } else if (tmp.length() > 1) {
          MessageDialogs.alert("WSCC0007", CoreBundle.get("key.quoteChar"), 1);

          // Revert selection
          if (oldQuoteChar == null) {
            quoteChar.setSelectedIndex(0);
          } else {
            quoteChar.setSelectedItem(oldQuoteChar);
          }
        } else {
          Character inputChar = tmp.charAt(0);
          setQuoteChar(inputChar);
        }
      }

      updateFormEnabled();

    } else if (evt.getStateChange() == ItemEvent.DESELECTED && !evt.getItem().equals(OTHERS)) {
      oldQuoteChar = evt.getItem();
    }
  }// GEN-LAST:event_quoteCharItemStateChanged

  private void setQuoteChar(Character c) {
    ComboBoxModel model = quoteChar.getModel();
    int count = model.getSize();
    for (int i = 0; i < count; i++) {
      Object data = model.getElementAt(i);
      if (data.equals(c)) {
        quoteChar.setSelectedIndex(i);
        break;
      } else if (i == count - 1) {
        quoteChar.insertItemAt(c, i);
        quoteChar.setSelectedIndex(i);
        break;
      }
    }
  }

  private void delimiterCharItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_delimiterCharItemStateChanged

    if (evt.getStateChange() == ItemEvent.SELECTED) {

      if (evt.getItem().equals(OTHERS)) {
        delimiterChar.setEnabled(false);
        String tmp = MessageDialogs.prompt("ISCA0001", CoreBundle.get("key.delimiterChar"));
        delimiterChar.setEnabled(true);
        if (tmp == null || tmp.length() == 0) {
          // Revert selection
          if (oldDelimiterChar == null) {
            delimiterChar.setSelectedIndex(0);
          } else {
            delimiterChar.setSelectedItem(oldDelimiterChar);
          }
        } else if (tmp.length() > 1) {
          MessageDialogs.alert("WSCC0007", CoreBundle.get("key.delimiterChar"), 1);

          // Revert selection
          if (oldDelimiterChar == null) {
            delimiterChar.setSelectedIndex(0);
          } else {
            delimiterChar.setSelectedItem(oldDelimiterChar);
          }
        } else {
          Character inputChar = tmp.charAt(0);
          setDelimiterChar(inputChar);
        }
      }
    } else if (evt.getStateChange() == ItemEvent.DESELECTED && !evt.getItem().equals(OTHERS)) {
      oldDelimiterChar = evt.getItem();
    }
  }// GEN-LAST:event_delimiterCharItemStateChanged

  private void setDelimiterChar(Character c) {
    ComboBoxModel model = delimiterChar.getModel();
    int count = model.getSize();
    for (int i = 0; i < count; i++) {
      Object data = model.getElementAt(i);
      if (data.equals(c)) {
        delimiterChar.setSelectedIndex(i);
        break;
      } else if (i == count - 1) {
        delimiterChar.insertItemAt(c, i);
        delimiterChar.setSelectedIndex(i);
        break;
      }
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JLabel charsetLabel;
  private javax.swing.JComboBox delimiterChar;
  private javax.swing.JLabel dummy;
  private javax.swing.JComboBox encoding;
  private javax.swing.JTextField escapeChar;
  private javax.swing.JLabel escapeCharLabel;
  private ExRadioButton escapeTypeDuplicate;
  private ExRadioButton escapeTypeEscapechar;
  private javax.swing.JLabel escapeTypeLabel;
  private javax.swing.JCheckBox hasBOM;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JComboBox newlineCharacter;
  private javax.swing.JLabel newlineCharacterLabel;
  private ExRadioButton<CsvQuoteApplyRule> quoteAll;
  private javax.swing.JComboBox quoteChar;
  private javax.swing.JLabel quoteLabel;
  private ExRadioButton<CsvQuoteApplyRule> quoteRuleMacro;
  private javax.swing.JLabel quoteTypeLabel;
  private ExRadioButton<CsvQuoteApplyRule> quoteWhenNeeded;
  private javax.swing.JLabel separatorLabel;
  private GridBagConstraints gridBagConstraints_1;
  private GridBagConstraints gridBagConstraints_2;
  private GridBagConstraints gridBagConstraints_3;
  private GridBagConstraints gridBagConstraints_4;
  private GridBagConstraints gridBagConstraints_5;
  private GridBagConstraints gridBagConstraints_6;
  private GridBagConstraints gridBagConstraints_7;
  private GridBagConstraints gridBagConstraints_8;
  private GridBagConstraints gridBagConstraints_9;
  private GridBagConstraints gridBagConstraints_10;
  private GridBagConstraints gridBagConstraints_11;
  private GridBagConstraints gridBagConstraints_12;
  private GridBagConstraints gridBagConstraints_13;
  private GridBagConstraints gridBagConstraints_14;
  private GridBagConstraints gridBagConstraints_15;
  private GridBagConstraints gridBagConstraints_17;
  // End of variables declaration//GEN-END:variables

}
