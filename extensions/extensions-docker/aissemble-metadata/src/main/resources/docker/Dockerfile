FROM @docker.baseline.repo.id@/boozallen/aissemble-quarkus:@project.version@

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

COPY --chown=default target/quarkus-app/lib/ $JAVA_APP_DIR/lib/
COPY --chown=default target/quarkus-app/*.jar $JAVA_APP_DIR/
COPY --chown=default target/quarkus-app/app/ $JAVA_APP_DIR/app/
COPY --chown=default target/quarkus-app/quarkus/ $JAVA_APP_DIR/quarkus/

# Spark needs to use an internal API that has been made inaccessible by default, this is resolved by add-exports.
# Apache Hive currently does not support java 17, so the add-opens workaround is used to allow the illegal reflection 
# call until Hive updates their compatible Java versions.  
# https://stackoverflow.com/questions/41265266/how-to-solve-inaccessibleobjectexception-unable-to-make-member-accessible-m?rq=2
ENV JAVA_OPTS_APPEND="${JAVA_OPTS_APPEND} --add-exports java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED"