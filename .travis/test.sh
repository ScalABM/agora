#!/usr/bin/env bash

# enable error checking!
set -e

# run tests and generate code coverage report...
sbt coverage test functional:test
sbt coverageReport coveralls