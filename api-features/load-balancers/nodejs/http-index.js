'use strict'

const http = require('http')

const PORT = process.env.PORT || 80

const server = http.createServer(
    (req, res) => (
        console.log('Received request from', req.socket.remoteAddress),
        console.log(`${req.method} ${req.url} HTTP/${req.httpVersion}`),
        console.log(`Host: ${req.headers.host}`),
        console.log(`User-Agent: ${req.headers['user-agent']}`),
        console.log(`Accept: ${req.headers.accept}`),
        console.log(),

        res.writeHead(200, { 'Content-Type': 'text/plain' }),
        (req.url === '/' && req.method === 'GET')
        ? (
            console.log('auto'),
            res.end('Auto Load-Balancer')
        )
        : res.end('Auto Load-Balancer')
    )
)

server.listen(PORT, () =>
    console.log(`Load-Balancer running and listening on port: ${PORT}`)
)
