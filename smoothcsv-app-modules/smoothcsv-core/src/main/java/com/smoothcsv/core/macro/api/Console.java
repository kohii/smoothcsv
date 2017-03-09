package com.smoothcsv.core.macro.api;

import com.smoothcsv.core.macro.apiimpl.APIBase;
import com.smoothcsv.core.macro.bridge.ConsoleBridge;

/**
 * The Console object provides access to the SmoothCSV's debugging console.
 *
 * @author kohii
 */
public class Console extends APIBase {

  /**
   * Outputs a message to the console.
   *
   * @param objs A list of objects to output.
   *             The string representations of each of these objects are appended together in the order listed and output.
   */
  public void log(Object... objs) {
    ConsoleBridge.log(objs);
  }
}
