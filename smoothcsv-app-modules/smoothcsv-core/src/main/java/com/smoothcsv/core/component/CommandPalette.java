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

import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.smoothcsv.core.SmoothCsvApp;
import com.smoothcsv.framework.command.CommandDef;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.util.KeyStrokeUtils;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ReadOnlyExTableCellValueExtracter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CommandPalette extends FilterableItemDialogBase<CommandDef> {

  public CommandPalette() {
    super(SmoothCsvApp.components().getFrame());

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

    initialize(columns, false);

    TableColumn keybindingCol = getItemListTable().getColumnModel().getColumn(1);

    // make second column right aligned
    DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
    cr.setHorizontalAlignment(SwingConstants.RIGHT);
    keybindingCol.setCellRenderer(cr);
  }

  @Override
  protected void onItemSelected(CommandDef item) {
    CommandRegistry.instance().runCommand(item.getCommandId());
  }

  @Override
  protected String createTextForSearchingItem(CommandDef item) {
    return item.getCommandId();
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      updateSearchKeyword("");
      updateItems(CommandRegistry.instance().getEnabledCommands());
      fitColumnSizeToFit(1);
    }
    super.setVisible(b);
  }
}
