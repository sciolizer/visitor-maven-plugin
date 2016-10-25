#!/usr/bin/env bash

set -ev

test "${TRAVIS_PULL_REQUEST}" == "false"
#test "${TRAVIS_BRANCH}" == "master"
mvn --batch-mode release:prepare --settings travis/settings.xml
mvn --batch-mode release:perform --settings travis/settings.xml
