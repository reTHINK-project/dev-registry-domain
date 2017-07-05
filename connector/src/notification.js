var notification = {

  checkNotification: function(statusCode, response, notificationsEnabled) {
    return (
      statusCode === 200 && notification.isStatusUpdate(response) && notificationsEnabled
    );
  },

  isStatusUpdate: function(response) {
    return response.statusChanged === "true";
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
