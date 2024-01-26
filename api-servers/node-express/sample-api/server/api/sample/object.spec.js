'use strict'

const should = require('should')
const app = require('../../../../../app')
const request = require('supertest')
const describe = require('describe')

describe('GET /api/sths', function () {
  it('should respond with JSON array', function (done) {
    request(app)
      .get('/api/sths')
      .expect(200)
      .expect('Content-Type', /json/)
      .end(function (err, res) {
        if (err) return done(err)
        res.body.should.be.instanceof(Array)
        done()
      })
  })
})
