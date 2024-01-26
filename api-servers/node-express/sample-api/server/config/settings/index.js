'use strict'

const express = require('express')
const functions = require('../../functions')
const auth = functions.auth
const controller = require('./settings.controller')
const router = express.Router()

const type = 'setting'

router.get('/company', auth.isAuthenticated(), controller.getCompanySettings)

router.post('/default', auth.isAuthorized('edit', type), controller.setDefault)
router.put('/default', auth.isAuthorized('edit', type), controller.setDefault)
router.patch('/default', auth.isAuthorized('edit', type), controller.setDefault)

router.post('/', auth.isAuthorized('edit', type), controller.update)
router.put('/', auth.isAuthorized('edit', type), controller.update)
router.patch('/', auth.isAuthorized('edit', type), controller.update)

module.exports = router
