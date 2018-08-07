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
package com.smoothcsv.commons.data;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 * JavaBeans that contains <code>key</code> and <code>value</code>.
 *
 * @param <K> key
 * @param <V> value
 * @author kohii
 */
public class KeyValue<K, V> implements Serializable, Entry<K, V> {

  private static final long serialVersionUID = -3503096327504778726L;

  private K key;
  private V value;

  public KeyValue(K key, V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public V setValue(V value) {
    return (this.value = value);
  }

}
