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
package com.smoothcsv.framework.component;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class SCFrame extends JFrame {

  private final EventListenerSupport eventListenerSupport = new EventListenerSupportImpl();

  public SCFrame() {
    setFocusable(false);
    setMinimumSize(new Dimension(200, 200));
    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        BaseTabView<?> tab = SCApplication.components().getTabbedPane().getSelectedView();
        tab.requestFocusInWindow();
      }
    });

    setTransferHandler(new TransferHandler() {

      @Override
      public boolean importData(TransferHandler.TransferSupport support) {
        try {
          for (Object o : (List<?>) support.getTransferable().getTransferData(
              DataFlavor.javaFileListFlavor)) {
            if (o instanceof File) {
              File file = (File) o;
              listeners().invokeListeners(new FileDroppedEvent(file));
            }
          }
          return true;
        } catch (Throwable t) {
          ErrorHandlerFactory.getErrorHandler().handle(t);
          return false;
        }
      }

      @Override
      public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
      }
    });
  }

  @Override
  protected JRootPane createRootPane() {
    JRootPane rp = new SCRootPane();
    rp.setOpaque(true);
    return rp;
  }

  public EventListenerSupport listeners() {
    return eventListenerSupport;
  }

  public static class FileDroppedEvent implements SCEvent {

    private final File file;

    public FileDroppedEvent(File file) {
      this.file = file;
    }

    public File getFile() {
      return file;
    }
  }

}
