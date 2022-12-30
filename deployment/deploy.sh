#!/bin/bash

BUCKET_NAME=$1
STACK_NAME=example-lambda

sbt clean assembly

docker build -t graalvm-build deployment

docker run \
  --volume $PWD/target/scala-2.13:/opt/assembly \
  --volume $PWD/graalvm-lambda:/opt/native-image \
  graalvm-build \
  --static \
  --no-fallback \
  -jar /opt/assembly/lambda-scala.jar \
  lambda-binary

rm -f deployment/out.yml
aws cloudformation package --template-file deployment/template.yaml --s3-bucket $BUCKET_NAME --region eu-central-1 --output-template-file deployment/out.yml
aws cloudformation deploy --template-file deployment/out.yml --stack-name $STACK_NAME --region eu-central-1 --capabilities CAPABILITY_NAMED_IAM
