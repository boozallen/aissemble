FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:1.20

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

#This is technically the default if nothing is set, but this ensures if we move it `run-java.sh` will still be able to find it
ENV JAVA_APP_DIR="/deployments"

COPY --chown=default ./src/main/resources/truststore/aissemble-secure.jks $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/krausening/base/aiops-security.properties $JAVA_APP_DIR/krausening/base/
COPY --chown=default ./src/main/resources/authorization/policies/test-policy.xml $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/authorization/attributes/test-attributes.json $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/authorization/pdp.xml $JAVA_APP_DIR/
COPY --chown=default target/dockerbuild/*.jar $JAVA_APP_DIR/

ENV KRAUSENING_BASE="$JAVA_APP_DIR/krausening/base"

# Setting log manager prevents vertx thread blocked exceptions. Password automatically masked by `run-java.sh`
ENV JAVA_OPTS_APPEND="-Djava.util.logging.manager=org.jboss.logmanager.LogManager -DKRAUSENING_BASE=$KRAUSENING_BASE -DKRAUSENING_EXTENSIONS=$KRAUSENING_EXTENSIONS -DKRAUSENING_PASSWORD=$KRAUSENING_PASSWORD"