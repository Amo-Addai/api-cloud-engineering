'use strict'

module.exports = function(types=['profiles', 'comments']) {
    let data = {}
    for (let type of types) data[type] = require(`./${type}.json`)
    return data
}