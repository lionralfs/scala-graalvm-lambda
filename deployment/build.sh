#!/bin/bash

sbt clean assembly

docker build -t graalvm-build deployment

docker run \
  --volume $PWD/target/scala-2.13:/opt/assembly \
  --volume $PWD/graalvm-lambda:/opt/native-image \
  --volume $PWD/src/main/resources:/opt/config \
  graalvm-build \
  --static \
  --no-fallback \
  -H:ReflectionConfigurationFiles=/opt/config/reflect-config.json \
  -jar /opt/assembly/lambda-scala.jar \
  lambda-binary
