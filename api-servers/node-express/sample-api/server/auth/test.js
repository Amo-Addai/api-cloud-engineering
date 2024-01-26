'use strict'

// import our express app. We will be passing this to supertest
const app = require('../../app')
const should = require('should')
const request = require('supertest')
const describe = require('describe')
const User = require('../api/USER/user.model').Model

describe('Passport: routes', function () {
  const baseUrl = '/auth/local'
  const emailAddress = 'berry@example.com'
  const realPassword = 'secret1'

  // This function will run before each test.
  beforeEach(function (done) {
    // TODO this should be refactored into a User.new() function.
    // Hash the password
    User.hashPassword(realPassword, function (err, passwordHash) {
      // Create a User
      const u = {
        passwordHash: passwordHash,
        emails: [
          {
            value: emailAddress
          }
        ]
      }
      User.create(u, function (err, u) {
        // call the done() method so the mocha knows we are done.
        done()
      })
    })
  })

  describe('POST /auth/local', function () {
    it('should redirect to "/account" if authentication fails', function (done) {
      // post is what we will be sending to the /auth/local
      const post = {
        email: 'berry@example.com',
        password: realPassword
      }
      request(app)
        .post(baseUrl)
        .send(post)
        .expect(302)
        .end(function (err, res) {
          should.not.exist(err)
          // confirm the redirect
          res.header.location.should.include('/account')
          done()
        })
    })
    it('should redirect to "/login" if authentication fails', function (done) {
      const post = {
        email: 'berry@example.com',
        password: 'fakepassword'
      }
      request(app)
        .post(baseUrl)
        .send(post)
        .expect(302)
        .end(function (err, res) {
          should.not.exist(err)
          // confirm the redirect
          res.header.location.should.include('/login')
          done()
        })
    })
  })
})
