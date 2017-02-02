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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.command.CommandDef;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.util.KeyStrokeUtils;
import com.smoothcsv.swing.action.SimpleAction;
import com.smoothcsv.swing.table.ExTable;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ExTableModel;
import com.smoothcsv.swing.table.ExTableRowFilter;
import com.smoothcsv.swing.table.ReadOnlyExTableCellValueExtracter;
import com.smoothcsv.swing.utils.SwingUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CommandPalette extends JDialog {

  private int dialogHeaderHeight;

  private ExTableModel<CommandDef> commandListModel;
  private JTextField textfield;
  private ExTable<CommandDef> commandList;
  private JScrollPane scrollPane;

  public CommandPalette() {
    super((Frame) null, false);
    initialize();
  }

  private void initialize() {
    setUndecorated(true);
    setFocusable(false);
    JComponent contentPane = (JComponent) getContentPane();
    contentPane.setFocusable(false);
    contentPane.setBorder(null);

    textfield = new JTextField();
    SwingUtils.installUndoManager(textfield);
    getContentPane().add(textfield, BorderLayout.NORTH);

    List<ExTableColumn> columns = new ArrayList<>();
    ReadOnlyExTableCellValueExtracter<CommandDef> commandValueExtracter =
        new ReadOnlyExTableCellValueExtracter<CommandDef>() {
          @Override
          public Object getValue(CommandDef rowData, ExTableColumn column, int rowIndex, int columnIndex) {
            return rowData.getDisplayName();
          }
        };
    columns.add(new ExTableColumn("command", commandValueExtracter));

    ReadOnlyExTableCellValueExtracter<CommandDef> keybindingValueExtracter =
        new ReadOnlyExTableCellValueExtracter<CommandDef>() {
          @Override
          public Object getValue(CommandDef rowData, ExTableColumn column, int rowIndex, int columnIndex) {
            KeyStroke ks = CommandKeymap.getDefault().findKeyStroke(rowData.getCommandId());
            return ks == null ? "" : KeyStrokeUtils.getKeyStrokeText(ks);
          }
        };
    columns.add(new ExTableColumn("keybinding", keybindingValueExtracter));
    commandListModel = new ExTableModel<CommandDef>(Collections.emptyList(), columns);
    commandList = new ExTable<CommandDef>(commandListModel);
    commandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    commandList.setTableHeader(null);
    commandList.setIntercellSpacing(new Dimension());
    commandList.setBorder(null);
    commandList.setFocusable(false);
    commandList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
          runCommand();
        }
      }
    });

    TableColumn keybindingCol = commandList.getColumnModel().getColumn(1);

    // make second column right aligned
    DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
    cr.setHorizontalAlignment(SwingConstants.RIGHT);
    keybindingCol.setCellRenderer(cr);

    scrollPane = new JScrollPane(commandList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportBorder(null);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    contentPane.add(scrollPane, BorderLayout.CENTER);

    getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
    getRootPane().getActionMap().put("Cancel", new AbstractAction("cancel") {
      @Override
      public void actionPerformed(ActionEvent e) {
        CommandPalette.this.setVisible(false);
      }
    });

    textfield.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        CommandPalette.this.setVisible(false);
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
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "selectPrevCommand");
    ActionMap am = textfield.getActionMap();
    am.put("selectPrevCommand", new SimpleAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (commandList.getRowCount() == 0) {
          return;
        }
        int selIndex = commandList.getSelectedRow() - 1;
        if (selIndex < 0) {
          selIndex = commandListModel.getRowCount() - 1;
        }
        commandList.getSelectionModel().setSelectionInterval(selIndex, selIndex);
        commandList.ensureCellIsVisible(selIndex, 0);
      }
    });

    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "selectNextCommand");
    am.put("selectNextCommand", new SimpleAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (commandListModel.getRowCount() == 0) {
          return;
        }
        int selIndex = commandList.getSelectedRow() + 1;
        if (commandListModel.getRowCount() <= selIndex) {
          selIndex = 0;
        }
        commandList.getSelectionModel().setSelectionInterval(selIndex, selIndex);
        commandList.ensureCellIsVisible(selIndex, 0);
      }
    });

    textfield.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        runCommand();
      }
    });

    // Enable "app:ToggleCommandPalette" command in this component.
    // TODO There should be any better way to achieve it.
    String toggleCommandId = "app:ToggleCommandPalette";
    Command toggleCommand = CommandRegistry.instance().getCommandOrNull(toggleCommandId);
    if (toggleCommand != null) {
      im.put(CommandKeymap.getDefault().findKeyStroke(toggleCommandId), toggleCommandId);
      am.put(toggleCommandId, new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          CommandRegistry.instance().runCommandIfExists(toggleCommandId);
        }
      });
    }
  }

  private void runCommand() {
    if (commandList.getRowCount() == 0) {
      return;
    }
    CommandDef commandDef = commandList.getModel().getRowDataAt(commandList.getSelectedRow());
    if (commandDef != null) {
      CommandPalette.this.setVisible(false);

      // to wait for changing focused component, use #invokeLater twice.
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              CommandRegistry.instance().runCommand(commandDef.getCommandId());
            }
          });
        }
      });
    }
  }

  protected void filter() {
    String text = textfield.getText();
    if (StringUtils.isNotBlank(text)) {
      String[] searchKeywords = StringUtils.split(text.toLowerCase(), " 　\t");
      commandListModel.doFilter(new ExTableRowFilter<CommandDef>() {
        @Override
        public boolean include(CommandDef rowData, int index) {
          String id = rowData.getCommandId().toLowerCase();
          for (String keyword : searchKeywords) {
            if (!id.contains(keyword)) {
              return false;
            }
          }
          return true;
        }
      });
    } else {
      commandListModel.doFilter(null);
    }
    if (commandList.getRowCount() != 0) {
      commandList.getSelectionModel().setSelectionInterval(0, 0);
      scrollPane.getVerticalScrollBar().setValue(0);
    }

    JFrame frame = SCApplication.components().getFrame();
    int h = Math.min(textfield.getPreferredSize().height + commandList.getPreferredSize().height,
        (int) (frame.getHeight() * 0.8));
    setSize(getWidth(), h + dialogHeaderHeight);
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      textfield.setText("");

      List<CommandDef> items = CommandRegistry.instance().getEnabledCommands();
      commandListModel.setData(items);

      autofitKeyStrokeColumnWidth();

      JFrame frame = SCApplication.components().getFrame();
      int width = (int) (frame.getWidth() * 0.8);

      setMinimumSize(new Dimension(width, 1));
      setMaximumSize(new Dimension(width, (int) (frame.getHeight() * 0.8)));
      pack();

      dialogHeaderHeight = calculateDialogHeaderHeight();
      filter();

      setLocationRelativeTo(frame);
      setLocation(getLocation().x, frame.getLocation().y + 40);
    }
    super.setVisible(b);
  }

  private void autofitKeyStrokeColumnWidth() {
    TableColumn tc = commandList.getColumnModel().getColumn(1);

    int max = 10;

    int vrows = commandList.getRowCount();
    for (int i = 0; i < vrows; i++) {
      TableCellRenderer r = commandList.getCellRenderer(i, 1);
      Object value = commandList.getValueAt(i, 1);
      Component c = r.getTableCellRendererComponent(commandList, value, false, false, i, 1);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
    }

    tc.setMaxWidth(max);
    tc.setPreferredWidth(max);
  }

  private int calculateDialogHeaderHeight() {
    Container c = getContentPane();
    Point pt = c.getLocation();
    pt = SwingUtilities.convertPoint(c, pt, this);
    return pt.y;
  }

  protected static class CommandPaletteListModel extends AbstractListModel<CommandDef> {

    private List<CommandDef> allItems;
    private List<CommandDef> filteredItem;

    public CommandPaletteListModel() {
      allItems = CommandRegistry.instance().getEnabledCommands();
      filteredItem = allItems;
    }

    public void doFilter(String input) {

      if (input == null || input.isEmpty()) {
        filteredItem = allItems;
      } else {
        String[] searchKeywords = input.toLowerCase().split(" ");
        filteredItem = allItems.stream().filter(c -> {
          String id = c.getCommandId().toLowerCase();
          for (String keyword : searchKeywords) {
            if (!id.contains(keyword)) {
              return false;
            }
          }
          return true;
        }).collect(Collectors.toList());
      }

      fireContentsChanged(this, 0, getSize() - 1);
    }

    @Override
    public int getSize() {
      return filteredItem.size();
    }

    @Override
    public CommandDef getElementAt(int index) {
      return filteredItem.get(index);
    }
  }
}
