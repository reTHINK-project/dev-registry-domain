var RegistryConnector = function(registryURL) {

  if(engine.factory.engineName.contains("Nashorn")) {
    var RequestWrapper = require(__dirname + '/src/java-request');
  }else {
    var RequestWrapper = require(__dirname + '/src/js-request');
  }

  this._request = new RequestWrapper();
  this._registryURL = registryURL;
};

RegistryConnector.prototype.processMessage = function(msg, callback) {
  switch(msg.type) {
      case "READ":
      print("[Registry-Connector] Get user with " + msg.body.resource);
      this.getUser(msg.body.resource, callback);
      break;

      case "CREATE":
      print("[Registry-Connector] Add Hyperty with " + msg.body.value.hypertyURL);
      this.addHyperty(msg.body.value.user, msg.body.value.hypertyURL, msg.body.value.hypertyDescriptorURL, callback);
      break;

      case "DELETE":
      print("[Registry-Connector] Delete Hyperty with " + msg.body.value.hypertyURL);
      this.deleteHyperty(msg.body.value.user, msg.body.value.hypertyURL, callback);
      break;
  }
};

RegistryConnector.prototype.getUser = function(userid, callback) {
  this._request.get(this._registryURL + '/hyperty/user/' + encodeURIComponent(userid), function(err, response, statusCode) {
    print("Get user: " + response);

    var body = {
      'code': statusCode,
      'value': JSON.parse(response)
    };

    callback(body);
  });
};

RegistryConnector.prototype.addHyperty = function(userid, hypertyid, hypertyDescriptor, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(userid) + '/' + encodeURIComponent(hypertyid);
  var data = { 'descriptor': hypertyDescriptor };

  this._request.put(this._registryURL + endpoint, JSON.stringify(data), function(err, response, statusCode) {
    print("Add hyperty: " + response);

    var body = {
      'code': statusCode
    };

    callback(body);
  });
};

RegistryConnector.prototype.deleteHyperty = function(userid, hypertyid, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(userid) + '/' + encodeURIComponent(hypertyid);

  this._request.del(this._registryURL + endpoint, function(err, response, statusCode) {
    print("Delete hyperty: " + response);

    var body = {
      'code': statusCode
    };

    callback(body);
  });
};

module.exports = RegistryConnector;