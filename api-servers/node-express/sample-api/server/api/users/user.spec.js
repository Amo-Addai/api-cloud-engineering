'use strict'

const should = require('should')
const app = require('../../../app')
const request = require('supertest')
const describe = require('describe')
const User = require('./user.model').Model

describe('GET /api/users', function () {
  it('should respond with JSON array', function (done) {
    request(app)
      .get('/api/users')
      .expect(200)
      .expect('Content-Type', /json/)
      .end(function (err, res) {
        if (err) return done(err)
        res.body.should.be.instanceof(Array)
        done()
      })
  })
})

let user = new User({
  provider: 'local',
  full_name: 'Fake User',
  email: 'test@test.com',
  password: 'password'
})

describe('User Model', function () {
  before(function (done) {
    // Clear users before testing
    User.remove().exec().then(function () {
      done()
    })
  })

  afterEach(function (done) {
    User.remove().exec().then(function () {
      done()
    })
  })

  it('should begin with no users', function (done) {
    User.find({}, function (err, users) {
      users.should.have.length(0)
      done()
    })
  })

  it('should fail when saving a duplicate user', function (done) {
    user.save(function () {
      const userDup = new User(user)
      userDup.save(function (err) {
        should.exist(err)
        done()
      })
    })
  })

  it('should fail when saving without an email', function (done) {
    user.email = ''
    user.save(function (err) {
      should.exist(err)
      done()
    })
  })

  it('should authenticate user if password is valid', function () {
    return user.authenticate('password').should.be.true
  })

  it('should not authenticate user if password is invalid', function () {
    return user.authenticate('blah').should.not.be.true
  })
})
