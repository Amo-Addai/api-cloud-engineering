'use strict'

import mongoose from 'mongoose'
import data from './fixtures/index.js'

const seedData = data

const seedDB = async () => {
    try {
        for (let type of Object.keys(seedData)) {
            // console.log(`Seeding ${type} ..`)
            let data = seedData[type]
            // console.log(data)
            let M = mongoose.model(type)
            await M.deleteMany({})
            M.create(data).then(data => {
                // if (data) console.log(data)
            }).catch(err => {
                if (err) {
                    console.log(err)
                    throw err
                }
            })
        }
    } catch (e) {
        console.log(e)
    }
}

export default { seedDB }
