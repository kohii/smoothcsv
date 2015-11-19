/*
 * Copyright 2014 kohii.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.csv.reader.DefaultCsvReader;
import com.smoothcsv.framework.component.SCMenuBar;
import com.smoothcsv.framework.exception.IllegalConfigException;
import com.smoothcsv.framework.io.CsvSupport;

/**
 * @author kohii
 *
 */
public class MainMenuItems {

  private static final String SEPARATOR = "-";

  private static MainMenuItems instance;

  /**
   * @return the instance
   */
  public static MainMenuItems instance() {
    if (instance == null) {
      instance = new MainMenuItems();
    }
    return instance;
  }

  public void discardInstance() {
    instance = null;
  }

  private List<ParentMenu> topLevelMenus = new ArrayList<>();

  public void add(ParentMenu menu) {
    topLevelMenus.add(menu);
  }

  public ParentMenu getTopLevelMenu(String caption) {
    for (int i = 0; i < topLevelMenus.size(); i++) {
      if (topLevelMenus.get(i).getCaption().equals(caption)) {
        return topLevelMenus.get(i);
      }
    }
    return null;
  }

  public void loadToMenuBar(SCMenuBar menuBar) {
    for (int i = 0; i < topLevelMenus.size(); i++) {
      menuBar.add(topLevelMenus.get(i));
    }
    // Discard
    topLevelMenus = null;
  }

  public void loadConfig(InputStream in, String resourceName) {
    LinkedList<ParentMenu> menuStack = new LinkedList<>();
    ParentMenu previous = null;
    try (InputStream in2 = in;
        DefaultCsvReader reader =
            new DefaultCsvReader(new InputStreamReader(in, "UTF-8"), CsvSupport.TSV_PROPERTIES,
                CsvSupport.SKIP_EMPTYROW_OPTION)) {
      List<String> rowData;
      while ((rowData = reader.readRow()) != null) {
        int newDepth = 0;
        for (int i = 0; i < rowData.size(); i++) {
          if (!rowData.get(i).isEmpty()) {
            newDepth = i;
            break;
          }
        }
        String caption = rowData.get(newDepth);
        if (caption.isEmpty()) {
          throw new IllegalConfigException(resourceName);
        }
        String commandId;
        if (newDepth + 1 < rowData.size()) {
          commandId = rowData.get(newDepth + 1);
          if (commandId.isEmpty()) {
            commandId = null;
          }
        } else {
          commandId = null;
        }

        int currentDepth = menuStack.size();
        if (newDepth < currentDepth) {
          for (int i = currentDepth - newDepth; i > 0; i--) {
            menuStack.removeLast();
          }
        } else if (currentDepth < newDepth) {
          if (currentDepth + 1 != newDepth) {
            throw new IllegalConfigException(resourceName);
          }
          menuStack.addLast(previous);
        }

        if (caption.equals(SEPARATOR)) {
          if (menuStack.isEmpty()) {
            // top level menu
            throw new IllegalConfigException(resourceName);
          }
          menuStack.getLast().addSeparator();
        } else if (commandId != null) {
          if (menuStack.isEmpty()) {
            // top level menu
            throw new IllegalConfigException(resourceName);
          }
          CommandMenuItem menuItem = new CommandMenuItem(caption, commandId);
          menuStack.getLast().add(menuItem);
        } else {
          if (menuStack.isEmpty()) {
            // top level menu
            ParentMenu topLevelMenu = getTopLevelMenu(caption);
            if(topLevelMenu == null){
              topLevelMenu = new ParentMenu(caption);
              add(topLevelMenu);
            }
            previous = topLevelMenu;
          } else {
            ParentMenu menu = new ParentMenu(caption);
            menuStack.getLast().add(menu);
            previous = menu;
          }
        }
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }
}
