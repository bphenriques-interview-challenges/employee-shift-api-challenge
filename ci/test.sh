#!/bin/sh
set -xu

# Reference page: https://gist.github.com/earthgecko/3089509
random_string() {
    cat /dev/urandom | env LC_ALL=C tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1
}

PROJECT_NAME="$(random_string)"
BUILD_TAG="$(random_string)"

echo "Building project..."
docker-compose -f docker-compose.yml --project-name "${PROJECT_NAME}" build tests

echo "Running tests..."
docker-compose -f docker-compose.yml --project-name "${PROJECT_NAME}" run --name "${BUILD_TAG}" tests
EXIT=$?

echo "Cleaning up..."
docker-compose -f docker-compose.yml --project-name "${PROJECT_NAME}" down --volumes

exit $EXIT
