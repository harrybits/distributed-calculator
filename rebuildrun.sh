#!/bin/sh

./mvnw package -DskipTests
docker compose build
docker compose -p dcalc up
