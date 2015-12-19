var rootPackage = Packages.com.smoothcsv.core.macro.api.impl;
module.exports = {
  getApiClass : function(name) {
    return rootPackage[name];
  },
  getApiInstance : function(name) {
    return this.getApiClass(name).getInstance();
  }
};
