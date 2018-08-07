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
package com.smoothcsv.swing.model;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * @author kohii
 */
@SuppressWarnings("serial")
public class FilteredListModel<E> extends AbstractListModel<E> {
  public static interface Filter {
    boolean accept(Object element);
  }

  private final ListModel<E> _source;
  private Filter _filter;
  private final ArrayList<Integer> _indices = new ArrayList<Integer>();

  public FilteredListModel(ListModel<E> source) {
    if (source == null)
      throw new IllegalArgumentException("Source is null");
    _source = source;
    _source.addListDataListener(new ListDataListener() {
      public void intervalRemoved(ListDataEvent e) {
        doFilter();
      }

      public void intervalAdded(ListDataEvent e) {
        doFilter();
      }

      public void contentsChanged(ListDataEvent e) {
        doFilter();
      }
    });
  }

  public void setFilter(Filter f) {
    _filter = f;
    doFilter();
  }

  private void doFilter() {
    _indices.clear();

    Filter f = _filter;
    if (f != null) {
      int count = _source.getSize();
      for (int i = 0; i < count; i++) {
        Object element = _source.getElementAt(i);
        if (f.accept(element)) {
          _indices.add(i);
        }
      }
      fireContentsChanged(this, 0, getSize() - 1);
    }
  }

  @Override
  public int getSize() {
    return (_filter != null) ? _indices.size() : _source.getSize();
  }

  @Override
  public E getElementAt(int index) {
    return (_filter != null) ? _source.getElementAt(_indices.get(index)) : _source
        .getElementAt(index);
  }
}
