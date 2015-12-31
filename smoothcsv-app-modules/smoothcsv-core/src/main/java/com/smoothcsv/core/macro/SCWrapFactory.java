/*
 * Copyright 2015 kohii
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
package com.smoothcsv.core.macro;

import java.lang.reflect.Array;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;

/**
 * @author kohii
 *
 */
public class SCWrapFactory extends WrapFactory {

  @Override
  public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
    if (obj == null || obj == Undefined.instance || obj instanceof Scriptable) {
      return obj;
    }
    if (staticType != null) {
      if (obj instanceof String || obj instanceof Number || obj instanceof Boolean
          || obj instanceof Character) {
        return Context.javaToJS(obj, scope);
      }
    }
    if (obj.getClass().isArray()) {
      if (obj.getClass().getComponentType() != Object.class) {
        Object[] array = new Object[Array.getLength(obj)];
        for (int i = 0; i < array.length; i++) {
          array[i] = Context.javaToJS(Array.get(obj, i), scope);
        }
        return cx.newArray(scope, array);
      }
    }
    return super.wrap(cx, scope, obj, staticType);
  }
}
