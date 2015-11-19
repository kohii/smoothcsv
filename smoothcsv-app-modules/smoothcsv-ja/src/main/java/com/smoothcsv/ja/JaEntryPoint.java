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
package com.smoothcsv.ja;

import com.smoothcsv.core.condition.AppConditions;
import com.smoothcsv.framework.command.CommandRepository;
import com.smoothcsv.framework.modular.AbstractModuleEntryPoint;

/**
 * @author kohii
 *
 */
public class JaEntryPoint extends AbstractModuleEntryPoint {
  @Override
  protected void loadCommands(CommandRepository repository) {
    repository.register("convert:fullWidthToHalfWidth", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    repository.register("convert:halfWidthToFullWidth", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    repository.register("convert:hiraganaToKatakana", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    repository.register("convert:katakanaToHiragana", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    repository.register("convert:kanaToLatin", AppConditions.WHEN_GRID_IS_NOT_EDITING);
    repository.register("convert:latinToKatakana", AppConditions.WHEN_GRID_IS_NOT_EDITING);
  }
}
