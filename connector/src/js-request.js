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
var http = require('http');
var querystring = require('querystring');
var url = require('url');
var requestify = require('requestify');

var JSRequest = {

  get: function(url, callback) {
    requestify.get(url)
    .then(function(response) {
      callback(null, response.getBody(), response.getCode());
    })
    .catch(function(error) {
      callback(error);
    });
  },

  put: function(url, message, callback) {
    requestify.request(url, {
      method: 'PUT',
      body: message,
      headers: {'content-type': 'application/json'},
      dataType: 'json'
    })
    .then(function(response) {
      callback(null, response.getBody(), response.getCode());
    })
    .catch(function(error) {
      callback(error);
    });
  },

  del: function(url, callback) {
    requestify.request(url, {
      method: 'DELETE'
    })
    .then(function(response) {
      callback(null, response.getBody(), response.getCode());
    })
    .catch(function(e) {
      callback(error);
    });
  }

};

module.exports = JSRequest;
