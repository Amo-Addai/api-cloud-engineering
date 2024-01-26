/**
 * Populate DB with sample data on server start
 * to disable, edit config/environment/index.js, and set `seedDB: false`
 */

'use strict'

const mongoose = require('mongoose')

// const fixturesData = require('./fixtures/fixturesData.json')
// const User = require('../api/users/user.model').Model

console.log("Seeding DB with sample initial data")

const defaultData = {
    "User": {
        password: '0',
        details: 'details of ...',
        home_address: 'Sample Home Address',
        postal_address: 'Sample Postal Address',
        app_id: "",
        type: "Employee",
        contact_method: 'Email',
        provider: 'local'
    }
}

const data = {
    "User": [
        {
            "username": "a",
            "first_name": "a",
            "last_name": "a",
            "other_names": "a",
            "gender": "Female",
            "nationality": "United States",
            "age": 22,
            "email": "a@a.com",
            "phone": "+233270809060",
            "contact_method": "Email"
        },
        {
            "username": "starboy",
            "first_name": "Kwadwo",
            "last_name": "Amo-Addai",
            "other_names": "Felix",
            "type": "Investor",
            "gender": "Male",
            "nationality": "Ghana",
            "age": 23,
            "email": "kwadwoamoad@gmail.com",
            "phone": "+233206998117",
            "contact_method": "Email",
            "date_of_birth": Date.parse("13-Nov-1995 18:00:00"),

            "data": {
                "KYC": {
                    "Passport": {
                        "id": "G1937710"
                    },
                    "National ID": {
                        "id": ""
                    },
                    "Voter's ID": {
                        "id": ""
                    },
                    "SSNIT": {
                        "id": ""
                    },
                    "TIN": {
                        "id": ""
                    },
                    "Driver's License": {
                        "id": ""
                    },
                    "Health Insurance ID": {
                        "id": ""
                    },
                    "Bank Verification Number": {
                        "id": ""
                    }
                }
            },
        }
    ]
}
let dataObject = {}, dataObjects = []
const dummyDefault = JSON.parse(JSON.stringify(defaultData))

// runAutoSeedOperation()

async function runAutoSeedOperation () {
    for (const type of ["User", "Message", "Account", "Prospect", "Proposal", "Project", "Property", "Asset", "Portfolio", "Investment", "Post"]) {
        // "User", "Message", "Account", "Prospect", "Investment" <- NEVER AUTO-SEED THESE ONES WHEN PUSHING TO PRODUCTION !!!  !!!!
        for (const o of data[type]) {
            dataObject = JSON.parse(JSON.stringify(Object.assign(dummyDefault[type], o)))

            //  YOU CAN PUT SOME EXTRA LOGIC HERE IF YOU PLEASE ..

            dataObjects.push(dataObject)
            dataObject = {}
        }
        if (type === "Prospect") {
            for (const prosp of (subscribers || [])) dataObjects.push(JSON.parse(JSON.stringify(prosp)))
        }
        await seedDB(type, dataObjects)
        dataObjects = []
    }
}

async function seedDB (type, data) {
    return new Promise(async (resolve, reject) => {
        try {
            console.log("")
            console.log("Now populating data for Table - " + type + " : " + data.length + " object(s)")
            console.log("DATA OBJECTS -> " + JSON.stringify(data))
            console.log("")
            //
            const M = mongoose.model(type)
            M.find({}).remove(function () {
                console.log(type + " Table has been emptied, now populating with new " + type + "(s) ...")
                M.create(data, function (err) {
                    if (err) {
                        console.log('Some error occurred during population of ' + type + 's')
                        console.log(err)
                        reject(err)
                    } else console.log('Finished populating ' + type + ' data')
                    resolve(true)
                })
            })
        } catch (e) {
            console.log("ERROR -> " + e)
            reject(e)
        }
    })
}
