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

import com.smoothcsv.commons.utils.ArrayUtils;
import com.smoothcsv.core.constants.UIConstants;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.core.sql.model.SqlColumnInfo;
import com.smoothcsv.core.sql.model.SqlCsvFileTableInfo;
import com.smoothcsv.core.sql.model.SqlCsvFileTables;
import com.smoothcsv.core.sql.model.SqlCsvSheetTableInfo;
import com.smoothcsv.core.sql.model.SqlTableInfo;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.SCToolBar;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.icon.AwesomeIcon;
import lombok.Getter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class SqlTableList extends JPanel implements SmoothComponent, TreeWillExpandListener,
    TreeSelectionListener {

  @Getter
  private final SmoothComponentSupport componentSupport = new SmoothComponentSupport(this,
      "sql-tablelist");

  private final JTree tree;
  private DefaultTreeModel model;

  private CategoryTreeNode categoryCsvSheetsNode;

  private CategoryTreeNode categoryCsvFilesNode;

  private BiConsumer<SqlTableInfo, SqlTableInfo> selectionChangeListener;

  public SqlTableList() {
    setLayout(new BorderLayout());
    setBorder(null);

    model = createTreeModel();
    tree = new JTree(model);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setCellRenderer(new MyRenderer());
    tree.addTreeWillExpandListener(this);
    tree.getSelectionModel().addTreeSelectionListener(this);

    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
        UIConstants.getDefaultBorderColor()));
    add(scrollPane, BorderLayout.CENTER);

    SCToolBar toolBar = new SCToolBar();
    add(toolBar, BorderLayout.NORTH);
    toolBar.add("sql:addTable", AwesomeIcon.FA_PLUS, "Add A New CSV File As Table");
    toolBar.add("sql:removeTable", AwesomeIcon.FA_MINUS, "Remove The Selected CSV File As Table");

    loadCsvSheetTables();

    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
    @SuppressWarnings("unchecked")
    Enumeration<DefaultMutableTreeNode> children = root.children();
    while (children.hasMoreElements()) {
      DefaultMutableTreeNode node = children.nextElement();
      tree.expandPath(new TreePath(node.getPath()));
    }
  }

  public SqlTableInfo getSelectedTableInfo() {
    return getSqlTableInfoFromPath(tree.getSelectionPath());
  }

  public void reloadCsvFileTables() {
    categoryCsvFilesNode.removeAllChildren();
    for (SqlCsvFileTableInfo table : SqlCsvFileTables.getInstance().getTables()) {
      categoryCsvFilesNode.add(new TableTreeNode(table));
    }
  }

  private DefaultTreeModel createTreeModel() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
    DefaultTreeModel model = new DefaultTreeModel(root);

    categoryCsvSheetsNode = new CategoryTreeNode("SmoothCSV Sheets");
    root.add(categoryCsvSheetsNode);
    categoryCsvFilesNode = new CategoryTreeNode("CSV Files");
    root.add(categoryCsvFilesNode);

    return model;
  }

  private void loadCsvSheetTables() {
    categoryCsvSheetsNode.removeAllChildren();
    List<CsvSheetView> views =
        SCApplication.components().getTabbedPane().getAllViews(CsvSheetView.class);
    String[] names = new String[views.size()];
    for (int i = 0; i < views.size(); i++) {
      String originalName = views.get(i).getViewInfo().getShortTitle();
      String name = originalName;
      int j = 0;
      while (ArrayUtils.contains(names, name)) {
        name = originalName + " (" + ++j + ")";
      }
      names[i] = name;
    }
    for (int i = 0; i < views.size(); i++) {
      SqlCsvSheetTableInfo tableInfo = new SqlCsvSheetTableInfo(views.get(i), names[i]);
      TableTreeNode node = new TableTreeNode(tableInfo);
      categoryCsvSheetsNode.add(node);
    }
  }

  private static abstract class AbstractTreeNode extends DefaultMutableTreeNode {
    public abstract Icon getIcon();

    public abstract String getText();

    public String getTooltip() {
      return null;
    }

    public void beforeExpand() {}

    @Override
    public boolean isLeaf() {
      return false;
    }
  }

  private static class CategoryTreeNode extends AbstractTreeNode {
    private static final Icon ICON = AwesomeIcon.create(AwesomeIcon.FA_FOLDER, Color.GRAY);

    public CategoryTreeNode(String categoryName) {
      setUserObject(categoryName);
    }

    @Override
    public Icon getIcon() {
      return ICON;
    }

    @Override
    public String getText() {
      return getUserObject().toString();
    }
  }

  private static class TableTreeNode extends AbstractTreeNode {
    private static final Icon ICON = AwesomeIcon.create(AwesomeIcon.FA_TABLE, Color.GRAY);

    private final SqlTableInfo tableInfo;
    private boolean columnsLoaded = false;

    public TableTreeNode(SqlTableInfo tableInfo) {
      this.tableInfo = tableInfo;
    }

    @Override
    public Icon getIcon() {
      return ICON;
    }

    @Override
    public String getText() {
      return tableInfo.getName();
    }

    @Override
    public void beforeExpand() {
      if (!columnsLoaded) {
        List<SqlColumnInfo> cols = tableInfo.getColumns();
        for (SqlColumnInfo columnInfo : cols) {
          add(new ColumnTreeNode(columnInfo));
        }
        columnsLoaded = true;
      }
    }
  }

  private static class ColumnTreeNode extends AbstractTreeNode {
    private static final Icon ICON = AwesomeIcon.create(AwesomeIcon.FA_COLUMNS, Color.GRAY);

    private final SqlColumnInfo columnInfo;

    public ColumnTreeNode(SqlColumnInfo columnInfo) {
      this.columnInfo = columnInfo;
    }

    @Override
    public Icon getIcon() {
      return ICON;
    }

    @Override
    public String getText() {
      String name = columnInfo.getName();
      if (name == null) {
        name = "c" + columnInfo.getColumnIndex();
      }
      return name + ": " + columnInfo.getType();
    }

    @Override
    public boolean isLeaf() {
      return true;
    }
  }

  public void setSelectionChangeListener(
      BiConsumer<SqlTableInfo, SqlTableInfo> selectionChangeListener) {
    this.selectionChangeListener = selectionChangeListener;
  }

  private static class MyRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (value instanceof AbstractTreeNode) {
        AbstractTreeNode node = (AbstractTreeNode) value;
        setIcon(node.getIcon());
        setText(node.getText());
        setToolTipText(node.getTooltip());
      }
      return this;
    }
  }

  @Override
  public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    TreePath path = event.getPath();
    Object obj = path.getLastPathComponent();
    if (obj instanceof AbstractTreeNode) {
      AbstractTreeNode node = (AbstractTreeNode) obj;
      node.beforeExpand();
    }
  }

  @Override
  public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {}

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    SqlTableInfo oldTableInfo = getSqlTableInfoFromPath(e.getOldLeadSelectionPath());
    SqlTableInfo newTableInfo = getSqlTableInfoFromPath(e.getNewLeadSelectionPath());
    if (oldTableInfo != newTableInfo) {
      selectionChangeListener.accept(oldTableInfo, newTableInfo);
    }
  }

  private SqlTableInfo getSqlTableInfoFromPath(TreePath path) {
    if (path == null) {
      return null;
    }
    if (path.getLastPathComponent() instanceof TableTreeNode) {
      return ((TableTreeNode) path.getLastPathComponent()).tableInfo;
    }
    if (path.getLastPathComponent() instanceof ColumnTreeNode) {
      return ((TableTreeNode) path.getParentPath().getLastPathComponent()).tableInfo;
    }
    return null;
  }
}
