var retry = require('async/retry');

var Request = function(sslConfig, retries) {

  var RequestWrapper;

  if( typeof(engine) != 'undefined' &&
     typeof(engine.factory) != 'undefined' &&
     typeof(engine.factory.engineName) != 'undefined' &&
     typeof(engine.factory.engineName.contains) == 'function' &&
           engine.factory.engineName.contains("Nashorn")) {

    RequestWrapper = require('./java-request');
  }else {
    RequestWrapper = require('./js-request');
  }

  this._request = new RequestWrapper(sslConfig);

  this._opts = {
    times: retries,
    interval: 1
  };

};

Request.prototype.get = function(url, callback) {
  retry(this._opts, function(cb) {
    this._request.get(url, cb)
  }.bind(this), callback);
};

Request.prototype.put = function(url, message, callback) {
  retry(this._opts, function(cb) {
    this._request.put(url, message, cb)
  }.bind(this), callback);
};

Request.prototype.del = function(url, callback) {
  retry(this._opts, function(cb) {
    this._request.del(url, cb)
  }.bind(this), callback);
};

module.exports = Request;
