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
import java.io.Reader;
import java.util.Properties;

/**
 *
 * @author kohii
 */
public class WebViewTest {

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
  
  private static void run(){
    long l0 = System.nanoTime();
    Properties data = new Properties();
    try (Reader reader =
        new InputStreamReader(WebViewTest.class.getResourceAsStream("file.prefs"), "UTF-8")) {
      data.load(reader);
    } catch (IOException | RuntimeException ex) {
    }
    long l1 = System.nanoTime();
    System.out.println((l1 - l0) / 1000);
  }
}
