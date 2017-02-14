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

var request = require('request');
var fs = require('fs');
var path = require('path');

var certFile = path.resolve(__dirname, 'connector.cert.pem');
var keyFile = path.resolve(__dirname, 'connector.key.pem');
var caFile = path.resolve(__dirname, 'ca-bundle.pem');

var defaulOptions = {
  cert: fs.readFileSync(certFile),
  key: fs.readFileSync(keyFile),
  ca: fs.readFileSync(caFile)
};

var JSRequest = {

  get: function(url, callback) {
    request.get(url, defaultOptions, function(err, response, body) {
    if(!err) {
      return callback(null, body, response.statusCode)
    } else if(err.statusCode !== 404 && err.statusCode !== 408) {
      console.log("[REGISTRY-CONNECTOR] Error: " + err);
      return callback(err);
    } else {
      return callback(null, err.body, err.statusCode);
    }
    });
  },

  put: function(url, message, callback) {

    var putOptions = {
      url: url,
      method: 'PUT',
      json: message
    };

    var options = Object.assign({}, defaultOptions, putOptions);

    request(options, function(err, response, body) {
      if(!err) {
        return callback(null, body, response.statusCode)
      } else if(err.statusCode !== 404 && err.statusCode !== 408) {
        console.log("[REGISTRY-CONNECTOR] Error: " + err);
        return callback(err);
      } else {
        return callback(null, err.body, err.statusCode);
      }
    });
  },

  del: function(url, callback) {

    var deleteOptions = {
      url: url,
      method: 'DELETE'
    };

    var options = Object.assign({}, defaultOptions, deleteOptions);

    request(options, function(err, response, body) {
      if(!err) {
        return callback(null, body, response.statusCode)
      } else if(err.statusCode !== 404 && err.statusCode !== 408) {
        console.log("[REGISTRY-CONNECTOR] Error: " + err);
        return callback(err);
      } else {
        return callback(null, err.body, err.statusCode);
      }
    });
  }

};

module.exports = JSRequest;
