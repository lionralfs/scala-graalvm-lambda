# First we pull the base image from DockerHub
FROM amazon/aws-lambda-provided:al2

# Copy our bootstrap and make it executable
WORKDIR /var/runtime/
COPY graalvm-lambda/bootstrap bootstrap
RUN chmod 755 bootstrap

# Copy our function code and make it executable
WORKDIR /var/task/
COPY graalvm-lambda/lambda-binary lambda-binary
RUN chmod 755 lambda-binary

# Set the handler
# by convention <fileName>.<handlerName>
# (I don't think this matters cause I'm directly invoking the binary in my bootstrap script)
CMD [ "lambda-binary" ]