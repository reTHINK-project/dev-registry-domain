var notification = {

  checkNotification: function(statusCode, data, notificationsEnabled) {
    return (
      statusCode === 200 && notification.isStatusUpdate(data) && notificationsEnabled
    );
  },

  isStatusUpdate: function(data) {
    return 'status' in data;
  }
};

module.exports = notification;
