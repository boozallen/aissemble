# Java 17 results in a module-not-exported error
FROM registry.access.redhat.com/ubi9/openjdk-11-runtime:1.20

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

#This is technically the default if nothing is set, but this ensures if we move it `run-java.sh` will still be able to find it
ENV JAVA_APP_DIR="/deployments"

COPY --chown=default target/quarkus-app/lib/ $JAVA_APP_DIR/lib/
COPY --chown=default target/quarkus-app/*.jar $JAVA_APP_DIR/
COPY --chown=default target/quarkus-app/app/ $JAVA_APP_DIR/app/
COPY --chown=default target/quarkus-app/quarkus/ $JAVA_APP_DIR/quarkus/

ENV KRAUSENING_BASE="$JAVA_APP_DIR/krausening/base"

# Setting log manager prevents vertx thread blocked exceptions. Password automatically masked by `run-java.sh`
ENV JAVA_OPTS_APPEND="-Djava.util.logging.manager=org.jboss.logmanager.LogManager -DKRAUSENING_BASE=$KRAUSENING_BASE -DKRAUSENING_EXTENSIONS=$KRAUSENING_EXTENSIONS -DKRAUSENING_PASSWORD=$KRAUSENING_PASSWORD"