'use strict'

import * as profiles from './profiles.json' assert { type: "json" }
import * as comments from './comments.json' assert { type: "json" }

export default { profiles: profiles.default, comments: comments.default }
