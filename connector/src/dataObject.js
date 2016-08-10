var search = function(body, request, url, callback) {

  var resourceType;

  var type = body.resource.split('://')[0];

  if(type === 'comm') {
    resourceType = 'url/';
  } else if(type === 'hyperty') {
    resourceType = 'reporter/';
  } else {
    resourceType = 'name/';
  }

  request.get(url + '/hyperty/dataobject/' + resourceType + encodeURIComponent(body.resource), function(err, response, statusCode) {

    if(statusCode == 200) {
      var body = {
        'code': statusCode,
        'value': JSON.parse(response)
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
  var endpoint = '/hyperty/dataobject/name/' + encodeURIComponent(body.resource) + '/do';

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

    if(statusCode == 200) {
      var body = {
        'code': statusCode,
        'value': JSON.parse(response)
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

var dataObject = {
  read: function(body, request, url, isAdvanced, callback) {
    if(isAdvanced) {
      advancedSearch(body, request, url, callback);
    }else {
      search(body, request, url, callback);
    }
  },

  create: function(body, request, url, callback) {
    var endpoint = '/hyperty/dataobject/' + encodeURIComponent(body.value.url);

    var data = {
      'name': body.value.name,
      'schema': body.value.schema,
      'url': body.value.url,
      'reporter': body.value.reporter,
      'expires': body.value.expires,
      'dataSchemes': body.value.dataSchemes,
      'resources': body.value.resources
    };

    request.put(url + endpoint, JSON.stringify(data), function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });
  },

  update: function(body, request, url, callback) {
    var endpoint = '/hyperty/dataobject/' + encodeURIComponent(body.value.url);

    var data = {
      'name': body.value.name,
      'schema': body.value.schema,
      'url': body.value.url,
      'reporter': body.value.reporter,
      'expires': body.value.expires,
      'dataSchemes': body.value.dataSchemes,
      'resources': body.value.resources
    };

    request.put(url + endpoint, JSON.stringify(data), function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });
  },

  del: function(body, request, url, callback) {
    var endpoint = '/hyperty/dataobject/url/' + encodeURIComponent(body.value.name);

    request.del(url + endpoint, function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });
  },
};

module.exports = dataObject;
