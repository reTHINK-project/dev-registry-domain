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

var client = vertx.createHttpClient({});

var JavaRequest = {

  get: function(url, callback) {
    client.getAbs(url, function (response) {
      response.bodyHandler(function(totalBuffer) {
        var body = totalBuffer.toString("UTF-8");
        callback(null, JSON.parse(body), response.statusCode());
      });
    })
    .exceptionHandler(function(e) {
      print("[REGISTRY-CONNECTOR] Error: " + e);
      callback(e, null, null);
    })
    .end();
  },

  put: function(url, data, callback) {
    var finalData = JSON.stringify(data);

    client.putAbs(url)
    .putHeader("content-type", "application/json")
    .putHeader("content-length", "" + finalData.length())
    .handler(function(response) {
      response.bodyHandler(function(totalBuffer) {
        var body = totalBuffer.toString("UTF-8");
        callback(null, JSON.parse(body), response.statusCode());
      });
    })
    .exceptionHandler(function(e) {
      print("[REGISTRY-CONNECTOR] Error: " + e);
      callback(e, null, null);
    })
    .write(finalData)
    .end();
  },

  del: function(url, callback) {
    client.deleteAbs(url, function(response) {
      response.bodyHandler(function(totalBuffer) {
        var body = totalBuffer.toString("UTF-8");
        callback(null, JSON.parse(body), response.statusCode());
      });
    })
    .exceptionHandler(function(e) {
      print("[REGISTRY-CONNECTOR] Error: " + e);
      callback(e, null, null);
    })
    .end();
  }

};

module.exports = JavaRequest;
