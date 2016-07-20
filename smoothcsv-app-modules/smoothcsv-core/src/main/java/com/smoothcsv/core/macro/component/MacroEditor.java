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
import com.smoothcsv.core.util.CoreBundle;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Condition.ConditionValueChangeEvent;
import com.smoothcsv.swing.icon.AwesomeIconConstants;
import lombok.Getter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
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
    textArea.setTabSize(4);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
    // textArea.setCodeFoldingEnabled(true);

    RTextScrollPane scrollPane = new RTextScrollPane(textArea);
    scrollPane.setBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.getDefaultBorderColor()));
    add(scrollPane, BorderLayout.CENTER);
  }

  @Override
  public boolean requestFocusInWindow() {
    return textArea.requestFocusInWindow();
  }
}
