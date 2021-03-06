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
package command.debug;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;

/**
 * @author kohii
 */
public class PrintMenuComponentTreeCommand extends Command {

  @Override
  public void run() {
    JMenuBar menubar = SCApplication.components().getFrame().getJMenuBar();
    for (int i = 0; i < menubar.getMenuCount(); i++) {
      print(menubar.getMenu(i), 0);
    }
  }

  void print(JMenuItem comp, int depth) {
    for (int i = 0; i < depth; i++) {
      System.out.print("  ");
    }
    System.out.println(comp);
    if (comp instanceof JMenu) {
      Component[] children = ((JMenu) comp).getMenuComponents();
      for (Component component : children) {
        if (component instanceof JMenuItem) {
          print((JMenuItem) component, depth + 1);
        }
      }
    }
  }
}
