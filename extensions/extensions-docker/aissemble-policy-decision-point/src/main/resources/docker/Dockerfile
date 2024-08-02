FROM @docker.baseline.repo.id@/boozallen/aissemble-quarkus:@project.version@

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

COPY --chown=default ./src/main/resources/truststore/aissemble-secure.jks ${JAVA_APP_DIR}/
COPY --chown=default ./src/main/resources/krausening/base/aissemble-security.properties ${JAVA_APP_DIR}/krausening/base/
COPY --chown=default ./src/main/resources/authorization/policies/test-policy.xml ${JAVA_APP_DIR}/
COPY --chown=default ./src/main/resources/authorization/attributes/test-attributes.json ${JAVA_APP_DIR}/
COPY --chown=default ./src/main/resources/authorization/pdp.xml ${JAVA_APP_DIR}/
COPY --chown=default target/quarkus-app/lib/ ${JAVA_APP_DIR}/lib/
COPY --chown=default target/quarkus-app/*.jar ${JAVA_APP_DIR}/
COPY --chown=default target/quarkus-app/app/ ${JAVA_APP_DIR}/app/
COPY --chown=default target/quarkus-app/quarkus/ ${JAVA_APP_DIR}/quarkus/
# Uncomment the following lines to enable remote debugging
#ENV JAVA_OPTS_APPEND='-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005'
#EXPOSE 5005