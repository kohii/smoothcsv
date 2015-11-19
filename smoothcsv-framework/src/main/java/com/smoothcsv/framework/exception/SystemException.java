/*
 * Copyright 2014 kohii.
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
 *
 */
public class SystemException extends RuntimeException {

  private static final long serialVersionUID = 5367509165812511308L;

  /**
   * @param message
   * @param cause
   */
  public SystemException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public SystemException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public SystemException(Throwable cause) {
    super(cause);
  }
}
