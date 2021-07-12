#!/bin/bash

echo Entering "$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0") in $(pwd)"

# Fail the whole script if any command fails
set -e

export SHELLOPTS

## Compile
echo "running \"mvn package\" for stubparser"
mvn --version
# Try twice in case of network lossage
(cd javaparser-core && ../mvnw -B -q package -Dmaven.test.skip=true) \
    || (sleep 2m && (cd javaparser-core && ../mvnw -B -q dependency:resolve || true) \
        && sleep 2m && (cd javaparser-core && ../mvnw -B -q package -Dmaven.test.skip=true))

echo Exiting "$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0") in $(pwd)"
