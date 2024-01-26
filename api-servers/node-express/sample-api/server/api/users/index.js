'use strict'

const express = require('express')
const functions = require('../../functions')
const auth = functions.auth
const funct = functions.funct

const controller = require('./user.controller')

const router = express.Router()
const type = 'user'

router.get('/', auth.isAuthorizedToGetData(type, controller.M))
router.get('/my', auth.isAuthorizedToGetData(type, controller.M))
router.get('/me', funct.getUserData(controller.M))
router.put('/me', funct.editUserData(controller.M))
router.get('/schema', auth.isAuthenticated(), controller.schema)
router.get('/count', auth.isAuthenticated(), controller.count)
router.get('/:id', auth.isAuthenticated(), controller.show)

router.post('/', auth.isAuthorized('add', type), controller.create)
router.post('/reset_password', controller.resetpassword)

router.post('/:id', auth.isAuthorized('edit', type), controller.update)
router.put('/:id', auth.isAuthorized('edit', type), controller.update)
router.patch('/:id', auth.isAuthorized('edit', type), controller.update)

router.delete('/:id', auth.isAuthorized('delete', type), controller.destroy)

module.exports = router
