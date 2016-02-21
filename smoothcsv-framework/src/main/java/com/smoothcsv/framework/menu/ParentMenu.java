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

import javax.swing.JMenu;

/**
 *
 * @author kohii
 */
@SuppressWarnings("serial")
public class ParentMenu extends JMenu implements IParentMenu {

  private final String caption;

  public ParentMenu(String caption) {
    this.caption = caption;
    setText(caption);
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

  public String getCaption() {
    return caption;
  }

  // private void refreshChildren() {
  // CommandRepository commands = CommandRepository.instance();
  // for (int i = 0, len = getItemCount(); i < len; i++) {
  // JMenuItem mi = getItem(i);
  // if (mi instanceof CommandMenuItem) {
  // CommandMenuItem cmi = (CommandMenuItem) mi;
  // cmi.setEnabled(commands.isEnabled(cmi.getCommandId()));
  // }
  // }
  // }

  @Override
  public void add(ParentMenu menu) {
    super.add(menu);
  }

  @Override
  public void add(CommandMenuItem menu) {
    super.add(menu);
  }
}
