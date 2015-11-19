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
package com.smoothcsv.framework.preference;

import org.apache.commons.lang3.StringUtils;

/**
 * @author kohii
 *
 */
public interface PrefTextValidator {

  PrefTextValidator NOT_NULL = new PrefTextValidator() {
    @Override
    public boolean validate(String text) {
      return StringUtils.isNotEmpty(text);
    }
  };

  PrefTextValidator MORE_THAN_ZERO = new PrefTextValidator() {
    @Override
    public boolean validate(String text) {
      return StringUtils.isNotEmpty(text) && StringUtils.isNumeric(text)
          && Integer.parseInt(text) > 0;
    }
  };

  boolean validate(String text);
}
