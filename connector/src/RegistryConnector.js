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
var dataObject = require('./src/dataObject');
var hyperty = require('./src/hyperty');

var RegistryConnector = function(registryURL) {

  if( typeof(engine) != 'undefined' &&
     typeof(engine.factory) != 'undefined' &&
     typeof(engine.factory.engineName) != 'undefined' &&
     typeof(engine.factory.engineName.contains) == 'function' &&
           engine.factory.engineName.contains("Nashorn")) {

    var RequestWrapper = require('./src/java-request');
  }else {
    var RequestWrapper = require('./src/js-request');
  }

  this._request = new RequestWrapper();
  this._registryURL = registryURL;
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

  var prefix = url.split('://')[0];

  if(prefix === 'hyperty' || prefix === 'user'){
    return 'hyperty';
  }else {
    return 'dataObject';
  }
};

RegistryConnector.prototype.readOperation = function(msg, callback) {
  if(msg.body.criteria != 'undefined') {
    if(this.checkResourceType(msg.body.resource) === 'hyperty') {
      hyperty.read(msg.body, this_request, this._registryURL, true, callback);
    }else {
      dataObject.read(msg.body, this._request, this._registryURL, false, callback);
    }
  }else {
    if(this.checkResourceType(msg.body.resource) === 'hyperty') {
      hyperty.read(msg.body, this._request, this._registryURL, false, callback);
    }else {
      dataObject.read(msg.body, this._request, this._registryURL, false, callback);
    }
  }
};

RegistryConnector.prototype.createOperation = function(msg, callback) {
  if(this.checkUrlType(msg.body.value.url) === 'hyperty') {
    hyperty.create(msg.body, this._request, this._registryURL, callback);
  }else {
    dataObject.create(msg.body, this._request, this._registryURL, callback);
  }
};

RegistryConnector.prototype.updateOperation = function(msg, callback) {
  if(this.checkUrlType(msg.body.value.url) === 'hyperty') {
    hyperty.update(msg.body, this._request, this._registryURL, callback);
  }else {
    dataObject.update(msg.body, this._request, this._registryURL, callback);
  }
};

RegistryConnector.prototype.deleteOperation = function(msg, callback) {
  if(this.checkUrlType(msg.body.value.url) === 'hyperty') {
    hyperty.del(msg.body, this._request, this._registryURL, callback);
  }else {
    dataObject.del(msg.body, this._request, this._registryURL, callback);
  }
};

module.exports = RegistryConnector;
