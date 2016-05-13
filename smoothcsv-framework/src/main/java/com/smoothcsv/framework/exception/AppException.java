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
package com.smoothcsv.framework.exception;

/**
 * @author kohii
 */
public class AppException extends RuntimeException {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 2477932495946568000L;

  private String messageId;
  private Object[] messageParams;

  /**
   * Constructs {@link AppException}
   *
   * @param msgId
   * @param args
   */
  public AppException(String msgId, Object... args) {
    this.messageId = msgId;
    this.messageParams = args;
  }

  /**
   * Constructs {@link AppException}
   *
   * @param cause
   * @param msgId
   * @param args
   */
  public AppException(Exception cause, String msgId, Object... args) {
    super(cause);
    this.messageId = msgId;
    this.messageParams = args;
  }

  public String getMessageId() {
    return messageId;
  }

  public Object[] getMessageParams() {
    return messageParams;
  }
}
