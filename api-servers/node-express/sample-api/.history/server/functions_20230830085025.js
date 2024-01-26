'use strict'

const request = require('request')
const mongoose = require('mongoose')
const crypto = require('crypto')
const fs = require('fs')
const _ = require('lodash')
const compose = require('composable-middleware')
const path = require('path')
const passport = require('passport')
const jwt = require('jsonwebtoken')
const expressJwt = require('express-jwt')


const dataHandler = require('./api/utils/db/data-handler')
// const models = dataHandler.models
// const modelscontrollers = dataHandler.modelscontrollers
const modelscontrollersHandler = dataHandler.modelscontrollersHandler

const oldConfig = require('./config/environment') || {}
const globalMM = require('./api/utils/helpers/mark-modify')

//      FIRST DEFINE ALL 'PUBLIC' FUNCTIONS USED BY MULTIPLE MODULES (OBJECTS) WITHIN THIS FILE
function sendResponse(res, status, data) {
    if (res) {
        if (data && data.hasOwnProperty('code') && data.hasOwnProperty('index') && data.hasOwnProperty('errmsg')) {
            // THEN IT MEANS MOST LIKELY THIS IS AN ERROR OBJECT
            return res.status(status).send({ success: false, message: data.errmsg })
        } else return res.status(status).send(data)
    }
}

function markModified (data, newdata, M) {
    console.log('DATA -> ' + data)
    console.log('NEW DATA -> ' + JSON.stringify(newdata))
    const updated = Object.assign(data, newdata)
    console.log('UPDATED -> ' + updated)
    const arr = (M.markModify || '').split(' ')
    // let str = ''
    console.log('MARK MODIFY ARRAY -> ' + arr)
    for (const d of arr) {
        console.log('Mark Modifying Property -> ' + d)
        updated.markModified('' + d + '')
    }
    console.log('DONE MARK MODIFYING! RETURNING OBJECT -> ')
    console.log(JSON.stringify(updated))
    return updated
}

function globalMarkModified (M, deepPop, data) {
    // Handles Global Mark Modification ..
}

function getModelName (type) {
    switch (type) {
        case 'user':
            return 'User'
    }
    return null
}

function getModel (type) {
    return mongoose.model(getModelName(type))
}

function getSchema (type) {
    return mongoose.model(getModelName(type)).schema
}

async function contact (type, body, defaultCmeth = false) {
    return await userempclienstakefunct.contact(type, body, defaultCmeth)
}

const settingsData = require('./config/settings/settings.json')
const defaultSettingsData = require('./config/settings/default_settings/default_settings.json')

const settings = {
    getDefaultSettingsFile: function (filename) {
        return defaultSettingsData
    },

    saveDefaultSettingsFile: function () {
    },

    getSettingsFile: function (filename) {
        return settingsData
    },

    // Auth Settings

    canEditSubSettings: function (subSettings, user) {
        return true
    },

    // Checks if the user role meets the minimum requirements of the route
    isAuthenticationAllowed: function (auth, dash) {
        return true
    },

    hasAccessRights: function (x, user, authData, security, params) {
        return true
    },

    checkAccessRights: function (user, authData, security, params) { // params SHOULD BE A constIATIC PARAMETER
        return true
    }
}

// This Module Referencer prevents any cyclic dependencies
const ModuleReferencer = {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // funct: ServerFunctions, // ref.funct
    get funct () {
        return this.ServerFunctions()
    },

    //////////////////////////////////////////////////////////////////////////////////////////////
    // fileHandler: this.prop, // ref.fileHandler,
    // get fileHandler () { return this.FileSystemFunctions() },
    // dataHandler: this.prop, // ref.dataHandler,
    // get dataHandler () { return this.DatabaseSystemFunctions() },
    //////////////////////////////////////////////////////////////////////////////////////////////

    // auth: AuthService, // ref.auth,
    get auth () {
        return this.AuthService()
    },
    // config: ConfigEnvironment, // ref.config,
    get config () {
        return this.ConfigEnvironment()
    },
    // settings: ConfigSettings, // ref.settings,
    get settings () {
        return this.ConfigSettings()
    },
    // errors: Errors, // ref.errors,
    get errors () {
        return this.Errors
    },

    //

    Errors: {
        404: function pageNotFound (req, res) {
            const viewFilePath = '404'
            const statusCode = 404
            const result = {
                status: statusCode
            }

            res.status(result.status)
            res.render(viewFilePath, function (err) {
                if (err) {
                    return res.status(result.status).json(result) // DEPRECATED -> res.json(result, result.status)
                }

                res.render(viewFilePath)
            })
        }
    },

    // Main Server Functions

    ServerFunctions: function () {

        const config = this.config
        const auth = this.auth

        const Funct = {

            sendResponse: function (res, status, data) {
                return sendResponse(res, status, data)
            },

            markModified: function (data, newdata) {
                return markModified(data, newdata)
            },

            globalMarkModified: function (type, deepPop, data) {
                globalMarkModified(type, deepPop, data)
            },

            getUsersInvolved: function (obj) {
                return [] // userempclientstakefunct.getUsersInvolved(obj)
            },

            handleCheckSchedule: function (type, obj) {
                return 'success'
            },

            handleAutoCheckSchedule: function (type, obj) {
                return 'success'
            },

            /////////////////////////////////////////////////////////////////////////////////////////////////
            ////    DATABASE SYSTEM FUNCTIONS

            encryptData: function (type, schema) {
                return schema
            },
            decryptData: function (type, schema) {
                return schema
            },

            // EXTRA FUNCTIONS

            migrateAllData: function (from, to) {
                return true
            },

            saveFile: function (data, filename, domain, filetype) {
                return true
            },

            deleteFile: function (filename, domain, filetype) {
                return true
            },

            /////////////////////////////////////////////////////////////////////////////////////////////////
            ////    AUTO-SECURITY FUNCTIONS

            getDataSecurityObject: function (type, obj) {
                return null
            },

            deleteDataSecurityObject: function (type, obj) {
                return true
            },

            ////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////  SERVER FUNCTIONS

            getUserData: function (userModel) {
                console.log('GETTING USER MODEL -> ' + Object.keys(userModel))
                return compose()
                    .use(auth.isAuthenticated()) // NOW GET USER AND COMPANY DATA
                    .use(async function getUser (req, res, next) {
                        try {
                            // if (!req.params.companyId) throw new Error('Company Id needs to be set')
                            const typeModel = userModel // 'user'
                            console.log('USER ID -> ' + req.user._id)
                            const result = await modelscontrollersHandler.get(typeModel, req.user._id)
                            if (result.code !== 200) return sendResponse(res, result.code, result.resultData)
                            const user = result.resultData

                            // typeModel = companyModel // 'company'
                            // const result = await modelscontrollersHandler.get(typeModel, req.params.companyId)
                            // if(result.code !== 200) return sendResponse(res, result.code, result.resultData)
                            // const company = result.resultData
                            // SIGN ANOTHER ACCESS TOKEN

                            // GET SETTINGS & MORE DATA BASED ON THE USER-TYPE

                            const token = '' // auth.signToken(user._id, null)
                            return sendResponse(res, 200, { access_token: token, user: user, settings: {}, data: {} })
                        } catch (err) {
                            console.log('Some Error has occurred -> ' + JSON.stringify(err))
                            return sendResponse(res, 404, { err: err, success: false, message: 'Some error occurred' })
                        }
                    })
            },

            editUserData: function (userModel) {
                return compose()
                    .use(auth.isAuthenticated()) // NOW EDIT USER AND COMPANY DATA
                    .use(auth.isUserEditTokenAuthenticated()) // VALIDATE USER-EDIT ACCESS TOKEN
                    .use(async function editUser (req, res, next) {
                        try {
                            // if (!req.params.companyId) throw new Error('Company Id needs to be set')
                            const user = req.body.user // , company = req.body.company
                            if (user) { // if(user && company){ // VALIDATE userEditingAllowance & user PROPS WITH EDITABLE PROPS
                                // if(settings.getUserEditingAllowance()){
                                // const props = settings.getEditableUserProfileData()
                                // if(!props) return sendResponse(res, 403, {success: false, message: 'User Editing not allowed'})
                                // Object.keys(user).forEach(function(prop, index){
                                //     if(!props.includes(prop))
                                //     return sendResponse(res, 404, {success: false,
                                //         message: 'User Property ' + prop + ' Editing not allowed'})
                                // })
                                let canEditUserData = true
                                if (canEditUserData) {
                                    // NOW CARRY ON WITH EDITING USER & COMPANY PROFILE DATA
                                    let userBool = false, companyBool = true, updateduser = null, updatedcompany = {},
                                        result = null
                                    const typeModel = userModel // 'user'
                                    result = await modelscontrollersHandler.update(typeModel, req.user._id, user)
                                    if (result.code === 200) {
                                        updateduser = result.resultData
                                        // if (updateduser.types && updateduser.types.length > 0) {
                                        //     updateduser = this.createUpdateUserWithTypes(updateduser)
                                        // } // NO NEED FOR THIS CODE ABOVE, COZ User HAS ONLY 1 .type
                                        userBool = true
                                    }
                                    // typeModel = companyModel // 'company'
                                    // result = await modelscontrollersHandler.update(typeModel, req.params.companyId, company)
                                    // if(result.code === 200){
                                    //   updatedcompany = result.resultData
                                    //   companyBool = true
                                    // }
                                    if (updateduser && userBool) {
                                        // SIGN ANOTHER ACCESS TOKEN & BLACKLIST THE USER-EDIT ACCESS TOKEN (BUT THIS IS COMMENTED FOR NOW ..)
                                        // console.log('UNSIGNING OLD ACCESS-TOKEN -> ' + auth.unsignToken(user._id, req.body.user_edit_access_token))
                                        const token = '' // auth.signToken(updateduser._id, null)
                                        return sendResponse(res, 200, {
                                            access_token: token,
                                            user: updateduser,
                                            settings: {}, data: {} // EMPTY OBJECTS FOR NOW ..
                                        })
                                    } else return sendResponse(res, 404, {
                                        success: false,
                                        message: 'User data could not be edited'
                                    })
                                } else return sendResponse(res, 404, {
                                    success: false,
                                    message: 'User Editing not allowed'
                                })
                            } else return sendResponse(res, 404, { success: false, message: 'No user data specified' })
                        } catch (err) {
                            console.log('Some Error has occurred -> ' + JSON.stringify(err))
                            return sendResponse(res, 404, { err: err, success: false, message: 'Some error occurred' })
                        }
                    })
            },

            createUpdateUserTypeWithUser: function (userType, obj) {
                return obj
            },

            createUpdateUserWithTypes: function (user) {
                return user
            }
        }
    },

    // Authentication Service Functions

    AuthService: function () {

        const validateJwt = expressJwt({ secret: oldConfig.secrets.session })

        const auth = {

            /**
             * Attaches the user object to the request if authenticated
             * Otherwise returns 403
             */
            isAuthenticationAllowed: function (authOption, dashboard = false) { // FIGURE OUT MORE AUTH OPTIONS
                if (!['local'].includes(authOption)) throw new Error('INCORRECT AUTHENTICATION OPTION')
                return compose()
                    // Validate jwt
                    .use(function checkAuthenticationAllowed(req, res, next) {
                        // allow access_token to be passed through query parameter as well
                        next()
                    })
            },

            handleExtraAuthentication: function (req, res, next) {
                // HANDLE COMPANY REGISTERED NAME, DEVICE IDs, ETC HERE
                if (req.body.companyRegisteredName) delete req.body.companyRegisteredName
                if (req.extraAuthenticationData) {
                    req.extraAuthenticationData = null // ATTACH ALL EXTRA AUTH DATA HERE - COMPANY, DEVICE, etc
                    delete req.extraAuthenticationData
                }
                next() //  RETURN THIS IN CASE OF ANY AUTH ERROR WITH THE CORRESPONDING extraAuth
                // sendResponse(res, 400, {success : false, message : extraAuth + ' not authorized'})
            },

            // isAuthenticated : function () {
            //     return compose()
            //         .use(function(req, res, next){
            //             next() // THIS SHOULD BE REMOVED TO FUNCTION PROPERLY
            //         })
            // },

            isAuthenticated: function () {
                return compose()
                    // Validate jwt
                    .use(function validateAccessToken (req, res, next) {
                        // allow access_token to be passed through query parameter as well
                        let aToken = ''
                        if (req.query && req.query.hasOwnProperty('access_token')) {
                            console.error('the access_token on query ', req.query.access_token)
                            req.headers.authorization = 'Bearer ' + req.query.access_token
                            aToken = req.query.access_token
                            if (req.query.access_token) {
                                delete req.query['access_token']
                            } // BY NOW THE .access_token PROP OF req.query SHOULD BE GONE
                        } else if (req.headers.authorization) {
                            // console.log('NO ACCESS TOKEN IN QUERY, BUT IT'S IN Authorization HEADER')
                            aToken = req.headers.authorization
                        }
                        req.access_token = aToken // ATTACH TOKEN TO REQUEST OBJECT
                        validateJwt(req, res, next)
                    })
                    // Attach user to request
                    .use(function attachUserToRequestAndUpdateLocation (req, res, next) {
                        console.log('FOUND USER : ') // THIS ISN'T THE WHOLE USER OBJECT THO
                        console.log(JSON.stringify(req.user)) // SO FIND THE WHOLE OBJECT AND ATTACH TO REQUEST
                        const M = { Model: getModel('user') } // WILL NOT INCLUDE validate & OTHER FUNCTIONS WITHIN .model.js FILE
                        // const M = getModel('user')
                        M.Model.findById(req.user._id, '-salt -hashedPassword', function (err, user) {
                            if (err) return next(err)
                            if (!user) return sendResponse(res, 401, { success: false, message: 'User not found' })
                            // //  UPDATE USER LOCATION IF AVAILABLE
                            // if (req.query && req.query.hasOwnProperty('location')) {
                            //     const updated = markModified(user, {location: req.query['location']})
                            //     updated.save(function (err) {
                            //         if (err) return sendResponse(res, 500, err)
                            //         console.log('updated obj: ' + JSON.stringify(updated))
                            //         delete req.query['location']
                            //         return sendResponse(res, 200, updated)
                            //     })
                            // }
                            req.user = user
                            console.log('Token -> ' + req.access_token)
                            console.log('User -> ' + req.user.full_name) // req.user FILLS UP CLI
                            next()
                        })
                    })
                    .use(async function checkIfAccessTokenIsBlackListed (req, res, next) {
                        try { // THIS FUNCTION RETURNS TRUE OR FALSE ALREADY (NO NEED FOR result.code === 200 :)
                            next() //  USE THIS TO SKIP THIS AUTHENTICATION FUNCTION
                            // if (await AutoSecurityFunct.checkIfAccessTokenIsBlackListed(req.access_token, req.user))
                            //     return sendResponse(res, 400, {
                            //         success: false,
                            //         message: 'This User's Access Token is blacklisted'
                            //     })
                            // else next()
                        } catch (err) {
                            console.log(err)
                            // RETURN next() FOR NOW, COZ SOMETIMES API MIGHT NOT FIND AutoAuditingFunctions.js
                            // (DUE TO CYCLIC-DEPENDENCY) eg. /api/autosecurity/autoauditing/autologs/handle/:id
                            next() // AutoSecurityFunct.autoAuditingFunct.autoauditEventHandlerFunct.eventHelperfunct
                            // return sendResponse(res, 400, {err: err, success: false, message: 'Some error occurred'})
                        }
                    })

            },

            isAuthorized: function isAuthorized(sth) { // sth SHOULD BE A constIATIC PARAMETER
                if (!sth) throw new Error('Some specification needs to be set')
                return compose()
                    .use(this.isAuthenticated())
                    .use(function isAuthorized (req, res, next) {
                        // THIS IS WHERE ARE AUTO-SECURITY AUTO-AUTHORIZATION LOGIC LIES ..
                        next()
                    })
            },

            signTokenForUserEditing: function signTokenForUserEditing(id) {
                return jwt.sign({ _id: id }, oldConfig.secrets.session, { expiresIn: oldConfig.userEditTokenExpiration })
            },

            isUserEditTokenAuthenticated: function isUserEditTokenAuthenticated () {
                return compose()
                    // Validate jwt
                    .use(function validateAccessToken (req, res, next) {
                        if (!req.body.user_edit_access_token) req.body.user_edit_access_token = 'DEFAULT USER-EDIT ACCESSTOKEN TOKEN'
                        next()
                    })
            },

            /**
             * Returns a jwt token signed by the app secret
             */
            signToken: function signToken (user, role) {
                const token = jwt.sign({ _id: user._id }, oldConfig.secrets.session, { expiresIn: oldConfig.tokenExpiration })
                try {
                    // FIRST, VALIDATE DEVICE TO BE SURE THAT THIS DEVICE IS RECOGNIZED

                    // const result = InternalAutoAuditingFunct.sendSessionTrackingAutoAudit(user, token, null, true)
                    // WORK WITH result HOWEVER YOU WANT BUT MUST BE AWAITED FIRST
                    // SHOULD YOU CHOOSE TO AWAIT THIS, YOU MUST EDIT sendSessionTrackingAutoAudit() TO RETURN A PROMISE
                } catch (err) {
                    console.log('FINAL ERROR -> ' + err)
                }
                return token // DO THIS SO NO MATTER WHAT YOU'LL RETURN A TOKEN
            },

            /**
             * Removes a jwt token signed by the app secret
             */
            unsignToken: async function unsignToken (user, token) {
                return new Promise(async (resolve, reject) => {
                    try {
                        // if(!validateIoTDevice(user, token, null)) resolve(false)
                        // NOW, USE USER ID TO SEND AUTOAUDIT TO AUTO-API
                        // if (await AutoSecurityFunct.blackListAccessTokens(user, [token])) {
                        //     console.log('TOKEN BLACKLISTED, SENDING AUTOAUDIT FOR LOGOUT ..')
                        //     const result = InternalAutoAuditingFunct.sendSessionTrackingAutoAudit(user, token, null, false)
                        //     // WORK WITH result HOWEVER YOU WANT BUT MUST BE AWAITED FIRST
                        //     // SHOULD YOU CHOOSE TO AWAIT THIS, YOU MUST EDIT sendSessionTrackingAutoAudit() TO RETURN A PROMISE
                        //     resolve(true)
                        // }
                        resolve(true)
                    } catch (err) {
                        console.log('FINAL ERROR -> ' + err)
                        resolve(false)
                    }
                })
            },

            /**
             * Set token cookie directly for oAuth strategies
             */
            setTokenCookie: function setTokenCookie (req, res) {
                if (!req.user) return sendResponse(res, 404, {
                    success: false,
                    message: 'Something went wrong, please try again.'
                })
                const token = signToken(req.user, null)
                res.cookie('access_token', JSON.stringify(token))
                // FIND A WAY TO ATTACH .user & .company TO THE REQUEST OBJECT req
                return sendResponse(res, 200, { access_token: token, user: req.user, company: {} })
                // res.redirect('/')
            },

            isAuthorizedToGetMyData: function isAuthorizedToGetMyData (type) {

                return compose()
                    .use(function checkRequirements (req, res, next) {
                        // 1ST CHECK IF THE req.url EVEN CONTAINS type+'/my' IN THE FIRST PLACE ..
                        // req.originalUrl = /public/autoinvestment/properties/my
                        // req.path = /my
                        // req.baseUrl = /public/autoinvestment/properties
                        // THIS WON'T WORK -> type + '/my' <- COZ type IS IN SINGULAR (BUT req.originalUrl HAS IT'S PLURAL VERSION)
                        if (req.path && req.path === '/my') {
                            try { // CREATE QUERY BASED ON THIS MODEL'S SCHEMA, USING req.user._id
                                console.log('Getting user\'s own "' + type + '" data ..')
                                console.log('USER CALLING REQUEST -> ' + req.user._id)
                                // FIND A WAY TO MAKE THESE RETRIEVABLE TYPES GENERIC

                                //  THIS SHOULD BE COMING FROM settings, & SHOULD BE DIFFERENT FOR THE DIFFERENT userTypes
                                let mydataTypes = ['project', 'proposal', 'account', 'investment', 'property', 'portfolio']

                                if (!mydataTypes.includes(type)) return sendResponse(res, 400, { success: false, message: type + ' data cannot be retrieved by this user' })
                                else { // ATTACH .condition TO req.query OBJECT
                                    console.log('CURRENT REQUEST QUERY -> ' + JSON.stringify(req.query))
                                    let newCondition = {}, user = req.user
                                    // 'mydata': ['projects', 'proposals', 'accounts', 'investments', 'properties', 'portfolios'],
                                    switch (type) {
                                        case 'project':
                                            let key = ''
                                            if (user.type === 'Employee') key = 'employees'
                                            else if (user.type === 'Property Developer') key = 'property_developers'
                                            // else if(user.type === 'Investor') key = 'investors' // <- NOT NEEDED FOR NOW ..
                                            // else // RUN SOME ERROR FUNCTION (IF REQUIRED)

                                            // FOR THIS, KEY MUST BE 'property_developers' WITHOUT '._id', FIND OUT WHY ..
                                            if (key.length > 0) newCondition = { [key]: user._id } // { [key + '._id']: user._id }

                                            // THIS CONDITION WILL ONLY BE REQUIRED WHEN SCALING, WHEN AN Employee CAN ALSO BE A Property Developer FOR A SPECIFIC PROJECT
                                            // OR WHEN AN Investor NEEDS TO BE ACTIVELY MONITORING STATUS OF HIS/HER my.project (AS AN INVESTOR, WHETHER HE'S A PDev / Employee)
                                            // newCondition = {
                                            //     $or: [
                                            //         { 'property_developers': user._id },
                                            //         { 'employees': user._id },
                                            //         // { 'investors': user._id }, // <- NOT NEEDED FOR NOW ..
                                            //     ]
                                            // }
                                            break
                                        case 'proposal':
                                            newCondition = { proposer: user._id }
                                            break
                                        case 'account':
                                            newCondition = { user: user._id }
                                            break
                                        case 'investment':
                                            newCondition = { investor: user._id }
                                            break
                                        case 'property': // FOR THIS, KEY MUST BE 'investors._id'
                                            newCondition = { 'investors._id': user._id } // <- FIND OUT WHY ..
                                            break
                                        case 'portfolio':
                                            newCondition = {
                                                $or: [
                                                    { owner: user._id },
                                                    { 'investors._id': user._id },
                                                ]
                                            }
                                            break
                                        default:
                                            break
                                    }
                                    req.query.condition = Object.assign(req.query.condition || {}, newCondition)
                                    console.log('REQUEST QUERY CONDITION NOW -> ' + JSON.stringify(req.query.condition))
                                    next()
                                }
                            } catch (err) { // RETURN THE REGULAR DATA, BUT LOG THE .err PROPERTY SO IT CAN BE HANDLED
                                console.log('Some error occured while retrieving mydata condition -> ' + JSON.stringify(err))
                                return sendResponse(res, 400, { success: false, message: type + ' data cannot be retrieved' })
                            }
                        } else next()
                    })
            },

            isAuthorizedToGetData: function isAuthorizedToGetData (type, M) {
                if (!type) throw new Error('Some specification needs to be set')
                console.log('Getting ' + type + ' data')
                return compose()
                    .use(this.isAuthenticated()) // OR .use(this.isAuthorized(['get', type]))
                    .use(this.isAuthorizedToGetMyData(type))
                    .use(function checkRequirements(req, res, next) {
                        console.log('This user is authenticated')
                        const condition = (req.query.condition || {})

                        /*
                        try {
                            if (!condition.hasOwnProperty('_id')) // DO THIS COZ IF IT INCLUDES '_id', IT'LL REPLACE IT WITH ANOTHER QUOTES, MESSING UP THE CONDITION
                                condition = (condition || '').replace(/([a-zA-Z0-9$\.][^:,]*)(?=\s*:)/g, ''$1'').replace(/''/igm, ''')
                            condition = JSON.parse(condition || null)
                            console.log('the current condition is ', condition)
                        } catch (e) {
                            console.log('ERROR -> ', e)
                            return sendResponse(res, 401, { success: false, message: 'Bad request condition' })
                        }
                        */

                        /// NOW WORK WITH condition TO DETERMINE THE LEVEL OF SECURITY REQUIRED
                        res.user = req.user // THIS MIGHT NOT EVEN BE NECESSARY
                        res.type = type // MAKE SURE YOU ASSIGN THIS const TO THE REQUEST

                        console.log('Getting ' + type + ' data for user "' + (req.user.full_name || req.user._id)
                            + '" (' + (req.user.type || '') + ') ..')

                        // IN CASE OF USER-TYPE FEATURE (req.user.type) RETURN ONLY THE DATA ACCESSIBLE TO THE USER
                        // eg. IF user.type === 'Investor', user CAN ONLY RETRIEVE DATA RELATED TO user
                        // THEREFORE, USE A condition = { user(s): req.user._id } TO GET RELATED data

                        M = M || { Model: getModel(type) } // WILL NOT INCLUDE validate & OTHER FUNCTIONS WITHIN .model.js FILE
                        M.Model.find(condition, M.dataToExclude || '')
                            .sort(M.sort || '')
                            .populate(M.deepPop || '')
                            // .deepPopulate(M.deepPop || '')
                            .exec(function (err, data) {
                                if (err) {
                                    console.log('Error -> ' + err)
                                    return sendResponse(res, 403, {
                                        err: err,
                                        success: false,
                                        message: 'Could not retrieve data',
                                        data: data || []
                                    })
                                } else if (!data) return sendResponse({
                                    code: 400,
                                    resultData: { success: false, message: type + ' objects do not exist' }
                                })
                                else { // EXECUTE THIS COMMENTED CODE BELOW IF NECESSARY
                                    // THEN CALL
                                    // res.data = data
                                    // next()

                                    // IN CASE OF USER-TYPE FEATURE (req.user.type) RETURN ONLY THE DATA ACCESSIBLE TO THE USER
                                    // eg. IF user.type === 'Investor', user CAN ONLY RETRIEVE DATA RELATED TO user

                                    return sendResponse(res, 200, data)
                                }
                            })
                    })
            },

            /////////////////////////////////////////////////////////////////////////////////////////
            // EXTRA AUTHORIZATION FUNCTIONS
            // THIS ISN'T REQUIRED, COZ IT'S ONLY USED WITHIN settings.controller.js FILE ITSELF
            userOremployeeCanEditSubSettings: function (settingsType, id, employee) {
                return true
            },

        }

        return auth
    },


    // Config Environment Functions

    ConfigEnvironment: function () {
        /*
        FIRST, GET THE WHOLE settings (ConfigSettings) OBJECT
        IF THIS DOESN'T WORK, THEN JUST SEND THIS WHOLE ConfigEnvironment PROPERTY TO AFTER ModuleReferencer's OBJECT LITERAL DECLARATION
        SENDING IT AFTER THE DECLARATION ALLOWS YOU TO ACCESS ModuleReferencer's settings PROPERTY EASILY
        const settings = settings // COZ, DOING IT LIKE THIS WON'T WORK SINCE get() CANNOT WORK WITHIN THE SAME OBJECT LITERAL (ONLY OUTSIDE)
        OR MAYBE YOU CAN ASSIGN A WHOLE SELF-CALLING FUNCTION (function(){}()) TO get settings(){} ACCESSOR UP THERE
        */

        const newConfig = {

            get AutoAPIURL () { // ONLY ADD THE oldConfig.port IF THE IP IS localhost (SO NOT, WITHIN PRODUCTION :)
                const url = oldConfig.protocol + oldConfig.ip + (oldConfig.ip == 'localhost' ? (':' + oldConfig.port) : '') + '/'
                return url
            },

            // Company Details

            get defaultCompanyId () {
                return ''
            }, // THIS PROPERTY MUST BE HARD CODED
            get companyName () {
                return 'Company'
            },
            get companyRegisteredName () {
                return 'Company'
            },
            get companyDetails () {
                return 'Company is a FinTech wallet platform.'
            },
            get companyEmail () {
                return 'info@company.com'
            },
            get companyPhoneNumber () {
                return '+1...'
            },
            get companyHomeAddress () {
                return 'Home Address'
            },
            get companyLocation () {
                return 'Location'
            },

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            _default_settings: {}, // settings.getThisCompanyDefaultSettings(),
            get default_settings () {
                return this._default_settings
            },

            //  Main Enumerations

            get userDataDefault () {
                return settings.getValue('userDataDefault') || this.default_settings.AUTOMANSettings.userDataDefault
            },
            //
            get userTypes () {
                return settings.getValue('userTypeOptions') || this.default_settings.AUTOMANSettings.userTypeOptions
            },
            get userTypesDefault () {
                return settings.getValue('userTypeOptionsDefault') || this.default_settings.AUTOMANSettings.userTypeOptionsDefault
            },
            get userGenders () {
                return settings.getValue('userGenderOptions') || this.default_settings.AUTOMANSettings.userGenderOptions
            },
            get userGendersDefault () {
                return settings.getValue('userGenderOptionsDefault') || this.default_settings.AUTOMANSettings.userGenderOptionsDefault
            },
            get userNationalities () {
                return settings.getValue('userNationalityOptions') || this.default_settings.AUTOMANSettings.userNationalityOptions
            },
            get userNationalitiesDefault () {
                return settings.getValue('userNationalityOptionsDefault') || this.default_settings.AUTOMANSettings.userNationalityOptionsDefault
            },
            get userIdentifications () {
                return settings.getValue('userIdentificationOptions') || this.default_settings.AUTOMANSettings.userIdentificationOptions
            },
            get userIdentificationsDefault () {
                return settings.getValue('userIdentificationOptionsDefault') || this.default_settings.AUTOMANSettings.userIdentificationOptionsDefault
            },
            get recipientsTypes () {
                return settings.getValue('recipientsTypeOptions') || this.default_settings.AUTOMANSettings.recipientsTypeOptions
            },
            get recipientsTypesDefault () {
                return settings.getValue('recipientsTypeOptionsDefault') || this.default_settings.AUTOMANSettings.recipientsTypeOptionsDefault
            },
            get contactMethods () {
                return settings.getValue('contactMethodOptions') || this.default_settings.AUTOMANSettings.contactMethodOptions
            },
            get contactMethodsDefault () {
                return settings.getValue('contactMethodOptionsDefault') || this.default_settings.AUTOMANSettings.contactMethodOptionsDefault
            },

            // Security Env consts

            get userRoles () {
                return settings.getValue('userRoleOptions') || this.default_settings.AUTO_SECURITYSettings.userRoleOptions
            },
            get userRolesDefault () {
                return settings.getValue('userRoleOptionsDefault') || this.default_settings.AUTO_SECURITYSettings.userRoleOptionsDefault
            },
            get securityLevels () {
                return settings.getValue('securityLevelOptions') || this.default_settings.AUTO_SECURITYSettings.securityLevelOptions
            },
            get securityLevelsDefault () {
                return settings.getValue('securityLevelOptionsDefault') || this.default_settings.AUTO_SECURITYSettings.securityLevelOptionsDefault
            }
        }

        return Object.assign(oldConfig, newConfig)
    },

    // Config Settings Functions

    ConfigSettings: function () {
        /*
        FIRST, GET THE WHOLE config (ConfigEnvironment) OBJECT
        IF THIS DOESN'T WORK, THEN JUST SEND THIS WHOLE ConfigSettings PROPERTY TO AFTER ModuleReferencer's OBJECT LITERAL DECLARATION
        SENDING IT AFTER THE DECLARATION ALLOWS YOU TO ACCESS ModuleReferencer's settings PROPERTY EASILY
         OR MAYBE YOU CAN ASSIGN A WHOLE SELF-CALLING FUNCTION (function(){}()) TO get settings(){} ACCESSOR UP THERE
        const config = this.config // COZ, DOING IT LIKE THIS WON'T WORK SINCE get() CANNOT WORK WITHIN THE SAME OBJECT LITERAL (ONLY OUTSIDE)
        ACTUALLY, DOING IT LIKE THIS UP HERE WILL CAUSE ANOTHER 'IMPLICIT' CYCLIC DEPENDENCY BETWEEN this.ConfigSettings & this.ConfigEnvironment
        BUT, HOWEVER!!! THE ONLY 'CONFIG' STUFF YOU NEED WITHIN THIS FUNCTION ARE .settingsFile & .defaultSettingsFile
        WHICH ARE BOTH WITHIN THE ACTUAL ./config/environment FILE, THEREFORE YOU DON'T EVEN NEED THE this.ConfigEnvironment 'CONFIG'
        */

        return settings
    },

    // Initialization function

    init: function () {
        // console.log('Initialization complete')
        return this
    }

}.init()

module.exports = ModuleReferencer
