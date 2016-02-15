"use strict";
var bridge = com.smoothcsv.core.macro.bridge.ConsoleBridge;
var Console = function() {
};
Console.prototype.log = function log() {
  var params = Array.prototype.slice.call(arguments)
  return bridge.log(params);
};
Console.prototype.toString = function toString() {
  return '[object Console]';
};
module.exports = new Console();
