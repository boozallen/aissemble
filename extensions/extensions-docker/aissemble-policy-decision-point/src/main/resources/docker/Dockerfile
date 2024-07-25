FROM ${docker.baseline.repo.id}/boozallen/aissemble-quarkus:${project.version}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

COPY --chown=default ./src/main/resources/truststore/aissemble-secure.jks $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/krausening/base/aiops-security.properties $JAVA_APP_DIR/krausening/base/
COPY --chown=default ./src/main/resources/authorization/policies/test-policy.xml $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/authorization/attributes/test-attributes.json $JAVA_APP_DIR/
COPY --chown=default ./src/main/resources/authorization/pdp.xml $JAVA_APP_DIR/
COPY --chown=default target/dockerbuild/*.jar $JAVA_APP_DIR/
