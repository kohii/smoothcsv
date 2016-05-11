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
package com.smoothcsv.framework.menu;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author kohii
 */
public class ContextMenuManager {

  private static ContextMenuManager instance = new ContextMenuManager();

  /**
   * @return the instance
   */
  public static ContextMenuManager instance() {
    return instance;
  }

  private List<ContextMenuDef> data = new ArrayList<>();

  @Getter
  private ContextMenu visibleContextMenu;

  public ContextMenu getContextMenu(SmoothComponent component) {
    ContextMenu contextMenu = new ContextMenu();
    for (int i = 0; i < data.size(); i++) {
      ContextMenuDef def = data.get(i);
      if (def.getContext().matches(component)) {
        if ("-".equals(def.caption)) {
          contextMenu.addSeparator();
        } else {
          CommandMenuItem item = new CommandMenuItem(def.caption, def.commandId, null, null, false, true);
          contextMenu.add(item);
        }
      }
    }
    if (contextMenu.getComponentCount() == 0) {
      return null;
    }

    contextMenu.addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        visibleContextMenu = contextMenu;
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        visibleContextMenu = null;
      }

      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
      }
    });

    return contextMenu;
  }

  public void loadConfig(InputStream in, String resourceName) {
    try (InputStream in2 = in;
         ArrayCsvReader reader =
             new ArrayCsvReader(new InputStreamReader(in, "UTF-8"), CsvSupport.TSV_PROPERTIES,
                 CsvSupport.SKIP_EMPTYROW_OPTION, 3)) {
      String prevContext = null;
      String[] rowData;
      while ((rowData = reader.readRow()) != null) {
        String context = rowData[0];
        String caption = rowData[1];
        String commandId = rowData[2];

        if (!StringUtils.isEmpty(context)) {
          prevContext = context;
        }
        if (!StringUtils.isEmpty(caption)) {
          if ("-".equals(caption)) {
            data.add(new ContextMenuDef(prevContext, caption, null));
          } else if (!StringUtils.isEmpty(commandId)) {
            data.add(new ContextMenuDef(prevContext, caption, commandId));
          }
        }
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  private static class ContextMenuDef {
    String caption;
    String commandId;
    // CommandMenuItem menuItem;
    String context;

    /**
     * @param context
     * @param caption
     * @param commandId
     */
    public ContextMenuDef(String context, String caption, String commandId) {
      this.context = context;
      this.caption = caption;
      this.commandId = commandId;
    }

    //
    // CommandMenuItem getMenuItem() {
    // if (menuItem == null) {
    // menuItem = new CommandMenuItem(caption, commandId);
    // }
    // return menuItem;
    // }

    CssSelector getContext() {
      return SelectorFactory.parseQuery(context);
    }
  }
}
