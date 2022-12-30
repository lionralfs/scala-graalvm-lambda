my attempt at setting up a lambda that is written in scala but uses graalvm to create a single, static binary + a custom aws runtime (see `bootstrap.sh`)

1. create a bucket to store the lambda code
2. run `./deployment/deploy.sh <the-bucket-name-from-step-1>`
3. wait
4. go into the aws console and invoke the function via the `Test` tab