'use strict'

// Test specific configuration
module.exports = {
  // Server Protocol
  protocol: 'http://',

  // Server IP
  ip: 'localhost/',

  // Server Port
  port: 8080,

  // MongoDB connection options
  mongo: {
    uri: 'mongodb://localhost/sampledb-test'
  },

  seedDB: true,
  testDB: true
}
