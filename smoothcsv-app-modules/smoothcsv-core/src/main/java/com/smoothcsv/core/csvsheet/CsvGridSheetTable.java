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
package com.smoothcsv.core.csvsheet;

import com.smoothcsv.commons.constants.Direction;
import com.smoothcsv.core.csvsheet.CsvGridSheetCellStringEditor.CsvGridEditorComponent;
import com.smoothcsv.core.macro.MacroRecorder;
import com.smoothcsv.framework.component.support.SCFocusManager;
import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.gridsheet.GridSheetCellEditor;
import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetTable;
import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;
import com.smoothcsv.swing.gridsheet.renderer.GridSheetCellRenderer;
import command.grid.FillSeriesCommand;
import lombok.Getter;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.EventObject;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CsvGridSheetTable extends GridSheetTable implements SmoothComponent {

  public static final Object END_OF_LINE = new Object() {
    public String toString() {
      return "\n";
    }
  };

  public static final Object END_OF_FILE = new Object() {
    public String toString() {
      return "[EOF]";
    }
  };

  @Getter
  private final SmoothComponentSupport componentSupport = new SmoothComponentSupport(this, "grid");

  private InputMethodRequests inputMethodRequestsHandler;

  /**
   * @param gridSheetPane
   * @param gridSheetCellRenderer
   */
  public CsvGridSheetTable(GridSheetPane gridSheetPane,
                           GridSheetCellRenderer gridSheetCellRenderer) {
    super(gridSheetPane, gridSheetCellRenderer);

    enableInputMethods(true);
    enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.INPUT_METHOD_EVENT_MASK);

    addPropertyChangeListener("gridCellEditor", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == null) {
          removePseudoClass("editing");
        } else {
          addPseudoClass("editing");
        }
      }
    });
  }

  @Override
  public boolean beforeShowPopupMenu(MouseEvent e) {
    if (isEditing()) {
      stopCellEditing();
    }
    Point p = e.getPoint();
    int row = getGridSheetPane().rowAtPoint(p);
    int column = getGridSheetPane().columnAtPoint(p);
    if (row < 0 || column < 0) {
      return false;
    }
    if (!getGridSheetPane().isCellSelected(row, column)) {
      getGridSheetPane().getSelectionModel().setSelectionInterval(row, column, row, column);
    }
    return SmoothComponent.super.beforeShowPopupMenu(e);
  }

  /**
   * Returns the suffix used to construct the name of the L&F class used to render this component.
   *
   * @return the string "GridSheetTableUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  @Override
  public String getUIClassID() {
    return "CsvGridSheetTableUI";
  }

  @Override
  public CsvGridSheetPane getGridSheetPane() {
    return (CsvGridSheetPane) super.getGridSheetPane();
  }

  @Override
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {

    if (super.processKeyBinding(ks, e, condition, pressed)) {
      return true;
    }

    // Start editing when a key is typed.
    if (condition == WHEN_FOCUSED && isFocusOwner()) {
      GridSheetCellEditor editor = getCellEditor();
      CsvGridEditorComponent editorComponent =
          editor == null ? null : (CsvGridEditorComponent) editor.getEditorComponent();
      if (editorComponent == null) {

        if (e == null || e.getID() != KeyEvent.KEY_PRESSED
            || e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
          return false;
        }
        if (e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown()) {
          return false;
        }
        // Try to install the editor
        editorComponent = editQuickly();
        if (editorComponent == null) {
          return false;
        }
        if (MacroRecorder.isRecording()) {
          MacroRecorder.getInstance().recordCommand("grid:StartQuickEdit");
        }
      }
      // pass the event to the cell editor.
      if (!e.isConsumed() && (e.getID() == KeyEvent.KEY_TYPED
          || e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
        return editorComponent.processKeyBinding(ks, e, WHEN_FOCUSED, pressed);
      }
    }
    return false;
  }

  @Override
  protected void createDefaultEditors() {
    defaultEditorsByColumnClass = new HashMap<Class<?>, GridSheetCellEditor>();
    defaultEditorsByColumnClass.put(Object.class, new CsvGridSheetCellStringEditor(this));// FIXME
  }

  @Override
  public boolean editCellAt(int row, int column, EventObject e) {
    return editCellAt(row, column, e, true);
  }

  public boolean editCellAt(int row, int column, EventObject e, boolean editorGrabsFocus) {
    boolean ret = super.editCellAt(row, column, e);
    if (ret) {
      CsvGridEditorComponent editorComp =
          (CsvGridEditorComponent) getCellEditor().getEditorComponent();
      if (editorGrabsFocus) {
        editorComp.requestFocusInWindow();
      }
      editorComp.setQuickEdit(false);
    }
    return ret;
  }

  @Override
  protected Object getCellValueAt(int row, int column) {
    CsvGridSheetPane gridSheetPane = getGridSheetPane();
    int modelRowIndex = gridSheetPane.convertRowIndexToModel(row);
    int modelColumnIndex = gridSheetPane.convertRowIndexToModel(column);
    CsvGridSheetModel model = gridSheetPane.getModel();
    Object value = model.getValueAt(modelRowIndex, modelColumnIndex);
    if (value != null) {
      return value;
    }
    if (modelColumnIndex == model.getColumnCountAt(row)) {
      if (modelRowIndex == model.getRowCount() - 1) {
        return END_OF_FILE;
      } else {
        return END_OF_LINE;
      }
    }
    return null;
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
    CsvGridSheetPane gridSheetPane = (CsvGridSheetPane) getGridSheetPane();
    if (gridSheetPane != null) {
      int invisibleCharsRectWidth = gridSheetPane.getNewlineCharacterRectWidth();
      if (invisibleCharsRectWidth != 0
          && x + width >= gridSheetPane.getTotalColumnWidth() - invisibleCharsRectWidth) {
        width += invisibleCharsRectWidth;
      }
    }
    super.repaint(tm, x, y, width, height);
  }

  @Override
  public void editingStopped(ChangeEvent e) {
    super.editingStopped(e);
    if (SCFocusManager.getFocusOwner() == CsvGridSheetCellValuePanel.getInstance().getTextArea()) {
      requestFocus();
    }
  }

  @Override
  public void autofill(GridSheetCellRange base, Direction direction, int num) {
    FillSeriesCommand.autofill((CsvGridSheetPane) getGridSheetPane(), base, direction, num);
  }

  @Override
  protected void processInputMethodEvent(InputMethodEvent e) {
    super.processInputMethodEvent(e);

    if (!e.isConsumed()) {
      // Try to install the editor
      CsvGridEditorComponent editorComponent = editQuickly();
      if (editorComponent != null) {
        editorComponent.ignoreNextKeyEvent();
        if (MacroRecorder.isRecording()) {
          MacroRecorder.getInstance().recordCommand("grid:StartQuickEdit");
        }
        editorComponent.processInputMethodEvent(e);
      }
      e.consume();
    }
  }

  public CsvGridEditorComponent editQuickly() {
    GridSheetPane gridSheetPane = getGridSheetPane();
    int anchorRow = gridSheetPane.getSelectionModel().getRowAnchorIndex();
    int anchorColumn = gridSheetPane.getSelectionModel().getColumnAnchorIndex();
    boolean retValue = editCellAt(anchorRow, anchorColumn);
    if (retValue) {
      CsvGridEditorComponent editorComponent =
          (CsvGridEditorComponent) getCellEditor().getEditorComponent();
      editorComponent.setQuickEdit(true);
      editorComponent.selectAll();
      return editorComponent;
    }
    return null;
  }

  public InputMethodRequests getInputMethodRequests() {
    // CsvGridEditorComponent editorComponent = (CsvGridEditorComponent) getEditorComponent();
    // if (editorComponent != null) {
    // return editorComponent.getInputMethodRequests();
    // }

    if (inputMethodRequestsHandler == null) {
      inputMethodRequestsHandler = new InputMethodRequests() {

        @Override
        public Rectangle getTextLocation(TextHitInfo offset) {
          if (getCellEditor() == null) {
            return null;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .getTextLocation(offset);
        }

        @Override
        public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
          if (getCellEditor() == null) {
            return null;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .getSelectedText(attributes);
        }

        @Override
        public TextHitInfo getLocationOffset(int x, int y) {
          if (getCellEditor() == null) {
            return null;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests().getLocationOffset(x, y);
        }

        @Override
        public int getInsertPositionOffset() {
          if (getCellEditor() == null) {
            return 0;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .getInsertPositionOffset();
        }

        @Override
        public int getCommittedTextLength() {
          if (getCellEditor() == null) {
            return 0;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .getCommittedTextLength();
        }

        @Override
        public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex,
                                                            Attribute[] attributes) {
          if (getCellEditor() == null) {
            return new AttributedString("").getIterator();
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .getCommittedText(beginIndex, endIndex, attributes);
        }

        @Override
        public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
          if (getCellEditor() == null) {
            return null;
          }
          return getCellEditor().getEditorComponent().getInputMethodRequests()
              .cancelLatestCommittedText(attributes);
        }
      };
    }
    return inputMethodRequestsHandler;
  }
}
