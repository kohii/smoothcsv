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
package com.smoothcsv.core.handler;

import com.smoothcsv.commons.exception.CancellationException;
import com.smoothcsv.commons.exception.IgnorableException;
import com.smoothcsv.commons.utils.ThrowableUtils;
import com.smoothcsv.core.component.SmoothCsvComponentManager;
import com.smoothcsv.core.macro.SCAppMacroRuntime;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.component.dialog.MessageDialogs;
import com.smoothcsv.framework.component.support.SmoothComponentManager;
import com.smoothcsv.framework.error.ErrorHandler;
import com.smoothcsv.framework.exception.AbortionException;
import com.smoothcsv.framework.exception.AppException;
import com.smoothcsv.framework.selector.SelectorFactory;
import com.smoothcsv.framework.util.MessageBundles;
import com.smoothcsv.swing.utils.SwingUtils;
import org.mozilla.javascript.RhinoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author kohii
 */
public class SmoothCsvErrorHandler implements ErrorHandler {

  private static final Logger LOG = LoggerFactory.getLogger(SmoothCsvErrorHandler.class);

  private final String outOfMemoryErrorMessage = MessageBundles.getString("ESCC0003");

  @Override
  public void handle(Throwable t) {
    try {
      if (t instanceof IgnorableException || t instanceof CancellationException) {
        return;
      }
      if (t instanceof AbortionException) {
        SwingUtils.beep();
        return;
      }
      if (!(t instanceof AppException)) {
        LOG.debug("Error!", t);
      }
      if (SwingUtilities.isEventDispatchThread()) {
        handleError(t);
      } else {
        SwingUtilities.invokeLater(() -> {
          handleError(t);
        });
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  private void handleError(Throwable t) {
    if (t instanceof AppException) {
      AppException appException = (AppException) t;
      SwingUtils.beep();
      MessageDialogs.alert(appException.getMessageId(), appException.getMessageParams());
    } else if (t instanceof RhinoException) {
      SmoothCsvComponentManager componentManager =
          (SmoothCsvComponentManager) SCApplication.components();
      if (!componentManager.getMacroTools().getConsolePanel().isVisible()
          || componentManager.getMacroTools().getConsolePanel().getParent() == null) {
        componentManager.getStatusBar().showTemporaryMessage(MessageBundles.getString("WSCA0003"));
      }
      componentManager.getMacroTools().getConsolePanel().append(t.getMessage());
    } else if (t instanceof OutOfMemoryError
        || (ThrowableUtils.getInitialCause(t) instanceof OutOfMemoryError)) {
      LOG.error("Error", t);
      MessageDialogs.showMessageString(null, outOfMemoryErrorMessage, JOptionPane.ERROR_MESSAGE);
    } else {
      if (SCAppMacroRuntime.getMacroRuntime().isMacroExecuting()
          && SmoothComponentManager.isComponentVisible(SelectorFactory.parseQuery("macro-console"))) {
        SmoothCsvComponentManager componentManager =
            (SmoothCsvComponentManager) SCApplication.components();
        componentManager.getMacroTools().getConsolePanel().append(t.toString());
      } else {
        LOG.error("Error", t);
        SwingUtils.beep();
        MessageDialogs.showMessageString(null, "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

}
