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
package com.smoothcsv.framework.preference;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.commons.utils.BeanUtils;
import com.smoothcsv.framework.setting.Settings;

/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class PrefSelectBox<E> extends JComboBox<E> {

  private String prefKey;
  private String valueFieldName;
  private String displayFieldName;

  private Method valueFieldGetter;

  @SuppressWarnings("unchecked")
  public PrefSelectBox(Settings settings, String prefKey, Collection<E> items,
                       String valueFieldName, String displayFieldName) {
    super((E[]) items.stream().toArray());
    this.prefKey = prefKey;
    this.valueFieldName = valueFieldName;
    this.displayFieldName = displayFieldName;
    setEditable(false);
    setRenderer(new Renderer());

    addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          Object item = e.getItem();
          try {
            Object val = getValueFieldGetter(item.getClass()).invoke(item);
            settings.save(prefKey, val);
          } catch (IllegalAccessException | IllegalArgumentException
              | InvocationTargetException e1) {
            throw new UnexpectedException(e1);
          }
        }
      }
    });

    // Load value
    String value = settings.get(prefKey);
    ComboBoxModel<E> model = getModel();
    try {
      for (int i = 0; i < model.getSize(); i++) {
        E item = model.getElementAt(i);
        Object val = getValueFieldGetter(item.getClass()).invoke(item);
        if (Objects.equals(value, val == null ? null : val.toString())) {
          setSelectedIndex(i);
          break;
        }
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new UnexpectedException(e);
    }
  }

  private Method getValueFieldGetter(Class<?> clazz) {
    if (valueFieldGetter == null) {
      valueFieldGetter = BeanUtils.getGetter(clazz, valueFieldName);
    }
    return valueFieldGetter;
  }

  private class Renderer extends DefaultListCellRenderer {

    private Method textFieldGetter;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object item, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      if (item == null) {
        return super.getListCellRendererComponent(list, item, index, isSelected, cellHasFocus);
      }
      if (textFieldGetter == null) {
        textFieldGetter = BeanUtils.getGetter(item.getClass(), displayFieldName);
      }
      try {
        Object val = textFieldGetter.invoke(item);
        return super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        throw new UnexpectedException(e);
      }
    }
  }
}
