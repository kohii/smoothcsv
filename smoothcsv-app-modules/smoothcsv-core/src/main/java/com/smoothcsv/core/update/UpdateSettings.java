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
package com.smoothcsv.core.update;

import com.smoothcsv.framework.setting.Settings;
import lombok.Getter;

/**
 * @author kohii
 */
public class UpdateSettings extends Settings {

  public static final String LAST_CHOICE = "lastChoice";
  public static final String LAST_VERSION = "lastVersion";
  public static final String LAST_CHECKED = "lastChecked";

  @Getter
  private static UpdateSettings instance = new UpdateSettings();

  private UpdateSettings() {
    super("update");
  }
}
