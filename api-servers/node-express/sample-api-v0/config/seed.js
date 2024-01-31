'use strict'

const mongoose = require('mongoose')
const fixtures = require('./fixtures')

const seedData = fixtures()

module.exports.seedDB = async () => {
    try {
        for (let type of Object.keys(seedData)) {
            // console.log(`Seeding ${type} ..`)
            let data = seedData[type]
            let M = mongoose.model(type)
            await M.deleteMany({})
            M.create(data).then(data => {
                if (data) {
                    // console.log(data)
                }
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
