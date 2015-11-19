package com.smoothcsv.framework.component;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.TransferHandler;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.error.ErrorHandlerFactory;
import com.smoothcsv.framework.event.EventListenerSupport;
import com.smoothcsv.framework.event.EventListenerSupportImpl;
import com.smoothcsv.framework.event.SCEvent;

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
