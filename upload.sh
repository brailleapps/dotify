#!/bin/bash

branch=`git rev-parse --abbrev-ref HEAD`
revision=`git rev-parse HEAD`
echo "$revision"
echo "$branch"

if [ $branch = "master" ]; then
	echo "On master branch."
	if [ -n "$SONATYPE_USER" ]; then
		if [ -n "$SONATYPE_PASSWORD" ]; then
			echo " Uploading..."
			./gradlew uploadArchives -PsonatypeUsername=$SONATYPE_USER -PsonatypePassword=$SONATYPE_PASSWORD -PrepositoryRevision=$revision
		else
			echo "SONATYPE_PASSWORD not set"
		fi
	else
		echo "SONATYPE_USER not set"
	fi
fi



