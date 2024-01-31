'use strict'

const { expect } = require('chai');
const request = require('supertest');
const app = require('../../app');
const Comment = require('../models/comment');

const mock = require('./mocks')()['comments']

describe('Comment API', () => {
  beforeEach(async () => {
    await Comment.deleteMany({});
  });

  describe('GET /api/comments', () => {
    it('should get comments', async () => {
      const comment1 = new Comment(mock[0]);
      const comment2 = new Comment(mock[1]);
      await comment1.save();
      await comment2.save();

      const response = await request(app).get('/api/comments');

      expect(response.status).to.equal(200);
      expect(response.body).to.be.an('array').that.has.lengthOf(2);
      expect(response.body[0]).to.have.property('text', 'Comment 1');
      expect(response.body[1]).to.have.property('text', 'Comment 2');
    });
  });

  describe('POST /api/comments', () => {
    it('should create a new comment', async () => {
      const response = await request(app)
        .post('/api/comments')
        .send({ text: 'Great post!', userId: 'user123' });

      expect(response.status).to.equal(201);
      expect(response.body).to.have.property('text', 'Great post!');
    });
  });

  describe('GET /api/comments/:id', () => {
    it('should get a comment by ID', async () => {
      const comment = new Comment(mock[0]);
      await comment.save();

      const response = await request(app).get(`/api/comments/${comment._id}`);

      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('text', 'Comment 1');
    });

    it('should return 404 if comment not found', async () => {
      const response = await request(app).get('/api/comments/65ba5da6051dd4c416e2ebb5');

      expect(response.status).to.equal(404);
    });

    it('should return 500 since id is invalid', async () => {
      const response = await request(app).get('/api/comments/invalid-id');

      expect(response.status).to.equal(500);
    });
  });

  describe('POST /api/comments/:id/like', () => {
    it('should increment likes for a comment', async () => {
      const comment = new Comment({ text: 'Awesome!', userId: 'user789' });
      await comment.save();

      const response = await request(app).post(`/api/comments/${comment._id}/like`);

      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('likes', 1);
    });

    it('should return 404 if comment not found', async () => {
      const response = await request(app).post('/api/comments/65ba5da6051dd4c416e2ebb5/like');

      expect(response.status).to.equal(404);
    });

    it('should return 500 since id is invalid', async () => {
      const response = await request(app).post('/api/comments/invalid-id/like');

      expect(response.status).to.equal(500);
    });
  });

  describe('POST /api/comments/:id/unlike', () => {
    it('should decrement likes for a comment', async () => {
      const comment = new Comment({ text: 'Good job!', userId: 'user987', likes: 3 });
      await comment.save();

      const response = await request(app).post(`/api/comments/${comment._id}/unlike`);

      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('likes', 2);
    });

    it('should not allow negative likes', async () => {
      const comment = new Comment({ text: 'Interesting.', userId: 'user555', likes: 0 });
      await comment.save();

      const response = await request(app).post(`/api/comments/${comment._id}/unlike`);

      expect(response.status).to.equal(200);
      expect(response.body).to.have.property('likes', 0);
    });

    it('should return 404 if comment not found', async () => {
      const response = await request(app).post('/api/comments/65ba5da6051dd4c416e2ebb5/unlike');

      expect(response.status).to.equal(404);
    });

    it('should return 500 since id is invalid', async () => {
      const response = await request(app).post('/api/comments/invalid-id/unlike');

      expect(response.status).to.equal(500);
    });
  });
});
