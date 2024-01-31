import mongoose from 'mongoose'

const profileSchema = new mongoose.Schema({
  name: { type: String, required: true },
  description: { type: String },
  mbti: { type: String },
  enneagram: { type: String },
  variant: { type: String },
  tritype: { type: Number },
  socionics: { type: String },
  sloan: { type: String },
  psyche: { type: String },
  image: { type: String }
})

const Profile = mongoose.model('profiles', profileSchema)

export default Profile
