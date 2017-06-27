var notification = {

  checkNotification: function(statusCode, data, notificationsEnabled) {
    return (
      statusCode === 200 && notification.isStatusUpdate(data) && notificationsEnabled
    );
  },

  isStatusUpdate: function(data) {
    return 'status' in data;
  },

  fetchUpdated: function(url, request, notificationCallback) {
    request.get(url + '/registry/updated', function(err, response, statusCode) {
      if(err) {
        return console.error("[Updated hyperties] Error fetching from domain registry");
      }else if(statusCode == 200) {
        var body = {
          'updated': response
        };
      }

      notificationCallback(null, body);
    });
  }
};

module.exports = notification;
