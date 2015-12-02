#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "joeha480/dotify" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R dotify.api/build/docs/javadoc $HOME/dotify.api
  cp -R dotify.common/build/docs/javadoc $HOME/dotify.common
  cp -R dotify.task-api/build/docs/javadoc $HOME/dotify.task-api

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet https://${GH_TOKEN}@github.com/joeha480/joeha480.github.io  > /dev/null

  cd joeha480.github.io
  git rm -rf ./dotify.api
  git rm -rf ./dotify.common
  git rm -rf ./dotify.task-api
  
  cp -Rf $HOME/dotify.api ./dotify.api
  cp -Rf $HOME/dotify.common ./dotify.common
  cp -Rf $HOME/dotify.task-api ./dotify.task-api
  
  git add -f .
  git commit -m "Lastest successful travis build of dotify ($TRAVIS_BUILD_NUMBER) auto-pushed to joeha480.github.io"
  git push -fq origin master > /dev/null

  echo -e "Published javadocs to joeha480.github.io.\n"
  
fi
