const mongoose = require('mongoose');

const commentSchema = new mongoose.Schema({
  text: { type: String, required: true, },
  likes: { type: Number, default: 0, },
  userId: { type: String, },
});

const Comment = mongoose.model('comments', commentSchema);

module.exports = Comment;
