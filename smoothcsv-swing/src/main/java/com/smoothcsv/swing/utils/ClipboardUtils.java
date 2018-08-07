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
package com.smoothcsv.swing.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author kohii
 */
public class ClipboardUtils {

  public static String readText() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable transferable = clipboard.getContents(null);
    if (transferable == null) {
      return null;
    }
    try {
      return (String) transferable.getTransferData(DataFlavor.stringFlavor);
    } catch (UnsupportedFlavorException e) {
      // ignore
    } catch (IOException e) {
      // ignore
    }
    return null;
  }

  public static void writeText(String text) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection ss = new StringSelection(text);
    clipboard.setContents(ss, null);
  }

}
