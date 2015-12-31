/*
 * Copyright 2015 kohii
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
package com.smoothcsv.core.command;

import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.BaseTabView;

public abstract class CsvSheetCommandBase extends Command {

  public CsvSheetCommandBase() {}

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.framework.commands.Command#run()
   */
  @Override
  public final void run() {
    BaseTabView<?> view = SCApplication.components().getTabbedPane().getSelectedView();
    run((CsvSheetView) view);
  }

  public abstract void run(CsvSheetView view);
}
