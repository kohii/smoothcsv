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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author kohii
 */
public class SerializeUtils {

  public static Object deserialize(File file) throws IOException, ClassNotFoundException {
    if (file.exists() && file.isFile() && file.canRead()) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        Object obj = ois.readObject();
        return obj;
      }
    }
    return null;
  }

  public static void serialize(File file, Object obj) throws IOException {
    FileUtils.ensureWritable(file);
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(obj);
    }
  }
}
