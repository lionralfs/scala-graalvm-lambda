# use a linux container to build a linux binary (this would probably work without docker on linux, but I'm on a mac)
FROM ghcr.io/graalvm/graalvm-ce:ol7-java11-22.3.0
WORKDIR /opt/native-image
RUN gu install native-image
ENTRYPOINT ["native-image"]