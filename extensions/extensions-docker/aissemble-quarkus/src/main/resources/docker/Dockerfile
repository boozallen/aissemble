# Script for creating base Quarkus application Docker image
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.20

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

#Copy Kraus env vars to JAVA_OPTS_APPEND with run-env.s
COPY --chown=default ./src/main/resources/scripts/ /deployments/

#This is technically the default if nothing is set, but this ensures if we move it `run-java.sh` will still be able to find it
ENV JAVA_APP_DIR="/deployments"
ENV KRAUSENING_BASE="$JAVA_APP_DIR/krausening/base"

# Setting log manager prevents vertx thread blocked exceptions.
ENV JAVA_OPTS_APPEND="-Djava.util.logging.manager=org.jboss.logmanager.LogManager"
