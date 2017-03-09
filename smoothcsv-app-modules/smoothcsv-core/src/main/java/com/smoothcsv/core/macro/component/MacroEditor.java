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
package com.smoothcsv.core.macro.component;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.core.macro.MacroRuntime;
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Condition.ConditionValueChangeEvent;
import com.smoothcsv.swing.icon.AwesomeIconConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.java.buildpath.ClasspathLibraryInfo;
import org.fife.rsta.ac.js.JavaScriptCompletionProvider;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.PreProcessingScripts;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.engine.RhinoJavaScriptEngine;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.javascript.Context;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
@Slf4j
public class MacroEditor extends JPanel implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport =
      new SmoothComponentSupport(this, "macro-editor");
  @Getter
  private RSyntaxTextArea textArea;

  public MacroEditor() {
    setLayout(new BorderLayout(0, 0));

    SCToolBar toolBar = new SCToolBar();
    toolBar.setFocusable(false);
    toolBar.setFloatable(false);
    add(toolBar, BorderLayout.NORTH);

    toolBar.add("macro:Open", AwesomeIconConstants.FA_FOLDER_OPEN_O,
        CoreBundle.get("key.open") + "...");
    toolBar.add("macro:Save", AwesomeIconConstants.FA_SAVE, CoreBundle.get("key.save") + "...");
    toolBar.addSeparator();
    JButton startRecordButton = toolBar.add("macro:ToggleRecordingMacro",
        AwesomeIconConstants.FA_CIRCLE, CoreBundle.get("key.startRecordMacro"));
    JButton stopRecordButton = toolBar.add("macro:ToggleRecordingMacro",
        AwesomeIconConstants.FA_STOP, CoreBundle.get("key.stopRecordMacro"));
    stopRecordButton.setVisible(false);
    toolBar.addSeparator();
    toolBar.add("macro:Run", AwesomeIconConstants.FA_PLAY, CoreBundle.get("key.run"));

    MacroRecorder.RECORDING
        .addValueChangedListener(new Consumer<Condition.ConditionValueChangeEvent>() {
          @Override
          public void accept(ConditionValueChangeEvent t) {
            startRecordButton.setVisible(!t.newValue);
            stopRecordButton.setVisible(t.newValue);
          }
        });

    textArea = new RSyntaxTextArea();
    // textArea.setWrapStyleWord(true);
    // textArea.setLineWrap(true);
    textArea.setTabSize(2);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
    textArea.setCodeFoldingEnabled(true);
    textArea.setMarkOccurrences(true);
    textArea.setTabsEmulated(true);
    installAutoComplete(textArea);

    RTextScrollPane scrollPane = new RTextScrollPane(textArea);
    scrollPane.setBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.getDefaultBorderColor()));
    add(scrollPane, BorderLayout.CENTER);
  }

  private void installAutoComplete(RSyntaxTextArea textArea) {

    SmoothCSVJavaScriptLanguageSupport languageSupport = new SmoothCSVJavaScriptLanguageSupport();
    languageSupport.setLanguageVersion(Context.VERSION_ES6);
    languageSupport.setAutoActivationEnabled(true);
    languageSupport.setAutoActivationDelay(600);
    languageSupport.setParameterAssistanceEnabled(true);
    languageSupport.setClient(true);

    try {
      languageSupport.getJarManager().addClassFileSource(new ClasspathLibraryInfo(
          new String[]{
              com.smoothcsv.core.macro.api.App.class.getName(),
              com.smoothcsv.core.macro.api.CellEditor.class.getName(),
              com.smoothcsv.core.macro.api.CellVisitor.class.getName(),
              com.smoothcsv.core.macro.api.Clipboard.class.getName(),
              com.smoothcsv.core.macro.api.Command.class.getName(),
              com.smoothcsv.core.macro.api.CsvProperties.class.getName(),
              com.smoothcsv.core.macro.api.CsvSheet.class.getName(),
              com.smoothcsv.core.macro.api.Macro.class.getName(),
              com.smoothcsv.core.macro.api.Range.class.getName(),
              com.smoothcsv.core.macro.api.Window.class.getName()
          }
      ));
    } catch (IOException e) {
      log.warn("can't load macro api classes for auto completion", e);
    }

    languageSupport.install(textArea);
  }


  @Override
  public boolean requestFocusInWindow() {
    return textArea.requestFocusInWindow();
  }

  static class SmoothCSVJavaScriptLanguageSupport extends JavaScriptLanguageSupport {

    @Override
    protected JavaScriptCompletionProvider createJavaScriptCompletionProvider() {
      return new JavaScriptCompletionProvider(new SmoothCSVSourceCompletionProvider(), getJarManager(), this);
    }

    @Override
    public void install(RSyntaxTextArea textArea) {
      //remove javascript support and replace with Rhino support
      LanguageSupport support = (LanguageSupport) textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport");
      if (support != null) {
        support.uninstall(textArea);
      }
      super.install(textArea);
    }
  }

  static class SmoothCSVSourceCompletionProvider extends SourceCompletionProvider {

    private boolean initialized = false;

    public SmoothCSVSourceCompletionProvider() {
      super(RhinoJavaScriptEngine.RHINO_ENGINE, false);
    }

    @Override
    public String getAlreadyEnteredText(JTextComponent comp) {
      if (!initialized) {
        PreProcessingScripts pps = new PreProcessingScripts(this);
        pps.parseScript(MacroRuntime.getInitScript(), new TypeDeclarationOptions("init.js", false, true));
        setPreProcessingScripts(pps);
      }

      return super.getAlreadyEnteredText(comp);
    }
  }
}
