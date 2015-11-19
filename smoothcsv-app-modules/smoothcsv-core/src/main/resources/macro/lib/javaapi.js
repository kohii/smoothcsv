var rootPackage = Packages.com.smoothcsv.core.macro.api.impl;
module.exports = function(name) {
  return rootPackage[name].getInstance();
}
