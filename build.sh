#!/bin/sh

./mvnw clean package -U -Pnative -Dquarkus.native.container-build=true
docker rmi quay.io/qiotmanufacturing/factory-facility-manager:1.0.0-alpha7 --force
docker build -f src/main/docker/Dockerfile.native -t quay.io/qiotmanufacturing/factory-facility-manager:1.0.0-alpha7 .
docker push quay.io/qiotmanufacturing/factory-facility-manager:1.0.0-alpha7