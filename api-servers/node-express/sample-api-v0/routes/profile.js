'use strict';

const express = require('express');
const router = express.Router();

const profileController = require('../api/controllers/profile');

router.get('/', profileController.getProfiles);
router.post('/', profileController.createProfile);
router.get('/:id', profileController.getProfileById);

module.exports = router;
