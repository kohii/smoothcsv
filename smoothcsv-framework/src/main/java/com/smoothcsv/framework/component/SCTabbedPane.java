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
package com.smoothcsv.framework.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.framework.component.view.ViewInfo;
import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;

public class SCTabbedPane extends JTabbedPane implements SmoothComponent {

  private static final long serialVersionUID = 8786435500332783844L;

  /**
   * @see #getUIClassID
   * @see #readObject
   */
  private static final String uiClassID = "SCTabbedPaneUI";

  private static final int LINEWIDTH = 3;
  private static final String TABDRAG_PRESENTABLE_NAME = SCTabbedPane.class.getName();
  private final GhostGlassPane glassPane = new GhostGlassPane();
  private final Rectangle lineRect = new Rectangle();
  private final Color lineColor = new Color(0, 100, 255);
  private final Map<Integer, BaseTabView<?>> idViewMap = new HashMap<>();
  private int dragTabIndex = -1;

  private EventListenerSupport eventListenerSupport = new EventListenerSupportImpl();

  @Getter
  private final SmoothComponentSupport componentSupport;

  private BaseTabView<?> oldSelectedView;

  private PropertyChangeListener titleChangeListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String fullTitle = (String) evt.getNewValue();
      ViewInfo viewInfo = (ViewInfo) evt.getSource();
      List<BaseTabView<?>> views = getAllViews();
      for (int i = 0; i < views.size(); i++) {
        if (views.get(i).getViewInfo() == viewInfo) {
          setToolTipTextAt(i, fullTitle);
          break;
        }
      }
      SCApplication.components().getFrame().setTitle(fullTitle);
    }
  };

  public SCTabbedPane(String componentTypeName) {
    super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

    componentSupport = new SmoothComponentSupport(this, componentTypeName);

    // setOpaque(false);
    setBorder(null);

    setFocusable(true);

    setMinimumSize(new Dimension(50, 50));

    addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        BaseTabView<?> newView = getSelectedView();
        SCTabbedPane.this.setFocusable(newView == null);

        if (oldSelectedView != null) {
          oldSelectedView.getViewInfo().getPropertyChangeSupport()
              .removePropertyChangeListener("fullTitle", titleChangeListener);
          oldSelectedView.onTabDeactivated();
        }

        if (newView == null) {
          SCTabbedPane.this.setFocusable(true);
          requestFocusInWindow();
          SCApplication.components().getFrame().setTitle(SCApplication.getApplication().getName());
        } else {
          SCTabbedPane.this.setFocusable(false);
          newView.requestFocusInWindow();
          newView.getViewInfo().getPropertyChangeSupport().addPropertyChangeListener("fullTitle",
              titleChangeListener);
          newView.getViewInfo().getPropertyChangeSupport().firePropertyChange("fullTitle", null,
              newView.getViewInfo().getFullTitle());
          newView.onTabActivated();
        }

        listeners().invokeListeners(new ViewChangeEvent(oldSelectedView, newView));
        oldSelectedView = newView;
      }
    });

    initDnD();
  }

  @Override
  public boolean beforeShowPopupMenu(MouseEvent e) {
    return indexAtLocation(e.getX(), e.getY()) >= 0;
  }

  /**
   * Returns the name of the UI class that implements the L&amp;F for this component.
   *
   * @return the string "TabbedPaneUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID() {
    return uiClassID;
  }

  public EventListenerSupport listeners() {
    return eventListenerSupport;
  }

  public void addTab(BaseTabView<?> comp) {
    addTab(comp.getViewInfo().getShortTitle(), null, comp, comp.getViewInfo().getFullTitle());

    comp.requestFocusInWindow();
    setSelectedComponent(comp);
  }

  @Override
  public void insertTab(String title, Icon icon, Component component, String tip, int index) {

    if (!(component instanceof BaseTabView)) {
      throw new IllegalArgumentException();
    }

    BaseTabView<?> view = (BaseTabView<?>) component;

    super.insertTab(title, icon, component, tip, index);
    setTabComponentAt(index, view.getTabComponent());
    idViewMap.put(view.getViewId(), view);
  }

  @Override
  public boolean requestFocusInWindow() {
    Component com = getSelectedComponent();
    if (com != null) {
      return com.requestFocusInWindow();
    } else {
      return super.requestFocusInWindow();
    }
  }

  @Override
  public void removeTabAt(int index) {
    Component comp = getComponentAt(index);
    super.removeTabAt(index);
    idViewMap.remove(((BaseTabView<?>) comp).getViewId());
    requestFocusInWindow();
  }

  public void removeTabWithoutConfirm(int index) {
    super.removeTabAt(index);
    requestFocusInWindow();
  }

  public BaseTabView<?> getSelectedView() {
    return (BaseTabView<?>) getSelectedComponent();
  }

  public List<BaseTabView<?>> getAllViews() {
    List<BaseTabView<?>> ret = new ArrayList<>(getTabCount());
    for (int i = 0, ln = getTabCount(); i < ln; i++) {
      ret.add((BaseTabView<?>) getComponentAt(i));
    }
    return ret;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getAllViews(Class<T> clazz) {
    return (List<T>) getAllViews().stream().filter(v -> v.getClass() == clazz)
        .collect(Collectors.toList());
  }

  @Override
  public BaseTabView<?> getComponentAt(int index) {
    return (BaseTabView<?>) super.getComponentAt(index);
  }

  public BaseTabView<?> getViewById(int viewId) {
    return idViewMap.get(viewId);
  }

  private Rectangle rBackward = new Rectangle();
  private Rectangle rForward = new Rectangle();

  private void initDnD() {
    final DragSourceListener dsl = new DragSourceListener() {
      @Override
      public void dragEnter(DragSourceDragEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
      }

      @Override
      public void dragExit(DragSourceEvent e) {
        e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        lineRect.setRect(0, 0, 0, 0);
        glassPane.setPoint(new Point(-1000, -1000));
        glassPane.repaint();
      }

      @Override
      public void dragOver(DragSourceDragEvent e) {
        Point glassPt = e.getLocation();
        SwingUtilities.convertPointFromScreen(glassPt, glassPane);
        int targetIdx = getTargetTabIndex(glassPt);
        // if(getTabAreaBounds().contains(tabPt) && targetIdx>=0 &&
        if (getTabAreaBounds().contains(glassPt) && targetIdx >= 0 && targetIdx != dragTabIndex
            && targetIdx != dragTabIndex + 1) {
          e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
          glassPane.setCursor(DragSource.DefaultMoveDrop);
        } else {
          e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
          glassPane.setCursor(DragSource.DefaultMoveNoDrop);
        }
      }

      @Override
      public void dragDropEnd(DragSourceDropEvent e) {
        lineRect.setRect(0, 0, 0, 0);
        dragTabIndex = -1;
        glassPane.setVisible(false);
        glassPane.setImage(null);
      }

      @Override
      public void dropActionChanged(DragSourceDragEvent e) {}
    };
    final Transferable t = new Transferable() {
      private final DataFlavor FLAVOR =
          new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, TABDRAG_PRESENTABLE_NAME);

      @Override
      public Object getTransferData(DataFlavor flavor) {
        return SCTabbedPane.this;
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
        DataFlavor[] f = new DataFlavor[1];
        f[0] = this.FLAVOR;
        return f;
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.getHumanPresentableName().equals(TABDRAG_PRESENTABLE_NAME);
      }
    };
    final DragGestureListener dgl = new DragGestureListener() {
      @Override
      public void dragGestureRecognized(DragGestureEvent e) {
        if (getTabCount() <= 1) {
          return;
        }
        Point tabPt = e.getDragOrigin();
        dragTabIndex = indexAtLocation(tabPt.x, tabPt.y);
        // "disabled tab problem".
        if (dragTabIndex < 0 || !isEnabledAt(dragTabIndex)) {
          return;
        }
        initGlassPane(e.getComponent(), e.getDragOrigin());
        try {
          e.startDrag(DragSource.DefaultMoveDrop, t, dsl);
        } catch (InvalidDnDOperationException idoe) {
          idoe.printStackTrace();
        }
      }
    };
    new DropTarget(glassPane, DnDConstants.ACTION_COPY_OR_MOVE, new CDropTargetListener(), true);
    new DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE,
        dgl);
    // DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this,
    // DnDConstants.ACTION_COPY_OR_MOVE, dgl);
  }

  class CDropTargetListener implements DropTargetListener {

    @Override
    public void dragEnter(DropTargetDragEvent e) {
      if (isDragAcceptable(e)) {
        e.acceptDrag(e.getDropAction());
      } else {
        e.rejectDrag();
      }
    }

    @Override
    public void dragExit(DropTargetEvent e) {}

    @Override
    public void dropActionChanged(DropTargetDragEvent e) {}

    private Point _glassPt = new Point();

    @Override
    public void dragOver(final DropTargetDragEvent e) {
      Point glassPt = e.getLocation();
      if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
        initTargetLeftRightLine(getTargetTabIndex(glassPt));
      } else {
        initTargetTopBottomLine(getTargetTabIndex(glassPt));
      }
      if (hasGhost()) {
        glassPane.setPoint(glassPt);
      }
      if (!_glassPt.equals(glassPt)) {
        glassPane.repaint();
      }
      _glassPt = glassPt;
      // autoScrollTest(glassPt);
    }

    @Override
    public void drop(DropTargetDropEvent e) {
      if (isDropAcceptable(e)) {
        swapTab(dragTabIndex, getTargetTabIndex(e.getLocation()));
        e.dropComplete(true);
      } else {
        e.dropComplete(false);
      }
      repaint();
    }

    private boolean isDragAcceptable(DropTargetDragEvent e) {
      Transferable t = e.getTransferable();
      if (t == null) {
        return false;
      }
      DataFlavor[] f = e.getCurrentDataFlavors();
      if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
        return true;
      }
      return false;
    }

    private boolean isDropAcceptable(DropTargetDropEvent e) {
      Transferable t = e.getTransferable();
      if (t == null) {
        return false;
      }
      DataFlavor[] f = t.getTransferDataFlavors();
      if (t.isDataFlavorSupported(f[0]) && dragTabIndex >= 0) {
        return true;
      }
      return false;
    }
  }

  private boolean hasGhost = true;

  public void setPaintGhost(boolean flag) {
    hasGhost = flag;
  }

  public boolean hasGhost() {
    return hasGhost;
  }

  private boolean isScrollAreaVisible = true;

  public void setScrollAreaVisible(boolean flag) {
    isScrollAreaVisible = flag;
  }

  public boolean isScrollAreaVisible() {
    return isScrollAreaVisible;
  }

  private int getTargetTabIndex(Point glassPt) {
    Point tabPt = SwingUtilities.convertPoint(glassPane, glassPt, SCTabbedPane.this);
    boolean isTB = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM;
    for (int i = 0; i < getTabCount(); i++) {
      Rectangle r = getBoundsAt(i);
      if (isTB) {
        r.setRect(r.x - r.width / 2, r.y, r.width, r.height);
      } else {
        r.setRect(r.x, r.y - r.height / 2, r.width, r.height);
      }
      if (r.contains(tabPt)) {
        return i;
      }
    }
    Rectangle r = getBoundsAt(getTabCount() - 1);
    if (isTB) {
      r.setRect(r.x + r.width / 2, r.y, r.width, r.height);
    } else {
      r.setRect(r.x, r.y + r.height / 2, r.width, r.height);
    }
    return r.contains(tabPt) ? getTabCount() : -1;
  }

  private void swapTab(int prev, int next) {
    if (next < 0 || prev == next) {
      return;
    }
    Component cmp = getComponentAt(prev);
    Component tab = getTabComponentAt(prev);
    String str = getTitleAt(prev);
    Icon icon = getIconAt(prev);
    String tip = getToolTipTextAt(prev);
    boolean flg = isEnabledAt(prev);
    int tgtindex = prev > next ? next : next - 1;
    removeTabWithoutConfirm(prev);
    insertTab(str, icon, cmp, tip, tgtindex);
    setEnabledAt(tgtindex, flg);
    // When you drag'n'drop a disabled tab, it finishes enabled and
    // selected.
    // pointed out by dlorde
    if (flg) {
      setSelectedIndex(tgtindex);
    }
    // I have a component in all tabs (jlabel with an X to close the tab)
    // and when i move a tab the component disappear.
    // pointed out by Daniel Dario Morales Salas
    setTabComponentAt(tgtindex, tab);
  }

  private void initTargetLeftRightLine(int next) {
    if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
      lineRect.setRect(0, 0, 0, 0);
    } else if (next == 0) {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
      lineRect.setRect(r.x - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
    } else {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
      lineRect.setRect(r.x + r.width - LINEWIDTH / 2, r.y, LINEWIDTH, r.height);
    }
  }

  private void initTargetTopBottomLine(int next) {
    if (next < 0 || dragTabIndex == next || next - dragTabIndex == 1) {
      lineRect.setRect(0, 0, 0, 0);
    } else if (next == 0) {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(0), glassPane);
      lineRect.setRect(r.x, r.y - LINEWIDTH / 2, r.width, LINEWIDTH);
    } else {
      Rectangle r = SwingUtilities.convertRectangle(this, getBoundsAt(next - 1), glassPane);
      lineRect.setRect(r.x, r.y + r.height - LINEWIDTH / 2, r.width, LINEWIDTH);
    }
  }

  private void initGlassPane(Component c, Point tabPt) {
    getRootPane().setGlassPane(glassPane);
    if (hasGhost()) {
      Rectangle rect = getBoundsAt(dragTabIndex);
      BufferedImage image =
          new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics g = image.getGraphics();
      c.paint(g);
      rect.x = rect.x < 0 ? 0 : rect.x;
      rect.y = rect.y < 0 ? 0 : rect.y;
      image = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
      glassPane.setImage(image);
    }
    Point glassPt = SwingUtilities.convertPoint(c, tabPt, glassPane);
    glassPane.setPoint(glassPt);
    glassPane.setVisible(true);
  }

  private Rectangle getTabAreaBounds() {
    Rectangle tabbedRect = getBounds();
    // pointed out by daryl. NullPointerException: i.e. addTab("Tab",null)
    // Rectangle compRect = getSelectedComponent().getBounds();
    Component comp = getSelectedComponent();
    int idx = 0;
    while (comp == null && idx < getTabCount()) {
      comp = getComponentAt(idx++);
    }
    Rectangle compRect = (comp == null) ? new Rectangle() : comp.getBounds();
    int tabPlacement = getTabPlacement();
    if (tabPlacement == TOP) {
      tabbedRect.height = tabbedRect.height - compRect.height;
    } else if (tabPlacement == BOTTOM) {
      tabbedRect.y = tabbedRect.y + compRect.y + compRect.height;
      tabbedRect.height = tabbedRect.height - compRect.height;
    } else if (tabPlacement == LEFT) {
      tabbedRect.width = tabbedRect.width - compRect.width;
    } else if (tabPlacement == RIGHT) {
      tabbedRect.x = tabbedRect.x + compRect.x + compRect.width;
      tabbedRect.width = tabbedRect.width - compRect.width;
    }
    tabbedRect.grow(2, 2);
    return tabbedRect;
  }

  class GhostGlassPane extends JPanel {

    private static final long serialVersionUID = 5243361633791516765L;
    private final AlphaComposite composite;
    private Point location = new Point(0, 0);
    private BufferedImage draggingGhost = null;

    public GhostGlassPane() {
      setOpaque(false);
      composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
      // http://bugs.sun.com/view_bug.do?bug_id=6700748
      // setCursor(null);
    }

    public void setImage(BufferedImage draggingGhost) {
      this.draggingGhost = draggingGhost;
    }

    public void setPoint(Point location) {
      this.location = location;
    }

    @Override
    public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setComposite(composite);
      if (isScrollAreaVisible() && getTabLayoutPolicy() == SCROLL_TAB_LAYOUT) {
        g2.setPaint(Color.RED);
        g2.fill(rBackward);
        g2.fill(rForward);
      }
      if (draggingGhost != null) {
        double xx = location.getX() - (draggingGhost.getWidth(this) / 2d);
        double yy = location.getY() - (draggingGhost.getHeight(this) / 2d);
        g2.drawImage(draggingGhost, (int) xx, (int) yy, null);
      }
      if (dragTabIndex >= 0) {
        g2.setPaint(lineColor);
        g2.fill(lineRect);
      }
    }
  }

  @Getter
  public static class ViewChangeEvent implements SCEvent {
    private BaseTabView<?> oldView;
    private BaseTabView<?> newView;

    /**
     * @param oldView
     * @param newView
     */
    public ViewChangeEvent(BaseTabView<?> oldView, BaseTabView<?> newView) {
      super();
      this.oldView = oldView;
      this.newView = newView;
    }
  }
}
