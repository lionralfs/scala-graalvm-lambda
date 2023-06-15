#!/bin/bash

BUCKET_NAME=$1
STACK_NAME=scala-snapstart-lambda-experiment

./deployment/build.sh

rm -f deployment/out.yml
aws cloudformation package --template-file deployment/template.yaml --s3-bucket $BUCKET_NAME --region eu-central-1 --output-template-file deployment/out.yml
aws cloudformation deploy --template-file deployment/out.yml --stack-name $STACK_NAME --region eu-central-1 --capabilities CAPABILITY_NAMED_IAM
