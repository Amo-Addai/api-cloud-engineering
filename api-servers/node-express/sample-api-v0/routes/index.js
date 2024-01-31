'use strict'

const express = require('express');
const router = express.Router();

const profileRoutes = require('./profile')
const commentRoutes = require('./comment')

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

exports.api = function() {
    // Use routes
    router.use('/profiles', profileRoutes)
    router.use('/comments', commentRoutes)
    return router
}

exports.view = function() {
    router.get('/', function(req, res, next) {
      res.render('profile_template', {
        profile: profileData[0],
      });
    });
    return router
}
