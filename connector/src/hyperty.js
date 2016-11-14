var search = function(body, request, url, callback) {

  request.get(url + '/hyperty/user/' + encodeURIComponent(body.resource), function(err, response, statusCode) {

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

var advancedSearch = function(body, request, url, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(body.resource) + '/hyperty';

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

  request.get(url + endpoint + querystring, function(err, response, statusCode) {
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

var hyperty = {
  read: function(body, request, url, isAdvanced, callback) {
    if(isAdvanced) {
      advancedSearch(body, request, url, callback);
    }else {
      search(body, request, url, callback);
    }
  },

  create: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.url);

    var data = {
      'descriptor': body.value.descriptor,
      'expires': body.value.expires,
      'resources': body.value.resources,
      'dataSchemes': body.value.dataSchemes
    };

    request.put(url + endpoint, data, function(err, response, statusCode) {

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

  },

  update: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.url);

    var data = {
      'descriptor': body.value.descriptor,
      'expires': body.value.expires,
      'resources': body.value.resources,
      'dataSchemes': body.value.dataSchemes
    };

    request.put(url + endpoint, data, function(err, response, statusCode) {

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

  },

  del: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.url);

    request.del(url + endpoint, function(err, response, statusCode) {

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

  },
};

module.exports = hyperty;
