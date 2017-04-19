var notification = require('./notification');

var DataObject = function(request, url, notificationCallback) {
  this._request = request;
  this._url = url
  this._notificationsEnabled = typeof notificationCallback !== 'undefined';

  if(this._notificationsEnabled) {
    this._notificationCallback = notificationCallback;
  }
};

DataObject.prototype.search = function(body, callback) {

  var resourceType;

  var type = body.resource.split('://')[0];

  if(type === 'comm') {
    resourceType = 'url/';
  } else {
    resourceType = 'name/';
  }

  this._request.get(this._url + '/hyperty/dataobject/' + resourceType + encodeURIComponent(body.resource), function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    }else if(statusCode == 200) {
      var body = {
        'code': statusCode,
        'value': response
      };
    }else {
      var body = {
        'code': statusCode,
        'description': response.message
      }
    }

    callback(body);
  });
};

DataObject.prototype.advancedSearch = function(body, callback) {

  var endpoint;

  if('reporter' in body.criteria) {
    if('resources' in body.criteria || 'dataSchemes' in body.criteria) {
      endpoint = '/hyperty/dataobject/reporter/' + encodeURIComponent(body.criteria.reporter) + '/do';
    } else {
      endpoint = '/hyperty/dataobject/reporter/' + encodeURIComponent(body.criteria.reporter);
    }
  } else {
    endpoint = '/hyperty/dataobject/name/' + encodeURIComponent(body.resource) + '/do';
  }

  var resources = body.criteria.resources;
  var dataschemes = body.criteria.dataSchemes;

  var qsResources = '';
  var qsDataschemes = '';
  var querystring = '';

  if(typeof resources != "undefined" && resources != null && resources.length > 0) {
    var qsResources = 'resources=' + resources.join(',');
  }

  if(typeof dataschemes != "undefined" && dataschemes != null && dataschemes.length > 0) {
    var qsDataschemes = 'dataSchemes=' + dataschemes.join(',');
  }

  if(qsResources != "" && qsDataschemes != "") {
    var querystring = '?' + qsResources + '&' + qsDataschemes;
  }else if(qsResources != "") {
    var querystring = '?' + qsResources;
  }else if(qsDataschemes != "") {
    var querystring = '?' + qsDataschemes;
  }

  this._request.get(this._url + endpoint + querystring, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    }else if(statusCode == 200) {
      var body = {
        'code': statusCode,
        'value': response
      };
    }else {
      var body = {
        'code': statusCode,
        'description': response.message
      }
    }

    callback(body);
  });
};

DataObject.prototype.read = function(body, isAdvanced, callback) {
    if(isAdvanced) {
      this.advancedSearch(body, callback);
    }else {
      this.search(body, callback);
    }
};

DataObject.prototype.create = function(body, callback) {
  var endpoint = '/hyperty/dataobject/' + encodeURIComponent(body.value.url);

  var data = {
    'name': body.value.name,
    'schema': body.value.schema,
    'url': body.value.url,
    'reporter': body.value.reporter,
    'expires': body.value.expires,
    'dataSchemes': body.value.dataSchemes,
    'resources': body.value.resources,
    'status': body.value.status,
    'runtime': body.value.runtime,
    'p2pRequester': body.value.p2pRequester
  };

  this._request.put(this._url + endpoint, data, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    } else {
      var body = {
        'code': statusCode
      };
    }

    callback(body);
  });
};

DataObject.prototype.update = function(body, callback) {
  var endpoint = '/dataobject/url/' + encodeURIComponent(body.resource);

  if(typeof body.value != "undefined" && body.value != null) {
    data = {
      'name': body.value.name,
      'schema': body.value.schema,
      'url': body.value.url,
      'reporter': body.value.reporter,
      'expires': body.value.expires,
      'dataSchemes': body.value.dataSchemes,
      'resources': body.value.resources,
      'status': body.value.status,
      'runtime': body.value.runtime,
      'p2pRequester': body.value.p2pRequester
    };
  } else {
    data = {};
  }

  this._request.put(this._url + endpoint, data, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    } else {
      var body = {
        'code': statusCode
      };
    }

    //check if notify
    if(notification.checkNotification(statusCode, data, this._notificationsEnabled)) {
      this._notificationCallback(null, {
        'object': body.resource,
        'status': data.status
      });
    }

    callback(body);
  }.bind(this));
};

DataObject.prototype.del = function(body, callback) {
  var endpoint = '/hyperty/dataobject/url/' + encodeURIComponent(body.value.name);

  this._request.del(this._url + endpoint, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    } else {
      var body = {
        'code': statusCode
      };
    }

    callback(body);
  });
};

module.exports = DataObject;
