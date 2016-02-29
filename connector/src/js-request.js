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

var JSRequest = function() {
  this._client = require('request');
};

JSRequest.prototype.get = function(url, callback) {
  this._client
      .get(url)
      .on('response', function(response) {
        callback(null, response);
      });
};

JSRequest.prototype.put = function(url, message, callback) {
  this._client
      .post({
        headers: {'content-type', 'application/json'},
        url: url,
        body: message
      }, function(error, response, body) {
        if(err) {
          callback(err, null, null);
        }

        callback(null, body, response.statusCode);
      });
};

JSRequest.prototype.del = function(url, callback) {
  this._client
      .del(url)
      .on('response', function(response) {
        callback(null, response.statusCode);
      });
};

module.exports = JSRequest;
