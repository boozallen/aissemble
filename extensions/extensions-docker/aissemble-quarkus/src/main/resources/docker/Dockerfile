# Script for creating base Quarkus application Docker image

FROM registry.access.redhat.com/ubi8/ubi-minimal:8.3 

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

ARG JAVA_PACKAGE=java-11-openjdk-headless
ARG RUN_JAVA_VERSION=1.3.8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# Install java and the run-java script
# Also set up permissions for user `1001`
RUN microdnf install curl ca-certificates ${JAVA_PACKAGE} tzdata-java \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && mkdir /deployments/classpath_extra \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && curl https://repo1.maven.org/maven2/io/fabric8/run-java-sh/${RUN_JAVA_VERSION}/run-java-sh-${RUN_JAVA_VERSION}-sh.sh -o /deployments/run-java.sh \
    && chown 1001 /deployments/run-java.sh \
    && chmod 540 /deployments/run-java.sh \
    && chown -R 1001:root /deployments \
    && echo "securerandom.source=file:/dev/urandom" >> /etc/alternatives/jre/conf/security/java.security

# Configure the JAVA_OPTIONS
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

EXPOSE 8080
USER 1001


COPY ./src/main/resources/scripts/ /deployments/

ENTRYPOINT [ "/deployments/run-java.sh" ]