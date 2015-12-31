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
package com.smoothcsv.debug;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.debug.command.EnableWatchThreadCommand;
import com.smoothcsv.debug.command.PrintComponentTreeCommand;
import com.smoothcsv.debug.command.PrintConditionsCommand;
import com.smoothcsv.debug.command.PrintFocusOwnerCommand;
import com.smoothcsv.debug.command.PrintGridDataCommand;
import com.smoothcsv.debug.command.PrintKeymapCommand;
import com.smoothcsv.debug.command.PrintMenuComponentTreeCommand;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandKeymap.Keybinding;
import com.smoothcsv.framework.command.CommandRepository;
import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.modular.ModuleEntryPointBase;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.util.DirectoryResolver;

/**
 *
 * @author kohii
 */
public class DebugEntryPoint extends ModuleEntryPointBase {

  static Logger LOG = LoggerFactory.getLogger(DebugEntryPoint.class);

  @Override
  public void activate() {

    SCApplication app = SCApplication.getApplication();

    app.listeners().on(SCApplication.WindowOpendEvent.class, e -> {
      LOG.debug("Start Up Time: {}ms", System.currentTimeMillis() - app.getStartTime());
      try {
        FileUtils.append(
            Arrays.asList(String.format("Start Up Time: %dms",
                System.currentTimeMillis() - app.getStartTime())),
            new File(DirectoryResolver.instance().getAppDataDirectory(), "startuptimes.log"),
            "UTF-8");
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    });

    app.listeners().on(SCApplication.WindowOpendEvent.class, e -> {

      LOG.debug(
          "\ncommands:*************************************** \n" + CommandRepository.instance());
      LOG.debug(
          "\nkeymaps:**************************************** \n" + CommandKeymap.getDefault());

      for (Entry<CssSelector, List<Keybinding>> entry : CommandKeymap.getDefault().getAll()
          .entrySet()) {
        for (Keybinding kb : entry.getValue()) {
          if (!CommandRepository.instance().isValid(kb.getCommand())) {
            LOG.error("keymap contains invalid command: {}", kb.getCommand());
          }
        }
      }
    });

    SCFocusManager.addListener(e -> {
      System.out.println("FocusOwnerChanged:" + e.getNewFocusOwner());
    });

    SmoothComponentManager.addVisibleComponentChangeListener(new Consumer<List<SmoothComponent>>() {

      @Override
      public void accept(List<SmoothComponent> comps) {
        // for (SmoothComponent smoothComponent : comps) {
        // System.out.println(smoothComponent.getComponentType());
        // }
      }
    });
  }

  @Override
  protected void loadCommands(CommandRepository commands) {
    commands.register("debug:enable-watch-thread", new EnableWatchThreadCommand());
    commands.register("debug:print-focus-owner", new PrintFocusOwnerCommand());
    commands.register("debug:print-keymap", new PrintKeymapCommand());
    commands.register("debug:print-components", new PrintComponentTreeCommand());
    commands.register("debug:print-menu-components", new PrintMenuComponentTreeCommand());
    commands.register("debug:print-grid-data", new PrintGridDataCommand());
    commands.register("debug:print-conditions", new PrintConditionsCommand());
  }
}
