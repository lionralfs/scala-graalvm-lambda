my attempt at setting up a lambda that is written in scala but uses graalvm to create a single, static binary + a custom
aws runtime (see `bootstrap.sh`)

1. create a bucket to store the lambda code
2. run `./deployment/deploy.sh <the-bucket-name-from-step-1>`
3. wait
4. go into the aws console and invoke the function via the `Test` tab

### Results

(not scientific at all):

- Init duration: 205.22 ms
- ~1.25ms

### for local testing

1. `sbt clean assembly`
2. `docker build -t graalvm-build deployment`
3. ```
   docker run \
   --volume $PWD/target/scala-2.13:/opt/assembly \
   --volume $PWD/graalvm-lambda:/opt/native-image \
   graalvm-build \
   --static \
   --no-fallback \
   -jar /opt/assembly/lambda-scala.jar \
   lambda-binary
   ```
4. `docker build -t docker-lambda .`
5. `docker run -p 9000:8080 docker-lambda:latest`
6. Send a test event: `curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d 'Hello world!'`

### Resources

- https://github.com/andthearchitect/aws-lambda-java-runtime
- https://docs.aws.amazon.com/lambda/latest/dg/images-test.html
- https://github.com/aws/aws-lambda-runtime-interface-emulator/
- https://towardsdatascience.com/aws-lambda-with-custom-docker-images-as-runtime-9645b7baeb6f
- https://aripalo.com/blog/2020/aws-lambda-container-image-support/
- https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html