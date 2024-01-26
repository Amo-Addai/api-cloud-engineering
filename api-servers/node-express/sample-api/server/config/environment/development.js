'use strict'

// Development specific configuration
module.exports = {
  // Server Protocol
  protocol: 'http://',

  // Server IP
  ip: 'localhost/',

  // Server Port
  port: 8080,

  // MongoDB connection options
  mongo: {
    uri: 'mongodb://localhost/sampledb-dev',
    options: {
      useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true
    }
  },

  seedDB: true,
  testDB: false
}
