var retry = require('async/retry');

var Request = function() {

  if( typeof(engine) != 'undefined' &&
     typeof(engine.factory) != 'undefined' &&
     typeof(engine.factory.engineName) != 'undefined' &&
     typeof(engine.factory.engineName.contains) == 'function' &&
           engine.factory.engineName.contains("Nashorn")) {

    var RequestWrapper = require('./java-request');
  }else {
    var RequestWrapper = require('./js-request');
  }

  this._request = new RequestWrapper();
  this._registryURL = url;
};

Request.prototype.get = function(url, callback) {
  retry(3, this._request.get, callback);
};

Request.prototype.put = function(url, message, callback) {
  retry(3, this._request.put, callback);
};

Request.prototype.del = function(url, callback) {
  retry(3, this._request.del, callback);
};

module.exports = Request;

