'use strict'

import test from 'ava';
import request from 'supertest';

import app from '../../app.js';
import Comment from '../models/comment.js';

import mocks from './mocks/index.js'
const mock = mocks['comments']

test.beforeEach(async () => {
  await Comment.deleteMany({});
});

test.serial('GET /api/comments should get comments with optional sorting', async (t) => {
  const comment1 = new Comment(mock[0]);
  const comment2 = new Comment(mock[1]);
  await comment1.save();
  await comment2.save();

  const response = await request(app).get('/api/comments'); // .query({ sort: '-createdAt' });

  t.is(response.status, 200);
  t.true(Array.isArray(response.body));
  t.is(response.body.length, 2);
  t.is(response.body[0].text, 'Comment 1');
  t.is(response.body[1].text, 'Comment 2');
});

test.serial('POST /api/comments should create a new comment', async (t) => {
  const response = await request(app)
    .post('/api/comments')
    .send({ text: 'Great post!', userId: 'user123' });

  t.is(response.status, 201);
  t.is(response.body.text, 'Great post!');
});

test.serial('GET /api/comments/:id should get a comment by ID', async (t) => {
    const comment = new Comment(mock[0]);
    await comment.save();

  const response = await request(app).get(`/api/comments/${comment._id}`);

  t.is(response.status, 200);
  t.is(response.body.text, 'Comment 1');
});

test.serial('POST /api/comments/:id/like should increment likes for a comment', async (t) => {
  const comment = new Comment({ text: 'Awesome!', userId: 'user789' });
  await comment.save();

  const response = await request(app).post(`/api/comments/${comment._id}/like`);

  t.is(response.status, 200);
  t.is(response.body.likes, 1);
});

test.serial('POST /api/comments/:id/like should return 404 if comment not found', async (t) => {
  const response = await request(app).post('/api/comments/65ba5da6051dd4c416e2ebb5/like');

  t.is(response.status, 404);
});

test.serial('POST /api/comments/:id/like should return 500 since id is invalid', async (t) => {
  const response = await request(app).post('/api/comments/invalid-id/like');

  t.is(response.status, 500);
});

test.serial('POST /api/comments/:id/unlike should decrement likes for a comment', async (t) => {
  const comment = new Comment({ text: 'Good job!', userId: 'user987', likes: 3 });
  await comment.save();

  const response = await request(app).post(`/api/comments/${comment._id}/unlike`);

  t.is(response.status, 200);
  t.is(response.body.likes, 2);
});

test.serial('POST /api/comments/:id/unlike should not allow negative likes', async (t) => {
  const comment = new Comment({ text: 'Interesting.', userId: 'user555', likes: 0 });
  await comment.save();

  const response = await request(app).post(`/api/comments/${comment._id}/unlike`);

  t.is(response.status, 200);
  t.is(response.body.likes, 0);
});

test.serial('POST /api/comments/:id/unlike should return 404 if comment not found', async (t) => {
  const response = await request(app).post('/api/comments/65ba5da6051dd4c416e2ebb5/unlike');

  t.is(response.status, 404);
});

test.serial('POST /api/comments/:id/unlike should return 500 since id is invalid', async (t) => {
  const response = await request(app).post('/api/comments/invalid-id/unlike');

  t.is(response.status, 500);
});
