'use strict'

// Production specific configuration
module.exports = {

  // Server Protocol
  protocol: process.env.OPENSHIFT_NODEJS_PROTOCOL ||
    process.env.PROTOCOL ||
    'https://',

  // Server IP
  ip: process.env.OPENSHIFT_NODEJS_IP ||
    process.env.IP ||
    'domain.com/',
    // 'domain.herokuapp.com/',

  // Server Domain
  domain: process.env.OPENSHIFT_NODEJS_DOMAIN ||
    process.env.DOMAIN ||
    'www.domain.com/',

  // Server port
  port: process.env.OPENSHIFT_NODEJS_PORT ||
    process.env.PORT ||
    8080,

  // MongoDB connection options
  mongo: {
    uri: process.env.MONGOLAB_URI ||
      process.env.MONGOHQ_URL ||
      process.env.OPENSHIFT_MONGODB_DB_URL + process.env.OPENSHIFT_APP_NAME ||
      'mongodb+srv://user:pass@sampledb.sifrt.mongodb.net/sampledb?retryWrites=true&w=majority',
    options: {
      useNewUrlParser: true, useUnifiedTopology: true, useCreateIndex: true
    }
  },

  // Or, if you want to set ENV VARs locally, run these commands ..
  // LINUX: export MONGOLAB_URI="heroku_z8gss0n5:lvteoep29hfv8sg0ne83cme2cg@ds041494.mongolab.com:41494/heroku_z8gss0n5"
  // WINDOWS: SET MONGOLAB_URI="heroku_z8gss0n5:lvteoep29hfv8sg0ne83cme2cg@ds041494.mongolab.com:41494/heroku_z8gss0n5"

  seedDB: false,
  testDB: false
}
