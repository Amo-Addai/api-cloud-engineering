#!/bin/bash

# /tmp/avva-backend/db must exist and be owned by podman
podman run --rm --name avva-backend-db -d -v /tmp/avva-backend/db:/var/lib/postgresql/data -p 5432:5432 -e POSTGRES_USER=avva -e POSTGRES_PASSWORD=avva -t docker.io/postgres:15.1
podman run --rm --name avva-backend-redis -d -p 6379:6379 -t docker.io/redis:7.0
