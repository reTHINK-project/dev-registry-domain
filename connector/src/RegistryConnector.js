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
var DataObject = require('./dataObject');
var Hyperty = require('./hyperty');
var Request = require('./request');

var RegistryConnector = function(config) {

  this._request = new Request(config.ssl, config.retries);
  this._registryURL = config.url;

  this.hyperty = new Hyperty(this._request, this._registryURL);
  this.dataObject = new DataObject(this._request, this._registryURL);
};

RegistryConnector.prototype.processMessage = function(msg, callback) {
  switch(msg.type.toLowerCase()) {
    case "read":
      this.readOperation(msg, callback);
    break;

    case "create":
      this.createOperation(msg, callback);
    break;

    case "update":
      this.updateOperation(msg, callback);
    break;

    case "delete":
      this.deleteOperation(msg, callback);
    break;
  }
};

RegistryConnector.prototype.checkResourceType = function(url) {

  var prefix;

  if(url.startsWith('/')) {
    prefix = url.split('/')[1];
  } else {
    prefix = url.split('://')[0];
  }

  if(prefix === 'user' || prefix === 'hyperty' || prefix === 'hyperty-runtime' || prefix === 'user-guid'){
    return 'hyperty';
  }else {
    return 'dataObject';
  }
};

RegistryConnector.prototype.readOperation = function(msg, callback) {
  if('criteria' in msg.body && Object.keys(msg.body.criteria).length !== 0) {
    if(this.checkResourceType(msg.body.resource) === 'hyperty') {
      this.hyperty.read(msg.body, true, callback);
    }else {
      this.dataObject.read(msg.body, true, callback);
    }
  }else {
    if(this.checkResourceType(msg.body.resource) === 'hyperty') {
      this.hyperty.read(msg.body, false, callback);
    }else {
      this.dataObject.read(msg.body, false, callback);
    }
  }
};

RegistryConnector.prototype.createOperation = function(msg, callback) {
  if(this.checkResourceType(msg.body.value.url) === 'hyperty') {
    this.hyperty.create(msg.body, callback);
  }else {
    this.dataObject.create(msg.body, callback);
  }
};

RegistryConnector.prototype.updateOperation = function(msg, callback) {
  if(this.checkResourceType(msg.body.resource) === 'hyperty') {
    this.hyperty.update(msg.body, callback);
  }else {
    this.dataObject.update(msg.body, callback);
  }
};

RegistryConnector.prototype.deleteOperation = function(msg, callback) {
  if(this.checkResourceType(msg.body.value.url) === 'hyperty') {
    this.hyperty.del(msg.body, callback);
  }else {
    this.dataObject.del(msg.body, callback);
  }
};

module.exports = RegistryConnector;
