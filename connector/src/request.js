var retry = require('async/retry');

var Request = function(retries) {

  if( typeof(engine) != 'undefined' &&
     typeof(engine.factory) != 'undefined' &&
     typeof(engine.factory.engineName) != 'undefined' &&
     typeof(engine.factory.engineName.contains) == 'function' &&
           engine.factory.engineName.contains("Nashorn")) {

    this._request = require('./java-request');
  }else {
    this._request = require('./js-request');
  }

  this._retries = retries;

};

Request.prototype.get = function(url, callback) {

  retry(this._retries, function(cb) {
    this._request.get(url, cb)
  }.bind(this), callback);
};

Request.prototype.put = function(url, message, callback) {
  retry(this._retries, function(cb) {
    this._request.put(url, message, cb)
  }.bind(this), callback);
};

Request.prototype.del = function(url, callback) {
  retry(this._retries, function(cb) {
    this._request.del(url, cb)
  }.bind(this), callback);
};

module.exports = Request;

