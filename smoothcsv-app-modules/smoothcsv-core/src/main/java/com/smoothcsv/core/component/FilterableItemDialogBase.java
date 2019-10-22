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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.swing.action.SimpleAction;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;
import com.smoothcsv.swing.table.ExTableRowFilter;
import com.smoothcsv.swing.utils.SwingUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public abstract class FilterableItemDialogBase<T> extends JDialog {

  private int dialogHeaderHeight;

  private ExTableModel<T> itemListModel;
  private JTextField textfield;
  @Getter
  private ExTable<T> itemListTable;
  private JScrollPane scrollPane;

  @Setter
  boolean closeOnSelect = true;

  private Map<T, String> textsForSearch;

  @Getter(AccessLevel.PROTECTED)
  private boolean selectionConfirmed = false;

  public FilterableItemDialogBase(Frame parent) {
    super(parent, false);
  }

  public FilterableItemDialogBase(Dialog parent) {
    super(parent, false);
  }

  protected void initialize(List<ExTableColumn> columns, boolean tableHeaderVisible) {
    setUndecorated(true);
    setFocusable(false);
    JComponent contentPane = (JComponent) getContentPane();
    contentPane.setFocusable(false);
    contentPane.setBorder(null);

    textfield = new JTextField();
    SwingUtils.installUndoManager(textfield);
    getContentPane().add(textfield, BorderLayout.NORTH);

    itemListModel = new ExTableModel<>(Collections.emptyList(), columns);
    itemListTable = new ExTable<>(itemListModel);
    itemListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    if (!tableHeaderVisible) {
      itemListTable.setTableHeader(null);
    }
    itemListTable.setIntercellSpacing(new Dimension());
    itemListTable.setBorder(null);
    itemListTable.setFocusable(false);
    itemListTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          onSelectItem();
        }
      }
    });
    itemListTable.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int row = itemListTable.rowAtPoint(e.getPoint());
        if (row < 0) {
          return;
        }
        itemListTable.getSelectionModel().setSelectionInterval(row, row);
        itemListTable.ensureCellIsVisible(row, 0);
      }
    });

    scrollPane = new JScrollPane(itemListTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportBorder(null);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    contentPane.add(scrollPane, BorderLayout.CENTER);

    getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
    getRootPane().getActionMap().put("Cancel", new AbstractAction("cancel") {
      @Override
      public void actionPerformed(ActionEvent e) {
        FilterableItemDialogBase.this.setVisible(false);
      }
    });

    textfield.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        FilterableItemDialogBase.this.setVisible(false);
      }
    });

    textfield.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        filter();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        filter();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        filter();
      }
    });

    InputMap im = textfield.getInputMap();
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "selectPrevItem");
    ActionMap am = textfield.getActionMap();
    am.put("selectPrevItem", new SimpleAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (itemListTable.getRowCount() == 0) {
          return;
        }
        int selIndex = itemListTable.getSelectedRow() - 1;
        if (selIndex < 0) {
          selIndex = itemListModel.getRowCount() - 1;
        }
        itemListTable.getSelectionModel().setSelectionInterval(selIndex, selIndex);
        itemListTable.ensureCellIsVisible(selIndex, 0);
      }
    });

    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "selectNextItem");
    am.put("selectNextItem", new SimpleAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (itemListModel.getRowCount() == 0) {
          return;
        }
        int selIndex = itemListTable.getSelectedRow() + 1;
        if (itemListModel.getRowCount() <= selIndex) {
          selIndex = 0;
        }
        itemListTable.getSelectionModel().setSelectionInterval(selIndex, selIndex);
        itemListTable.ensureCellIsVisible(selIndex, 0);
      }
    });

    textfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onSelectItem();
      }
    });
  }

  private void onSelectItem() {
    if (itemListTable.getRowCount() == 0) {
      return;
    }
    T item = itemListTable.getModel().getRowDataAt(itemListTable.getSelectedRow());
    if (item != null) {

      selectionConfirmed = true;

      if (closeOnSelect) {
        FilterableItemDialogBase.this.setVisible(false);
      }

      // to wait for changing focused component, use #invokeLater twice.
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              onItemSelected(item);
            }
          });
        }
      });
    }
  }

  protected void filter() {
    String text = textfield.getText();
    if (StringUtils.isNotBlank(text)) {
      String[] searchKeywords = Arrays.stream(StringUtils.split(canonicalize(text), " 　\t"))
          .filter(s -> !s.isEmpty())
          .toArray(String[]::new);
      itemListModel.doFilter(new ExTableRowFilter<T>() {
        @Override
        public boolean include(T rowData, int index) {
          return itemMatches(rowData, searchKeywords);
        }
      });
    } else {
      itemListModel.doFilter(null);
    }
    if (itemListTable.getRowCount() != 0) {
      itemListTable.getSelectionModel().setSelectionInterval(0, 0);
      scrollPane.getVerticalScrollBar().setValue(0);
    }

    JFrame frame = SCApplication.components().getFrame();
    int h = Math.min(textfield.getPreferredSize().height + itemListTable.getPreferredSize().height,
        (int) (frame.getHeight() * 0.8));
    setSize(getWidth(), h + dialogHeaderHeight);
  }

  public void updateItems(List<T> items) {
    itemListModel.setData(items);
    textsForSearch = items.stream()
        .collect(Collectors.toMap(
            item -> item,
            item -> canonicalize(createTextForSearchingItem(item))
        ));
  }

  protected abstract String createTextForSearchingItem(T item);

  public void updateSearchKeyword(String keyword) {
    textfield.setText(keyword);
    filter();
  }

  protected String canonicalize(String s) {
    return s.toLowerCase();
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      textfield.setText("");

      selectionConfirmed = false;

      JFrame frame = SCApplication.components().getFrame();
      int width = (int) (frame.getWidth() * 0.8);

      setMinimumSize(new Dimension(width, 1));
      setMaximumSize(new Dimension(width, (int) (frame.getHeight() * 0.8)));
      pack();

      dialogHeaderHeight = calculateDialogHeaderHeight();
      filter();

      setLocationRelativeTo(frame);
      setLocation(getLocation().x, frame.getLocation().y + 40);

      SwingUtilities.invokeLater(() -> textfield.requestFocusInWindow());
    }
    super.setVisible(b);
  }

  protected void fitColumnSizeToFit(int columnIndex) {
    TableColumn tc = itemListTable.getColumnModel().getColumn(columnIndex);

    int max = 10;

    int vrows = itemListTable.getRowCount();
    for (int i = 0; i < vrows; i++) {
      TableCellRenderer r = itemListTable.getCellRenderer(i, columnIndex);
      Object value = itemListTable.getValueAt(i, columnIndex);
      Component c = r.getTableCellRendererComponent(itemListTable, value, false, false, i, columnIndex);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
    }

    tc.setMaxWidth(max);
    tc.setPreferredWidth(max);
  }

  private int calculateDialogHeaderHeight() {
    if (itemListTable.getTableHeader() == null) {
      return 0;
    }
    return itemListTable.getTableHeader().getHeight();
  }

  protected boolean itemMatches(T item, String[] searchKeywords) {
    String s = textsForSearch.get(item);
    for (String keyword : searchKeywords) {
      if (!s.contains(keyword)) {
        return false;
      }
    }
    return true;
  }

  protected abstract void onItemSelected(T item);

//  protected class ItemListModel extends AbstractListModel<T> {
//
//    private List<T> allItems;
//    private List<T> filteredItem;
//
//    public ItemListModel(List<T> allItems) {
//      this.allItems = allItems;
//      this.filteredItem = allItems;
//    }
//
//    public void doFilter(String input) {
//      if (input == null || input.isEmpty()) {
//        filteredItem = allItems;
//      } else {
//        String[] searchKeywords = input.toLowerCase().split(" ");
//        System.out.println(2);
//        filteredItem = allItems.stream()
//            .filter(item -> itemMatches(item, searchKeywords))
//            .collect(Collectors.toList());
//      }
//      fireContentsChanged(this, 0, getSize() - 1);
//    }
//
//    @Override
//    public int getSize() {
//      return filteredItem.size();
//    }
//
//    @Override
//    public T getElementAt(int index) {
//      return filteredItem.get(index);
//    }
//  }
}
