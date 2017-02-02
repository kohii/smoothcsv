package com.smoothcsv.core.celleditor;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ActionMap;

import com.smoothcsv.framework.component.support.SmoothComponent;
import com.smoothcsv.framework.component.support.SmoothComponentSupport;
import com.smoothcsv.swing.components.text.ExTextPane;
import com.smoothcsv.swing.components.text.ExTextPaneConfig;
import lombok.Getter;

/**
 * @author kohii
 */
public class SCTextPane extends ExTextPane implements SmoothComponent {

  @Getter
  private final SmoothComponentSupport componentSupport;

  private final ActionMap originalAm;

  public SCTextPane(String componentTypeName, ExTextPaneConfig config) {
    super(config);
    this.originalAm = getActionMap();
    this.componentSupport = new SmoothComponentSupport(this, componentTypeName);
  }

  public void invokeOriginalAction(String key) {
    ActionEvent e = new ActionEvent(this, 0, key);
    Action action = originalAm.get(key);
    action.actionPerformed(e);
  }
}
