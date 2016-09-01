/**
* Copyright 2016 PT Inovação e Sistemas SA
* Copyright 2016 INESC-ID
* Copyright 2016 QUOBIS NETWORKS SL
* Copyright 2016 FRAUNHOFER-GESELLSCHAFT ZUR FOERDERUNG DER ANGEWANDTEN FORSCHUNG E.V
* Copyright 2016 ORANGE SA
* Copyright 2016 Deutsche Telekom AG
* Copyright 2016 Apizee
* Copyright 2016 TECHNISCHE UNIVERSITAT BERLIN
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
**/

var JSRequest = function() {
  this.http = require('http');
  this.querystring = require('querystring');
  this.url = require('url');
  this.requestify = require('requestify');
};

JSRequest.prototype.get = function(url, callback) {
  console.log("url: ", url);
  this.requestify.get(url)
  .then(function(response) {
    callback(null, response.getBody(), response.getCode());
  })
  .catch(function(error) {
    console.error(error);
  });
}

JSRequest.prototype.put = function(url, message, callback) {
  this.requestify.request(url, {
      method: 'PUT',
      body: message,
      headers: {'content-type': 'application/json'},
      dataType: 'json'
  })
  .then(function(response) {
      callback(null, response.getBody(), response.getCode());
  })
  .catch(function(e) {
      console.error(e);
  });
};

JSRequest.prototype.del = function(url, callback) {
  this.requestify.request(url, {
      method: 'DELETE'
  })
  .then(function(response) {
      callback(null, response.getBody(), response.getCode());
  })
  .catch(function(e) {
      console.error(e);
  });
};

module.exports = JSRequest;
