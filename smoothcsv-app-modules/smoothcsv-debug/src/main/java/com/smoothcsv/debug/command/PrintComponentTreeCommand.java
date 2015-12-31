/*
 * Copyright 2015 kohii
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
package com.smoothcsv.debug.command;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JPanel;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;

/**
 * @author kohii
 *
 */
public class PrintComponentTreeCommand extends Command {

  @Override
  public void run() {
    Container comp = (JPanel) SCApplication.components().getFrame().getContentPane();
    print(comp, 0);
  }

  void print(Component comp, int depth) {
    for (int i = 0; i < depth; i++) {
      System.out.print("  ");
    }
    System.out.println(comp);
    if (comp instanceof Container) {
      Component[] children = ((Container) comp).getComponents();
      for (Component component : children) {
        print(component, depth + 1);
      }
    }
  }
}
