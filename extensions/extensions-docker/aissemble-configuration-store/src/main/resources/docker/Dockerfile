FROM @docker.baseline.repo.id@/boozallen/aissemble-quarkus:@project.version@

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root

RUN microdnf install -y openssl

# Switch back to default user
USER 185

COPY --chown=default target/quarkus-app/lib/ $JAVA_APP_DIR/lib/
COPY --chown=default target/quarkus-app/*.jar $JAVA_APP_DIR/
COPY --chown=default target/quarkus-app/app/ $JAVA_APP_DIR/app/
COPY --chown=default target/quarkus-app/quarkus/ $JAVA_APP_DIR/quarkus/

# Custom scripts to generate certs for the mutating webhook and create the webhook itself
COPY --chmod=755 ./src/main/resources/scripts/genCerts.sh /usr/bin/
COPY --chmod=755 ./src/main/resources/scripts/genWebhook.sh /usr/bin/