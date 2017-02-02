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
package com.smoothcsv.debug;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.smoothcsv.commons.utils.FileUtils;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandKeymap.Keybinding;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Condition.ConditionValueChangeEvent;
import com.smoothcsv.framework.condition.Conditions;
import com.smoothcsv.framework.modular.ModuleEntryPointBase;
import com.smoothcsv.framework.modular.ModuleManifest;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.util.DirectoryResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kohii
 */
public class DebugEntryPoint extends ModuleEntryPointBase {

  static Logger LOG = LoggerFactory.getLogger(DebugEntryPoint.class);

  @Override
  public ModuleManifest getManifest() {
    return ModuleManifest.builder()
        .name("smoothcsv-debug")
        .author("kohii")
        .dependencies(new String[]{"smoothcsv-core"})
        .build();
  }

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
          "\ncommands:*************************************** \n" + CommandRegistry.instance());
      LOG.debug(
          "\nkeymaps:**************************************** \n" + CommandKeymap.getDefault());

      for (Entry<CssSelector, List<Keybinding>> entry : CommandKeymap.getDefault().getAll()
          .entrySet()) {
        for (Keybinding kb : entry.getValue()) {
          if (!CommandRegistry.instance().isValid(kb.getCommand())) {
            LOG.error("keymap contains invalid command: {}", kb.getCommand());
          }
        }
      }
    });

    app.listeners().on(SCApplication.AfterOpenWindowEvent.class, e -> {
      Set<String> conditionNames = Conditions.getConditionNames();
      for (String conditionName : conditionNames) {
        Conditions.getCondition(conditionName)
            .addValueChangedListener(new Consumer<Condition.ConditionValueChangeEvent>() {
              @Override
              public void accept(ConditionValueChangeEvent t) {
                System.out.println("ConditionChanged: " + conditionName + " -> " + t.newValue);
              }
            });
      }
    });

    SCFocusManager.addListener(e -> {
      System.out.println("FocusOwnerChanged: " + e.getNewFocusOwner());
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
}
