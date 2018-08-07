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
package com.smoothcsv.commons.exception;

/**
 * @author kohii
 */
public class IORuntimeException extends RuntimeException {

  private static final long serialVersionUID = 7357300549143045437L;

  public IORuntimeException(Exception cause) {
    super(cause);
  }

  public IORuntimeException(String message, Exception cause) {
    super(message, cause);
  }

  public IORuntimeException(String message) {
    super(message);
  }
}
