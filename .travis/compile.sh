#!/usr/bin/env bash

# enable error checking...
set -e

# clean and compile of the project modules using sbt...
sbt clean compile test:compile functional:compile performance:compile