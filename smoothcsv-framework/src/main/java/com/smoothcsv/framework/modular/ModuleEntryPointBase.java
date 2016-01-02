/*
 * Copyright 2015 kohii
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
package com.smoothcsv.framework.modular;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.SCApplication.AfterCreateGuiEvent;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.condition.Conditions;
import com.smoothcsv.framework.event.SCListener;
import com.smoothcsv.framework.io.ArrayCsvReader;
import com.smoothcsv.framework.io.CsvSupport;
import com.smoothcsv.framework.menu.ContextMenuManager;
import com.smoothcsv.framework.menu.MainMenuItems;
import com.smoothcsv.framework.menu.ToolBarItems;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.selector.SelectorFactory;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.framework.util.SCBundle;

/**
 * @author kohii
 *
 */
public class ModuleEntryPointBase implements ModuleEntryPoint {

  private static final String RESOURCE_NAME_KEYMAP = "smoothcsv-keymap";
  private static final String RESOURCE_NAME_MAIN_MENU = "smoothcsv-menubar";
  private static final String RESOURCE_NAME_CONTEXT_MENU = "smoothcsv-contextmenu";
  private static final String RESOURCE_NAME_TOOL_BAR = "smoothcsv-toolbar";
  private static final String RESOURCE_NAME_COMMANDS = "smoothcsv-command";
  private static final String SETTINGFILE_POSTFIX = ".tsv";

  private ModuleManifest manifest;

  /*
   * (non-Javadoc)
   *
   * @see com.smoothcsv.framework.modular.ModuleEntryPoint#activate()
   */
  @Override
  public final void activate(ModuleManifest manifest) {

    this.manifest = manifest;

    activate();

    loadConditions();
    loadBundles();
    loadCommands(CommandRegistry.instance());
    loadKeymap(CommandKeymap.getDefault());

    SCApplication.getApplication().listeners().on(SCApplication.AfterCreateGuiEvent.class,
        new SCListener<AfterCreateGuiEvent>() {
          @Override
          public void call(AfterCreateGuiEvent e) {
            loadMainMenus(MainMenuItems.instance());
            loadContextMenus(ContextMenuManager.instance());
            loadToolBar(ToolBarItems.instance());
          }
        });
  }

  protected void activate() {

  }

  protected void loadBundles() {
    SCBundle.register(manifest.getName() + ".bundle");
    MessageBundles.register(manifest.getName() + ".message");
  }

  protected void loadConditions() {}


  /**
   * @param registry
   */
  protected void loadCommands(CommandRegistry registry) {
    InputStream in = getResourceAsStream(RESOURCE_NAME_COMMANDS, SETTINGFILE_POSTFIX, manifest);
    if (in == null) {
      return;
    }
    try (InputStream _in = in;
        ArrayCsvReader reader = new ArrayCsvReader(new InputStreamReader(in, "UTF-8"),
            CsvSupport.TSV_PROPERTIES, CsvSupport.SKIP_EMPTYROW_OPTION, 3)) {
      String[] rowData;
      while ((rowData = reader.readRow()) != null) {
        if (StringUtils.isNotEmpty(rowData[0])) {
          registry.register(rowData[0], Conditions.getCondition(rowData[1]), rowData[2]);
        }
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  protected void loadKeymap(CommandKeymap keymap) {
    InputStream in = getResourceAsStream(RESOURCE_NAME_KEYMAP, SETTINGFILE_POSTFIX, manifest);
    if (in == null) {
      return;
    }
    try (InputStream _in = in;
        ArrayCsvReader reader = new ArrayCsvReader(new InputStreamReader(in, "UTF-8"),
            CsvSupport.TSV_PROPERTIES, CsvSupport.SKIP_EMPTYROW_OPTION, 3)) {
      CssSelector prevContext = null;
      String[] rowData;
      boolean ignore = false;
      while ((rowData = reader.readRow()) != null) {
        String context = rowData[0];
        String keyText = rowData[1];
        String commandId = rowData[2];

        if (!StringUtils.isEmpty(context)) {
          ignore = false;
          if (context.charAt(0) == '@') {
            if (context.startsWith("@mac ")) {
              if (Env.getOS() == Env.OS_MAC) {
                context = context.substring("@mac ".length()).trim();
              } else {
                ignore = true;
              }
            } else if (context.startsWith("@win ")) {
              if (Env.getOS() == Env.OS_WINDOWS) {
                context = context.substring("@win ".length()).trim();
              } else {
                ignore = true;
              }
            }
          }
          prevContext = ignore ? null : SelectorFactory.parseQuery(context);
        }

        if (!StringUtils.isEmpty(keyText) && !StringUtils.isEmpty(commandId) && !ignore) {
          keymap.add(keyText, commandId, prevContext);
        }
      }
    } catch (IOException e) {
      throw new UnexpectedException(e);
    }
  }

  protected void loadMainMenus(MainMenuItems mainMenuItems) {
    InputStream in = getResourceAsStream(RESOURCE_NAME_MAIN_MENU, SETTINGFILE_POSTFIX, manifest);
    if (in == null) {
      return;
    }
    mainMenuItems.loadConfig(in, RESOURCE_NAME_MAIN_MENU);
  }

  protected void loadContextMenus(ContextMenuManager contextMenuItems) {
    InputStream in = getResourceAsStream(RESOURCE_NAME_CONTEXT_MENU, SETTINGFILE_POSTFIX, manifest);
    if (in == null) {
      return;
    }
    contextMenuItems.loadConfig(in, RESOURCE_NAME_CONTEXT_MENU);
  }

  protected void loadToolBar(ToolBarItems toolBarItems) {
    InputStream in = getResourceAsStream(RESOURCE_NAME_TOOL_BAR, SETTINGFILE_POSTFIX, manifest);
    if (in == null) {
      return;
    }
    toolBarItems.loadConfig(in, RESOURCE_NAME_TOOL_BAR);
  }

  private InputStream getResourceAsStream(String name, String postfix, ModuleManifest manifest) {
    String resourceName = '/' + manifest.getName() + '/' + name;
    InputStream is = this.getClass()
        .getResourceAsStream(resourceName + "_" + Locale.getDefault().getLanguage() + postfix);
    if (is == null) {
      is = this.getClass().getResourceAsStream(resourceName + postfix);
    }
    return is;
  }
}
