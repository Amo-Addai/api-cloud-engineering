'use strict'

const express = require('express')
const morgan = require('morgan')
const compression = require('compression')
const bodyParser = require('body-parser')
const busboyBodyParser = require('busboy-body-parser')
const methodOverride = require('method-override')
const cookieParser = require('cookie-parser')
const errorHandler = require('errorhandler')
const path = require('path')
const passport = require('passport')
const session = require('express-session')
const mongoStore = require('connect-mongo')(session)
const mongoose = require('mongoose')
const cors = require('cors')
const fileUpload = require('express-fileupload')
// const favicon = require('serve-favicon')
// const ejs = require('ejs')

const functions = require('../functions')
const config = functions.config

module.exports = function (app) {
  const env = app.get('env') || 'development'

  app.set('views', config.root + '/server/views')
  app.set('view engine', 'ejs')
  // Or, you can set view engine this way
  // app.engine('html', ejs.renderFile)
  // app.set('view engine', 'html')
  app.use(compression())
  app.use(cors())
  app.use(bodyParser.urlencoded({ extended: true }))
  app.use(bodyParser.json())
  app.use(busboyBodyParser())
  app.use(methodOverride())
  app.use(cookieParser())

  app.use(session({
    secret: config.secrets.session,
    resave: true,
    saveUninitialized: true,
    store: new mongoStore({ mongooseConnection: mongoose.connection })
  }))
  app.use(passport.initialize())
  // app.use(passport.session()) // To serialize & deserialize users

  app.use(fileUpload({
    limits: { fileSize: 50 * 1024 * 1024 }
  }))

  switch (env) {
    case 'development' || 'test':
      app.use(require('connect-livereload')())
      app.use('/files', express.static(path.join(config.root, 'FILE_SYSTEM')))
      app.use(morgan('dev'))
      app.use(errorHandler()) // Error handler - has to be last
      break
    case 'production':
      app.use('/files', express.static(path.join(config.root, 'FILE_SYSTEM')))
      app.use(morgan('dev'))
      break
  }
}
