package com.smoothcsv.framework.error;

import com.smoothcsv.commons.utils.ThrowableUtils;

/**
 *
 * @author kohii
 */
public class ErrorHandlerFactory {

  private static final ErrorHandler DEFAULT_HANDLER = (t) -> {
    System.out.println(ThrowableUtils.getStackTrace(t));
  };

  private static ErrorHandler errorHandler;

  public static ErrorHandler getErrorHandler() {
    return errorHandler != null ? errorHandler : DEFAULT_HANDLER;
  }

  public static void setErrorHandler(ErrorHandler errorHandler) {
    ErrorHandlerFactory.errorHandler = errorHandler;
  }
}
