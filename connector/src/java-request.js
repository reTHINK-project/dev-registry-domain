var JavaRequest = function() {
  this._client = vertx.createHttpClient({});
};

JavaRequest.prototype.get = function(url, callback) {
  this._client.getAbs(url, function (response) {
    response.bodyHandler(function(totalBuffer) {
      var body = totalBuffer.toString("UTF-8");
      callback(null, body, response.statusCode());
    });
  }).end();
};

JavaRequest.prototype.put = function(url, data, callback) {
  this._client.putAbs(url)
  .putHeader("content-type", "application/json")
  .putHeader("content-length", "" + data.length())
  .handler(function(response) {
    response.bodyHandler(function(totalBuffer) {
      var body = totalBuffer.toString("UTF-8");
      callback(null, body, response.statusCode());
    });
  })
  .write(data)
  .end();
};

JavaRequest.prototype.del = function(url, callback) {
  this._client.deleteAbs(url, function(response) {
    response.bodyHandler(function(totalBuffer) {
      var body = totalBuffer.toString("UTF-8");
      callback(null, body, response.statusCode());
    });
  }).end();
};

module.exports = JavaRequest;
