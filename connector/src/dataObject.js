var search = function(body, request, url, callback) {
  request.get(url + '/hyperty/dataobject/url/' + encodeURIComponent(body.resource), function(err, response, statusCode) {

    var body = {
      'code': statusCode,
      'value': JSON.parse(response)
    };

    callback(body);
  });
};

var advancedSearch = function(body, request, url, callback) {
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
      'expires': body.value.expires
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
      'expires': body.value.expires
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
