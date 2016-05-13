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
package com.smoothcsv.framework.component.support;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.framework.command.CommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.Action;
import javax.swing.ActionMap;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class CommandActionMap extends ActionMap {

  private static final Logger LOG = LoggerFactory.getLogger(CommandActionMap.class);

  @Override
  public Action get(Object key) {
    if (key instanceof String) {
      String keyString = (String) key;
      if (keyString.indexOf(',') >= 0) {
        // multiple commands
        String[] ids = StringUtils.split(keyString, ',');
        ArrayList<String> actualIds = new ArrayList<>(ids.length);
        for (String id : ids) {
          if (CommandRegistry.instance().contains(id)) {
            boolean isEnabled = CommandRegistry.instance().isEnabled(id);
            LOG.debug("Command handler found. id:{}, enabled:{}", id, isEnabled);
            actualIds.add(id);
          } else {
            LOG.warn("Command doesn't exists. id:{}", id);
          }
        }
        if (!actualIds.isEmpty()) {
          return createActionFromCommand(actualIds.toArray(new String[actualIds.size()]));
        } else {
          return null;
        }
      } else {
        // single command
        String id = keyString;
        if (CommandRegistry.instance().contains(id)) {
          boolean isEnabled = CommandRegistry.instance().isEnabled(id);
          LOG.debug("Command handler found. id:{}, enabled:{}", id, isEnabled);
          if (isEnabled) {
            return createActionFromCommand(new String[]{id});
          }
        } else {
          LOG.warn("Command doesn't exists. id:{}", id);
        }
      }
    }
    return super.get(key);
  }

  protected Action createActionFromCommand(String[] commandId) {
    return new CommandWrapperAction(commandId);
  }

  protected static class CommandWrapperAction implements Action {

    protected final String[] commandIds;

    public CommandWrapperAction(String[] commandId) {
      this.commandIds = commandId;
    }

    @Override
    public Object getValue(String key) {
      return null;
    }

    @Override
    public void putValue(String key, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setEnabled(boolean b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled() {
      for (String id : commandIds) {
        if (CommandRegistry.instance().isEnabled(id)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      for (String id : commandIds) {
        if (CommandRegistry.instance().isEnabled(id)) {
          executeCommand(id);
        }
      }
    }

    protected void executeCommand(String commandId) {
      CommandRegistry.instance().runCommand(commandId);
    }
  }
}
