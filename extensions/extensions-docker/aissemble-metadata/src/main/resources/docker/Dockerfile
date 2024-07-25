FROM ${docker.baseline.repo.id}/boozallen/aissemble-quarkus:${project.version}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

COPY --chown=default target/quarkus-app/lib/ $JAVA_APP_DIR/lib/
COPY --chown=default target/quarkus-app/*.jar $JAVA_APP_DIR/
COPY --chown=default target/quarkus-app/app/ $JAVA_APP_DIR/app/
COPY --chown=default target/quarkus-app/quarkus/ $JAVA_APP_DIR/quarkus/
