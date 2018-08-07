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
package com.smoothcsv.swing.table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.smoothcsv.commons.utils.StringUtils;

/**
 * @author kohii
 */
public class DefaultTableCellValueExtracter implements ExTableCellValueExtracter<Object> {

  private boolean getterExtracted = false;
  private boolean setterExtracted = false;

  private Method getter;
  private Method setter;

  private final String fieldName;

  public DefaultTableCellValueExtracter(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public Object getValue(Object rowData, ExTableColumn column, int rowIndex, int columnIndex) {
    if (rowData == null) {
      return null;
    }
    try {
      Method getter = getGetter(rowData);
      return getter.invoke(rowData);
    } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
      // fallback
      try {
        Field field = rowData.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(rowData);
      } catch (NoSuchFieldException | SecurityException | IllegalAccessException e1) {
        throw new RuntimeException(e1);
      }
    }
  }

  @Override
  public void setValue(Object value, Object rowData, ExTableColumn column, int rowIndex,
                       int columnIndex) {
    if (rowData == null) {
      return;
    }
    try {
      Class<?> type = getType(rowData);
      value = convertValueToSet(value, type, rowData);
      Method setter = getSetter(rowData);
      setter.invoke(rowData, value);
    } catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
      // fallback
      try {
        Field field = rowData.getClass().getField(fieldName);
        field.set(rowData, value);
      } catch (NoSuchFieldException | SecurityException | IllegalAccessException e1) {
        throw new RuntimeException(e1);
      }
    }
  }

  private Method getGetter(Object rowData) {
    if (getterExtracted) {
      return getter;
    }
    try {
      getterExtracted = true;
      String gettterName = "get" + StringUtils.capitalize(fieldName);
      getter = rowData.getClass().getMethod(gettterName);
      return getter;
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }
  }

  private Class<?> getType(Object rowData) {
    return getGetter(rowData).getReturnType();
  }

  private Method getSetter(Object rowData) {
    if (setterExtracted) {
      return setter;
    }
    try {
      setterExtracted = true;
      String gettterName = "set" + StringUtils.capitalize(fieldName);
      setter = rowData.getClass().getMethod(gettterName, getType(rowData));
      return setter;
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }
  }

  protected Object convertValueToSet(Object value, Class<?> type, Object rowData) {
    if (type == String.class) {
      return value == null ? null : value.toString();
    } else if (type == Integer.class || type == int.class) {
      return value == null || "".equals(value) ? 0 : Integer.valueOf(value.toString());
    }
    return value;
  }
}
