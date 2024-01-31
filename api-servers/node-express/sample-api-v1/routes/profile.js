'use strict';

import express from 'express';
const router = express.Router();

import * as profileController from '../api/controllers/profile.js'

router.get('/', profileController.getProfiles);
router.post('/', profileController.createProfile);
router.get('/:id', profileController.getProfileById);

export default router;
