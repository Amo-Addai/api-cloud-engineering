const express = require('express')
const mongoose = require('mongoose')
const config = require('./server/config/environment')
const app = express()

// Set default node environment to development
const environment = process.env.NODE_ENV = process.env.NODE_ENV || 'development'
const pid = process.pid

// Connect to database - Mongoose
console.log('Now connecting to Database: ' + config.mongo.uri)
console.log('With Options: ' + JSON.stringify(config.mongo.options))

mongoose.connect(config.mongo.uri, config.mongo.options)
  .then(() => {
    console.log('Connected to Database server (' + config.mongo.uri + ') successfully...')
    console.log('Now, checking if Database seeding/population is required ...')

    // Populate DB with sample data
    if (config.seedDB) require('./server/config/seed')
    else console.log('Not Required ..\n')
    // Test DB with sample data
    if (config.testDB) require('./server/config/unit.testing')
  })
  .catch((err) => {
    console.log('Could not connect to Database server (' + config.mongo.uri + ') successfully...')
    if (err) console.log('Error: ' + JSON.stringify(err))
  })

// CORS middleware
const allowCrossDomain = function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,PATCH')
  res.header('Access-Control-Allow-Headers', 'Content-Type')
  next()
}
app.use(allowCrossDomain)

// Only 'https' is allowed in the production environment
const server = require(environment === 'production' ? 'https' : 'http').createServer(app)
const socketio = require('socket.io')(server, {
  serveClient: (config.env !== 'production'),
  path: '/socket.io-client'
})

require('./server/config/express')(app)
require('./server/config/socketio')(socketio)
require('./server/routes')(app)

// Start server
server.listen(config.port, /* config.ip, */ function () {
  console.log('Server (%s) listening on port %d, in %s mode with process %d', config.ip, config.port, app.get('env'), pid)
})

// Expose app
module.exports = app
