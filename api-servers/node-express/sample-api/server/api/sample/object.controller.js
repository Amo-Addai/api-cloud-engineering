'use strict'

const functions = require('../../functions')
// const config = functions.config
// const auth = functions.auth
// const settings = functions.settings
const funct = functions.funct

const dataHandler = require('../utils/db/DataHandler')
const modelscontrollersHandler = dataHandler.modelscontrollersHandler

const M = require('./type.model.js.js')

exports.M = M
exports.dataToExclude = M.dataToExclude
exports.imgdata = M.imgdata.length > 0 ? imgdata : null
exports.markModify = M.markModify
exports.deepPop = M.deepPop
exports.mainData = M.mainData
exports.sort = M.sort
exports.type = M.type
exports.validate = M.validate

// Get all objects within the database
exports.get = async function (req, res) {
    try {
        const result = await modelscontrollersHandler.getAll(M, req.query.condition)
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

// Get a single object
exports.show = async function (req, res, next) {
    try {
        const result = await modelscontrollersHandler.get(M, req.params.id)
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

// Creates a new object
exports.create = async function (req, res, next) {
    try {
        const result = await modelscontrollersHandler.add(M, req.body) // DON'T RETURN USER DATA WITH ACCESS_TOKEN, LET USER LOGIN
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

// Updates an existing object in the DB.
exports.update = async function (req, res) {
    try {
        const result = await modelscontrollersHandler.update(M, req.params.id, req.body) // DON'T RETURN USER DATA WITH ACCESS_TOKEN, LET USER LOGIN
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

// Deletes an object
exports.destroy = async function (req, res) {
    try {
        const result = await modelscontrollersHandler.delete(M, req.params.id)
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

// Get number of objects in database
exports.count = async function (req, res, next) {
    try {
        const result = await modelscontrollersHandler.count(M, req.params.id)
        return funct.sendResponse(res, result.code, result.resultData)
    } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
        console.log("Error: " + JSON.stringify(err.err))
        return funct.sendResponse(res, err.code, err.resultData)
    }
}

//////////////////////////////////////////
//  OTHER AVAILABLE CONTROLLER FUNCTIONS
exports.schema = function (req, res, next) {
    const schema = []
    return funct.sendResponse(res, 200, schema)
}
