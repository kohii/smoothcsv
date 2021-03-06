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
package com.smoothcsv.core.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.smoothcsv.csv.prop.LineSeparator;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.condition.ManualCondition;
import command.app.ToggleCommandPaletteCommand;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author kohii
 */
public class MacroRecorder {

  public static final ManualCondition RECORDING = new ManualCondition(false);

  @Getter
  private static boolean recording = false;

  private static MacroRecorder instance;

  @Setter
  private static Consumer<List<String>> macroAppendListener;

  public static void start() {
    if (instance != null || recording) {
      throw new IllegalStateException();
    }
    instance = new MacroRecorder();
    recording = true;
    RECORDING.setValueManually(true);

    macroAppendListener.accept(Collections.emptyList());
  }


  public static Macro stop() {
    if (!recording) {
      throw new IllegalStateException();
    }
    Macro macro = getInstance().createMacro();
    instance = null;
    recording = false;
    RECORDING.setValueManually(false);
    return macro;
  }


  public static MacroRecorder getInstance() {
    return instance;
  }

  private final ArrayList<String> lines = new ArrayList<>();
  private StringBuilder keyTyped = new StringBuilder();

  protected MacroRecorder() {}

  public void recordKeyTyping(String s) {
    keyTyped.append(s);
  }

  public void recordCommand(String commandId) {
    Command command = CommandRegistry.instance().getCommandOrNull(commandId);
    if (command != null && command instanceof ToggleCommandPaletteCommand) {
      return;
    }
    completeKeyTyping();
    collectLine("Command.run('" + commandId + "');");
  }

  public void recordMacroExecution(String pathname) {
    completeKeyTyping();
    collectLine("new Macro(new java.io.File('" + StringEscapeUtils.escapeEcmaScript(pathname)
        + "')).execute();");
  }

  private void completeKeyTyping() {
    if (keyTyped.length() != 0) {
      collectLine("App.getActiveCellEditor(true).type('"
          + StringEscapeUtils.escapeEcmaScript(keyTyped.toString()) + "');");
      keyTyped.setLength(0);
    }
  }

  private void collectLine(String line) {
    lines.add(line);
    if (macroAppendListener != null) {
      macroAppendListener.accept(lines);
    }
  }

  private Macro createMacro() {
    completeKeyTyping();
    return new Macro(String.join(LineSeparator.DEFAULT.stringValue(),
        lines.toArray(new String[lines.size()])));
  }
}
