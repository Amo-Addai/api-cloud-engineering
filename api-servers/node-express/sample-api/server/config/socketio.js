'use strict'

// const chatio = require('chat.io')

// When the user disconnects.. perform this
function onDisconnect (socket) { }

// When the user connects.. perform this
function onConnect (socket) {
  io.sockets.sockets.id = socket.id // Make ID unique, for every user

  // When the client emits 'info', this listens and executes
  socket.on('info', function (data) {
    console.info('[%s] %s', socket.address, JSON.stringify(data, null, 2))
  })

  // Insert sockets below
  require('../api/users/user.socket').register(socket)

// chatio.createChat(io.sockets)
}

/*
function onChatMessage(msg){
  console.log('message: ' + msg)
  /// SEND MESSAGE TO RECEIPIENT
}
function broadcastMsg (socket, msg){
  socket.emit('some event', msg)
  // socket.broadcast.emit(msg)  // USE THIS TO BROADCAST TO EVERYONE EXCEPT SOME SPECIFIC SOCKET
}
function sendMessageToSpecificReceipient(socket, msg){
  io.sockets.sockets.id.emit('chat message', msg) // KNOW HOW TO GET UNIQUE ID FOR USER
}
*/

let io = null
module.exports = function (socketio) {
  /*
  socket.io (v1.x.x) is powered by debug.
  In order to see all the debug output, set DEBUG (in server/config/local.env.js) to including the desired scope.

  ex: DEBUG: 'http*,socket.io:socket'

  We can authenticate socket.io users and access their token through socket.handshake.decoded_token

  1. You will need to send the token in `client/components/socket/socket.service.js`

  2. Require authentication here:
  socketio.use(require('socketio-jwt').authorize({
    secret: config.secrets.session,
    handshake: true
  }))
  */

  io = socketio
  socketio.on('connection', function (socket) {
    socket.address = socket.handshake.address !== null
      ? socket.handshake.address.address + ':' + socket.handshake.address.port
      : process.env.DOMAIN

    socket.connectedAt = new Date()

    // Call onDisconnect.
    socket.on('disconnect', function () {
      onDisconnect(socket)
      console.info('[%s] has disconnected from the server', socket.address)
    })

    // // Call onChatMessage.
    // socket.on('chat message', function(msg){
    //   onChatMessage(msg)
    // })

    // Call onConnect.
    onConnect(socket)
    console.info('[%s] has connected to the server', socket.address)
  })
}
