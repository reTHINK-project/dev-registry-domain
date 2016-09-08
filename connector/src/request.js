var retry = require('async/retry');

var Request = function() {

  if( typeof(engine) != 'undefined' &&
     typeof(engine.factory) != 'undefined' &&
     typeof(engine.factory.engineName) != 'undefined' &&
     typeof(engine.factory.engineName.contains) == 'function' &&
           engine.factory.engineName.contains("Nashorn")) {

    this._request = require('./java-request');
  }else {
    this._request = require('./js-request');
  }

};

Request.prototype.get = function(url, callback) {

  retry(3, function(cb) {
    this._request.get(url, cb)
  }.bind(this), callback);
};

Request.prototype.put = function(url, message, callback) {
  retry(3, function(cb) {
    this._request.put(url, message, cb)
  }.bind(this), callback);
};

Request.prototype.del = function(url, callback) {
  retry(3, function(cb) {
    this._request.del(url, cb)
  }.bind(this), callback);
};

module.exports = Request;

