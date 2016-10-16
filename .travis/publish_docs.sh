#!/bin/bash
set -ev

echo -e "Generating scaladoc.\n"

sbt doc

echo -e "Publishing scaladoc.\n"

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/EconomicSL/agora gh-pages > /dev/null

cd gh-pages

# get rid of old docs
git rm -rf ./docs

# copy over the new docs
mkdir -p ./docs/api/latest
cp -Rf ../target/scala-2.11/api/* ./docs/api/latest

# push to github!
git add -f .
git commit -m "Lastest docs for travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published scaladoc to gh-pages.\n"

