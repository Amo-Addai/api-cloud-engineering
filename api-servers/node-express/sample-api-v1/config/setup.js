'use strict'

import mongoose from 'mongoose'
import { MongoMemoryServer } from 'mongodb-memory-server'

const mongod = async () => {
    try {
        // Set up MongoDB in-memory server for testing
        const mongod =  await MongoMemoryServer.create()

        // MongoDB connection URL
        const mongoUri = mongod.getUri()
        
        // Setup mongoose
        mongoose.connect(mongoUri, { useNewUrlParser: true, useUnifiedTopology: true })

        return mongod
    } catch (e) {
        console.log(e)
    }
}

export default { mongod }
