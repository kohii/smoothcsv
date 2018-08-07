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
package com.smoothcsv.swing.components.grid;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.smoothcsv.swing.gridsheet.GridSheetPane;
import com.smoothcsv.swing.gridsheet.GridSheetUtils;
import com.smoothcsv.swing.gridsheet.model.GridSheetModel;
import com.smoothcsv.swing.utils.SwingUtils;

/**
 * @author kohii
 */
public class GridTest {


  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          GridSheetUtils.initializeUI();
          TestApp window = new TestApp();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
}


class TestApp {

  JFrame frame;

  /**
   * Create the application.
   */
  public TestApp() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {

    frame = new JFrame();
    frame.setBounds(100, 100, 450, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.getContentPane().setLayout(new BorderLayout());

    ArrayList dataList = new ArrayList();
    for (int i = 0; i < 300; i++) {
      ArrayList row = new ArrayList();
      for (int j = 0; j < 20; j++) {
        row.add((i + 1) + "" + (j + 1));
      }
      dataList.add(row);
    }
    final GridSheetPane grid = new GridSheetPane(new GridSheetModel(dataList));
    grid.getTable().setBorder(BorderFactory.createEmptyBorder(40, 40, 0, 40));

    SwingUtils.setKeyAction(grid, KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.META_DOWN_MASK),
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            grid.getScrollPane().setFrozen(true);
          }
        }, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    SwingUtils.setKeyAction(grid, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.META_DOWN_MASK),
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            grid.getScrollPane().setFrozen(false);
          }
        }, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    frame.getContentPane().add(grid, BorderLayout.CENTER);
  }

}
