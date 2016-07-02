var search = function(body, request, url, callback) {
  request.get(url + '/hyperty/user/' + encodeURIComponent(body.value.user), function(err, response, statusCode) {

    var body = {
      'code': statusCode,
      'value': JSON.parse(response)
    };

    callback(body);
  });
};

var advancedSearch = function(body, request, url, callback) {
  var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/hyperty';

  var resources = body.value.resources;
  var dataschemes = body.value.dataSchemes;

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

    var body = {
      'code': statusCode,
      'value': JSON.parse(response)
    };

    callback(body);
  });

};

var hyperty = {
  read: function(body, request, url, callback) {
    if(body.criteria != 'undefined') {
      advancedSearch(body, request, url, callback);
    }else {
      search(body, request, url, callback);
    }
  },

  create: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(userid) + '/' + encodeURIComponent(body.value.hypertyURL);

    var data = {
      'descriptor': body.value.hypertyDescriptor,
      'expires': body.value.expires,
      'resources': body.value.resources,
      'dataSchemes': body.value.dataSchemes
    };

    request.put(url + endpoint, JSON.stringify(data), function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });

  },

  update: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(userid) + '/' + encodeURIComponent(body.value.hypertyURL);

    var data = {
      'descriptor': body.value.hypertyDescriptor,
      'expires': body.value.expires,
      'resources': body.value.resources,
      'dataSchemes': body.value.dataSchemes
    };

    request.put(url + endpoint, JSON.stringify(data), function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });

  },

  del: function(body, request, url, callback) {
    var endpoint = '/hyperty/user/' + encodeURIComponent(body.value.user) + '/' + encodeURIComponent(body.value.hypertyURL);

    request.del(url + endpoint, function(err, response, statusCode) {

      var body = {
        'code': statusCode
      };

      callback(body);
    });

  },
};

module.exports = hyperty;
