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
import java.awt.Container;
import java.awt.Window;

import com.smoothcsv.framework.command.Command;

/**
 * @author kohii
 */
public class PrintComponentTreeCommand extends Command {

  @Override
  public void run() {
    Window[] windows = Window.getWindows();
    for (Window window : windows) {
      if (!window.isVisible()) {
        continue;
      }
      print(window, 0);
    }
  }

  void print(Component comp, int depth) {
    for (int i = 0; i < depth; i++) {
      System.out.print("  ");
    }
    System.out.println(comp.getClass()
        + " {visible: " + comp.isVisible()
        + ", focusable: " + comp.isFocusable()
        + "}");
    if (comp instanceof Container) {
      Component[] children = ((Container) comp).getComponents();
      for (Component component : children) {
        print(component, depth + 1);
      }
    }
  }
}
