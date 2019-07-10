#!/bin/bash

# Fail the whole script if any command fails
set -e

export SHELLOPTS

## Compile
echo "running \"mvn package\" for stubparser"
cd javaparser-core && mvn -q package -Dmaven.test.skip=true
