'use strict'

import test from 'ava';
import request from 'supertest';

import app from '../../app.js';
import Profile from '../models/profile.js';

import mocks from './mocks/index.js'
const mock = mocks['profiles']

test.beforeEach(async () => {
  await Profile.deleteMany({});
});

test.serial('GET /api/profiles should get profiles with optional sorting', async (t) => {
  const profile1 = new Profile(mock[1]);
  const profile2 = new Profile(mock[2]);
  await profile1.save();
  await profile2.save();

  const response = await request(app).get('/api/profiles'); // .query({ sort: '-createdAt' });

  t.is(response.status, 200);
  t.true(Array.isArray(response.body));
  t.is(response.body.length, 2);
  t.is(response.body[0].name, 'John Doe');
  t.is(response.body[1].name, 'Jane Doe');
});

test.serial('POST /api/profiles should create a new profile', async (t) => {
  const response = await request(app)
    .post('/api/profiles')
    .send(mock[1]);

  t.is(response.status, 201);
  t.is(response.body.name, 'John Doe');
});

test.serial('GET /api/profiles/:id should get a profile by ID', async (t) => {
  const profile = new Profile(mock[2]);
  await profile.save();

  const response = await request(app).get(`/api/profiles/${profile._id}`);

  t.is(response.status, 200);
  t.is(response.body.name, 'Jane Doe');
});

test.serial('GET /api/profiles/:id should return 404 if profile not found', async (t) => {
  const response = await request(app).get('/api/profiles/65ba5da6051dd4c416e2ebb5');

  t.is(response.status, 404);
});

test.serial('GET /api/profiles/:id should return 500 since id is invalid', async (t) => {
  const response = await request(app).get('/api/profiles/invalid-id');

  t.is(response.status, 500);
});
