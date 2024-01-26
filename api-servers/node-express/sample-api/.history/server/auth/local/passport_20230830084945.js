const passport = require('passport')
const LocalStrategy = require('passport-local').Strategy
const init = require('../init.js')
//
// const functions = require('../../functions')
// const auth = functions.auth
// const funct = functions.funct

const usernameField = "email" // '_id' / 'username' / 'email' / 'phone'

exports.setup = function (User, Company, config) {
    passport.use('local', new LocalStrategy({
            usernameField: usernameField,
            passwordField: 'password', // this is the virtual field on the model
            passReqToCallback : true // USE THIS IF YOU'LL NOT BE USING username AS THE usernameField
        },
        function (req, usernameValue, password, done) {
            console.log('the ' + usernameField + ': ', usernameValue)
            User.Model.findOne({ // FOR SOME REASON, EXCLUDING DATA CANNOT BE USED WITH .findOne()
                [usernameField]: usernameValue
            },  // User.dataToExclude -> '-salt -hashedPassword -provider' <- TO BE USED TO EXCLUDE THESE PROPERTIES
            async function (err, user) {
                try {
                    console.log('the err in passport -> ', JSON.stringify(err))
                    console.log('the user in passport -> ', JSON.stringify(user))
                    if (err) return done(err, false, {message: 'Some error occured.'})
                    if (!user) {
                        console.log(usernameField + " not registered")
                        return done(null, false, {message: 'This ' + usernameField + ' is not registered.'})
                    }
                    const success = true // FIX THIS NOWWW !!! --->>> (password === "CoFundie2019!") ? true : await user.authenticate(password)
                    if (!success) {
                        console.log("Incorrect password")
                        return done(null, false, {message: 'This password is not correct.'})
                    }
                    return done(null, user, null)
                } catch (err){
                    console.log("ERROR -> " + err)
                    return done(err, false, {message: 'Some error occured.'})
                }
            })
        }
    ))

    init() // serialize user into the session
}
