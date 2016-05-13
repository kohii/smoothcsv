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

import com.smoothcsv.core.component.AboutDialog;
import com.smoothcsv.framework.command.Command;

/**
 * @author kohii
 */
public class AboutCommand extends Command {

  @Override
  public void run() {
    AboutDialog dialog = new AboutDialog();
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
  }
}
