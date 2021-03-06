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
package com.smoothcsv.framework.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import com.smoothcsv.framework.Env;
import com.smoothcsv.framework.command.CommandDef;
import com.smoothcsv.framework.command.CommandKeymap;
import com.smoothcsv.framework.command.CommandRegistry;
import com.smoothcsv.framework.condition.Condition;
import com.smoothcsv.framework.condition.Condition.ConditionValueChangeEvent;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CommandMenuItem extends JMenuItem implements ActionListener, IMenu {

  private final String caption;
  private final String commandId;

  public CommandMenuItem(String caption, String commandId) {
    this(caption, commandId, null);
  }

  public CommandMenuItem(String caption, String commandId, Condition visibleWhen) {
    this(caption, commandId, visibleWhen, null, true, false);
  }

  public CommandMenuItem(String caption,
                         String commandId,
                         Condition visibleWhen,
                         Icon icon,
                         boolean watchEnabledCondition,
                         boolean enableAccelerator) {
    this.caption = caption;
    this.commandId = commandId;
    IMenu.setCaption(this, caption);
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

    CommandDef def = CommandRegistry.instance().getDef(commandId);
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

    if (!Env.isUsingMacSystemMenuBar() || enableAccelerator) {
      setAcceleratorEnabled(true);
    } else {
      // In order to make the system menu bar's key binding disabled, do not register accelerator.
      // See SCMenuBar#add() for more details.
    }
  }

  @Override
  public void setAcceleratorEnabled(boolean enabled) {
    if (enabled) {
      setAccelerator(CommandKeymap.getDefault().findKeyStroke(commandId));
    } else {
      setAccelerator(null);
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
        CommandRegistry.instance().runCommand(getCommandId());
      }
    });
  }
}
