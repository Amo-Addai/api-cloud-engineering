'use strict'

const express = require('express')
const passport = require('passport')

const funct = require('../functions')
const config = funct.config
const auth = funct.auth
funct = funct.funct

// THIS MIGHT OR MIGHT NOT CAUSE AN ISSUE (SO CHECK FIRST, BEFORE USING)
const User = require('../api/users/user.model.js')
const Company = {} // require('../api/COMPANY/company.model.js')

const init = require('./init')
// NOT TOO SURE ABOUT THIS THOUGH - CHECK IT! (COZ init() IS ALSO BEING USED AT THE ENDING OF ALL passport.js AUTH FILES)
init() // SETUP SERIALIZATION & DESERIALIZATION OF USERS

// Passport Configurations
require('./local/passport').setup(User, Company, config)

// Now setup auth routes

const router = express.Router()

router.use('/local', auth.handleExtraAuthentication, require('./local'))

/*
router.post('/signup', function (req, res, next) {
    // FIND OUT HOW YOU CAN MAKE THIS HAPPEN FOR ALL POSSIBLE AUTHENTICATIONS
})

router.post('/dashboard/signup', function (req, res, next) {
    // FIND OUT HOW YOU CAN MAKE THIS HAPPEN FOR ALL POSSIBLE AUTHENTICATIONS
})

router.post('/logout', function (req, res, next) {
    // FIND OUT HOW YOU CAN MAKE THIS HAPPEN FOR ALL POSSIBLE AUTHENTICATIONS
})

router.post('/dashboard/logout', function (req, res, next) {
    // FIND OUT HOW YOU CAN MAKE THIS HAPPEN FOR ALL POSSIBLE AUTHENTICATIONS
})
*/

module.exports = router
