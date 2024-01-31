import Comment  from '../models/comment.js'

export const getComments = async (req, res) => {
  try {
    const { sort, ...condition } = req.query || {};
    // console.log(sort, condition)
    const comments = await Comment.find(condition).sort(sort).exec();
    res.status(200).json(comments);
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const createComment = async (req, res) => {
  try {
    const body = req.body;
    const comment = new Comment(body);
    await comment.save();
    res.status(201).json(comment);
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const getCommentById = async (req, res) => {
  try {
    const { id } = req.params;
    const comment = await Comment.findById(id);
    if (comment) {
      res.status(200).json(comment);
    } else {
      res.status(404).json({ error: 'Comment not found' });
    }
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const likeComment = async (req, res) => {
  try {
    const { id } = req.params;
    const comment = await Comment.findById(id);
    if (comment) {
      comment.likes += 1;
      await comment.save();
      res.status(200).json(comment);
    } else {
      res.status(404).json({ error: 'Comment not found' });
    }
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const unlikeComment = async (req, res) => {
  try {
    const { id } = req.params;
    const comment = await Comment.findById(id);
    if (comment) {
      comment.likes = Math.max(0, comment.likes - 1);
      await comment.save();
      res.status(200).json(comment);
    } else {
      res.status(404).json({ error: 'Comment not found' });
    }
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export default { getComments, createComment, getCommentById, likeComment, unlikeComment }
