'use strict'

const express = require('express')

const app = express()

const PORT = process.env.PORT || 80

app.use(
    (req, res, next) => (
        console.log('Received request from', req.ip),
        console.log(`${req.method} ${req.originalUrl} HTTP/${req.httpVersion}`),
        console.log(`Host: ${req.headers.host}`),
        console.log(`User-Agent: ${req.headers['user-agent']}`),
        console.log(`Accept: ${req.headers.accept}`),
        console.log(),
        next()
    )
)

app.get('/', (req, res) =>
    res.send('Auto Load-Balancer')
)

app.listen(PORT, () =>
    console.log(`Load-Balancer running and listening on port: ${PORT}`)
)
