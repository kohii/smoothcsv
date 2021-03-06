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
package com.smoothcsv.core.sql.component;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.icon.AwesomeIconConstants;
import lombok.Getter;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlEditor extends JPanel implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport = new SmoothComponentSupport(this,
      "sql-editor");

  @Getter
  private final RSyntaxTextArea textArea;

  @Getter
  private final SqlToolsDialog dialog;

  public SqlEditor(SqlToolsDialog dialog) {
    this.dialog = dialog;
    setBorder(null);
    setLayout(new BorderLayout(0, 0));

    SCToolBar toolBar = new SCToolBar();
    toolBar.add("sql:Run", AwesomeIconConstants.FA_PLAY, "Run SQL");
    toolBar.addSeparator();
//    toolBar.add("sql:Open", AwesomeIconConstants.FA_FOLDER_OPEN_O, "Open...");
//    toolBar.add("sql:Save", AwesomeIconConstants.FA_SAVE, "Save...");
//    toolBar.addSeparator();
//    toolBar.add("sql:ShowSqlHistory", AwesomeIconConstants.FA_HISTORY, "Show SQL History...");
//    toolBar.add("sql:PreviousSql", AwesomeIconConstants.FA_ARROW_CIRCLE_LEFT,
//        "Restore Previous SQL From History");
//    toolBar.add("sql:NextSql", AwesomeIconConstants.FA_ARROW_CIRCLE_RIGHT,
//        "Restore Next SQL From History");

    add(toolBar, BorderLayout.NORTH);

    textArea = new RSyntaxTextArea();
    // textArea.setWrapStyleWord(true);
    // textArea.setLineWrap(true);
    textArea.setTabSize(4);
    textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
    // textArea.setCodeFoldingEnabled(true);

    RTextScrollPane scrollPane = new RTextScrollPane(textArea);
    scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
        UIConstants.getDefaultBorderColor()));
    add(scrollPane, BorderLayout.CENTER);
  }

  public String getSQL() {
    return textArea.getText();
  }

  public boolean requestFocusTextAreaInWindow() {
    return textArea.requestFocusInWindow();
  }
}
