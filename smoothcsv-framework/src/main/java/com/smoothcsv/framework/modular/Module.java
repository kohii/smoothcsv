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
package com.smoothcsv.framework.modular;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.commons.exception.IORuntimeException;
import com.smoothcsv.framework.exception.SystemException;
import com.smoothcsv.framework.util.MessageBundles;

/**
 * @author kohii
 *
 */
public class Module {

  private static final Logger LOG = LoggerFactory.getLogger(Module.class);

  public static final int NOT_YET = 1;
  public static final int LOADING = 2;
  public static final int LOADED = 3;

  @Getter
  private final ModuleManifest manifest;

  private ModuleEntryPoint entryPoint;

  @Getter
  private URI classpathRoot;

  @Getter
  private int status = NOT_YET;

  /**
   * @param manifest
   * @param classpathRoot2
   */
  public Module(ModuleManifest manifest, URI classpathRoot) {
    this.manifest = manifest;
    this.classpathRoot = classpathRoot;
  }

  /**
   * @param status the status to set
   */
  void setStatus(int status) {
    this.status = status;
  }

  public ModuleEntryPoint getEntryPoint() {
    if (entryPoint == null) {
      try {
        entryPoint = (ModuleEntryPoint) Class.forName(manifest.getEntryPoint()).newInstance();
      } catch (ClassNotFoundException ex) {
        LOG.warn("Module class not found. {}", manifest.getEntryPoint(), ex);
        throw new SystemException(MessageBundles.getString("WSCC0006", manifest.getEntryPoint()),
            ex);
      } catch (InstantiationException | IllegalAccessException ex) {
        LOG.error("Cannot instantate module. " + manifest.getEntryPoint(), ex);
        throw new SystemException(MessageBundles.getString("WSCC0006", manifest.getEntryPoint()),
            ex);
      }
    }
    return entryPoint;
  }

  public URL getResource(String name) {
    String path = name.startsWith("/") ? name.substring(1) : name;
    URI uri = classpathRoot.resolve(path);
    try {
      return uri.toURL();
    } catch (IOException e) {
      throw new IORuntimeException(e);
    }
  }
}
