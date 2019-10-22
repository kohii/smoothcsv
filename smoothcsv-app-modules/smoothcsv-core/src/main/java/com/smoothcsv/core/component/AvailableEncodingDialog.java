package com.smoothcsv.core.component;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.smoothcsv.commons.encoding.FileEncoding;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.swing.table.ExTableColumn;
import com.smoothcsv.swing.table.ReadOnlyExTableCellValueExtracter;

/**
 * @author kohii
 */
public class AvailableEncodingDialog extends FilterableItemDialogBase<FileEncoding> {

  private static final Pattern CHARS_TO_REMOVE = Pattern.compile("[-_ ()]");

  private final Consumer<FileEncoding> onSelectHandler;
  private final Runnable onCancelHandler;

  private AvailableEncodingDialog(Dialog parent,
                                  Consumer<FileEncoding> onSelectHandler,
                                  Runnable onCancelHandler) {
    super(parent);
    this.onSelectHandler = onSelectHandler;
    this.onCancelHandler = onCancelHandler;

    List<ExTableColumn> columns = new ArrayList<>();
    ReadOnlyExTableCellValueExtracter<FileEncoding> nameExtractor =
        new ReadOnlyExTableCellValueExtracter<FileEncoding>() {
          @Override
          public Object getValue(FileEncoding rowData, ExTableColumn column, int rowIndex, int columnIndex) {
            return rowData.getName();
          }
        };
    columns.add(new ExTableColumn(CoreBundle.get("key.encoding"), nameExtractor));

    ReadOnlyExTableCellValueExtracter<FileEncoding> aliasesExtractor =
        new ReadOnlyExTableCellValueExtracter<FileEncoding>() {
          @Override
          public Object getValue(FileEncoding rowData, ExTableColumn column, int rowIndex, int columnIndex) {
            return String.join(" / ", rowData.getAliases());
          }
        };
    columns.add(new ExTableColumn(CoreBundle.get("key.alias"), aliasesExtractor));

    initialize(columns, true);

    updateItems(FileEncoding.getAvailableEncodings());
    fitColumnSizeToFit(0);
  }

  public static void show(Dialog parent,
                          Consumer<FileEncoding> onSelectHandler,
                          Runnable onCancelHandler) {
    new AvailableEncodingDialog(parent, onSelectHandler, onCancelHandler).setVisible(true);
  }

  @Override
  protected void onItemSelected(FileEncoding item) {
    onSelectHandler.accept(item);
  }

  @Override
  public void updateItems(List<FileEncoding> items) {
    super.updateItems(items);
  }

  @Override
  protected String createTextForSearchingItem(FileEncoding item) {
    StringBuilder sb = new StringBuilder();
    sb.append(item.getName()).append('\t');
    for (String alias : item.getAliases()) {
      sb.append(alias).append('\t');
    }
    return sb.toString();
  }

  @Override
  protected String canonicalize(String s) {
    return CHARS_TO_REMOVE.matcher(s.toLowerCase()).replaceAll("");
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      updateSearchKeyword("");
    } else {
      if (!isSelectionConfirmed()) {
        onCancelHandler.run();
      }
    }
    super.setVisible(b);
  }
}
