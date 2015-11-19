/*
 * Copyright 2014 kohii.
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

import java.io.IOException;
import java.io.InputStreamReader;

import com.smoothcsv.csv.CsvProperties;
import com.smoothcsv.csv.reader.CsvReaderOptions;
import com.smoothcsv.framework.io.ArrayCsvReader;

/**
 *
 * @author kohii
 */
public class CopyOfWebViewTest {

  public static void main(String[] args) {

    run();
    run();
    run();
    run();
    run();
    run();
    run();
    run();
  }

  private static void run() {
    long l0 = System.nanoTime();
    try (ArrayCsvReader reader =
        new ArrayCsvReader(new InputStreamReader(
            CopyOfWebViewTest.class.getResourceAsStream("file.csv"), "UTF-8"),
            CsvProperties.DEFAULT, CsvReaderOptions.DEFAULT, 2)) {
      reader.readAll();
    } catch (IOException | RuntimeException ex) {
    }
    long l1 = System.nanoTime();
    System.out.println((l1 - l0) / 1000);
  }
}
