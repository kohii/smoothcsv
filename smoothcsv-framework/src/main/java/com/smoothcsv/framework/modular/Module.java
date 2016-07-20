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
package com.smoothcsv.framework.modular;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kohii
 */
public class Module {

  private static final Logger LOG = LoggerFactory.getLogger(Module.class);

  public static final int NOT_YET = 1;
  public static final int LOADING = 2;
  public static final int LOADED = 3;

  @Getter
  private final ModuleManifest manifest;

  @Getter
  private ModuleEntryPoint entryPoint;

  @Getter
  private int status = NOT_YET;

  /**
   * @param manifest
   * @param entryPoint
   */
  public Module(ModuleManifest manifest, ModuleEntryPoint entryPoint) {
    this.manifest = manifest;
    this.entryPoint = entryPoint;
  }

  /**
   * @param status the status to set
   */
  void setStatus(int status) {
    this.status = status;
  }
}