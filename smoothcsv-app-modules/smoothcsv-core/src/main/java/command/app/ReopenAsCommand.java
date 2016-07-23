package command.app;

import com.smoothcsv.core.command.CsvSheetCommandBase;
import com.smoothcsv.core.csv.CsvMeta;
import com.smoothcsv.core.csvsheet.CsvSheetView;
import com.smoothcsv.framework.SCApplication;

/**
 * @author kohei
 */
public class ReopenAsCommand extends CsvSheetCommandBase {

  @Override
  public void run(CsvSheetView view) {
    int pos = SCApplication.components().getTabbedPane().indexOfComponent(view);
    CloseCommand.beforeClose(view, true);
    CsvMeta properties = OpenFileCommand.chooseProperties();

    CloseCommand.close(view, false);
    OpenFileCommand.run(view.getViewInfo().getFile(), properties, view.getViewInfo().getOptions(), pos);
  }
}
