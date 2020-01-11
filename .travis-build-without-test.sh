#!/bin/bash

echo Entering "$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0")" in `pwd`

# Fail the whole script if any command fails
set -e

export SHELLOPTS

## Compile
echo "running \"mvn package\" for stubparser"
mvn --version
cd javaparser-core && mvn -B -q package -Dmaven.test.skip=true

echo Exiting "$(cd "$(dirname "$0")" && pwd -P)/$(basename "$0")" in `pwd`
