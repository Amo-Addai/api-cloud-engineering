'use strict';

const express = require('express');
const router = express.Router();

const commentController = require('../api/controllers/comment');

router.get('/', commentController.getComments);
router.post('/', commentController.createComment);
router.get('/:id', commentController.getCommentById);
router.post('/:id/like', commentController.likeComment);
router.post('/:id/unlike', commentController.unlikeComment);

module.exports = router;
