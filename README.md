my attempt at setting up a lambda that is written in scala but uses graalvm to create a single, static binary + a custom
aws runtime (see [`Bootstrap.scala`](./src/main/scala/example/Bootstrap.scala))

1. create a bucket to store the lambda code
2. run `./deployment/deploy.sh <the-bucket-name-from-step-1>`
3. wait
4. go into the aws console and invoke the function via the `Test` tab

from https://dev.to/aws-builders/introducing-lambda-snapstart-46no:
> SnapStart is only usable when invoking fully qualified Lambda ARNs, which means publishing and invoking an specific
> function version.

```sh
aws lambda invoke --invocation-type RequestResponse --function-name <FUNCTION_NAME>:<FUNCTION_VERSION> --cli-binary-format raw-in-base64-out --payload \"world\" outputfile.txt
```

### Results

(not scientific at all, first row is non-coldstarts, second row are the coldstarts, in milliseconds):

![Results](./results.png)

### for local testing

1. `./deployment/build.sh`
2. `docker build -t docker-lambda .`
3. `docker run -p 9000:8080 docker-lambda:latest`
4. Send a test event: `curl "http://localhost:9000/2015-03-31/functions/function/invocations" -d 'Hello world!'`

### Resources

- https://github.com/andthearchitect/aws-lambda-java-runtime
- https://docs.aws.amazon.com/lambda/latest/dg/images-test.html
- https://github.com/aws/aws-lambda-runtime-interface-emulator/
- https://towardsdatascience.com/aws-lambda-with-custom-docker-images-as-runtime-9645b7baeb6f
- https://aripalo.com/blog/2020/aws-lambda-container-image-support/
- https://docs.aws.amazon.com/lambda/latest/dg/runtimes-api.html
- https://aws.amazon.com/blogs/compute/build-a-custom-java-runtime-for-aws-lambda/
- https://www.formkiq.com/blog/tutorials/aws-lambda-graalvm/
- https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html#configuration-envvars-runtime
