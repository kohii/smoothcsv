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
package com.smoothcsv.commons.utils;

/**
 * @author kohii
 */
public class ObjectUtils {

  public static final boolean isEmpty(Object o) {
    return o == null || (o instanceof CharSequence && ((CharSequence) o).length() == 0);
  }

  public static String toString(Object object) {
    return object == null ? null : object.toString();
  }
}
