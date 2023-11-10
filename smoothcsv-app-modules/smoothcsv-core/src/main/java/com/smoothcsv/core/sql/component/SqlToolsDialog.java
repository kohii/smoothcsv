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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCTabbedPaneUI;
import com.smoothcsv.framework.component.dialog.DialogBase;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import lombok.Getter;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlToolsDialog extends DialogBase {

  private SqlEditor sqlEditor;
  private SqlTableList tableListPanel;
  private JTabbedPane tabbedPane;
  private SqlTableColumnsEditorPanel tableColumnsEditorPanel;
  private SqlCsvPropertiesPanel csvPropertiesPanel;
  private SqlTablePreviewPanel tablePreviewPanel;

  private boolean initialized = false;

  public SqlToolsDialog() {
    super(SCApplication.components().getFrame(), "SQL");

    setCanBeActive(true);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setDividerSize(5);
    splitPane.setBorder(null);
    getContentPanel().add(splitPane, BorderLayout.CENTER);

    JSplitPane leftSplitPane = new JSplitPane();
    leftSplitPane.setDividerSize(5);
    leftSplitPane.setBorder(null);
    leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setLeftComponent(leftSplitPane);

    tableListPanel = new SqlTableList();
    tableListPanel.setBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 1, UIConstants.getDefaultBorderColor()));
    tableListPanel.setSelectionChangeListener((oldTableInfo, newTableInfo) -> {
      adjustTableDetailsPanel(newTableInfo);
    });
    tableListPanel.setTableNameInsertButtonListener(tableName -> {
      sqlEditor.getTextArea().replaceSelection("`" + tableName + "`");
      sqlEditor.getTextArea().requestFocusInWindow();
    });
    leftSplitPane.setTopComponent(tableListPanel);

    {
      tableColumnsEditorPanel = new SqlTableColumnsEditorPanel();
      tableColumnsEditorPanel.setColumnNameInsertButtonListener(columnName -> {
        sqlEditor.getTextArea().replaceSelection("`" + columnName + "`");
        sqlEditor.getTextArea().requestFocusInWindow();
      });

      csvPropertiesPanel = new SqlCsvPropertiesPanel();

      tablePreviewPanel = new SqlTablePreviewPanel();

      tabbedPane = new JTabbedPane();
      tabbedPane.setFocusable(false);
      tabbedPane.setUI(new SCTabbedPaneUI());
      tabbedPane.setBorder(
          BorderFactory.createMatteBorder(1, 0, 0, 1, UIConstants.getDefaultBorderColor()));
      tabbedPane.add("Columns", tableColumnsEditorPanel);
      tabbedPane.add("Preview", tablePreviewPanel);
      tabbedPane.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          adjustTableDetailsPanel(tableListPanel.getSelectedTableInfo());
        }
      });
      adjustTableDetailsPanel(null);
      leftSplitPane.setBottomComponent(tabbedPane);
    }

    sqlEditor = new SqlEditor(this);
    sqlEditor.setBorder(
        BorderFactory.createMatteBorder(0, 1, 0, 0, UIConstants.getDefaultBorderColor()));
    splitPane.setRightComponent(sqlEditor);

    setSize(800, 600);

    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentShown(ComponentEvent componentEvent) {

        tableListPanel.stopEditing();

        if (!initialized) {
          splitPane.setResizeWeight(0);
          splitPane.setDividerLocation(0.35);
          leftSplitPane.setResizeWeight(0.5);
          leftSplitPane.setDividerLocation(0.4);

          // hack: avoid dialog not focusable
          JDialog dummyDialog = new JDialog(SqlToolsDialog.this, true);
          dummyDialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
              dummyDialog.setVisible(false);
            }
          });
          dummyDialog.setVisible(true);

          initialized = true;
        }

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            sqlEditor.requestFocusTextAreaInWindow();
          }
        });
      }
    });
  }

  public void stopTableNameEdition() {
    tableListPanel.stopEditing();
    tableColumnsEditorPanel.stopEditiong();
  }

  public void loadCsvSheetTables() {
    tableListPanel.loadCsvSheetTables();
  }

  @Override
  protected JPanel createContentPanel() {
    return new SqlToolsBody();
  }

  @Override
  protected JPanel createButtonPanel(DialogOperationAction[] actions) {
    return null;
  }

  private void adjustTableDetailsPanel(SqlTableInfo tableInfo) {
    tabbedPane.setEnabled(tableInfo != null);
    if (tableInfo == null) {
      tabbedPane.setSelectedComponent(tableColumnsEditorPanel);
      tableColumnsEditorPanel.setEnabled(false);
      if (csvPropertiesPanel.getParent() != null) {
        tabbedPane.remove(csvPropertiesPanel);
      }
    } else {
      tableColumnsEditorPanel.setEnabled(true);
      if (tableInfo instanceof SqlCsvSheetTableInfo) {
        if (csvPropertiesPanel.getParent() != null) {
          tabbedPane.remove(csvPropertiesPanel);
        }
      } else {
        if (csvPropertiesPanel.getParent() == null) {
          tabbedPane.insertTab("CSV Properties", null, csvPropertiesPanel, null, 0);
        }
      }
    }
    ((AbstractSqlTableDetailsPanel) tabbedPane.getSelectedComponent()).load(tableInfo);
  }

  private static class SqlToolsBody extends JPanel implements SmoothComponent {
    @Getter
    private final SmoothComponentSupport componentSupport =
        new SmoothComponentSupport(this, "sql-tools");

    public SqlToolsBody() {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
  }
}
