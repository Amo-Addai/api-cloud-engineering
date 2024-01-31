'use strict'

import express from 'express'
import dotenv from 'dotenv'

import setup from './config/setup.js'
import seed from './config/seed.js'

import routes from './routes/index.js'

// Load environment variables
dotenv.config()

const app = express()
const port = process.env.PORT || 3000

// Set up MongoD Server
setup.mongod()

// Seed the database
seed.seedDB()

// Middleware to parse JSON
app.use(express.json())

// set the view engine to ejs
app.set('view engine', 'ejs')

// set views
app.set('views', './views')

// Use routes
app.use('/api', routes.api())

// view routes
app.use('/', routes.view())

// Start the server, and return it for tests
app.listen(port, () => {
  console.log(`Server is running at http://localhost:${port}`)
})

export default app
