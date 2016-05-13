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
package command.app;

import com.smoothcsv.framework.SCApplication;
import com.smoothcsv.framework.command.Command;
import com.smoothcsv.framework.component.BaseTabView;
import com.smoothcsv.framework.component.SCTabbedPane;

/**
 * @author kohii
 */
public class CloseOthersCommand extends Command {

  /*
   * (non-Javadoc)
   * 
   * @see com.smoothcsv.framework.commands.Command#run()
   */
  @Override
  public void run() {
    SCTabbedPane tabbedPane = SCApplication.components().getTabbedPane();
    BaseTabView<?> selectedView = tabbedPane.getSelectedView();
    for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
      BaseTabView<?> view = (BaseTabView<?>) tabbedPane.getComponentAt(i);
      if (view != selectedView) {
        CloseCommand.close(view);
      }
    }
  }
}
