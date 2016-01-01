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
package com.smoothcsv.framework.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.KeyStroke;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.selector.CssSelector;
import com.smoothcsv.framework.util.KeyStrokeUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author kohii
 *
 */
public class CommandKeymap {

  private static CommandKeymap defaultInstance;

  /**
   * @return the defaultInstance
   */
  public static CommandKeymap getDefault() {
    if (defaultInstance == null) {
      defaultInstance = new CommandKeymap();
    }
    return defaultInstance;
  }

  /**
   * @param defaultInstance the defaultInstance to set
   */
  public static void setDefault(CommandKeymap defaultInstance) {
    CommandKeymap.defaultInstance = defaultInstance;
  }

  private final Map<CssSelector, List<Keybinding>> keymaps = new HashMap<>();

  public void add(String key, String commandId, CssSelector context) {
    Keybinding kb = new Keybinding(KeyStrokeUtils.parse(key), commandId);
    // if (context == null) {
    // noContextKeyBindings.add(kb);
    // return;
    // }
    List<Keybinding> keybindings = keymaps.get(context);
    if (keybindings == null) {
      keybindings = new ArrayList<>();
      keymaps.put(context, keybindings);
    }
    keybindings.add(kb);
  }

  public String findCommand(KeyStroke key, SmoothComponent component) {
    for (Entry<CssSelector, List<Keybinding>> entry : keymaps.entrySet()) {
      if (entry.getKey().matches(component)) {
        List<Keybinding> keybindings = entry.getValue();
        if (keybindings != null) {
          for (int j = 0, len = keybindings.size(); j < len; j++) {
            if (keybindings.get(j).key.equals(key)) {
              return keybindings.get(j).command;
            }
          }
        }
      }
    }
    return null;
  }

  public KeyStroke findKeyStroke(String command) {
    for (Entry<CssSelector, List<Keybinding>> entry : keymaps.entrySet()) {
      List<Keybinding> keybindings = entry.getValue();
      for (int i = 0, len = keybindings.size(); i < len; i++) {
        if (keybindings.get(i).command.equals(command)) {
          return keybindings.get(i).key;
        }
      }
    }
    return null;
  }

  public void clear() {
    keymaps.clear();
  }

  public Map<CssSelector, List<Keybinding>> getAll() {
    return keymaps;
  }

  @Getter
  @NoArgsConstructor
  public static final class Keybinding {
    private KeyStroke key;
    private String command;

    Keybinding(KeyStroke key, String command) {
      this.key = key;
      this.command = command;
    }

    public String getKeyS() {
      return KeyStrokeUtils.stringify(key);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    // buf.append("*").append('\n');
    // for (Keybinding keybinding : noContextKeyBindings) {
    // buf.append("  ").append(keybinding.key).append(':').append(keybinding.commandId).append('\n');
    // }
    keymaps
        .forEach((context, keybindings) -> {
          buf.append(context).append('\n');
          for (Keybinding keybinding : keybindings) {
            buf.append("  ").append(keybinding.key).append(':').append(keybinding.command)
                .append('\n');
          }
        });
    return buf.toString();
  }
}
