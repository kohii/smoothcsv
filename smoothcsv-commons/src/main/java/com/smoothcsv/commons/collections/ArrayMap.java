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
package com.smoothcsv.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.smoothcsv.commons.data.KeyValue;

/**
 * @param <K>
 * @param <V>
 * @author kohii
 */
public class ArrayMap<K, V> implements Map<K, V> {

  private static final long serialVersionUID = 4403572609048585134L;

  private final List<KeyValue<K, V>> data = new ArrayList<>();

  public ArrayMap() {}

  public V getAndRemove(K key) {
    for (int i = 0; i < this.size(); i++) {
      KeyValue<K, V> e = data.get(i);
      if (e.getKey().equals(key)) {
        data.remove(i);
        return e.getValue();
      }
    }
    return null;
  }

  @Override
  public boolean containsKey(Object key) {
    for (int i = 0; i < size(); i++) {
      KeyValue<K, V> e = data.get(i);
      if (e.getKey().equals(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean containsValue(Object value) {
    for (int i = 0; i < size(); i++) {
      KeyValue<K, V> e = data.get(i);
      if (e.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public V get(Object key) {
    for (int i = 0; i < size(); i++) {
      KeyValue<K, V> e = data.get(i);
      if (e.getKey().equals(key)) {
        return e.getValue();
      }
    }
    return null;
  }

  public KeyValue<K, V> getEntry(Object key) {
    for (int i = 0; i < size(); i++) {
      KeyValue<K, V> e = data.get(i);
      if (e.getKey().equals(key)) {
        return e;
      }
    }
    return null;
  }

  public KeyValue<K, V> get(int i) {
    return data.get(i);
  }

  @Override
  public V put(K key, V value) {
    remove(key);
    data.add(new KeyValue<>(key, value));
    return value;
  }

  public V put(int index, K key, V value) {
    remove(key);
    data.add(index, new KeyValue<>(key, value));
    return value;
  }

  @Override
  public V remove(Object key) {
    for (int i = 0, len = size(); i < len; i++) {
      if (key.equals(get(i))) {
        return data.remove(i).getValue();
      }
    }
    return null;
  }

  public KeyValue<K, V> remove(int i) {
    return data.remove(i);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.entrySet().stream().forEach((en) -> {
      put(en.getKey(), en.getValue());
    });
  }

  @Override
  public Set<K> keySet() {
    return data.stream().map(e -> e.getKey()).collect(Collectors.toSet());
  }

  @Override
  public Collection<V> values() {
    return data.stream().map(e -> e.getValue()).collect(Collectors.toList());
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return data.stream().collect(Collectors.toSet());
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public void clear() {
    data.clear();
  }

  public KeyValue<K, V>[] toArray() {
    return data.toArray(new KeyValue[data.size()]);
  }

  // public void forEach(BiConsumer<K, V> action) {
  // for (int i = 0; i < data.size(); i++) {
  // KeyValue<K, V> entry = data.get(i);
  // action.accept(entry.getKey(), entry.getValue());
  // }
  // }
}
