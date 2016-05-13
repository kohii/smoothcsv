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
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.utils.SwingUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 * @author kohii
 */
public class ToolBarItems {

  private static ToolBarItems instance = new ToolBarItems();

  /**
   * @return the instance
   */
  public static ToolBarItems instance() {
    return instance;
  }

  private List<ToolBarItemDef> items = new ArrayList<>();

  public void loadConfig(InputStream in, String resourceName) {
    try (InputStream in2 = in;
         ArrayCsvReader reader =
             new ArrayCsvReader(new InputStreamReader(in, "UTF-8"), CsvSupport.TSV_PROPERTIES,
                 CsvSupport.SKIP_EMPTYROW_OPTION, 3)) {
      String[] rowData;
      while ((rowData = reader.readRow()) != null) {
        String commandId = rowData[0];
        String icon = rowData[1];
        String caption = rowData[2];
        items.add(new ToolBarItemDef(commandId, icon, caption));
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  public void loadToToolBar(SCToolBar toolBar) {
    for (int i = 0; i < items.size(); i++) {
      ToolBarItemDef itemDef = items.get(i);
      String iconStr = itemDef.icon;
      Icon icon;
      if (iconStr.startsWith("#")) {
        char awesomeIconKey = (char) Integer.decode("#3044").intValue();
        icon = AwesomeIcon.create(awesomeIconKey);
      } else {
        icon = SwingUtils.getImageIcon(iconStr);
      }
      toolBar.add(itemDef.commandId, icon, itemDef.caption);
    }

    // Discard
    items = null;
  }

  public static class ToolBarItemDef {

    private String commandId;
    private String icon;
    private String caption;

    /**
     * @param commandId
     * @param icon
     * @param caption
     */
    public ToolBarItemDef(String commandId, String icon, String caption) {
      this.commandId = commandId;
      this.icon = icon;
      this.caption = caption;
    }
  }
}
