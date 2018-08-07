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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author kohii
 */
public class JsonUtils {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
    // OBJECT_MAPPER.setSerializationInclusion(Inclusion.NON_DEFAULT);
  }

  public static void write(Object object, File file) throws IOException {
    OBJECT_MAPPER.writeValue(file, object);
  }

  public static <T> T read(File file, Class<T> clazz) throws IOException {

    return OBJECT_MAPPER.readValue(file, clazz);
  }

  public static <T> T read(InputStream is, Class<T> clazz) throws IOException {
    return OBJECT_MAPPER.readValue(is, clazz);
  }

  public static String stringify(Object object) throws IOException {
    return OBJECT_MAPPER.writeValueAsString(object);
  }

  public static <T> T parse(String content, Class<T> clazz) throws IOException {
    return OBJECT_MAPPER.readValue(content, clazz);
  }

  public static <T> T parse(String content, TypeReference<T> valueTypeRef) throws IOException {
    return OBJECT_MAPPER.readValue(content, valueTypeRef);
  }
}
