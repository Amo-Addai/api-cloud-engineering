'use strict'

const { expect } = require('chai');
const request = require('supertest');
const app = require('../../app');
const Profile = require('../models/profile');

const mock = require('./mocks')()['profiles']

describe('Profile API', () => {
  beforeEach(async () => {
    await Profile.deleteMany({});
  });

  describe('GET /api/profiles', () => {
    it('should get profiles with optional sorting', async () => {
      const profile1 = new Profile(mock[1]);
      const profile2 = new Profile(mock[2]);
      await profile1.save();
      await profile2.save();

      const response = await request(app).get('/api/profiles');

      expect(response.status).to.equal(200);
      expect(response.body).to.be.an('array').that.has.lengthOf(2);
      expect(response.body[0]).to.have.property('name', 'John Doe');
      expect(response.body[1]).to.have.property('name', 'Jane Doe');
    });
  });

  describe('POST /api/profiles', () => {
    it('should create a new profile', async () => {
      const response = await request(app)
        .post('/api/profiles')
        .send(mock[1]);

      expect(response.status).to.equal(201);
      expect(response.body).to.have.property('name', 'John Doe');
    });
  });

  describe('GET /api/profiles/:id', () => {
    it('should get a profile by ID', async () => {
      const profile = new Profile(mock[2]);
      await profile.save();

      const response = await request(app).get(`/api/profiles/${profile._id}`);

      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('name', 'Jane Doe');
    });

    it('should return 404 if profile not found', async () => {
      const response = await request(app).get('/api/profiles/65ba5da6051dd4c416e2ebb5');

      expect(response.status).to.equal(404);
    });

    it('should return 500 since id is invalid', async () => {
      const response = await request(app).get('/api/profiles/invalid-id');

      expect(response.status).to.equal(500);
    });
  });
});
