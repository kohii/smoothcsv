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
package com.smoothcsv.core.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
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
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.smoothcsv.commons.constants.OperatorSymbol;
import com.smoothcsv.core.filter.FilterConditionPanel.OperatorSymbolTreeNode;
import com.smoothcsv.framework.component.dialog.DialogOperation;
import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.utils.JTreeUtils;

/**
 * @author kohii
 */
public class FilterConditionPanel extends JPanel {

  private static final long serialVersionUID = -6242726780101059724L;

  private DnDTree tree;

  private DefaultMutableTreeNode root = new OperatorSymbolTreeNode(OperatorSymbol.AND);

  public FilterConditionPanel() {
    setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    add(panel, BorderLayout.SOUTH);

    tree = new DnDTree();

    DefaultTreeModel model = new DefaultTreeModel(root);
    tree.setModel(model);

    JButton button = new JButton(SCBundle.get("key.filter.add.cond"));
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ConditionItemDialog conditionItemDialog = new ConditionItemDialog(null, null);
        conditionItemDialog.setVisible(true);
        if (conditionItemDialog.getSelectedOperation() != DialogOperation.OK) {
          return;
        }
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = null;
        if (path != null) {
          node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        if (node == null) {
          node = root;
        } else if (node instanceof ConditionTreeNode) {
          node = (DefaultMutableTreeNode) node.getParent();
          if (node == null) {
            node = root;
          }
        }
        ConditionTreeNode newNode = new ConditionTreeNode(conditionItemDialog.getItem());
        node.add(newNode);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();
        tree.updateUI();
        JTreeUtils.expandAll(tree);

        TreePath treePath = new TreePath(newNode.getPath());
        tree.setSelectionPath(treePath);
      }
    });
    panel.add(button);

    JButton button_2 = new JButton(SCBundle.get("key.filter.add.and"));
    button_2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = null;
        if (path != null) {
          node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        if (node == null) {
          node = root;
        } else if (node instanceof ConditionTreeNode) {
          node = (DefaultMutableTreeNode) node.getParent();
        }
        OperatorSymbolTreeNode newNode = new OperatorSymbolTreeNode(OperatorSymbol.AND);
        node.add(newNode);
        tree.updateUI();
        JTreeUtils.expandAll(tree);

        TreePath treePath = new TreePath(newNode.getPath());
        tree.setSelectionPath(treePath);
      }
    });
    panel.add(button_2);

    JButton button_3 = new JButton(SCBundle.get("key.filter.add.or"));
    button_3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = null;
        if (path != null) {
          node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        if (node == null) {
          node = root;
        } else if (node instanceof ConditionTreeNode) {
          node = (DefaultMutableTreeNode) node.getParent();
        }
        OperatorSymbolTreeNode newNode = new OperatorSymbolTreeNode(OperatorSymbol.OR);
        node.add(newNode);
        tree.updateUI();
        JTreeUtils.expandAll(tree);

        TreePath treePath = new TreePath(newNode.getPath());
        tree.setSelectionPath(treePath);

      }
    });
    panel.add(button_3);

    final JButton editButton = new JButton(SCBundle.get("key.edit"));
    editButton.setEnabled(false);
    panel.add(editButton);

    final JButton button_1 = new JButton(SCBundle.get("key.delete"));
    panel.add(button_1);

    final JButton btnNewButton = new JButton(SCBundle.get("key.filter.toggle"));
    panel.add(btnNewButton);

    editButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        TreePath path = tree.getSelectionPath();
        if (path != null && path.getLastPathComponent() instanceof ConditionTreeNode) {
          ConditionTreeNode node = (ConditionTreeNode) path.getLastPathComponent();
          ConditionItemDialog conditionItemDialog =
              new ConditionItemDialog(null, node.getFilterConditionItem());
          conditionItemDialog.setVisible(true);
          if (conditionItemDialog.getSelectedOperation() != DialogOperation.OK) {
            return;
          }
          FilterConditionItem con = conditionItemDialog.getItem();
          node.setFilterConditionItem(con);
          node.setUserObject(con);
          DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
          model.reload();
          tree.updateUI();

        }
      }
    });

    button_1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = null;
        if (path != null) {
          node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        if (node == null || 0 == node.getLevel()) {
          return;
        }
        node.removeFromParent();
        button_1.setEnabled(false);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.reload();
        tree.updateUI();
        JTreeUtils.expandAll(tree);
      }
    });

    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = null;
        if (path != null) {
          node = (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        if (node == null) {
          return;
        } else if (node instanceof OperatorSymbolTreeNode) {
          OperatorSymbolTreeNode operatorSymbolTreeNode = (OperatorSymbolTreeNode) node;
          if (operatorSymbolTreeNode.getType().equals(OperatorSymbol.AND)) {
            operatorSymbolTreeNode.setType(OperatorSymbol.OR);
          } else {
            operatorSymbolTreeNode.setType(OperatorSymbol.AND);
          }
          tree.getModel().valueForPathChanged(path, operatorSymbolTreeNode.getType().toString());
        }

      }
    });

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane);

    tree.addTreeWillExpandListener(new TreeWillExpandListener() {

      @Override
      public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {}

      @Override
      public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        throw new ExpandVetoException(event);
      }
    });

    scrollPane.setViewportView(tree);
    Dimension s = scrollPane.getPreferredSize();
    s.height = 280;
    scrollPane.setPreferredSize(s);

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setClosedIcon(null);
    renderer.setOpenIcon(null);

    tree.setCellRenderer(renderer);

    JTreeUtils.expandAll(tree);

    button_1.setEnabled(false);
    btnNewButton.setEnabled(false);

    tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        if (path == null) {
          button_1.setEnabled(false);
          btnNewButton.setEnabled(false);
          editButton.setEnabled(false);

          return;
        }
        Object tmp = path.getLastPathComponent();
        if (tmp == null) {
          button_1.setEnabled(false);
          btnNewButton.setEnabled(false);
          editButton.setEnabled(false);
          return;
        }

        if (tmp instanceof DefaultMutableTreeNode) {
          if (((DefaultMutableTreeNode) tmp).getLevel() == 0) {
            button_1.setEnabled(false);
            btnNewButton.setEnabled(true);
            editButton.setEnabled(false);

            return;
          }
        }
        if (tmp instanceof OperatorSymbolTreeNode) {
          btnNewButton.setEnabled(true);
          editButton.setEnabled(false);

        } else {
          btnNewButton.setEnabled(false);
          editButton.setEnabled(true);

        }

        button_1.setEnabled(true);
      }
    });

    TreePath treePath = new TreePath(root.getPath());
    tree.setSelectionPath(treePath);
  }

  @Override
  public void setVisible(boolean aFlag) {
    JTreeUtils.expandAll(tree);
    super.setVisible(aFlag);
  }

  static class ConditionTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = -7173477330197158879L;

    private FilterConditionItem filterConditionItem;

    public FilterConditionItem getFilterConditionItem() {
      return filterConditionItem;
    }

    public void setFilterConditionItem(FilterConditionItem filterConditionItem) {
      this.filterConditionItem = filterConditionItem;
    }

    public ConditionTreeNode(FilterConditionItem filterConditionItem) {
      super(filterConditionItem.toString());
      setAllowsChildren(false);
      this.filterConditionItem = filterConditionItem;
    }
  }

  static class OperatorSymbolTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = -7173477330197158878L;

    private OperatorSymbol type;

    public OperatorSymbolTreeNode(OperatorSymbol type) {
      super(type.toString());
      this.type = type;
    }

    public void setType(OperatorSymbol type) {
      this.type = type;
    }

    public OperatorSymbol getType() {
      return type;
    }
  }

  public FilterConditionGroup getConditions() {
    return getCondition(root);
  }

  private FilterConditionGroup getCondition(Object o) {
    if (o instanceof OperatorSymbolTreeNode) {
      // AND/OR
      OperatorSymbolTreeNode node = (OperatorSymbolTreeNode) o;

      List<FilterConditionGroup> childrenCon = new ArrayList<FilterConditionGroup>();
      Enumeration<?> children = node.children();
      while (children.hasMoreElements()) {
        Object child = children.nextElement();
        childrenCon.add(getCondition(child));
      }
      OperatorSymbol symbol = node.getType();
      return new FilterConditionGroup(symbol, childrenCon.toArray(new FilterConditionGroup[0]));

    } else {
      // Condition
      ConditionTreeNode node = (ConditionTreeNode) o;
      return node.getFilterConditionItem();
    }
  }
}


class DnDTree extends JTree implements DragSourceListener, DropTargetListener, DragGestureListener {
  private static final long serialVersionUID = 8997106532443581443L;
  private static final String NAME = "TREE-TEST";
  private static final DataFlavor localObjectFlavor =
      new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME);
  private static final DataFlavor[] supportedFlavors = {localObjectFlavor};
  // private DragSource dragSource;
  // private DropTarget dropTarget;
  private TreeNode dropTargetNode = null;
  private TreeNode draggedNode = null;

  public DnDTree() {
    super();
    setCellRenderer(new DnDTreeCellRenderer());
    setModel(new DefaultTreeModel(new DefaultMutableTreeNode("default")));
    // dragSource = new DragSource();
    // DragGestureRecognizer dgr =
    new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    // dropTarget =
    new DropTarget(this, this);

    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  // DragGestureListener ---->
  @Override
  public void dragGestureRecognized(DragGestureEvent dge) {
    // System.out.println("dragGestureRecognized");
    Point pt = dge.getDragOrigin();
    TreePath path = getPathForLocation(pt.x, pt.y);
    if (path == null || path.getParentPath() == null) {
      return;
    }
    // System.out.println("start "+path.toString());
    draggedNode = (TreeNode) path.getLastPathComponent();
    Transferable trans = new RJLTransferable(draggedNode);
    new DragSource().startDrag(dge, Cursor.getDefaultCursor(), trans, this);
  }

  // <---- DragGestureListener

  // DragSourceListener ---->
  @Override
  public void dragDropEnd(DragSourceDropEvent dsde) {
    dropTargetNode = null;
    draggedNode = null;
    repaint();
  }

  @Override
  public void dragEnter(DragSourceDragEvent dsde) {
    dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
  }

  @Override
  public void dragExit(DragSourceEvent dse) {
    dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
  }

  @Override
  public void dragOver(DragSourceDragEvent dsde) {}

  @Override
  public void dropActionChanged(DragSourceDragEvent dsde) {}

  // <---- DragSourceListener

  // DropTargetListener ---->
  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {}

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {}

  @Override
  public void dragExit(DropTargetEvent dte) {}

  @Override
  public void dragOver(DropTargetDragEvent dtde) {
    DataFlavor[] f = dtde.getCurrentDataFlavors();
    boolean isDataFlavorSupported = f[0].getHumanPresentableName().equals(NAME);
    if (!isDataFlavorSupported) {
      rejectDrag(dtde);
      return;
    }
    // figure out which cell it's over, no drag to self
    Point pt = dtde.getLocation();
    TreePath path = getPathForLocation(pt.x, pt.y);
    if (path == null) {
      rejectDrag(dtde);
      return;
    }
    // Object draggingObject;
    // if(!isWebStart()) {
    // try{
    // draggingObject =
    // dtde.getTransferable().getTransferData(localObjectFlavor);
    // }catch(Exception ex) {
    // rejectDrag(dtde);
    // return;
    // }
    // }else{
    // draggingObject = getSelectionPath().getLastPathComponent();
    // }
    Object draggingObject = getSelectionPath().getLastPathComponent();
    MutableTreeNode draggingNode = (MutableTreeNode) draggingObject;
    DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) targetNode.getParent();
    while (parentNode != null) {
      if (draggingNode.equals(parentNode)) {
        // Can't drop a child into its parent
        rejectDrag(dtde);
        return;
      }
      parentNode = (DefaultMutableTreeNode) parentNode.getParent();
    }
    dropTargetNode = targetNode; // (TreeNode) path.getLastPathComponent();
    dtde.acceptDrag(dtde.getDropAction());
    repaint();
  }

  @Override
  public void drop(DropTargetDropEvent dtde) {
    // System.out.println("drop");
    // if(!isWebStart()) {
    // try{
    // draggingObject =
    // dtde.getTransferable().getTransferData(localObjectFlavor);
    // }catch(Exception ex) {
    // rejectDrag(dtde);
    // return;
    // }
    // }else{
    // draggingObject = getSelectionPath().getLastPathComponent();
    // }
    Object draggingObject = getSelectionPath().getLastPathComponent();
    DefaultTreeModel model = (DefaultTreeModel) getModel();
    Point p = dtde.getLocation();
    TreePath path = getPathForLocation(p.x, p.y);
    if (path == null || !(draggingObject instanceof MutableTreeNode)) {
      dtde.dropComplete(false);
      return;
    }
    // System.out.println("drop path is " + path);
    MutableTreeNode draggingNode = (MutableTreeNode) draggingObject;
    DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) targetNode.getParent();
    if (targetNode.equals(draggingNode)) {
      // Can't drop into myself
      dtde.dropComplete(false);
      return;
    }
    dtde.acceptDrop(DnDConstants.ACTION_MOVE);
    model.removeNodeFromParent(draggingNode);
    if (targetNode instanceof OperatorSymbolTreeNode) {
      model.insertNodeInto(draggingNode, targetNode, targetNode.getChildCount());
    } else if (parentNode != null && targetNode.isLeaf()) {
      model.insertNodeInto(draggingNode, parentNode, parentNode.getIndex(targetNode));
    } else {
      model.insertNodeInto(draggingNode, targetNode, targetNode.getChildCount());
    }
    dtde.getSource();

    setSelectionPath(path);

    JTreeUtils.expandAll(this);

    dtde.dropComplete(true);
  }

  private void rejectDrag(DropTargetDragEvent dtde) {
    dtde.rejectDrag();
    dropTargetNode = null;
    repaint();
  }

  // <---- DropTargetListener

  static class RJLTransferable implements Transferable {
    Object object;

    public RJLTransferable(Object o) {
      object = o;
    }

    @Override
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
      if (isDataFlavorSupported(df)) {
        return object;
      } else {
        throw new UnsupportedFlavorException(df);
      }
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor df) {
      return (df.getHumanPresentableName().equals(NAME));
      // return (df.equals(localObjectFlavor));
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return supportedFlavors;
    }
  }

  // custom renderer
  @SuppressWarnings("serial")
  class DnDTreeCellRenderer extends DefaultTreeCellRenderer {
    // private static final int BOTTOM_PAD = 30;
    private boolean isTargetNode;
    private boolean isTargetNodeLeaf;
    // private boolean isLastItem;
    // private Insets normalInsets;
    // private Insets lastItemInsets;

    public DnDTreeCellRenderer() {
      super();
      // normalInsets = super.getInsets();
      // lastItemInsets = new Insets(normalInsets.top, normalInsets.left,
      // normalInsets.bottom + BOTTOM_PAD, normalInsets.right);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected,
                                                  boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
      isTargetNode = (value == dropTargetNode);
      isTargetNodeLeaf = (isTargetNode && ((TreeNode) value).isLeaf());
      // isLastItem = (index == list.getModel().getSize()-1);
      // boolean showSelected = isSelected & (dropTargetNode == null);
      return super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row,
          hasFocus);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (isTargetNode) {
        g.setColor(Color.BLACK);
        if (isTargetNodeLeaf) {
          g.drawLine(0, 0, getSize().width, 0);
        } else {
          g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        }
      }
    }
  }

}
