#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Generating coverage report.\n"

  sbt coverageReport

  echo -e "Publishing coverage report.\n"

  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ScalABM/markets-sandbox gh-pages > /dev/null

  cd gh-pages

  # get rid of old coverage stats
  git rm -rf ./coverage

  # copy over the new coverage stats
  mkdir -p ./coverage
  cp -Rf ../target/scala-2.11/scoverage-report ./coverage

  # push to github!
  git add -f .
  git commit -m "Lastest coverage report on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published coverage report to gh-pages.\n"

fi
