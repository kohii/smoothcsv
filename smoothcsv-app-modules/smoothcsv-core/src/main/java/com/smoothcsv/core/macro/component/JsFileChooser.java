package com.smoothcsv.core.macro.component;

import java.io.File;
import java.util.function.Function;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.constants.AppSettingKeys;
import com.smoothcsv.framework.component.dialog.BasicFileChooser;
import com.smoothcsv.framework.setting.SettingManager;
import com.smoothcsv.framework.util.DirectoryResolver;

public class JsFileChooser extends BasicFileChooser {

  private static final long serialVersionUID = -6977376491214445715L;

  private static JsFileChooser instance;

  private JsFileChooser() {}

  public static JsFileChooser getInstance() {
    if (instance == null) {
      instance = new JsFileChooser();
      FileFilter csvfilter = new FileNameExtensionFilter("JavaScript(*.js)", "js");
      instance.addChoosableFileFilter(csvfilter);
      instance.setFileFilter(csvfilter);

      instance.addOnApproveSelection(new Function<File, Boolean>() {
        @Override
        public Boolean apply(File t) {
          String lastUsedDir = instance.getCurrentDirectory().getAbsolutePath();
          SettingManager.save(AppSettingKeys.Session.LAST_USED_DIRECTORY_JS, lastUsedDir);
          return true;
        }
      });
      String initDir = SettingManager.get(AppSettingKeys.Session.LAST_USED_DIRECTORY_JS);
      File file;
      if (StringUtils.isEmpty(initDir)) {
        file = DirectoryResolver.instance().getMacroFileDirectory();
      } else {
        file = new File(initDir);
      }
      if (file.exists()) {
        instance.setCurrentDirectory(file);
      }
    }
    return instance;
  }
}
