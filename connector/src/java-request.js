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

var HttpRequest = Java.type('eu.rethink.mn.util.HttpRequest');

var JavaRequest =  function(sslConfig) {
  if(sslConfig.enabled) {
    this._client = new HttpRequest(sslConfig.trustStore, sslConfig.trustStorePass,
          sslConfig.keyStore, sslConfig.keyStorePass, sslConfig.keyPassphrase);
  } else {
    this._client = new HttpRequest();
  }
};

JavaRequest.prototype.get = function(url, callback) {
  try {
    var response = this._client.get(url);
    var parsedResponse = JSON.parse(response);

    callback(null, JSON.parse(parsedResponse.data), parsedResponse.code);
  } catch(e) {
    e.printStackTrace();
  }
};

JavaRequest.prototype.put = function(url, data, callback) {
  try {
    var finalData = JSON.stringify(data);
    var response = this._client.put(url, finalData);
    var parsedResponse = JSON.parse(response);

    callback(null, JSON.parse(parsedResponse.data), parsedResponse.code);
  } catch(e) {
    e.printStackTrace();
  }
};

JavaRequest.prototype.del = function(url, callback) {
  try {
    var response = this._client.del(url);
    var parsedResponse = JSON.parse(response);

    callback(null, JSON.parse(parsedResponse.data), parsedResponse.code);
  } catch(e) {
    e.printStackTrace();
  }
};

module.exports = JavaRequest;
