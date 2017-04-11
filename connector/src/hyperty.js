var Hyperty = function(request, url, notificationCallback) {
  this._request = request;
  this._url = url
};

Hyperty.prototype.search = function(body, callback) {

  var endpoint;
  var prefix;
  var id;

  if(body.resource.startsWith('/')) {
    var urlComponents = body.resource.split('/');
    prefix = urlComponents[2];
    id = urlComponents[3];
  } else {
    prefix = body.resource.split('://')[0];
  }


  if(prefix === 'user') {
    endpoint = '/hyperty/user/' + encodeURIComponent(body.resource);
  } else if(prefix === 'user-guid') {
    endpoint = '/hyperty/guid/' + encodeURIComponent(body.resource);
  } else if(prefix === 'idp-identifier') {
    endpoint = '/hyperty/email/' + encodeURIComponent(id);
  } else {
    endpoint = '/hyperty/url/' + encodeURIComponent(body.resource);
  }

  this._request.get(this._url + endpoint, function(err, response, statusCode) {

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
    }else if(statusCode == 408) {
      var body = {
        'code': statusCode,
        'description': 'Temporarily Unavailable',
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

Hyperty.prototype.advancedSearch = function(body, callback) {

  if(body.resource.startsWith('/')) {
    endpoint = '/hyperty/email/' + encodeURIComponent(body.resource.split('/')[3]);
  } else {
    endpoint = '/hyperty/user/' + encodeURIComponent(body.resource) + '/hyperty';
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
    }else if(statusCode == 408) {
      var body = {
        'code': statusCode,
        'description': 'Temporarily Unavailable',
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

Hyperty.prototype.read = function(body, isAdvanced, callback) {
  if(isAdvanced) {
    this.advancedSearch(body, callback);
  }else {
    this.search(body, callback);
  }
};

Hyperty.prototype.create = function(body, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.url);

  var data = {
    'descriptor': body.value.descriptor,
    'expires': body.value.expires,
    'resources': body.value.resources,
    'dataSchemes': body.value.dataSchemes,
    'status': body.value.status,
    'runtime': body.value.runtime,
    'p2pRequester': body.value.p2pRequester,
    'p2pHandler': body.value.p2pHandler,
    'guid': body.value.guid
  };

  this._request.put(this._url + endpoint, data, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    }else {
      var body = {
        'code': statusCode
      };
    }

    callback(body);
  });
};

Hyperty.prototype.update = function(body, callback) {

  var endpoint = '/hyperty/url/' + encodeURIComponent(body.resource);
  var data;

  if(typeof body.value != "undefined" && body.value != null) {
    data = {
      'descriptor': body.value.descriptor,
      'expires': body.value.expires,
      'resources': body.value.resources,
      'dataSchemes': body.value.dataSchemes,
      'status': body.value.status,
      'runtime': body.value.runtime,
      'p2pRequester': body.value.p2pRequester,
      'p2pHandler': body.value.p2pHandler
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
    }else {
      var body = {
        'code': statusCode
      };
    }

    callback(body);
  });
};

Hyperty.prototype.del = function(body, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.url);

  this._request.del(this._url + endpoint, function(err, response, statusCode) {

    if(err) {
      var body = {
        'code': 504,
        'description': 'Error contacting the domain registry.'
      };
    }else {
      var body = {
        'code': statusCode
      };
    }

    callback(body);
  });

};

module.exports = Hyperty;
