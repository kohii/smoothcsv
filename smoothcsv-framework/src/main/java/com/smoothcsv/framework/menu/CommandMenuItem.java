/**
 *
 */
package com.smoothcsv.framework.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.command.CommandDef;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRepository;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Condition.ConditionValueChangeEvent;

import lombok.Setter;

/**
 * @author kohii
 *
 */
@SuppressWarnings("serial")
public class CommandMenuItem extends JMenuItem implements ActionListener, IMenu {

  private final String caption;
  private final String commandId;
  @Setter
  private boolean contextMenu = false;

  public CommandMenuItem(String caption, String commandId) {
    this(caption, commandId, (Condition) null);
  }

  public CommandMenuItem(String caption, String commandId, Condition visibleWhen) {
    this(caption, commandId, visibleWhen, null, true);
  }

  public CommandMenuItem(String caption, String commandId, Icon icon) {
    this(caption, commandId, null, icon, true);
  }

  public CommandMenuItem(String caption, String commandId, Condition visibleWhen, Icon icon,
      boolean watchEnabledCondition) {
    this.caption = caption;
    this.commandId = commandId;
    setText(caption);
    if (icon != null) {
      setIcon(icon);
    }
    addActionListener(this);

    if (visibleWhen != null) {
      visibleWhen.addValueChangedListener(new Consumer<Condition.ConditionValueChangeEvent>() {
        @Override
        public void accept(ConditionValueChangeEvent event) {
          setVisible(event.newValue);
        }
      });
      setVisible(visibleWhen.getValue());
    }

    CommandDef def = CommandRepository.instance().getDef(commandId);
    Condition enableCondition = def.getEnableWhen();

    if (enableCondition != null) {
      setEnabled(enableCondition.getValue());
      if (watchEnabledCondition) {
        enableCondition
            .addValueChangedListener(new Consumer<Condition.ConditionValueChangeEvent>() {
              @Override
              public void accept(ConditionValueChangeEvent e) {
                setEnabled(e.newValue);
              }
            });
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.AbstractButton#setText(java.lang.String)
   */
  @Override
  public void setText(String text) {
    int indexOfMnemonicKey = text.indexOf('&');
    if (indexOfMnemonicKey >= 0) {
      // TODO performance tuning using char array.
      text = new StringBuilder(text).deleteCharAt(indexOfMnemonicKey).toString();
      int mnemonic = text.charAt(indexOfMnemonicKey + 1);
      setMnemonic(mnemonic);
    }
    super.setText(text);
  }

  @Override
  public KeyStroke getAccelerator() {
    if (Env.getOS() != Env.OS_MAC || contextMenu) {
      return CommandKeymap.getDefault().findKeyStroke(commandId);
    } else {
      // In order to make the system menu bar's key binding disabled, do not use accelerator.
      // TODO Is there any better way?
      return null;
    }
  }

  /**
   * @return the caption
   */
  @Override
  public String getCaption() {
    return caption;
  }

  /**
   * @return the commandId
   */
  public String getCommandId() {
    return commandId;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        CommandRepository.instance().runCommand(getCommandId());
      }
    });
  }
}
