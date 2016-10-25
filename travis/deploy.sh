#!/usr/bin/env bash

set -ev

test "${TRAVIS_PULL_REQUEST}" == "false"
#test "${TRAVIS_BRANCH}" == "master"
mvn release:prepare --settings travis/settings.xml
mvn release:perform --settings travis/settings.xml
