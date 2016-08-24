package com.smoothcsv.core.csvsheet;

import com.smoothcsv.core.util.CoreSettings;
import com.smoothcsv.swing.components.text.ExTextPaneConfig;
import lombok.Getter;

import java.awt.Color;

/**
 * @author kohii
 */
public class CsvSheetTextPaneConfig extends ExTextPaneConfig {

  @Getter
  private static CsvSheetTextPaneConfig instance = new CsvSheetTextPaneConfig(
      Color.LIGHT_GRAY,
      true,
      true,
      true,
      true,
      false
  );

  public CsvSheetTextPaneConfig(Color color,
                                boolean wordWrap,
                                boolean showSpace,
                                boolean showTab,
                                boolean showEOL,
                                boolean showEOF) {
    super(color, wordWrap, showSpace, showTab, showEOL, showEOF);
    loadFromPreferences();
  }

  public void loadFromPreferences() {
    CoreSettings settings = CoreSettings.getInstance();
    setShowEOL(settings.getBoolean("textArea.showEOL"));
    setShowSpace(settings.getBoolean("textArea.showSpace"));
    setShowTab(settings.getBoolean("textArea.showTab"));
    setWordWrap(settings.getBoolean("textArea.wrap"));
  }
}
