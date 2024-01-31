'use strict'

import express from 'express';
const router = express.Router();

import profileRoutes from './profile.js'
import commentRoutes from './comment.js'

const profileData = [
  {
    "id": 1,
    "name": "A Martinez",
    "description": "Adolph Larrue Martinez III.",
    "mbti": "ISFJ",
    "enneagram": "9w3",
    "variant": "sp/so",
    "tritype": 725,
    "socionics": "SEE",
    "sloan": "RCOEN",
    "psyche": "FEVL",
    "image": "https://soulverse.boo.world/images/1.png",
  }
];

export const api = function() {
    // Use routes
    router.use('/profiles', profileRoutes)
    router.use('/comments', commentRoutes)
    return router
}

export const view = function() {
    router.get('/', function(req, res, next) {
      res.render('profile_template', {
        profile: profileData[0],
      });
    });
    return router
}

export default { api, view }
