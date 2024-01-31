import Profile from '../models/profile.js'

export const getProfiles = async (req, res) => {
  try {
    const { sort, ...condition } = req.query || {};
    // console.log(sort, condition)
    const profiles = await Profile.find(condition).sort(sort).exec();
    res.status(200).json(profiles);
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const createProfile = async (req, res) => {
  try {
    const body = req.body;  
    const profile = new Profile(body);
    await profile.save();
    res.status(201).json(profile);
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export const getProfileById = async (req, res) => {
  try {
    const { id } = req.params;
    const profile = await Profile.findById(id);
    if (profile) {
      res.status(200).json(profile);
    } else {
      res.status(404).json({ error: 'Profile not found' });
    }
  } catch (error) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

export default { getProfiles, createProfile, getProfileById }
