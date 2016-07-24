package command.app;

import com.smoothcsv.core.command.CsvSheetCommandBase;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;

/**
 * @author kohei
 */
public class ReloadCommand extends CsvSheetCommandBase {

  @Override
  public void run(CsvSheetView view) {
    int pos = SCApplication.components().getTabbedPane().indexOfComponent(view);
    CloseCommand.close(view);
    OpenFileCommand.run(view.getViewInfo().getFile(),
        view.getViewInfo().getCsvMeta(),
        view.getViewInfo().getOptions(),
        pos);
  }
}
