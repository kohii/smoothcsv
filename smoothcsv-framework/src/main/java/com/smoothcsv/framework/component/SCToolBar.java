package com.smoothcsv.framework.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import com.smoothcsv.framework.command.CommandDef;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRepository;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.swing.icon.AwesomeIcon;

public class SCToolBar extends JToolBar {

  private static final long serialVersionUID = 7597981227923233779L;
  private Border buttonBorder = BorderFactory.createEmptyBorder(1, 4, 0, 4);

  public SCToolBar() {
    setFocusable(false);
    setFloatable(false);
  }

  public CommandDef add(String commandId, Icon icon, String caption) {
    CommandDef commandDef = CommandRepository.instance().getDef(commandId);
    super.add(new ToolBarAction(commandDef, icon, caption));
    return commandDef;
  }

  public CommandDef add(String commandId, char iconCode, String caption) {
    return add(commandId, AwesomeIcon.create(iconCode), caption);
  }

  public void setDefaultButtonBorder(Border buttonBorder) {
    this.buttonBorder = buttonBorder;
  }

  @Override
  protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
    // do nothing
    return false;
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    if (comp instanceof AbstractButton) {
      AbstractButton btn = ((AbstractButton) comp);
      btn.setBorder(buttonBorder);
      if (btn.getIcon() instanceof AwesomeIcon) {
        AwesomeIcon icon = (AwesomeIcon) btn.getIcon();
        btn.setRolloverIcon(AwesomeIcon.create(icon.getCode(), icon.getSize(), icon.getColor()
            .brighter()));
      }
    }
    super.addImpl(comp, constraints, index);
  }

  @SuppressWarnings("serial")
  static class ToolBarAction extends AbstractAction {

    private final String commandId;

    /**
     * @param commandIds
     */
    public ToolBarAction(CommandDef commandDef, Icon icon, String caption) {
      this.commandId = commandDef.getCommandId();
      Condition enableCondition = commandDef.getEnableWhen();
      if (enableCondition != null) {
        enableCondition.addValueChangedListener(e -> setEnabled(e.newValue));
      }
      putValue(Action.SMALL_ICON, icon);
      putValue(Action.SHORT_DESCRIPTION, caption);

      KeyStroke key = CommandKeymap.getDefault().findKeyStroke(commandId);
      putValue(Action.ACCELERATOR_KEY, key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      CommandRepository.instance().runCommand(commandId);
    }
  }

}
