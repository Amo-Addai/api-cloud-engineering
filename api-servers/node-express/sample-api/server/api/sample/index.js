'use strict'

const express = require('express')
const functions = require('../../functions')
const auth = functions.auth
// const funct = functions.funct

const controller = require('./type.controller.js')
// const typefunct = require('../../TypeFunctions')

const router = express.Router()
const type = 'type'

router.get('/', auth.isAuthorizedToGetData(type, controller.M))
router.get('/my', auth.isAuthorizedToGetData(type, controller.M))
router.get('/schema', auth.isAuthenticated(), controller.schema)
router.get('/count', auth.isAuthenticated(), controller.count)
router.get('/:id', auth.isAuthenticated(), controller.show)

router.post('/', auth.isAuthorized('add', type), controller.create)

router.post('/:id', auth.isAuthorized('edit', type), controller.update)
router.put('/:id', auth.isAuthorized('edit', type), controller.update)
router.patch('/:id', auth.isAuthorized('edit', type), controller.update)

router.delete('/:id', auth.isAuthorized('delete', type), controller.destroy)

module.exports = router
