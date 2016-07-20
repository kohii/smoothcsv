package com.smoothcsv.windows;

import com.smoothcsv.framework.modular.ModuleEntryPointBase;
import com.smoothcsv.framework.modular.ModuleManifest;

/**
 * @author kohei
 */
public class WindowsEntryPoint extends ModuleEntryPointBase {

  @Override
  public ModuleManifest getManifest() {
    return ModuleManifest.builder()
        .name("smoothcsv-windows")
        .author("kohii")
        .dependencies(new String[]{"smoothcsv-core"})
        .build();
  }
}
