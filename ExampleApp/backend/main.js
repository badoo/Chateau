// Please note: This code was written by an Android developer pretending to be a JS developer!

var Image = require("parse-image");

Parse.Cloud.beforeSave('Message', function(request, response) {
  if (request.user == null) {
    response.success();
    return;
  }
  request.object.set('from', request.user);
  response.success();
});

Parse.Cloud.afterSave('Message', function(request) {
  if (request.user == null || request.object.existed()) {
    return;
  }
  var chatId = request.object.get('chat').id;
  var messageId = request.object.id;

  query = new Parse.Query('Chat');
  query.include('subscriptions');
  query.get(chatId).then(function(chat) {
    // Update the lastMessage field of chat
    chat.set('lastMessage', request.object);
    // Update message count
    chat.increment('messageCount');
    return chat.save();
  }).then(function(chat) {
    // Notify participants about the new message
    var subscriptions = chat.get('subscriptions');
    var updatedSubscriptions = [];
    subscriptions.forEach(function(subscription) {
      var userId = subscription.get('user').id;

      if (request.user.id == userId) {
        console.log('Skipping originator: ' + userId);
      } else {
        console.log('Notifying: ' + userId);
        var message = request.object.get('message') == null ? 'Picture' : request.object.get('message');
        var data = {
          'type': 'newMessageInChat',
          'userId': request.user.id,
          'chatId': chatId,
          'name': request.user.get('displayName'),
          'messageId': messageId,
          'message': message,
          'timestamp': request.object.createdAt.getTime()
        };
        sendPushToUser(userId, 'newMessageInChat', data, function(success){});
        // Check if the subscription should become reactivated (from being deleted)
        if (subscription.get('deleted') == true) {
          subscription.set('deleted', false);
          subscription.save();
          updatedSubscriptions.push(subscription);
        }
      }
    });
    return Parse.Object.saveAll(updatedSubscriptions);
  }).then(function(ignored) {
    // Mark as read for the poster
    markChatAsRead(request.user.id, chatId);
  });
});

Parse.Cloud.beforeSave('Image', function(request, response) {
  if (request.user == null) {
    response.success();
    return;
  }
  var image = request.object;
  createThumbnail(image.get('image')).then(function(thumbnail) {
    image.set('thumbnail', thumbnail);
    response.success();
  });
});

Parse.Cloud.afterSave('Image', function(request) {
  if (request.user == null) {
    return;
  }
  var localMessageId = request.object.get('localMessageId');
  var imageId = request.object.id;
  var query = new Parse.Query('Message');
  query.equalTo('localId', localMessageId);
  query.include('chat.subscriptions');
  query.find({
    success: function(result) {
      if (result.length == 0) {
        console.error('Message with local id: ' + localMessageId + ' was not found');
        request.object.destroy();
      }
      else {
          var message = result[0];
          var chat = message.get('chat');
          message.set('image', Parse.Object.extend('Image').createWithoutData(imageId));
          message.save().then(function(savedMessage) {
            // Notify participants that the photo was uploaded
            var pushPayload = {
              'type': 'imageUploaded',
              'chatId': chat.id,
              'messageId': message.id
            };
            chat.get('subscriptions').forEach(function(subscription) {
              var userId = subscription.get('user').id;
              sendSocketPushToUser(userId, pushPayload);
            });
          });
      }
    },
    error: function(error) {
      console.error(error);
    }});
});

// Custom error message if user name is taken
Parse.Cloud.beforeSave(Parse.User, function(request, response) {
  var query = new Parse.Query(Parse.User);
  query.equalTo('username', request.object.get('username'));
  query.find({
    success: function(result) {
      if (result.length > 0 && result[0].id != request.object.id) {
        response.error('User name is already taken');
      }
      else {
        response.success();
      }
    },
    error: function(error) {
      response.error('Something went wrong: ' + error);
    }});
});

// ((this has been written by the best PM Ever just so he can have his name in history)) <--- Antoine

Parse.Cloud.beforeDelete('Chat', function(request, response) {
  var chatId = request.object.id;
  var chatPointer = Parse.Object.extend('Chat').createWithoutData(chatId);
  var query = new Parse.Query('ChatSubscription');
  query.equalTo('chat', chatPointer);
  query.find({
    success: function(subs){
      Parse.Object.destroyAll(subs);
      // Remove all messages as well
      var query = new Parse.Query('Message');
      query.equalTo('chat', chatPointer);
      query.find({
        success: function(messages){
          Parse.Object.destroyAll(messages);
          response.success();
        },
        error: function (error) {
          response.error(error);
        }
      });
    },
    error: function (error) {
      response.error(error);
    }
  });
});

function sendPushToUser(userId, type, data, callback) {
  console.log("Sending push to user")
  // Send socket push
  Parse.Cloud.httpRequest({
    method: 'POST',
    url: 'http://ec2-52-49-166-124.eu-west-1.compute.amazonaws.com:9001/notify?key=test123&id=' + userId,
    body: JSON.stringify(data, null, 3)
  }).then(function(httpResponse) {
    console.log("Push sent via socket " + httpResponse.status);
    console.log(httpResponse.text);
  }, function(httpResponse) {
    console.log("Attempting to send push via GCM");
    sendPushUsingGCM(userId, type, data, callback);
  });
}

function sendPushUsingGCM(userId, type, data, callback) {
  Parse.Cloud.useMasterKey();
  var query = new Parse.Query('_Installation');
  query.equalTo('user', Parse.User.createWithoutData(userId));
  Parse.Push.send({
      where: query,
      data: {
        'type': type,
        'data': data
      }
    },{
      success: function(){
        callback(true);
      },
      error: function (error) {
        callback(false);
      }
    });
}

function sendSocketPushToUser(userId, data) {
  Parse.Cloud.httpRequest({
    method: 'POST',
    url: 'http://ec2-52-49-166-124.eu-west-1.compute.amazonaws.com:9001/notify?key=test123&id=' + userId,
    body: JSON.stringify(data, null, 3)
  }).then(function(httpResponse) {
    console.log(httpResponse.text);
  }, function(httpResponse) {
    console.error('Request failed with response code ' + httpResponse.status);
  });
}

Parse.Cloud.define('notifyUserTyping', function(request, response) {
  var userId = request.user.id;
  var chatId = request.params.chatId;
  var pushPayload = {
    'type': 'userTyping',
    'chatId': chatId,
    'userId': userId
  };
  // Find all subscriptions for the chat and notify the other users
  var query = new Parse.Query('ChatSubscription');
  query.equalTo('chat', Parse.Object.extend('Chat').createWithoutData(chatId));
  query.notEqualTo('user', Parse.User.createWithoutData(userId));
  query.find().then(function(subscriptions) {
      subscriptions.forEach(function(sub) {
          sendSocketPushToUser(sub.get('user').id, pushPayload);
      });
      response.success();
  }, function(error) {
      response.error(error);
  });
});

Parse.Cloud.define('createChat', function(request, response) {
  findChat(request.user.id, request.params.otherUserIds, function(error, chat) {
      if (error != null) {
        response.error(error);
      }
      else if (chat != null) {
        response.success(chat);
      }
      else {
        // No chat found, need to create a new chat
        createChat(request.user.id, request.params.otherUserIds, request.params.groupName, response);
      }
  });
});

Parse.Cloud.define('generateThumbnail', function(request, response) {
  var imageId = request.params.imageId;
  var query = new Parse.Query('Image');
  query.get(imageId, {
    success: function(image) {
      createThumbnail(image.get('image')).then(function(thumbnail) {
        image.set('thumbnail', thumbnail);
        return image.save();
      }).then(function(image) {
        response.success('Generate thumbnail for ' + imageId);
      });
    },
    error: function(error) {
      response.error(error);
    }
  });
});

function createThumbnail(file, callback) {
  return Parse.Cloud.httpRequest({
    url: file.url()
  }).then(function(httpResponse) {
    var image = new Image();
    return image.setData(httpResponse.buffer);
  }).then(function(image) { // Resize
    var aspect = image.height() / image.width();
    if (aspect > 1) {
      return image.scale({
        width: 300,
        height: 300 * aspect
      });
    }
    else {
      return image.scale({
        width: 300 / aspect,
        height: 300
      });
    }
  }).then(function(image) {
    return image.crop({
      width: 300,
      height: 300,
      left: (image.width() - 300) / 2,
      top: (image.height() - 300) / 2
    });
  }).then(function(thumbnailImage) {
    return thumbnailImage.data();
  }).then(function(data) {
    var encodedData = data.toString("base64");
    var name = "thumbnail.png";
    var thumbnail = new Parse.File(name, { base64: encodedData });
    return thumbnail.save();
  });
}

function findChat(myUserId, otherUserIds, callback) {
  if (otherUserIds.length > 1) {
    callback(null, null);
  }
  else {
    // Find all my chats and check if there is one containing only you and the other person
    var otherUserId = otherUserIds[0];
    var query = new Parse.Query('ChatSubscription');
    query.equalTo('user', Parse.User.createWithoutData(myUserId));
    query.include('chat.subscriptions.user');
    query.include('chat.lastMessage');
    query.find({
      success: function(subscriptions) {
        for (var i = 0; i < subscriptions.length; i++) {
          var sub = subscriptions[i];
          var subsForChat = sub.get('chat').get('subscriptions');
          if (subsForChat != null && subsForChat.length == 2 && (subsForChat[0].get('user').id == otherUserId || subsForChat[1].get('user').id == otherUserId)) {
            // Make sure that the chat is not deleted and if so, undelete it
            sub.set('deleted', false);
            sub.save().then(function(sub) {
              callback(null, sub.get('chat'));
            });
            return;
          }
        }
        callback(null, null);
      },
      error: function(error) {
        callback(error, null);
      }
    });
  }
}

function createChat(myUserId, otherUserIds, groupName, response) {
  var userIds = otherUserIds;
  userIds.push(myUserId); // Also include own user in the new Chat
  var chat = new Parse.Object('Chat');
  chat.set('name', groupName);
  // First save it so that we can refer to it (using pointers) from the subscriptions
  chat.save(null, {
    success: function(chat) {
      var subscriptions = [];
      for (var i = 0; i < userIds.length; i++) {
        subscriptions.push(createSubscription(userIds[i], chat.id));
      }
      Parse.Object.saveAll(subscriptions, {
        success: function(subscriptions) {
          // Add references to the subscriptions to the Chat
          for (var i = 0; i < subscriptions.length; i++) {
            chat.add('subscriptions', Parse.Object.extend('ChatSubscription').createWithoutData(subscriptions[i].id))
          }
          chat.save(null, {
            success: function(chat) {
              if (groupName == null && chat.get('subscriptions').length == 2) {
                // Update all the subscriptions to make sure that they have names
                updateSubscriptionNamesForSingleChat(chat, response);
              }
              else {
                response.success(chat);
              }
            },
            error: function(error) {
              response.error('Failed to add subscriptions to chat:' + error);
            }
          });
        },
        error: function(error) {
          response.error(error);
        }
      });
    },
    error: function(chat, error) {
      response.error(error);
    }
  });
}

function updateSubscriptionNamesForSingleChat(chat, response) {
  query = new Parse.Query('Chat');
  query.include('subscriptions.user');
  query.get(chat.id, {
    success: function(chat) {
      var subs = chat.get('subscriptions');
      subs[0].set('name', subs[1].get('user').get('displayName'));
      subs[1].set('name', subs[0].get('user').get('displayName'));
      Parse.Object.saveAll(subs, {
        success: function(subs) {
          response.success(chat);
        },
        error: function(error) {
          response.error(error);
        }
      });
    },
    error: function(error) {
      response.error(error);
    }
  });
}

Parse.Cloud.define('getChat', function(request, response) {
  var chatId = request.params.chatId;
  query = new Parse.Query('Chat');
  query.include('subscriptions.user');
  query.include('lastMessage');
  query.get(chatId, {
    success: function(chats) {
      response.success(chats);
    },
    error: function(error) {
      response.error('Failed to load chat.' + error);
    }
  });
});

Parse.Cloud.define('deleteConversations', function(request, response) {
  var userId = request.user.id;
  var chatRefs = [];
  request.params.ids.forEach(function(chatId) {
      chatRefs.push(Parse.Object.extend('Chat').createWithoutData(chatId));
  });
  query = new Parse.Query('ChatSubscription');
  query.containedIn('chat', chatRefs);
  query.equalTo('user', Parse.User.createWithoutData(userId));
  query.find().then(function(subscriptions) {
    subscriptions.forEach(function(subscription) {
      subscription.set('deleted', true);
    })
    return Parse.Object.saveAll(subscriptions);
  }).then(function(subscriptions) {
    response.success(subscriptions);
  });
});

Parse.Cloud.define('markChatAsRead', function(request, response) {
  var chatId = request.params.chatId;
  var userId = request.user.id;
  query = new Parse.Query('ChatSubscription');
  query.include('chat');
  query.include('chat.lastMessage');
  query.equalTo('chat', Parse.Object.extend('Chat').createWithoutData(chatId));
  query.equalTo('user', Parse.User.createWithoutData(userId));
  query.first({
    success: function(subscription) {
      var chat = subscription.get('chat');
      if (chat.get('lastMessage') == null) {
        response.success(subscription);
        return;
      }
      subscription.set('lastSeenCount', chat.get('messageCount'));
      subscription.set('lastReadMessage', Parse.Object.extend('Message').createWithoutData(chat.get('lastMessage').id));
      subscription.save(null, {
        success: function(subscription) {
          response.success(subscription);
        },
        error: function(error) {
          response.error('Failed to update subscription: ' + error);
        }
      });
    },
    error: function(error) {
      response.error(error);
    }
  });
});

function markChatAsRead(userId, chatId) {
  var query = new Parse.Query('ChatSubscription');
  query.include('chat');
  query.equalTo('chat', Parse.Object.extend('Chat').createWithoutData(chatId));
  query.equalTo('user', Parse.User.createWithoutData(userId));
  return query.first().then(function(subscription) {
    var chat = subscription.get('chat');
    console.log('Changing last read count from ' + subscription.get('lastSeenCount') + ' to ' + chat.get('messageCount'));
    subscription.set('lastReadMessage', Parse.Object.extend('Message').createWithoutData(chat.get('lastMessage').id));
    subscription.set('lastSeenCount', chat.get('messageCount'));
    return subscription.save();
  });
}

Parse.Cloud.define('getMySubscriptions', function(request, response) {
  var userId = request.user.id;
  var query = new Parse.Query('ChatSubscription');
  query.equalTo('user', Parse.User.createWithoutData(userId));
  query.notEqualTo('deleted', true);
  query.include('chat.subscriptions.user');
  query.include('chat.lastMessage');
  query.find({
    success: function(chats) {
      response.success(chats);
    },
    error: function(error) {
      console.error('Failed to load chats for user');
      response.error(error);
    }
  });
});

Parse.Cloud.define('subscribeToChat', function(request, response) {
  var userId = request.user.id;
  var chatId = request.params.chatId;
  query = new Parse.Query('Chat');
  query.get(chatId, {
    success: function(chat) {
      createSubscriptionAndSave(userId, chatId, function(error, subscription) {
          if (error != null) {
            response.error(error);
          }
          else {
            chat.add('subscriptions', Parse.Object.extend('ChatSubscription').createWithoutData(subscription.id));
            chat.save();
            response.success('Updated');
          }
      });
    },
    error: function(error) {
      response.error('Got an error ' + error + ' when trying to add user to chat ' + chatId);
    }
  });
});

function createSubscriptionAndSave(userId, chatId, callback) {
  var object = createSubscription(userId, chatId);
  object.save(null, {
    success: function(subscription) {
      callback(null, subscription);
    },
    error: function(error) {
      callback(error, null);
    }
  });
}

function createSubscription(userId, chatId) {
  var object = new Parse.Object('ChatSubscription');
  object.set('user', Parse.User.createWithoutData(userId));
  object.set('chat', Parse.Object.extend('Chat').createWithoutData(chatId));
  return object;
}

Parse.Cloud.define('addUserToChat', function(request, response) {
  Parse.Cloud.useMasterKey();
  var userId = request.params.userId;
  var chatId = request.params.chatId;
  query = new Parse.Query('Chat');
  query.get(chatId, {
    success: function(chat) {
      chat.add('users', userId);
      chat.save();
      response.success('Updated');
    },
    error: function(error) {
      response.error('Got an error ' + error + ' when trying to add user to chat ' + chatId);
    }
  });
});
