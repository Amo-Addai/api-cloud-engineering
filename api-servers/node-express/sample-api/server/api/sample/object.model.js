'use strict'

const mongoose = require('mongoose'),
  timestamps = require('mongoose-timestamp'),
  deepPopulate = require('mongoose-deep-populate')(mongoose),
  Schema = mongoose.Schema

const functions = require('../../functions')
const settings = functions.settings
const env = functions.config
const funct = functions.funct
const type = 'type'

const SampleObjectSchema = new Schema({
  name: String,
  details: String,

  date_created: { type: Date, default: Date.now },
  image_stub: String
})

SampleObjectSchema.plugin(timestamps, {})
SampleObjectSchema.plugin(deepPopulate, {})
const Model = mongoose.model('SampleObject', SampleObjectSchema)

const PublicMethods = {
  markModify: '',
  deepPop: '',
  mainData: 'name details',
  dataToExclude: '',
  imgdata: '',
  sort: {'date_created': -1},
  type: type,

  validate: function validate (obj, add) {
    // date_created, image_stub SHOULD BE SETTLED HERE
    if (add) {
      obj.date_created = Date.now()
    } else {
      if (obj._id) {
        delete obj._id
      }
    }
    if (obj.security) {
      delete obj.security
    // obj.datasecurity = funct.getDataSecurityObject(type, obj)
    // delete obj.security
    }
    return obj
  },

  Model: Model

}
module.exports = PublicMethods
