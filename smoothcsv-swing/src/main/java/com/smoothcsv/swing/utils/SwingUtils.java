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
package com.smoothcsv.swing.utils;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import javax.swing.undo.UndoManager;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.ObjectUtils;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.swing.components.ExButtonGroup;

/**
 * @author kohii
 */
public class SwingUtils {

  private static int actionSequence = 0;

  private static Boolean isRetina;

  // private static final int defaultDismissDelay;
  // private static final int defaultInitialDelay;
  // private static final int defaultReshowDelay;
  //
  // static {
  // ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
  // defaultDismissDelay = toolTipManager.getDismissDelay();
  // defaultInitialDelay = toolTipManager.getInitialDelay();
  // defaultReshowDelay = toolTipManager.getReshowDelay();
  // }
  //
  // public static final void emphasizeToolTip() {
  // ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
  // toolTipManager.setDismissDelay(100000);
  // toolTipManager.setInitialDelay(200);
  // toolTipManager.setReshowDelay(100);
  // }
  //
  // public static final void resetToolTip() {
  // ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
  // toolTipManager.setDismissDelay(defaultDismissDelay);
  // toolTipManager.setInitialDelay(defaultInitialDelay);
  // toolTipManager.setReshowDelay(defaultReshowDelay);
  // }

  public static boolean isMoveKey(int keyCode) {
    return isArrowKey(keyCode) || (KeyEvent.VK_PAGE_UP <= keyCode && keyCode <= KeyEvent.VK_HOME);
  }

  public static boolean isArrowKey(int keyCode) {
    return KeyEvent.VK_LEFT <= keyCode && keyCode <= KeyEvent.VK_DOWN
        || KeyEvent.VK_KP_UP <= keyCode && keyCode <= KeyEvent.VK_KP_RIGHT;
  }

  public static final Component getFocusOwner() {
    return KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
  }

  public static void autofitColumnWidth(JTable table) {
    autofitColumnWidth(table, 2);
  }

  public static void autofitColumnWidth(JTable table, int buf) {
    int columncount = table.getColumnCount();
    for (int i = 0; i < columncount; i++) {
      autofitColumnWidth(table, i, buf);
    }
  }

  public static void autofitColumnWidth(JTable table, int vc, int buf) {
    TableColumn tc = table.getColumnModel().getColumn(vc);

    int max = table.getTableHeader() == null ? 1
        : table.getTableHeader().getDefaultRenderer()
        .getTableCellRendererComponent(table, tc.getHeaderValue(), false, false, 0, vc)
        .getPreferredSize().width;

    int vrows = table.getRowCount();
    for (int i = 0; i < vrows; i++) {
      TableCellRenderer r = table.getCellRenderer(i, vc);
      Object value = table.getValueAt(i, vc);
      Component c = r.getTableCellRendererComponent(table, value, false, false, i, vc);
      int w = c.getPreferredSize().width;
      if (max < w) {
        max = w;
      }
    }

    tc.setPreferredWidth(max + buf);
  }

  public static void trimByWindowSize(Component c) {
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Rectangle rect = env.getMaximumWindowBounds();

    Rectangle b = c.getBounds();
    if (b.y + b.height > rect.y + rect.height) {
      b.height -= (b.y + b.height) - (rect.y + rect.height);
    }
    if (b.x + b.width > rect.x + rect.width) {
      b.width -= (b.x + b.width) - (rect.x + rect.width);
    }
    c.setBounds(b);
  }

  public static void addPopup(Component component, final JPopupMenu popup) {
    component.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showMenu(e);
        }
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showMenu(e);
        }
      }

      private void showMenu(MouseEvent e) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    });
  }

  public static void installRestoreText(JTextField textField) {
    installRestoreText(textField, textField.getText());
  }

  public static void installRestoreText(final JTextField textField, final String defaultText) {
    FocusListener focusListener = new FocusListener() {

      private String text;

      @Override
      public void focusLost(FocusEvent focusevent) {
        String cur = textField.getText();
        if (StringUtils.isEmpty(cur)) {
          ((JTextField) focusevent.getSource())
              .setText(StringUtils.isEmpty(text) ? defaultText : (text));
        }
      }

      @Override
      public void focusGained(FocusEvent focusevent) {
        text = ((JTextField) focusevent.getSource()).getText();
      }
    };

    textField.addFocusListener(focusListener);
  }

  public static void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public static int getMenuShortcutKeyMask() {
    return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  }

  public static void save(JComponent comp, Object model) {
    Field[] fields = comp.getClass().getDeclaredFields();
    Class dstClass = model.getClass();
    for (Field field : fields) {
      Class type = field.getType();
      if (type.isAssignableFrom(JComponent.class)) {
        if (type.isAssignableFrom(JCheckBox.class)) {
          // try {
          // boolean value = ((JCheckBox) field.get(src)).isSelected();
          // PropertyUtils.setProperty(dst, field.getName(), value);
          // } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
          // {
          // // ignore
          // }
        }
      } else if (type.isAssignableFrom(ExButtonGroup.class)) {

      }
    }
  }

  // public static void load(JComponent comp, Object model) {
  //
  // try {
  // Field[] fields = model.getClass().getDeclaredFields();
  // for (Field field : fields) {
  // String propName = field.getName();
  // Field compField = comp.getClass().getDeclaredField(propName);
  // if (compField != null) {
  // Object value = field.get(model);
  //
  // if (!compField.isAccessible()) {
  // compField.setAccessible(true);
  // }
  // Object compFieldValue = compField.get(comp);
  //
  // if (compFieldValue instanceof JTextComponent) {
  // ((JTextComponent) compFieldValue).setText(value == null ? "" : value.toString());
  // } else if (compFieldValue instanceof JComboBox) {
  // ((JComboBox) compFieldValue).getModel().setSelectedItem(value);
  // } else if (compFieldValue instanceof ExButtonGroup) {
  // ((ExButtonGroup) compFieldValue).set
  // }
  // }
  // }
  // } catch (NoSuchFieldException ex) {
  // Logger.getLogger(SwingUtils.class.getName()).log(Level.SEVERE, null, ex);
  // } catch (SecurityException ex) {
  // Logger.getLogger(SwingUtils.class.getName()).log(Level.SEVERE, null, ex);
  // } catch (IllegalArgumentException ex) {
  // Logger.getLogger(SwingUtils.class.getName()).log(Level.SEVERE, null, ex);
  // } catch (IllegalAccessException ex) {
  // Logger.getLogger(SwingUtils.class.getName()).log(Level.SEVERE, null, ex);
  // }
  //
  // }
  public static ImageIcon getImageIcon(String name) {
    return new ImageIcon(SwingUtils.class.getResource(name));
  }

  public static Image getImage(String name) {
    Toolkit tk = Toolkit.getDefaultToolkit();
    return tk.createImage(SwingUtils.class.getResource(name));
  }

  public static final void setMaskFormatter(JFormattedTextField textField, String format) {
    try {
      MaskFormatter mf = new MaskFormatter(format);
      textField.setFormatterFactory(new DefaultFormatterFactory(mf));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public static final JFormattedTextField createMaskFormattedTextField(String format) {
    try {
      MaskFormatter mf = new MaskFormatter(format);
      JFormattedTextField field = new JFormattedTextField(mf);
      return field;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void refleshAction(JComponent com, KeyStroke keyStroke) {
    InputMap im = com.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    Object o = im.get(keyStroke);
    if (o == null) {
      im = com.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      o = im.get(keyStroke);
    }
    if (o != null) {
      Action a = com.getActionMap().get(o);
      a.setEnabled(a.isEnabled());
    }
  }

  public static void putNemonicKey(AbstractButton button, int mnemonicKey) {
    String text = button.getText();
    if ('A' <= mnemonicKey && mnemonicKey <= 'Z') {
      button.setText(getLabelName(text, (char) mnemonicKey, false));
    }
    button.setMnemonic(mnemonicKey);
  }

  public static void putNemonicKey(AbstractButton button, char mnemonicKey) {
    String text = button.getText();
    if (text.indexOf(mnemonicKey) >= 0) {
      button.setText(getLabelName(text, mnemonicKey, false));
    }
    button.setMnemonic(mnemonicKey);
  }

  private static String getLabelName(String name, char mnemonicKey, boolean addDot) {
    if (name.charAt(0) == mnemonicKey || mnemonicKey == '0') {
      return addDot ? name + "..." : name;
    }
    StringBuilder sb = new StringBuilder();
    sb.append(name).append('(').append(mnemonicKey).append(')');
    if (addDot) {
      sb.append("...");
    }
    return sb.toString();
  }

  public static void setKeyAction(JComponent com, Action a, int... conditions) {
    KeyStroke key = (KeyStroke) a.getValue(Action.ACCELERATOR_KEY);
    setKeyAction(com, key, a, conditions);
  }

  public static void setKeyAction(JComponent com, KeyStroke key, Action a, int... conditions) {
    Object name = a.getClass().getSimpleName();
    if (ObjectUtils.isEmpty(name)) {
      name = "noNameAction" + actionSequence++;
    }

    InputMap im;
    if (conditions.length == 0) {
      im = com.getInputMap(JComponent.WHEN_FOCUSED);
      im.put(key, name);
    } else {
      for (int i : conditions) {
        im = com.getInputMap(i);
        im.put(key, name);
      }
    }

    ActionMap am = com.getActionMap();
    am.put(name, a);
  }

  public static void setKeyAction(JComponent com, KeyStroke[] keys, Action a, int... conditions) {
    for (KeyStroke keyStroke : keys) {
      setKeyAction(com, keyStroke, a, conditions);
    }
  }

  public static void setEnabled(JTextField textField, boolean b) {
    textField.setEditable(b);
    textField.setEnabled(b);
  }

  public static void installUndoManager(JTextComponent textComponent) {
    installUndoManager(textComponent, new UndoManager());
  }

  public static void addTextUpdateListener(JTextComponent textComponent,
                                           Consumer<DocumentEvent> listener) {
    Document doc = textComponent.getDocument();
    doc.addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
        listener.accept(e);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        listener.accept(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        listener.accept(e);
      }
    });
  }

  @SuppressWarnings("serial")
  public static void installUndoManager(JTextComponent textComponent,
                                        final UndoManager undoManager) {

    Document doc = textComponent.getDocument();
    doc.addUndoableEditListener(new UndoableEditListener() {
      public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
      }
    });

    ActionMap am = textComponent.getActionMap();
    InputMap im = textComponent.getInputMap();
    am.put("undo", new AbstractAction("undo") {
      @Override
      public void actionPerformed(ActionEvent e) {
        undoManager.undo();
      }

      @Override
      public boolean isEnabled() {
        return undoManager.canUndo();
      }
    });
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, getMenuShortcutKeyMask()), "undo");

    am.put("redo", new AbstractAction("redo") {
      @Override
      public void actionPerformed(ActionEvent e) {
        undoManager.redo();
      }

      @Override
      public boolean isEnabled() {
        return undoManager.canRedo();
      }
    });
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, getMenuShortcutKeyMask()), "redo");
  }

  /*
   * @see BasicGraphicsUtils#isMenuShortcutKeyDown(InputEvent)
   */
  public static boolean isMenuShortcutKeyDown(InputEvent event) {
    return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
  }


  /*
   * @see JComponent#setWriteObjCounter(JComponent, byte)
   */
  public static void setWriteObjCounter(JComponent comp, byte count) {
    try {
      JComponent.class.getDeclaredMethod("setWriteObjCounter", JComponent.class, byte.class)
          .invoke(JComponent.class, comp, count);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new UnexpectedException(e);
    }
  }

  /*
   * @see JComponent#getWriteObjCounter(JComponent);
   */
  public static byte getWriteObjCounter(JComponent comp) {
    try {
      return (byte) JComponent.class.getDeclaredMethod("getWriteObjCounter", JComponent.class)
          .invoke(JComponent.class, comp);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new UnexpectedException(e);
    }
  }

  /*
   * @see BasicScrollBarUI.scrollByUnits(JScrollBar, int, int, boolean)
   */
  public static void scrollByUnits(JScrollBar scrollbar, int direction, int units,
                                   boolean limitToBlock) {
    // This method is called from BasicScrollPaneUI to implement wheel
    // scrolling, as well as from scrollByUnit().
    int delta;
    int limit = -1;

    if (limitToBlock) {
      if (direction < 0) {
        limit = scrollbar.getValue() - scrollbar.getBlockIncrement(direction);
      } else {
        limit = scrollbar.getValue() + scrollbar.getBlockIncrement(direction);
      }
    }

    for (int i = 0; i < units; i++) {
      if (direction > 0) {
        delta = scrollbar.getUnitIncrement(direction);
      } else {
        delta = -scrollbar.getUnitIncrement(direction);
      }

      int oldValue = scrollbar.getValue();
      int newValue = oldValue + delta;

      // Check for overflow.
      if (delta > 0 && newValue < oldValue) {
        newValue = scrollbar.getMaximum();
      } else if (delta < 0 && newValue > oldValue) {
        newValue = scrollbar.getMinimum();
      }
      if (oldValue == newValue) {
        break;
      }

      if (limitToBlock && i > 0) {
        assert limit != -1;
        if ((direction < 0 && newValue < limit) || (direction > 0 && newValue > limit)) {
          break;
        }
      }
      scrollbar.setValue(newValue);
    }
  }

  /*
   * @see BasicScrollBarUI.scrollByBlock(JScrollBar, int)
   */
  public static void scrollByBlock(JScrollBar scrollbar, int direction) {
    // This method is called from BasicScrollPaneUI to implement wheel
    // scrolling, and also from scrollByBlock().
    int oldValue = scrollbar.getValue();
    int blockIncrement = scrollbar.getBlockIncrement(direction);
    int delta = blockIncrement * ((direction > 0) ? +1 : -1);
    int newValue = oldValue + delta;

    // Check for overflow.
    if (delta > 0 && newValue < oldValue) {
      newValue = scrollbar.getMaximum();
    } else if (delta < 0 && newValue > oldValue) {
      newValue = scrollbar.getMinimum();
    }

    scrollbar.setValue(newValue);
  }

  /**
   * @see SwingUtilities#getValidateRoot
   */
  public static Container getValidateRoot(Container c, boolean visibleOnly) {
    Container root = null;

    for (; c != null; c = c.getParent()) {
      if (!c.isDisplayable() || c instanceof CellRendererPane) {
        return null;
      }
      if (c.isValidateRoot()) {
        root = c;
        break;
      }
    }

    if (root == null) {
      return null;
    }

    for (; c != null; c = c.getParent()) {
      if (!c.isDisplayable() || (visibleOnly && !c.isVisible())) {
        return null;
      }
      if (c instanceof Window || c instanceof Applet) {
        return root;
      }
    }
    return null;
  }

  public static Color alpha(Color foreground, Color background, int alpha) {
    double a = ((double) alpha) / ((double) 255);
    int r = (int) (foreground.getRed() * a + background.getRed() * (1.00 - a));
    int g = (int) (foreground.getGreen() * a + background.getGreen() * (1.00 - a));
    int b = (int) (foreground.getBlue() * a + background.getBlue() * (1.00 - a));
    return new Color(r, g, b);
  }

  public static boolean isModalDialogShowing() {
    Window[] windows = Window.getWindows();
    if (windows != null) {
      for (Window w : windows) {
        if (w.isShowing() && w instanceof Dialog && ((Dialog) w).isModal())
          return true;
      }
    }
    return false;
  }

  public static void expandFontSize(JComponent comp, float f) {
    Font font = comp.getFont();
    comp.setFont(font.deriveFont(font.getSize() * f));
  }

  public static int getLineHeight(JComponent comp) {
    FontMetrics fm = comp.getFontMetrics(comp.getFont());
    return fm.getHeight();
  }

  public static void walkComponents(Container container, Consumer<Component> callback) {
    Component[] children = ((Container) container).getComponents();
    for (Component component : children) {
      callback.accept(component);
      if (component instanceof Container) {
        walkComponents((Container) component, callback);
      }
    }
  }

  public static void disableHtml(JLabel label) {
    label.putClientProperty("html.disable", Boolean.TRUE);
  }

  public static void removeButtonDecoration(AbstractButton button) {
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setContentAreaFilled(false);
  }

  public static Window getFrontWindow() {
    Window[] windows = Window.getWindows();
    for (int i = windows.length - 1; i >= 0; i--) {
      Window window = windows[i];
      if (window.isVisible()) {
        return window;
      }
    }
    return null;
  }

  public static boolean isRetina() {
    if (isRetina != null) {
      return isRetina;
    }
    return isRetina = isRetinaImpl();
  }

  private static boolean isRetinaImpl() {
    GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    try {
      Field field = graphicsDevice.getClass().getDeclaredField("scale");
      if (field != null) {
        field.setAccessible(true);
        Object scale = field.get(graphicsDevice);
        if (scale instanceof Integer && (Integer) scale == 2) {
          return true;
        }
      }
    } catch (Exception ignore) {
    }
    return false;
  }

  public static Component getClosestDialogOrFrame(Component c,
                                                  boolean visibleOnly) {
    for (; c != null; c = c.getParent()) {
      if ((c instanceof Dialog || c instanceof Frame)
          && (!visibleOnly || c.isVisible())) {
        return c;
      }
    }
    return null;
  }

  public static Component getClosestDialog(Component c,
                                           boolean visibleOnly) {
    for (; c != null; c = c.getParent()) {
      if ((c instanceof Dialog)
          && (!visibleOnly || c.isVisible())) {
        return c;
      }
    }
    return null;
  }

  public static Component getClosestAncestor(Component c,
                                             Class<? extends Component> ancestorClass,
                                             boolean visibleOnly) {
    for (; c != null; c = c.getParent()) {
      if (ancestorClass.isAssignableFrom(c.getClass())
          && (!visibleOnly || c.isVisible())) {
        return c;
      }
    }
    return null;
  }
}
