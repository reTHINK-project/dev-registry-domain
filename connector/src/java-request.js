/*
* Copyright 2015-2016 INESC-ID
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

var JavaRequest = function() {
  this._client = vertx.createHttpClient({});
};

JavaRequest.prototype.get = function(url, callback) {
  this._client.getAbs(url, function (response) {
    response.bodyHandler(function(totalBuffer) {
      var body = totalBuffer.toString("UTF-8");
      callback(null, body, response.statusCode());
    });
  })
  .exceptionHandler(function(e) {
    callback(e, null, null);
  })
  .end();
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
  .exceptionHandler(function(e) {
    callback(e, null, null);
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
  })
  .exceptionHandler(function(e) {
    callback(e, null, null);
  })
  .end();
};

module.exports = JavaRequest;
