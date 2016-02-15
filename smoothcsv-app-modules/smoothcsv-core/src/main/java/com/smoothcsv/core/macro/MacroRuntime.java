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
package com.smoothcsv.core.macro;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smoothcsv.commons.exception.UnexpectedException;
import com.smoothcsv.core.macro.apiimpl.AppImpl;
import com.smoothcsv.core.macro.apiimpl.ClipboardImpl;
import com.smoothcsv.core.macro.apiimpl.CommandImpl;

public class MacroRuntime {

  private static final Logger LOG = LoggerFactory.getLogger(MacroRuntime.class);

  private Context context;

  private ScriptableObject globalScope;

  private boolean started = false;

  private boolean macroExecuting = false;

  public MacroRuntime() {
    // this.classShutter = new MacroClassShutter();
  }

  public void start() {
    LOG.debug("Start MacroRuntime {}", this);

    context = Context.enter();
    context.setLanguageVersion(Context.VERSION_1_8);
    context.setWrapFactory(new SCWrapFactory());
    globalScope = new ImporterTopLevel(context);
    // try {
    List<URI> paths;
    try {
      paths = Arrays.asList(this.getClass().getResource("/macro/console.js").toURI());
    } catch (URISyntaxException e) {
      throw new UnexpectedException(e);
    }
    ModuleSourceProvider sourceProvider = new UrlModuleSourceProvider(paths, null);
    ModuleScriptProvider scriptProvider = new SoftCachingModuleScriptProvider(sourceProvider);
    RequireBuilder builder = new RequireBuilder();
    builder.setModuleScriptProvider(scriptProvider);
    builder.setSandboxed(false);
    Require require = builder.createRequire(context, globalScope);

    // Finally, if you want to execute a top-level module, do this:
    // require.requireMain(context, "mainModuleId");

    // Or, if you just want require() in your top level scope, but want to run
    // some script manually, then just do:
    require.install(globalScope);
    globalScope.defineProperty("global", globalScope, ScriptableObject.READONLY);
    loadScriptToGlobalVariable("App", AppImpl.getInstance());
    loadScriptToGlobalVariable("Clipboard", ClipboardImpl.getInstance());
    loadScriptToGlobalVariable("Command", CommandImpl.getInstance());
    loadScriptToGlobalVariable("Macro", com.smoothcsv.core.macro.api.Macro.class);
    loadScriptToGlobalVariable("CsvProperties", com.smoothcsv.core.macro.api.CsvProperties.class);
    loadScriptToGlobalVariable("Window", "window");
    loadScriptToGlobalVariable("console", "console");
    // } catch (URISyntaxException e) {
    // throw new UnexpectedException(e);
    // }
    // initBuiltinVariables();
    started = true;
  }

  private void loadScriptToGlobalVariable(String varName, String moduleName) {
    Object o = context.evaluateString(globalScope, "require('" + moduleName + "')", null, 1, null);
    loadScriptToGlobalVariable(varName, o);
  }

  private void loadScriptToGlobalVariable(String varName, Object o) {
    globalScope.defineProperty(varName, o, ScriptableObject.READONLY);
  }

  // protected void initBuiltinVariables() {
  // try {
  // readJsFromResources(globalScope, "smoothcsv.js");
  // } catch (IOException ex) {
  // throw new UnexpectedException(ex);
  // }
  // }

  public void shutdown() {
    LOG.debug("Shutdown MacroRuntime {}", this);
    Context.exit();
    context = null;
    globalScope = null;
    started = false;
  }

  public void ensureStarted() {
    if (!started) {
      start();
    }
  }

  public synchronized Object execute(Macro macro) {
    try {
      macroExecuting = true;
      Scriptable localScope = context.newObject(globalScope);
      localScope.setPrototype(globalScope);
      localScope.setParentScope(null);
      Script script = macro.getScript(context);
      Object result = script.exec(context, localScope);
      return result;
    } finally {
      macroExecuting = false;
    }
  }

  public boolean isMacroExecuting() {
    return macroExecuting;
  }
}
