'use strict';

import express from 'express';
const router = express.Router();

import * as commentController from '../api/controllers/comment.js'

router.get('/', commentController.getComments);
router.post('/', commentController.createComment);
router.get('/:id', commentController.getCommentById);
router.post('/:id/like', commentController.likeComment);
router.post('/:id/unlike', commentController.unlikeComment);

export default router;
