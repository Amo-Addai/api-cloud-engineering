'use strict'

const mongoose = require('mongoose')
const { MongoMemoryServer } = require('mongodb-memory-server')

module.exports.mongod = async () => {
    try {
        // Set up MongoDB in-memory server for testing
        const mongod =  await MongoMemoryServer.create()
        
        /** 
         * not required, after v7

        new MongoMemoryServer()

        Start MongoD Server 
        await mongod.start()
        */

        // MongoDB connection URL
        const mongoUri = mongod.getUri()
        
        // Setup mongoose
        mongoose.connect(mongoUri, { useNewUrlParser: true, useUnifiedTopology: true })

        return mongod
    } catch (e) {
        console.log(e)
    }
}
