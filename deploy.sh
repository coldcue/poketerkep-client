#!/usr/bin/env bash

./createRevision.sh

echo "Pushing to S3"
aws deploy push --application-name poketerkep-client --s3-location s3://poketerkep-client/revision.zip --source revision

echo "Deploying..."
aws deploy create-deployment --application-name poketerkep-client --s3-location bucket=poketerkep-client,key=revision.zip,bundleType=zip --deployment-group-name Production --description Production