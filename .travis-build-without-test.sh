#!/bin/bash
ROOT=$TRAVIS_BUILD_DIR/..

# Fail the whole script if any command fails
set -e

export SHELLOPTS

SLUGOWNER=${TRAVIS_PULL_REQUEST_SLUG%/*}
if [[ "$SLUGOWNER" == "" ]]; then
  SLUGOWNER=${TRAVIS_REPO_SLUG%/*}
fi
if [[ "$SLUGOWNER" == "" ]]; then
  SLUGOWNER=typetools
fi
echo SLUGOWNER=$SLUGOWNER

## Compile
echo "running \"mvn package\" for stubparser"
cd javaparser-core && mvn -q package -Dmaven.test.skip=true
