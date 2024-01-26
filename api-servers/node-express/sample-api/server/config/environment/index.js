'use strict'

const path = require('path')

function requiredProcessEnv (name) {
  if (!process.env[name]) {
    throw new Error('You must set the ' + name + ' environment variable')
  }
  return process.env[name]
}
// Ensure that all required ENV VARs are available
process.env['NODE_ENV'] = 'development'
for (const k of ['NODE_ENV' /*, 'PROTOCOL', 'IP', 'DOMAIN', 'PORT' */]) requiredProcessEnv(k)

// All configurations will extend these options
const all = {
  env: process.env.NODE_ENV || 'development',

  // Root path of server
  root: path.normalize(path.join(__dirname, '/../../..')),

  protocol: process.env.PROTOCOL || 'http://',
  ip: process.env.IP || 'localhost/',
  port: process.env.PORT || 8080,
  urls: {
    website: 'https://www.domain.com/',
    dashboard: 'https://app.domain.com/'
  },

  // MongoDB connection options
  mongo: { // MongoDB URI format: mongodb://username:password@host:port/database
    uri: 'mongodb://localhost:27017',
    options: {
      useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true
    // db: {
    //     safe: true
    // }
    }
  },

  seedDB: false,
  testDB: false,

  secrets: {
    session: process.env.SECRET_SESSION || 'default-secret'
  },

  _settingsFile: 'settings',
  get settingsFile() { // THESE KIND OF get ACCESSORS CAN ONLY BE CALLED OUTSIDE THIS LITERAL DEFINITION
    return this._settingsFile
  },
  _defaultSettingsFile: 'default_settings',
  get defaultSettingsFile() {
    return this._defaultSettingsFile
  },

  tokenExpiration: '1h' // settings.getValue('tokenExpiration') || // 10000000000 ms
// userEditTokenExpiration: settings.getValue('userEditTokenExpiration') || 500,
// autoAuditTokenExpiration: settings.getValue('autoAuditTokenExpiration') || 1000
}

// Export the config object based on the NODE_ENV
module.exports = Object.assign(all, require('./' + process.env.NODE_ENV + '.js') || {})
