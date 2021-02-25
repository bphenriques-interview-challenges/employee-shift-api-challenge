FROM adoptopenjdk/openjdk11:debian-slim as BUILD_IMAGE
MAINTAINER Bruno Henriques (bphenriques@outlook.com)

ARG DOCKERIZE_VERSION=v0.6.1
ENV GRADLE_OPTS "-Dorg.gradle.daemon=false"
ENV APP_HOME /app

WORKDIR $APP_HOME

RUN apt-get update && apt-get install -y wget && \
        wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
        && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
        && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

# Download dependencies
ADD build.gradle.kts $APP_HOME
ADD settings.gradle.kts $APP_HOME
ADD gradlew $APP_HOME
ADD gradle $APP_HOME/gradle

# Build
COPY src $APP_HOME/src
ADD db $APP_HOME/db
RUN $APP_HOME/gradlew clean build -x test

# Exposed ports.
EXPOSE 8080
EXPOSE 8081

# Build PRD image with the binaries only.
FROM adoptopenjdk/openjdk11:debian-slim as PRD_IMAGE
WORKDIR /app

COPY --from=BUILD_IMAGE /app/build/libs/employee-shift-api-*.jar ./employee-shift-api.jar
COPY --from=BUILD_IMAGE /app/db/migration ./db/migration

EXPOSE 8080
EXPOSE 8081
