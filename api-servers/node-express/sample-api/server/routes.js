'use strict'

const path = require('path')
const errors = require('./components/errors')

module.exports = function (app) {
  // View Routes
  app.use('/', require('./views'))

  // API Routes
  app.use('/api/sample', require('./api/sample'))
  app.use('/api/users', require('./api/users'))

  // Config Routes
  app.use('/api/settings', require('./config/settings'))

  // Auth Routes
  app.use('/auth', require('./auth'))

  // Public Routes
  app.use('/public', require('./public'))

  // All undefined asset or api routes should return a 404
  app.route('/:url(public|api|auth|components|app|bower_components|assets)/*')
    .get(errors[404])

  // All other routes should redirect to the index.html
  app.route('/*')
    .get(function (req, res) {
      res.sendFile(path.resolve(app.get('appPath') + '/index.html'))
    })
}
