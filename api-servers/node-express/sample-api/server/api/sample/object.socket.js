/**
 * Broadcast updates to client when the model changes
 */

'use strict'

const SampleObject = require('./type.model.js')

exports.register = function (socket) {
  SampleObject.schema.post('save', function (doc) {
    onSave(socket, doc)
  })
  SampleObject.schema.post('remove', function (doc) {
    onRemove(socket, doc)
  })
}

function onSave (socket, doc, cb) {
  socket.emit('type:save', doc)
}

function onRemove (socket, doc, cb) {
  socket.emit('type:remove', doc)
}
